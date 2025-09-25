import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProgramsComponent } from './programs.component';
import { ProgramDetailEditComponent } from './components/program-detail-edit/program-detail-edit.component';
import { ProgramDetailPreviewComponent } from './components/program-detail-preview/program-detail-preview.component';
import { ProgramDetailComponent } from './components/program-detail/program-detail.component';
import { ROUTER_LINKS } from '../core/router-links.constants';
import { UserRoleEnum } from '../core/auth/models/user-role.enum';
import { hasRoleGuard } from '../core/auth/guards/hasRoleGuard';
import { isAuthenticatedGuard } from '../core/auth/guards/auth.guard';

const routes: Routes = [
  { path: '', component: ProgramsComponent },
  {
    path: 'new',
    canActivate: [isAuthenticatedGuard, hasRoleGuard],
    data: { expectedRoles: [UserRoleEnum.ADMIN, UserRoleEnum.FUNDER] },
    component: ProgramDetailComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: ROUTER_LINKS.EDIT },
      { path: ROUTER_LINKS.EDIT, component: ProgramDetailEditComponent },
      { path: ROUTER_LINKS.PREVIEW, component: ProgramDetailPreviewComponent },
    ],
  },
  {
    path: ':id',
    component: ProgramDetailComponent,
    canActivate: [isAuthenticatedGuard, hasRoleGuard],
    data: {
      expectedRoles: [
        UserRoleEnum.ADMIN,
        UserRoleEnum.FUNDER,
        UserRoleEnum.ANNOTATOR,
      ],
    },
    children: [
      { path: '', pathMatch: 'full', redirectTo: ROUTER_LINKS.PREVIEW },
      { path: ROUTER_LINKS.PREVIEW, component: ProgramDetailPreviewComponent },
      { path: ROUTER_LINKS.EDIT, component: ProgramDetailEditComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProgramsRoutingModule {}
