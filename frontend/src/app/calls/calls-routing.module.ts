import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CallsComponent } from './calls.component';
import { CallDetailEditComponent } from './components/call-detail-edit/call-detail-edit.component';
import { CallDetailPreviewComponent } from './components/call-detail-preview/call-detail-preview.component';
import { CallDetailComponent } from './components/call-detail/call-detail.component';
import { ROUTER_LINKS } from '../core/router-links.constants';
import { UserRoleEnum } from '../core/auth/models/user-role.enum';
import { CallAnnotationListComponent } from './components/call-annotation-list/call-annotation-list.component';
import { CallDetailAnnotateComponent } from './components/call-detail-annotate/call-detail-annotate.component';
import { hasRoleGuard } from '../core/auth/guards/hasRoleGuard';
import { isAuthenticatedGuard } from '../core/auth/guards/auth.guard';

const routerLinks = ROUTER_LINKS;

const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: routerLinks.CALLS },
  {
    path: routerLinks.CALLS,
    component: CallsComponent,
    canActivate: [isAuthenticatedGuard()],
  },
  {
    path: routerLinks.CALL_NEW,
    canActivate: [
      isAuthenticatedGuard(),
      hasRoleGuard([UserRoleEnum.ADMIN, UserRoleEnum.FUNDER, UserRoleEnum.ANNOTATOR]),
    ],
    component: CallDetailComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: routerLinks.EDIT },
      { path: routerLinks.EDIT, component: CallDetailEditComponent },
      { path: routerLinks.PREVIEW, component: CallDetailPreviewComponent },
    ],
  },
  {
    path: routerLinks.CALL_ID,
    component: CallDetailComponent,
    canActivate: [isAuthenticatedGuard()],
    children: [
      { path: '', pathMatch: 'full', redirectTo: routerLinks.PREVIEW },
      { path: routerLinks.PREVIEW, component: CallDetailPreviewComponent },
      { path: routerLinks.EDIT, component: CallDetailEditComponent },
    ],
  },
  {
    path: 'overview',
    pathMatch: 'full',
    redirectTo: routerLinks.CALLS,
  },
  {
    path: 'overview/' + routerLinks.CALLS,
    canActivate: [
      isAuthenticatedGuard(),
      hasRoleGuard([UserRoleEnum.ANNOTATOR]),
    ],
    component: CallAnnotationListComponent,
  },
  {
    path: `overview/${routerLinks.CALL_ID}`,
    component: CallDetailAnnotateComponent,
    canActivate: [
      isAuthenticatedGuard(),
      hasRoleGuard([UserRoleEnum.ANNOTATOR]),
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CallsRoutingModule {}
