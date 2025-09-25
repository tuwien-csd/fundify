import { Component, inject } from '@angular/core';
import { AuthService } from '../../auth/services/auth.service';
import { HeaderComponent } from './ui/header.component';
import {
  MatSidenavContainer,
  MatSidenav,
  MatSidenavContent,
} from '@angular/material/sidenav';
import { SidebarComponent } from './ui/sidebar.component';
import { NgClass } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { FooterComponent } from './ui/footer.component';

@Component({
  selector: 'app-layout',
  template: `
    <body>
      @if (authService.isAuthenticated()) {
        <app-header (toggleSidebar)="sidenav.toggle()"></app-header>
        <main
          role="main"
          aria-label="main-content"
          class="app-container mat-app-background"
        >
          <mat-sidenav-container class="sidenav-container">
            <mat-sidenav #sidenav mode="side" opened="true">
              <app-sidebar [userRoles]="authService.roles()"></app-sidebar>
            </mat-sidenav>
            <mat-sidenav-content
              [ngClass]="{
                'sidebar-opened': sidenav.opened,
                'sidebar-closed': !sidenav.opened,
              }"
            >
              <section class="main-content">
                <router-outlet></router-outlet>
              </section>
            </mat-sidenav-content>
          </mat-sidenav-container>
        </main>
      } @else {
        <app-header></app-header>
        <main
          role="main"
          aria-label="main-content"
          class="app-container mat-app-background"
        >
          <section class="main-content">
            <router-outlet></router-outlet>
          </section>
        </main>
      }
      <footer role="contentinfo">
        <app-footer></app-footer>
      </footer>
    </body>
  `,
  styleUrls: ['./layout.component.scss'],
  imports: [
    HeaderComponent,
    MatSidenavContainer,
    MatSidenav,
    SidebarComponent,
    MatSidenavContent,
    NgClass,
    RouterOutlet,
    FooterComponent,
  ],
})
export class LayoutComponent {
  authService = inject(AuthService);
}
