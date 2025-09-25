import { AbstractControl, ValidationErrors } from '@angular/forms';
import { Duration } from '../models/interfaces/duration.interface';
import { DateRange } from '../models/interfaces/date-range.interface';
import { TranslatedText } from '../models/interfaces/translated-text.interface';

export class CustomValidators {
  static phone(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return null;
    }
    const PHONE_REGEX = new RegExp(
      '^[+]?[(]?[0-9]*[)]?([-/ ]?[0-9])*[- ]?[0-9]$'
    );

    return PHONE_REGEX.test(control.value) ? null : { phone: true };
  }

  static url(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return null;
    }
    const URL_REGEX = new RegExp(
      '^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$'
    );

    return URL_REGEX.test(control.value) ? null : { url: true };
  }

  static duration(control: AbstractControl): ValidationErrors | null {
    const value: Duration = control.value;
    if (!value) {
      return null;
    }

    return value.months > 0 || value.days > 0 ? null : { duration: true };
  }

  static dateRange(control: AbstractControl): ValidationErrors | null {
    const dateRange: DateRange = control.value;

    if (!dateRange.start || !dateRange.end) {
      return null;
    }

    const startDate = new Date(dateRange.start);
    const endDate = new Date(dateRange.end);

    return startDate.getTime() < endDate.getTime() ? null : { dateRange: true };
  }

  static translation(control: AbstractControl): ValidationErrors | null {
    const value: TranslatedText = control.value;

    if (!value) {
      return null;
    }

    const valid =
      (value.text && value.language) || (!value.text && !value.language);

    return valid ? null : { translation: true };
  }
}
