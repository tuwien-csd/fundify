import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { CanActivateFn } from '@angular/router';

export const isAuthenticatedGuard = (): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    if (!authService.isAuthenticated()) authService.login();

    return authService.isAuthenticated();
  };
};
