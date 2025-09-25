// not-authorized.component.ts
import { Component } from '@angular/core';

import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-not-authorized',
  imports: [RouterModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <mat-card appearance="outlined">
      <mat-card-header>
        <mat-icon mat-card-avatar>security</mat-icon>
        <mat-card-title>403 - Not Authorized</mat-card-title>
        <mat-card-subtitle>Access Denied</mat-card-subtitle>
      </mat-card-header>
      <mat-card-content>
        <p>You don't have permission to access this page.</p>
      </mat-card-content>
      <mat-card-actions>
        <button mat-raised-button color="primary" routerLink="/">
          <mat-icon>home</mat-icon>
          Go to Home
        </button>
      </mat-card-actions>
    </mat-card>
  `,
  styles: [
    `
      :host {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 50vh;
      }
      mat-card {
        max-width: 400px;
        width: 100%;
        text-align: center;
      }
      mat-card-header {
        justify-content: center;
      }
      mat-card-content {
        margin-top: 1rem;
      }
      mat-card-actions {
        justify-content: center;
        padding-bottom: 1rem;
      }
    `,
  ],
})
export class NotAuthorizedPage {}
