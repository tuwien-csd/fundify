import { Component, inject } from '@angular/core';
import { AuthService } from '../../../auth/services/auth.service';
import { MatButton } from '@angular/material/button';
import { MatMenuTrigger, MatMenu, MatMenuItem } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';
import { MatDivider } from '@angular/material/list';

@Component({
  selector: 'app-user-widget',
  template: `
    @if (authService.isAuthenticated()) {
      <button
        mat-button
        [matMenuTriggerFor]="userMenu"
        class="user-info-button"
      >
        <div class="user-info-content">
          <mat-icon class="user-icon">account_circle</mat-icon>
          <span class="username">{{ authService.username() }}</span>
          <mat-icon class="dropdown-icon">arrow_drop_down</mat-icon>
        </div>
      </button>
      <mat-menu #userMenu="matMenu">
        <mat-divider></mat-divider>
        <button mat-menu-item (click)="authService.logout()">
          <mat-icon>exit_to_app</mat-icon>
          <span>logout</span>
        </button>
      </mat-menu>
    } @else {
      <button
        mat-raised-button
        color="accent"
        (click)="authService.login()"
        class="login-button"
      >
        <mat-icon>login</mat-icon>
        <span>login</span>
      </button>
    }
  `,
  styles: `
    .user-info-button {
      padding: 0;
      height: 3rem;
      border-radius: 1.5rem;
      background-color: rgba(255, 255, 255, 0.1);

      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
      }
    }

    .user-info-content {
      display: grid;
      grid-template-columns: auto 1fr auto;
      align-items: center;
      gap: 0.5rem;
      padding: 0 0.75rem;
      height: 100%;
    }

    .user-icon {
      font-size: 2rem;
      height: 2rem;
      width: 2rem;
    }

    .username {
      justify-self: center;
      text-align: center;
      max-width: 150px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-weight: 500;
      font-size: 1rem;
    }

    .login-button {
      height: 2rem;
      font-size: 1rem;
      font-weight: bold;
    }

    @media (max-width: 600px) {
      .username {
        display: none;
      }

      .user-info-button {
        padding: 0 0.5rem;
        width: 3rem;
      }
    }
  `,
  imports: [
    MatButton,
    MatMenuTrigger,
    MatIcon,
    MatMenu,
    MatDivider,
    MatMenuItem,
  ],
})
export class UserWidgetComponent {
  authService = inject(AuthService);
}
