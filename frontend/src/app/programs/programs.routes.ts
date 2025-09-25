import { Routes } from '@angular/router';
import { ProgramsComponent } from './programs.component';
import { ProgramDetailEditComponent } from './components/program-detail-edit/program-detail-edit.component';
import { ROUTER_LINKS } from '../core/router-links.constants';
import { UserRoleEnum } from '../core/auth/models/user-role.enum';
import { hasRoleGuard } from '../core/auth/guards/hasRoleGuard';
import { isAuthenticatedGuard } from '../core/auth/guards/auth.guard';
import { programResolver } from './services/program-resolver';
import { ProgramDetailComponent } from './components/program-detail-new/program-detail.component';

export const programsRoutes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: ROUTER_LINKS.PROGRAMS },
  {
    path: ROUTER_LINKS.PROGRAMS,
    component: ProgramsComponent,
    canActivate: [isAuthenticatedGuard],
  },
  {
    path: ROUTER_LINKS.PROGRAM_ID,
    component: ProgramDetailComponent,
    canActivate: [isAuthenticatedGuard, hasRoleGuard],
    resolve: {
      program: programResolver,
    },
    data: { expectedRoles: [UserRoleEnum.ADMIN, UserRoleEnum.FUNDER] },
  },
  {
    path: ROUTER_LINKS.PROGRAM_NEW,
    component: ProgramDetailEditComponent,
    canActivate: [isAuthenticatedGuard, hasRoleGuard],
    data: { expectedRoles: [UserRoleEnum.ADMIN, UserRoleEnum.FUNDER] },
  },
  {
    path: ROUTER_LINKS.PROGRAM_EDIT,
    component: ProgramDetailEditComponent,
    canActivate: [isAuthenticatedGuard, hasRoleGuard],
    resolve: {
      program: programResolver,
    },
    data: { expectedRoles: [UserRoleEnum.ADMIN, UserRoleEnum.FUNDER] },
  },
];
