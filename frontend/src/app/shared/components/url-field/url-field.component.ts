import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  UntypedFormBuilder,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator,
  ValidatorFn,
  Validators,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { Subscription } from 'rxjs';
import { getFormValidationErrors } from '../../utils/validator-util';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { VALIDATORS } from '../../shared.constants';
import { ValidationService } from '../../services/validation.service';
import { CustomValidators } from '../../validators/custom-validators';
import { MatFormField, MatLabel, MatError } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';

@Component({
  selector:
    'app-url-field[formControl],' +
    'app-url-field[formControlName],' +
    'app-url-field[ngModel]',
  templateUrl: './url-field.component.html',
  styleUrls: ['./url-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: UrlFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: UrlFieldComponent,
    },
  ],
  imports: [
    MatFormField,
    FormsModule,
    ReactiveFormsModule,
    MatLabel,
    MatInput,
    MatError,
  ],
})
export class UrlFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);

  urlForm;

  @Input() placeholder: string = '';
  @Input() required: boolean = false;
  @Input() label: string = '';

  validators = VALIDATORS;

  constructor() {
    this.urlForm = this.fb.group({
      url: [null],
    });
  }

  ngOnInit(): void {
    this.urlForm.controls['url'].setValidators(this.validatorsForUrl());
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
    const sub = this.urlForm.controls['url'].valueChanges.subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.urlForm.disable();
    } else {
      this.urlForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    if (value) {
      this.urlForm.setValue({ url: value }, { emitEvent: false });
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.urlForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.urlForm);
  }

  private validatorsForUrl(): ValidatorFn | ValidatorFn[] | null {
    const validators = [];
    validators.push(CustomValidators.url);
    if (this.required) {
      validators.push(Validators.required);
    }
    return validators;
  }

  get url() {
    return this.urlForm.controls['url'];
  }
}
