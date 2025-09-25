import { Injectable } from '@angular/core';
import { AbstractControl } from '@angular/forms';
import { VALIDATORS } from '../shared.constants';

@Injectable({
  providedIn: 'root',
})
export class ValidationService {
  getValidationErrorMessage(
    control: AbstractControl | null,
    validatorName: string
  ): string | null {
    if (control && control.hasError(validatorName)) {
      switch (validatorName) {
        case VALIDATORS.REQUIRED:
          return 'This field is required';
        case VALIDATORS.EMAIL:
          return 'Invalid email address';
        case VALIDATORS.PHONE:
          return 'Invalid phone number';
        case VALIDATORS.URL:
          return 'Invalid URL';
        case VALIDATORS.DATE_RANGE:
          return 'Start date must be before end date';
        case VALIDATORS.DURATION:
          return 'Duration must be greater than 0';
        case VALIDATORS.MIN:
          return `Value must be greater than ${control.errors?.[VALIDATORS.MIN].min}`;
        case VALIDATORS.PATTERN:
          return 'Invalid value';
        case VALIDATORS.MIN_LENGTH:
          return `Length should be at least ${control.errors?.[VALIDATORS.MIN_LENGTH].requiredLength}`;
        case VALIDATORS.MAX_LENGTH:
          return `Length should be at most ${control.errors?.[VALIDATORS.MAX_LENGTH].requiredLength}`;
        case VALIDATORS.TRANSLATION:
          return 'Text and language must be specified together.';
        default:
          return null;
      }
    }
    return null;
  }
}
