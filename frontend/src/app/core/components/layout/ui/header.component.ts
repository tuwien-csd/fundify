import { Component, Output, EventEmitter, inject } from '@angular/core';
import { ROUTER_LINKS } from '../../../router-links.constants';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { UserWidgetComponent } from './user-widget.component';
import { EnvironmentIndicatorService } from '../../../services/environment-indicator.service';

@Component({
  selector: 'app-header',
  template: `
    <mat-toolbar
      color="primary"
      class="header-toolbar"
      [style.border-bottom]="
        environment.label ? '4px solid ' + environment.color : null
      "
    >
      <div class="header-left">
        <button
          mat-icon-button
          class="header-menu-button"
          (click)="toggleSidebar.emit()"
        >
          <mat-icon>menu</mat-icon>
        </button>
        <span class="headline" [routerLink]="ROUTER_LINKS.HOME"> FUNDify </span>
        @if (environment.label) {
          <span class="env-badge" [style.background-color]="environment.color">
            {{ environment.label }}
          </span>
        }
      </div>
      <div class="header-right">
        <app-user-widget></app-user-widget>
      </div>
    </mat-toolbar>
  `,
  styles: [
    `
      .header-toolbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 1rem;
        height: 4rem;
      }

      .header-left,
      .header-right {
        display: flex;
        align-items: center;
      }

      .header-menu-button {
        background-color: rgba(255, 255, 255, 0.1);
        margin-right: 1rem;
        &:hover {
          background-color: rgba(255, 255, 255, 0.2);
        }
      }

      .headline {
        display: flex;
        align-items: center;
        font-size: 1.5em;
        font-weight: bold;
        cursor: pointer;
        text-decoration: none;
        color: inherit;
      }

      .env-badge {
        margin-left: 0.75rem;
        padding: 0.15rem 0.5rem;
        border-radius: 0.25rem;
        font-size: 0.7em;
        font-weight: bold;
        letter-spacing: 0.08em;
        line-height: 1.4;
        color: white;
      }

      @media (max-width: 600px) {
        .headline {
          font-size: 1.2em;
        }

        .action-button span {
          display: none;
        }
      }
    `,
  ],
  imports: [
    MatToolbar,
    MatIconButton,
    MatIcon,
    RouterLink,
    UserWidgetComponent,
  ],
})
export class HeaderComponent {
  @Output() toggleSidebar = new EventEmitter<void>();
  protected readonly ROUTER_LINKS = ROUTER_LINKS;
  protected readonly environment = inject(EnvironmentIndicatorService);
}
