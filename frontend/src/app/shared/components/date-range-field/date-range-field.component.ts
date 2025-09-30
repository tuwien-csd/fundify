import {
  Component,
  Input,
  OnDestroy,
  OnInit,
  inject,
  Injector,
  AfterViewInit,
} from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator,
  ValidatorFn,
  Validators,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  NgControl,
  TouchedChangeEvent,
} from '@angular/forms';
import { Subscription } from 'rxjs';
import { getFormValidationErrors } from '../../utils/validator-util';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { ValidationService } from '../../services/validation.service';
import { VALIDATORS } from '../../shared.constants';
import {
  DateRange,
  MatDateRangeInput,
  MatStartDate,
  MatEndDate,
  MatDatepickerToggle,
  MatDateRangePicker,
} from '@angular/material/datepicker';
import { adjustDateForTimezone } from '../../utils/date-utils';
import {
  MatFormField,
  MatLabel,
  MatSuffix,
  MatError,
} from '@angular/material/form-field';

@Component({
  selector:
    'app-date-range-field[formControl],' +
    'app-date-range-field[formControlName],' +
    'app-date-range-field[ngModel]',
  templateUrl: './date-range-field.component.html',
  styleUrls: ['./date-range-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: DateRangeFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: DateRangeFieldComponent,
    },
  ],
  imports: [
    MatFormField,
    MatLabel,
    MatDateRangeInput,
    FormsModule,
    ReactiveFormsModule,
    MatStartDate,
    MatEndDate,
    MatDatepickerToggle,
    MatSuffix,
    MatDateRangePicker,
    MatError,
  ],
})
export class DateRangeFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor, AfterViewInit
{
  validadionService = inject(ValidationService);
  private injector = inject(Injector);

  rangeForm;
  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() formFieldLabel: string = this.label;

  readonly validators = VALIDATORS;

  constructor() {
    this.rangeForm = new FormGroup({
      start: new FormControl<Date | null>(null),
      end: new FormControl<Date | null>(null),
    });
  }

  ngOnInit(): void {
    this.rangeForm.controls['start'].setValidators(this.validatorsForStart());
    this.rangeForm.controls['end'].setValidators(this.validatorsForEnd());
    this.label = wrapLabelRequiredOrOptional(this.label, this.required);
  }

  ngOnDestroy() {
    this.touchedChangeSub?.unsubscribe();
    for (const sub of this.onChangeSubs) {
      sub.unsubscribe();
    }
  }

  private touchedChangeSub?: Subscription;

  ngAfterViewInit() {
    // Workaround for propagating touched state to all nested child controls:
    // as markAllAsTouched is not propagated to the child controls
    // we subscribe to the TouchedChangeEvent and mark all controls as touched.
    this.touchedChangeSub = this.injector
      .get(NgControl)
      .control!.events.subscribe((event) => {
        if (event instanceof TouchedChangeEvent) {
          this.rangeForm.markAllAsTouched();
          this.rangeForm.updateValueAndValidity();
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.rangeForm.valueChanges.subscribe((value) => {
      if (value?.start) {
        value.start = adjustDateForTimezone(new Date(value.start));
      }
      if (value?.end) value.end = adjustDateForTimezone(new Date(value.end));
      onChange(value);
    });
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.rangeForm.disable();
    } else {
      this.rangeForm.enable();
    }
  }

  writeValue(value: DateRange<Date>) {
    if (value) {
      this.rangeForm.setValue(
        {
          start: value.start,
          end: value.end,
        },
        { emitEvent: false }
      );
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.rangeForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.rangeForm);
  }

  private validatorsForStart(): ValidatorFn | ValidatorFn[] | null {
    return this.requiredValidators();
  }

  private validatorsForEnd(): ValidatorFn | ValidatorFn[] | null {
    return this.requiredValidators();
  }

  private requiredValidators(): ValidatorFn | ValidatorFn[] | null {
    if (this.required) {
      return [Validators.required];
    }
    return null;
  }

  get start(): FormControl {
    return this.rangeForm.controls['start'] as FormControl;
  }

  get end(): FormControl {
    return this.rangeForm.controls['end'] as FormControl;
  }
}
