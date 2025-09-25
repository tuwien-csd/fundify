import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function createCheckboxGroupValidator(required: boolean): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const valid = (control.value as boolean[]).some(Boolean);

    return valid || !required
      ? null
      : {
          required: 'At least one checkbox needs to be selected',
        };
  };
}
