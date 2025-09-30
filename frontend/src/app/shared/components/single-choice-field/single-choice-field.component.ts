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
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator,
  ValidatorFn,
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
import { MatRadioGroup, MatRadioButton } from '@angular/material/radio';
import { MatError } from '@angular/material/form-field';

@Component({
  selector:
    'app-single-choice-field[formControl],' +
    'app-single-choice-field[formControlName],' +
    'app-single-choice-field[ngModel]',
  templateUrl: './single-choice-field.component.html',
  styleUrls: ['./single-choice-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: SingleChoiceFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: SingleChoiceFieldComponent,
    },
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatRadioGroup,
    MatRadioButton,
    MatError,
  ],
})
export class SingleChoiceFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor, AfterViewInit
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);
  private injector = inject(Injector);

  singleChoiceForm: UntypedFormGroup;
  choices: string[] = [];

  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() type: object = {}; // Enum

  validators = VALIDATORS;

  constructor() {
    this.singleChoiceForm = this.fb.group({ value: [null] });
  }

  ngOnInit(): void {
    this.choices = Object.values(this.type).filter((item) => {
      return isNaN(Number(item));
    });
    this.singleChoiceForm.controls['value'].setValidators(
      this.validatorsForValue()
    );
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
          this.singleChoiceForm.markAllAsTouched();
          this.singleChoiceForm.updateValueAndValidity();
          console.log('single choice marked as touched');
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.singleChoiceForm.valueChanges.subscribe((val) => {
      onChange(val.value);
    });
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.singleChoiceForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.singleChoiceForm);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.singleChoiceForm.disable();
    } else {
      this.singleChoiceForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    if (value) {
      this.singleChoiceForm.setValue({ value });
    }
  }

  private validatorsForValue(): ValidatorFn | ValidatorFn[] | null {
    if (this.required) {
      return [Validators.required];
    }
    return null;
  }
}
