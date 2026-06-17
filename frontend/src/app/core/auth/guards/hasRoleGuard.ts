import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { filter, map, take } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { ROUTER_LINKS } from '../../router-links.constants';

export const hasRoleGuard = (expectedRoles: string[]): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    // Wait until auth has fully resolved (roles loaded) before deciding,
    // otherwise a hard page reload races the role fetch and wrongly denies access.
    return authService.authResolved$.pipe(
      filter((resolved) => resolved),
      take(1),
      map(
        () =>
          authService.hasAnyRole(expectedRoles) ||
          router.parseUrl(ROUTER_LINKS.NOT_AUTHORIZED)
      )
    );
  };
};
