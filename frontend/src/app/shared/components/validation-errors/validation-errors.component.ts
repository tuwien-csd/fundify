import { Component, Input } from '@angular/core';
import { MatCard, MatCardTitle, MatCardContent } from '@angular/material/card';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-validation-errors',
  templateUrl: './validation-errors.component.html',
  styleUrls: ['./validation-errors.component.scss'],
  imports: [MatCard, MatCardTitle, MatCardContent, JsonPipe],
})
export class ValidationErrorsComponent {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  @Input() errors: any = {};

  getErrorKeys(): string[] {
    return Object.keys(this.errors);
  }

  formatKey(key: string): string {
    return key
      .replace(/([A-Z])/g, ' $1') // Add a space before all uppercase letters that are not at the beginning
      .replace(/^./, (firstChar) => firstChar.toUpperCase()) // Capitalize the first letter
      .replace(/Control$/, '') // Remove 'Control' if it appears at the end
      .trim();
  }
}
