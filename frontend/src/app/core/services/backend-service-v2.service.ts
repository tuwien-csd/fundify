import { inject, Injectable } from '@angular/core';
import createClient, { Client } from 'openapi-fetch';
import { environment } from '../../../environments/environment';
import { paths } from '../../../generated/refop-be';
import { AuthService } from '../auth/services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class BackendServiceV2 {
  private readonly authService = inject(AuthService);

  readonly client: Client<paths, `${string}/${string}`>;

  constructor() {
    const client = createClient<paths>({ baseUrl: environment.backendBaseUrl });
    client.use({
      // Pass the authentication token in the Authorization header for every request if the user is authenticated
      onRequest: async ({ request }) => {
        // Wait for the OAuth flow to initialize first: on a hard page reload the
        // token is not available yet for the first few hundred ms, and requests
        // fired in that window (e.g. by a root store's init hook) would go out
        // anonymously and fail with a 401.
        await this.authService.authInitialized;
        if (this.authService.isAuthenticated()) {
          request.headers.set(
            'Authorization',
            `Bearer ${this.authService.token()}`
          );
        }
        return request;
      },
    });

    this.client = client;
  }
}
