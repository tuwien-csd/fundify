import { Routes } from '@angular/router';
import { ROUTER_LINKS } from '../core/router-links.constants';
import { hasRoleGuard } from '../core/auth/guards/hasRoleGuard';
import { UserRoleEnum } from '../core/auth/models/user-role.enum';
import { isAuthenticatedGuard } from '../core/auth/guards/auth.guard';
import { UniversitiesComponent } from './universities.component';
import { UniversityDetailEditComponent } from './components/university-detail-edit/university-detail-edit.component';
import { UniversityDetailComponent } from './components/university-detail/university-detail.component';
import { universityResolver } from './services/university-resolver';

export const universityRoutes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: ROUTER_LINKS.UNIVERSITIES },
  {
    path: ROUTER_LINKS.UNIVERSITIES,
    component: UniversitiesComponent,
    canActivate: [isAuthenticatedGuard],
  },
  {
    path: ROUTER_LINKS.UNIVERSITY_NEW,
    component: UniversityDetailEditComponent,
  },
  {
    path: ROUTER_LINKS.UNIVERSITY_ID,
    component: UniversityDetailComponent,
    canActivate: [isAuthenticatedGuard, hasRoleGuard],
    resolve: {
      university: universityResolver,
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
    path: ROUTER_LINKS.UNIVERSITY_EDIT,
    component: UniversityDetailEditComponent,
    canActivate: [isAuthenticatedGuard, hasRoleGuard],
    resolve: {
      university: universityResolver,
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
