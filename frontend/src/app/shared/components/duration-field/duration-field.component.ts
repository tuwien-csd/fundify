import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  UntypedFormBuilder,
  UntypedFormControl,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { Subscription } from 'rxjs';
import { getFormValidationErrors } from '../../utils/validator-util';
import { DURATION_LABELS, VALIDATORS } from '../../shared.constants';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { ValidationService } from '../../services/validation.service';
import { CustomValidators } from '../../validators/custom-validators';
import { NumberFieldComponent } from '../number-field/number-field.component';
import { MatError } from '@angular/material/form-field';

@Component({
  selector:
    'app-duration-field[formControl],' +
    'app-duration-field[formControlName],' +
    'app-duration-field[ngModel]',
  templateUrl: './duration-field.component.html',
  styleUrls: ['./duration-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: DurationFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: DurationFieldComponent,
    },
  ],
  imports: [FormsModule, ReactiveFormsModule, NumberFieldComponent, MatError],
})
export class DurationFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);

  durationForm;

  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() min: number = 0;

  constants = DURATION_LABELS;
  validators = VALIDATORS;

  constructor() {
    this.durationForm = this.fb.group({
      months: [null],
      days: [null],
    });
  }

  ngOnInit(): void {
    if (this.required) {
      this.durationForm.setValidators(CustomValidators.duration);
    }
    this.label = wrapLabelRequiredOrOptional(this.label, this.required);
  }

  ngOnDestroy() {
    for (const sub of this.onChangeSubs) {
      sub.unsubscribe();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.durationForm.valueChanges.subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.durationForm.disable();
    } else {
      this.durationForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    if (value) {
      this.durationForm.patchValue(value, { emitEvent: false });
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.durationForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.durationForm);
  }

  get months(): UntypedFormControl {
    return this.durationForm.controls['months'] as UntypedFormControl;
  }

  get days(): UntypedFormControl {
    return this.durationForm.controls['days'] as UntypedFormControl;
  }
}
