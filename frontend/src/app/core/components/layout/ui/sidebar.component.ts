import { Component, Input } from '@angular/core';
import { ROUTER_LINKS } from '../../../router-links.constants';
import { UserRoleEnum } from '../../../auth/models/user-role.enum';
import {
  MatNavList,
  MatListItem,
  MatListItemIcon,
} from '@angular/material/list';
import { RouterLinkActive, RouterLink } from '@angular/router';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-sidebar',
  template: `
    <mat-nav-list class="sidebar-container">
      <a
        mat-list-item
        [routerLink]="ROUTER_LINKS.HOME"
        routerLinkActive="active"
      >
        <mat-icon matListItemIcon>home</mat-icon>
        <span class="menu-text">Home</span>
      </a>
      @if (userRoles.includes(UserRoleEnum.ANNOTATOR)) {
        <a
          mat-list-item
          [routerLink]="[
            ROUTER_LINKS.ANNOTATIONS,
            'overview',
            ROUTER_LINKS.CALLS,
          ]"
          routerLinkActive="active"
        >
          <mat-icon matListItemIcon>edit_note</mat-icon>
          <span class="menu-text">Annotations</span>
        </a>
      }
      <a
        mat-list-item
        [routerLink]="ROUTER_LINKS.FUNDINGS"
        routerLinkActive="active"
      >
        <mat-icon matListItemIcon>payments</mat-icon>
        <span class="menu-text">Fundings</span>
      </a>
      <a
        mat-list-item
        [routerLink]="ROUTER_LINKS.INSTITUTIONS"
        routerLinkActive="active"
      >
        <mat-icon matListItemIcon>account_balance</mat-icon>
        <span class="menu-text">Institutions</span>
      </a>
      <!-- TODO: re-enable settings link when needed
      <mat-divider></mat-divider>
      <a
        mat-list-item
        [routerLink]="ROUTER_LINKS.SETTINGS"
        routerLinkActive="active"
      >
        <mat-icon matListItemIcon>settings</mat-icon>
        <span class="menu-text">Settings</span>
      </a>
      -->
    </mat-nav-list>
  `,
  styleUrls: ['./sidebar.component.scss'],
  imports: [
    MatNavList,
    MatListItem,
    RouterLinkActive,
    RouterLink,
    MatIcon,
    MatListItemIcon
  ],
})
export class SidebarComponent {
  protected readonly ROUTER_LINKS = ROUTER_LINKS;
  protected readonly UserRoleEnum = UserRoleEnum;
  @Input() userRoles!: string[];
}
