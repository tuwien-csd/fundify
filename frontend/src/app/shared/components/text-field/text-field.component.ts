import {
  Component,
  Input,
  OnDestroy,
  OnInit,
  ViewEncapsulation,
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
import {
  MatFormField,
  MatLabel,
  MatHint,
  MatError,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
@Component({
  selector:
    'app-text-field[formControl],' +
    'app-text-field[formControlName],' +
    'app-text-field[ngModel]',
  templateUrl: './text-field.component.html',
  styleUrls: ['./text-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: TextFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: TextFieldComponent,
    },
  ],
  encapsulation: ViewEncapsulation.None,
  imports: [
    MatFormField,
    FormsModule,
    ReactiveFormsModule,
    MatLabel,
    MatInput,
    MatHint,
    MatError,
  ],
})
export class TextFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor, AfterViewInit
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);
  private injector = inject(Injector);

  textForm;

  @Input() textPattern: string | RegExp = '';
  @Input() placeholder: string = '';
  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() textLabel: string = '';
  @Input() hint: string = '';

  validators = VALIDATORS;

  constructor() {
    this.textForm = this.fb.group({
      text: [null],
    });
  }

  ngOnInit(): void {
    this.textForm.controls['text'].setValidators(this.validatorsForText());
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
          this.textForm.markAllAsTouched();
          this.textForm.updateValueAndValidity();
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.textForm.controls['text'].valueChanges.subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.textForm.disable();
    } else {
      this.textForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    // An empty value has to be written through as well, otherwise clearing or
    // resetting the outer control leaves the previous text visible in the input
    // while the form value is already empty.
    this.textForm.setValue({ text: value ?? null }, { emitEvent: false });
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.textForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.textForm);
  }

  private validatorsForText(): ValidatorFn | ValidatorFn[] | null {
    const validators = [];
    if (this.textPattern) {
      validators.push(Validators.pattern(this.textPattern));
    }
    if (this.required) {
      validators.push(Validators.required);
    }
    return validators;
  }

  get text() {
    return this.textForm.controls['text'];
  }
}
