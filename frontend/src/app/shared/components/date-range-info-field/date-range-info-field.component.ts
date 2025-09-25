import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  UntypedFormBuilder,
  UntypedFormControl,
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
// eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
import { VALIDATORS } from '../../shared.constants';
// eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
import { ValidationService } from '../../services/validation.service';
import { DateRangeFieldComponent } from '../date-range-field/date-range-field.component';
import { TranslatedTextFieldComponent } from '../translated-text-field/translated-text-field.component';

@Component({
  selector: 'app-date-range-info-field',
  templateUrl: './date-range-info-field.component.html',
  styleUrls: ['./date-range-info-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: DateRangeInfoFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: DateRangeInfoFieldComponent,
    },
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    DateRangeFieldComponent,
    TranslatedTextFieldComponent,
  ],
})
export class DateRangeInfoFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor
{
  private fb = inject(UntypedFormBuilder);

  rangeInfoForm;

  @Input() required: boolean = false;
  @Input() label: string = '';

  constructor() {
    this.rangeInfoForm = this.fb.group({
      range: [null],
      info: [null],
    });
  }

  ngOnInit(): void {
    this.rangeInfoForm.controls['range'].setValidators(
      this.validatorsForRange()
    );
    this.rangeInfoForm.controls['info'].setValidators(this.validatorsForInfo());
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
    const sub = this.rangeInfoForm.valueChanges.subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.rangeInfoForm.disable();
    } else {
      this.rangeInfoForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    if (value) {
      this.rangeInfoForm.setValue(
        { range: value.range, info: value.info },
        { emitEvent: false }
      );
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.rangeInfoForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.rangeInfoForm);
  }

  private validatorsForInfo(): ValidatorFn | ValidatorFn[] | null {
    return null;
  }

  private validatorsForRange(): ValidatorFn | ValidatorFn[] | null {
    if (this.required) {
      return [Validators.required];
    }
    return null;
  }

  get range(): UntypedFormControl {
    return this.rangeInfoForm.controls['range'] as UntypedFormControl;
  }

  get info(): UntypedFormControl {
    return this.rangeInfoForm.controls['info'] as UntypedFormControl;
  }
}
