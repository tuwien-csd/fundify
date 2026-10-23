import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { filter, map, take } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { ROUTER_LINKS } from '../../router-links.constants';

/**
 * Keeps authenticated users that have no permissions configured in the DB (and
 * are not admins) on the start page. They may only see the home page until an
 * admin grants them permissions; any attempt to reach a protected area
 * redirects back home where a "permissions missing" message is shown.
 *
 * Waits for auth to fully resolve first, otherwise a hard reload would race the
 * async `/api/users/me` fetch and wrongly redirect.
 */
export const hasPermissionsGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.authResolved$.pipe(
    filter((resolved) => resolved),
    take(1),
    map(() =>
      authService.missingPermissions()
        ? router.parseUrl('/' + ROUTER_LINKS.HOME)
        : true
    )
  );
};
