import { Component, Input } from '@angular/core';
import { MatTabNav, MatTabLink, MatTabNavPanel } from '@angular/material/tabs';
import { RouterLinkActive, RouterLink } from '@angular/router';

export type TabInfo = {
  routerLink: string;
  label: string;
};

@Component({
  selector: 'app-top-navbar',
  template: `
    <nav mat-tab-nav-bar [tabPanel]="tabPanel">
      @for (tab of tabs; track tab) {
        <a
          mat-tab-link
          [routerLink]="[tab.routerLink]"
          routerLinkActive
          #rla="routerLinkActive"
          [active]="rla.isActive"
        >
          {{ tab.label }}
        </a>
      }
    </nav>
    <mat-tab-nav-panel #tabPanel></mat-tab-nav-panel>
  `,
  styles: [
    `
      ::ng-deep .mat-mdc-tab-list {
        flex-grow: 0 !important;
      }

      a[mat-tab-link] {
        font-size: 1rem;
        opacity: 1;
        transition: all 0.3s ease;
        border: 1px solid;
        border-color: var(--accent-color);
        border-top-left-radius: 8px;
        border-top-right-radius: 8px;
      }

      a[mat-tab-link]:hover {
        background-color: rgba(0, 0, 0, 0.04);
      }

      a[mat-tab-link].mdc-tab--active {
        background-color: white;
        border-color: var(--primary-color);
        border-bottom: none;
        color: var(--primary-color);
        font-weight: 500;
      }

      @media (max-width: 600px) {
        .tab-container {
          display: flex;
          overflow-x: auto;
          width: 100%;
        }

        a[mat-tab-link] {
          font-size: 1rem;
          padding: 0 16px;
          flex: 0 0 auto;
        }
      }
    `,
  ],
  imports: [
    MatTabNav,
    MatTabLink,
    RouterLinkActive,
    RouterLink,
    MatTabNavPanel,
  ],
})
export class TopNavbarComponent {
  @Input() tabs: TabInfo[] = [];
}
