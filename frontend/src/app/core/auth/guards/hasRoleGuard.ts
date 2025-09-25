import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { ROUTER_LINKS } from '../../router-links.constants';

export const hasRoleGuard = (expectedRoles: string[]): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    return (
      authService.hasAnyRole(expectedRoles) ||
      router.parseUrl(ROUTER_LINKS.NOT_AUTHORIZED)
    );
  };
};
