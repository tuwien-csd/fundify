import { Component, Output, EventEmitter } from '@angular/core';
import { ROUTER_LINKS } from '../../../router-links.constants';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { UserWidgetComponent } from './user-widget.component';

@Component({
  selector: 'app-header',
  template: `
    <mat-toolbar color="primary" class="header-toolbar">
      <div class="header-left">
        <button
          mat-icon-button
          class="header-menu-button"
          (click)="toggleSidebar.emit()"
        >
          <mat-icon>menu</mat-icon>
        </button>
        <span class="headline" [routerLink]="ROUTER_LINKS.HOME"> FUNDify </span>
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
}
