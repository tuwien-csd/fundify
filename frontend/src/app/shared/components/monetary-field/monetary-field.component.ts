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
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator,
  Validators,
  FormsModule,
  ReactiveFormsModule,
  NgControl,
  TouchedChangeEvent,
} from '@angular/forms';
import { Subscription } from 'rxjs';
import { getFormValidationErrors } from '../../utils/validator-util';
import { CurrencyEnum } from '../../models/enums/currency.enum';
import { MONETARY_LABELS, VALIDATORS } from '../../shared.constants';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { ValidationService } from '../../services/validation.service';
import { NumberFieldComponent } from '../number-field/number-field.component';
import { MatFormField, MatLabel, MatError } from '@angular/material/form-field';
import { MatSelect, MatOption } from '@angular/material/select';

@Component({
  selector:
    'app-monetary-field[formControl],' +
    'app-monetary-field[formControlName],' +
    'app-monetary-field[ngModel]',
  templateUrl: './monetary-field.component.html',
  styleUrls: ['./monetary-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: MonetaryFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: MonetaryFieldComponent,
    },
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    NumberFieldComponent,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    MatError,
  ],
})
export class MonetaryFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor, AfterViewInit
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);
  private injector = inject(Injector);

  monetaryForm: UntypedFormGroup;

  @Input() placeholder: string = '';
  @Input() required: boolean = false;
  @Input() label: string = '';

  constants = MONETARY_LABELS;
  validators = VALIDATORS;

  currencyCodes: CurrencyEnum[] = Object.values(CurrencyEnum);

  constructor() {
    this.monetaryForm = this.fb.group({
      amount: [null],
      currency: [null],
    });
  }

  ngOnInit(): void {
    if (this.required) {
      this.currency.setValidators(Validators.required);
      this.amount.setValidators(Validators.required);
    }
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
          this.monetaryForm.markAllAsTouched();
          this.monetaryForm.updateValueAndValidity();
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.monetaryForm.valueChanges.subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.monetaryForm.disable();
    } else {
      this.monetaryForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    if (value) {
      this.monetaryForm.setValue(value);
    }
  }

  get currency(): UntypedFormControl {
    return this.monetaryForm.controls['currency'] as UntypedFormControl;
  }

  get amount(): UntypedFormControl {
    return this.monetaryForm.controls['amount'] as UntypedFormControl;
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.monetaryForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.monetaryForm);
  }
}
