import { computed, inject, Injectable, resource, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { AuthConfig, OAuthService } from 'angular-oauth2-oidc';
import { User } from '../models/user.interface';
import { UserRoleEnum } from '../models/user-role.enum';
import { environment } from '../../../../environments/environment';
import { filter, take } from 'rxjs/operators';
import { ConfigService } from '../../services/config.service';

export interface AuthState {
  user: User | null;
  token: string | null;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private oAuthService = inject(OAuthService);
  private configService = inject(ConfigService);

  // state
  private state = signal<AuthState>({
    user: null,
    token: null,
  });

  // True once the OAuth flow has finished initializing (discovery doc loaded
  // and any persisted session restored). Used to know when token() is reliable.
  private oauthInitialized = signal(false);

  // selectors
  user = computed(() => this.state().user);
  token = computed(() => this.state().token);

  username = computed(() => this.user()?.name || null);

  roles = computed(() => this.userPermissions()?.roles ?? []);

  // Realm roles carried in the Keycloak token, independent of the permissions
  // configured in the DB. Used to recognize admins that have no DB permissions
  // yet (e.g. on first login) so they keep full access.
  private keycloakRoles = computed(() =>
    (this.user()?.roles ?? []).map((role) => role.toLowerCase())
  );

  isAuthenticated = computed(() => !!this.token());
  isAdmin = computed(
    () =>
      this.keycloakRoles().includes(UserRoleEnum.ADMIN) ||
      this.roles().includes(UserRoleEnum.ADMIN)
  );
  isFunder = computed(() => this.roles().includes(UserRoleEnum.FUNDER));

  /**
   * True once auth has resolved for a logged-in, non-admin user that has no
   * permissions configured in the DB. Such users are restricted to the start
   * page until an admin grants them permissions.
   */
  readonly missingPermissions = computed(
    () =>
      this.isAuthenticated() &&
      this.authResolved() &&
      !this.userPermissions() &&
      !this.isAdmin()
  );

  private userDetailsResource = resource({
    params: () => ({ token: this.token() }),
    loader: async ({ params }) => {
      return params.token ? await this.fetchUserInfo() : undefined;
    },
  });

  private userPermissions = computed(() => {
    if (this.userDetailsResource.hasValue()) {
      return this.userDetailsResource.value();
    }
    return undefined;
  });

  /**
   * Becomes true once the OAuth flow has initialized AND, when a user is logged
   * in, their permissions/roles have been loaded from the backend.
   *
   * Route guards must await this before evaluating roles: on a hard page reload
   * the guard would otherwise race the async `/api/users/me` fetch, see an empty
   * roles() and wrongly redirect to the "not authorized" page.
   */
  readonly authResolved = computed(() => {
    if (!this.oauthInitialized()) return false;
    // Not logged in: nothing more to wait for, let the guard decide/redirect.
    if (!this.token()) return true;
    // Logged in: wait until the user-details fetch has settled.
    const status = this.userDetailsResource.status();
    return status === 'resolved' || status === 'error';
  });

  readonly authResolved$ = toObservable(this.authResolved);

  userAffiliationId = computed(() => {
    return this.userPermissions()?.affiliationId;
  });

  login(): void {
    this.oAuthService.initLoginFlow();
  }

  logout(): void {
    this.oAuthService.logOut();
    // Clear our selectors after logout
    this.state.set({ user: null, token: null });
  }

  hasAnyRole = (expectedRoles: string[]) =>
    this.roles().some((role) => expectedRoles.includes(role));

  constructor() {
    this.init();
  }

  private async init() {
    //Service is set up once the config service emits a loaded config
    this.configService.$config.pipe(take(1)).subscribe(async (config) => {
      const authConfig: AuthConfig = {
        issuer: config.authUrl,
        clientId: config.authClient,
        redirectUri: window.location.origin,
        oidc: true,
        scope: config.authScope,
        responseType: 'code',
        requireHttps: environment.production,
        showDebugInformation: false,
      };
      this.oAuthService.configure(authConfig);
      // Let OAuthService manage refresh by itself
      this.oAuthService.setupAutomaticSilentRefresh();
      await this.oAuthService.loadDiscoveryDocumentAndTryLogin();

      this.oAuthService.events
        .pipe(
          filter(
            (event) =>
              event.type === 'token_received' ||
              event.type === 'token_refreshed' ||
              event.type === 'token_error' ||
              event.type === 'session_terminated' ||
              event.type === 'session_error'
          )
        )
        .subscribe((event) => {
          if (
            event.type === 'token_received' ||
            event.type === 'token_refreshed'
          ) {
            this.updateFromOAuth();
          } else {
            // Record the error/status and clear if session is terminated
            this.state.update((s) => ({ ...s, error: event }));
            if (
              event.type === 'session_terminated' ||
              event.type === 'session_error'
            ) {
              this.state.set({ user: null, token: null });
            }
          }
        });

      // Initialize state if tokens are already present (e.g., after app reload)
      if (
        this.oAuthService.hasValidAccessToken() &&
        this.oAuthService.hasValidIdToken()
      ) {
        this.updateFromOAuth();
      }

      // Signal that auth init is done (token() is now reliable) so route guards
      // can stop waiting and evaluate access.
      this.oauthInitialized.set(true);
    });
  }

  private updateFromOAuth(): void {
    const token = this.oAuthService.getAccessToken() || null;
    const user = this.buildUserFromClaims();
    this.state.update((s) => ({
      ...s,
      user,
      token,
      error: null,
    }));
  }

  private buildUserFromClaims(): User | null {
    const claims: Record<string, unknown> | null =
      this.oAuthService.getIdentityClaims() as Record<string, unknown> | null;

    if (!claims) return null;

    const id = String(claims['sub'] ?? '');
    const email = String(claims['email'] ?? '');
    const roles = claims['roles'] ?? [];

    // `preferred_username` is the primary display name, but it is not guaranteed
    // to be present in every token (depends on the IdP's claim mappers). Fall
    // back through the other standard OIDC name claims and finally the email so
    // the user widget never renders blank for an authenticated user.
    const fullName = [claims['given_name'], claims['family_name']]
      .filter(Boolean)
      .join(' ')
      .trim();
    const name =
      String(claims['preferred_username'] ?? '') ||
      String(claims['name'] ?? '') ||
      fullName ||
      email;

    if (!id || !email) return null;

    return {
      id,
      name,
      emailDomain: email.includes('@') ? email.split('@')[1] : '',
      roles: Array.isArray(roles) ? roles : [],
    };
  }

  /**
   * Fetches the details of the user from the backend.
   * This is required because our JWT has no information about the user's affiliation and their roles.
   *
   * Unfortuntely, this AuthService cannot directly use the generated OpenApi client, as this would
   * cause a circular dependency (since the BackendServiceV2 depends on this AuthService).
   * Instead, we use the fetch API directly.
   * @private
   */
  private async fetchUserInfo() {
    const res = await fetch(`${environment.backendBaseUrl}api/users/me`, {
      headers: {
        Authorization: `Bearer ${this.state().token}`,
      },
    });
    if (res.ok) {
      return await this.parseUserPermissions(res);
    }
    console.error('Error while fetching user details: ', res.statusText);
    return undefined;
  }

  private async parseUserPermissions(res: Response) {
    return (await res.json().then((it: UserPermissionHolder) => ({
      ...it,
      roles: it.roles.map((role) => role.toLowerCase()),
    }))) as UserPermissionHolder;
  }
}

type UserPermissionHolder = {
  id: string;
  affiliationId: string | null;
  roles: string[];
};
