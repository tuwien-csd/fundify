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
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { VALIDATORS } from '../../shared.constants';
import { ValidationService } from '../../services/validation.service';
import { MatFormField, MatLabel, MatError } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { NumbersOnlyDirective } from '../../directives/numbers-only.directive';

@Component({
  selector:
    'app-number-field[formControl],' +
    'app-number-field[formControlName],' +
    'app-number-field[ngModel]',
  templateUrl: './number-field.component.html',
  styleUrls: ['./number-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: NumberFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: NumberFieldComponent,
    },
  ],
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    FormsModule,
    NumbersOnlyDirective,
    ReactiveFormsModule,
    MatError,
  ],
})
export class NumberFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor, AfterViewInit
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);
  private injector = inject(Injector);
  numberForm;

  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() formFieldLabel: string = '';
  @Input() placeholder: string = '';
  @Input() integer: boolean = false;
  @Input() nonNegative: boolean = false;
  @Input() decimalPlaces: number = 8;
  @Input() hideValidation: boolean = true;

  validators = VALIDATORS;

  constructor() {
    this.numberForm = this.fb.control(null);
  }

  ngOnInit(): void {
    if (this.required) {
      this.numberForm.setValidators([Validators.required]);
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
          this.numberForm.markAllAsTouched();
          this.numberForm.updateValueAndValidity();
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.numberForm.valueChanges.subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.numberForm.disable();
    } else {
      this.numberForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    if (value) {
      this.numberForm.setValue(value, { emitEvent: false });
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.numberForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.numberForm);
  }
}
