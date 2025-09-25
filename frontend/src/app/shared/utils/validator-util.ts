import {
  AbstractControl,
  UntypedFormArray,
  UntypedFormGroup,
  ValidationErrors,
} from '@angular/forms';

export function collectFormGroupErrors(fg: UntypedFormGroup) {
  if (fg.valid) {
    return null;
  }
  const groupErrors = { ...fg.errors };
  Object.keys(fg.controls).forEach((controlName) => {
    const controlErrors = fg.controls[controlName].errors;
    if (controlErrors) {
      groupErrors[controlName] = controlErrors;
    }
  });
  return groupErrors;
}

export function getFormValidationErrors(
  control: AbstractControl,
  path: string = '',
  errors: ValidationErrors = {}
): ValidationErrors {
  const controlErrors = control.errors;
  if (controlErrors) {
    Object.keys(controlErrors).forEach((key) => {
      errors[path || 'root'] = `${key}: ${JSON.stringify(controlErrors[key])}`;
    });
  }
  if (control instanceof UntypedFormGroup) {
    Object.keys(control.controls).forEach((key) => {
      const newPath = path ? `${path}.${key}` : key;
      getFormValidationErrors(control.controls[key], newPath, errors);
    });
  } else if (control instanceof UntypedFormArray) {
    control.controls.forEach((childControl, index) => {
      const newPath = path ? `${path}[${index}]` : `[${index}]`;
      getFormValidationErrors(childControl, newPath, errors);
    });
  }
  return errors;
}
