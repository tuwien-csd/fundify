import {
  booleanAttribute,
  Component,
  computed,
  effect,
  inject,
  input,
  numberAttribute,
  OnDestroy,
  ViewEncapsulation,
} from '@angular/core';
import {
  ControlValueAccessor,
  FormsModule,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ReactiveFormsModule,
  UntypedFormBuilder,
  Validator,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { Subscription } from 'rxjs';

import {
  MatError,
  MatFormField,
  MatHint,
  MatLabel,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { CdkTextareaAutosize, TextFieldModule } from '@angular/cdk/text-field';

import { ValidationService } from 'src/app/shared/services/validation.service';
import { getFormValidationErrors } from '../../utils/validator-util';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { VALIDATORS } from '../../shared.constants';
import { noop } from '../../utils/noop';

@Component({
  selector:
    'app-text-area-field[formControl],' +
    'app-text-area-field[formControlName],' +
    'app-text-area-field[ngModel]',
  standalone: true,
  templateUrl: './text-area-field.component.html',
  styleUrls: ['./text-area-field.component.scss'],
  encapsulation: ViewEncapsulation.None,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatHint,
    MatError,
    MatInput,
    TextFieldModule,
    CdkTextareaAutosize,
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: TextAreaFieldComponent,
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: TextAreaFieldComponent,
      multi: true,
    },
  ],
})
export class TextAreaFieldComponent
  implements OnDestroy, Validator, ControlValueAccessor
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);

  // Signals-based inputs
  placeholder = input<string>('');
  required = input<boolean, unknown>(false, { transform: booleanAttribute });
  label = input<string>('');
  textLabel = input<string>('');
  hint = input<string>('');

  autoGrow = input<boolean, unknown>(true, { transform: booleanAttribute });
  minRows = input<number, unknown>(3, { transform: numberAttribute });
  maxRows = input<number, unknown>(8, { transform: numberAttribute });

  maxlength = input<number | undefined, unknown>(undefined, {
    transform: (v: unknown) =>
      v === null || v === undefined || v === '' ? undefined : Number(v),
  });

  hideValidation = input<boolean, unknown>(false, {
    transform: booleanAttribute,
  });

  // Derived label with required/optional suffix
  displayLabel = computed(() =>
    wrapLabelRequiredOrOptional(this.label(), this.required())
  );

  validators = VALIDATORS;

  areaForm = this.fb.group({
    text: [null],
  });

  onTouched = noop;
  onChangeSubs: Subscription[] = [];

  constructor() {
    effect(() => {
      const v = this.validatorsForText(this.required(), this.maxlength());
      this.areaForm.controls['text'].setValidators(v);
      this.areaForm.controls['text'].updateValueAndValidity({
        emitEvent: false,
      });
    });
  }
  // ControlValueAccessor
  writeValue(value: unknown) {
    if (value !== undefined) {
      this.areaForm.setValue({ text: value }, { emitEvent: false });
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  registerOnChange(onChange: any) {
    const sub = this.areaForm.controls['text'].valueChanges.subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  ngOnDestroy(): void {
    this.onChangeSubs.forEach((sub) => sub.unsubscribe());
  }

  registerOnTouched(onTouched: () => void) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.areaForm.disable();
    } else {
      this.areaForm.enable();
    }
  }

  validate() {
    if (this.areaForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.areaForm);
  }

  private validatorsForText(
    isRequired: boolean,
    maxLen?: number
  ): ValidatorFn | ValidatorFn[] | null {
    const v: ValidatorFn[] = [];
    if (isRequired) {
      v.push(Validators.required);
    }
    if (maxLen != null) {
      v.push(Validators.maxLength(maxLen));
    }
    return v;
  }

  get text() {
    return this.areaForm.controls['text'];
  }
}
