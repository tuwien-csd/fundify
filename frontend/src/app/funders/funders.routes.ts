import { Routes } from '@angular/router';
import { ROUTER_LINKS } from '../core/router-links.constants';
import { FunderDetailComponent } from './components/funder-detail/funder-detail.component';
import { FunderDetailEditComponent } from './components/funder-detail-edit/funder-detail-edit.component';
import { hasRoleGuard } from '../core/auth/guards/hasRoleGuard';
import { UserRoleEnum } from '../core/auth/models/user-role.enum';
import { isAuthenticatedGuard } from '../core/auth/guards/auth.guard';
import { FundersComponent } from './funders.component';
import { funderResolver } from './services/funder-resolver';

export const funderRoutes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: ROUTER_LINKS.FUNDERS },
  {
    path: ROUTER_LINKS.FUNDERS,
    component: FundersComponent,
    canActivate: [isAuthenticatedGuard],
  },
  {
    path: ROUTER_LINKS.FUNDER_NEW,
    component: FunderDetailEditComponent,
  },
  {
    path: ROUTER_LINKS.FUNDER_ID,
    component: FunderDetailComponent,
    canActivate: [isAuthenticatedGuard, hasRoleGuard],
    resolve: {
      funder: funderResolver,
    },
    data: {
      expectedRoles: [
        UserRoleEnum.ADMIN,
        UserRoleEnum.FUNDER,
        UserRoleEnum.ANNOTATOR,
      ],
    },
  },
  {
    path: ROUTER_LINKS.FUNDER_EDIT,
    component: FunderDetailEditComponent,
    canActivate: [isAuthenticatedGuard, hasRoleGuard],
    resolve: {
      funder: funderResolver,
    },
    data: {
      expectedRoles: [
        UserRoleEnum.ADMIN,
        UserRoleEnum.FUNDER,
        UserRoleEnum.ANNOTATOR,
      ],
    },
  },
];
