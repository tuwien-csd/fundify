import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private snackBar = inject(MatSnackBar);

  success(message: string) {
    return this.snackBar.open(message, '', {
      duration: 4000,
      panelClass: ['snackbar-success'],
    });
  }

  info(message: string) {
    return this.snackBar.open(message, '', {
      duration: 4000,
      panelClass: ['snackbar-info'],
    });
  }

  error(message: string, actionLabel: string = 'Close') {
    return this.snackBar.open(message, actionLabel, {
      duration: 5000,
      panelClass: ['snackbar-error'],
    });
  }
}
