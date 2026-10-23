import { Routes } from '@angular/router';
import { HomeComponent } from './core/components/home/home.component';
import { ROUTER_LINKS } from './core/router-links.constants';
import { NotAuthorizedPage } from './core/components/pages/not-authorized.page';
import { NotFoundPage } from './core/components/pages/not-found.page';
import { isAuthenticatedGuard } from './core/auth/guards/auth.guard';
import { hasPermissionsGuard } from './core/auth/guards/hasPermissionsGuard';
import { ContactFormContainerComponent } from './contact-form/contact-form-container/contact-form-container.component';
import { ImpressumComponent } from './impressum/impressum.component';

export const mainRoutes: Routes = [
  { path: ROUTER_LINKS.HOME, component: HomeComponent },
  { path: ROUTER_LINKS.NOT_AUTHORIZED, component: NotAuthorizedPage },
  { path: '', redirectTo: ROUTER_LINKS.HOME, pathMatch: 'full' },

  {
    path: ROUTER_LINKS.FUNDINGS,
    loadChildren: () =>
      import('./calls/calls.module').then((m) => m.CallsModule),
    canActivate: [isAuthenticatedGuard, hasPermissionsGuard],
  },
  {
    path: ROUTER_LINKS.ANNOTATIONS,
    loadChildren: () =>
      import('./calls/calls.module').then((m) => m.CallsModule),
    canActivate: [isAuthenticatedGuard, hasPermissionsGuard],
  },
  {
    path: ROUTER_LINKS.FUNDINGS,
    loadChildren: () =>
      import('./programs/programs.routes').then((it) => it.programsRoutes),
    canActivate: [isAuthenticatedGuard, hasPermissionsGuard],
  },
  {
    path: ROUTER_LINKS.INSTITUTIONS,
    loadChildren: () =>
      import('./funders/funders.routes').then((it) => it.funderRoutes),
    canActivate: [isAuthenticatedGuard, hasPermissionsGuard],
  },
  {
    path: ROUTER_LINKS.INSTITUTIONS,
    loadChildren: () =>
      import('./universities/universities.routes').then(
        (it) => it.universityRoutes
      ),
    canActivate: [isAuthenticatedGuard, hasPermissionsGuard],
  },
  {
    path: ROUTER_LINKS.USERS,
    loadChildren: () =>
      import('./users/users.routes').then((it) => it.userRoutes),
    canActivate: [isAuthenticatedGuard, hasPermissionsGuard],
  },
  {
    path: ROUTER_LINKS.CONTACT,
    component: ContactFormContainerComponent,
  },
  {
    path: ROUTER_LINKS.IMPRESSUM,
    component: ImpressumComponent,
  },
  { path: '**', component: NotFoundPage },
];
