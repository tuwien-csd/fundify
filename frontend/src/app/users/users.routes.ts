import { Routes } from '@angular/router';
import { ROUTER_LINKS } from '../core/router-links.constants';
import { hasRoleGuard } from '../core/auth/guards/hasRoleGuard';
import { UserRoleEnum } from '../core/auth/models/user-role.enum';
import { isAuthenticatedGuard } from '../core/auth/guards/auth.guard';
import { UserPermissionsEditComponent } from './components/user-permissions-edit/user-permissions-edit.component';

export const userRoutes: Routes = [
  {
    path: ROUTER_LINKS.USER_PERMISSIONS,
    component: UserPermissionsEditComponent,
    canActivate: [isAuthenticatedGuard, hasRoleGuard([UserRoleEnum.ADMIN])],
  },
];
