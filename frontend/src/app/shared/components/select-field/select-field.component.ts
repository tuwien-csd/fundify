import {
  booleanAttribute,
  Component,
  computed,
  effect,
  inject,
  input,
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
import { MatSelect } from '@angular/material/select';
import { MatOption } from '@angular/material/core';

// Shared validation scaffolding like your other fields
import { ValidationService } from 'src/app/shared/services/validation.service';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { VALIDATORS } from '../../shared.constants';
import { getFormValidationErrors } from '../../utils/validator-util';
import { noop } from '../../utils/noop';

type OptionItem<T = unknown> = { value: T; label: string };

@Component({
  selector:
    'app-select-field[formControl],' +
    'app-select-field[formControlName],' +
    'app-select-field[ngModel]',
  standalone: true,
  templateUrl: './select-field.component.html',
  styleUrls: ['./select-field.component.scss'],
  encapsulation: ViewEncapsulation.None,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatHint,
    MatError,
    MatSelect,
    MatOption,
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: SelectFieldComponent,
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: SelectFieldComponent,
      multi: true,
    },
  ],
})
export class SelectFieldComponent
  implements OnDestroy, Validator, ControlValueAccessor
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);

  // Signals-based inputs
  label = input<string>('');
  textLabel = input<string>('');
  hint = input<string>('');
  required = input<boolean, unknown>(false, { transform: booleanAttribute });
  multiple = input<boolean, unknown>(false, { transform: booleanAttribute });

  // Two ways to provide options:
  // 1) As an explicit array of {value, label}
  options = input<OptionItem[] | null>(null);
  // 2) As an enum-like object (values turned into labels)
  enumObject = input<object | null>(null);

  // Optional ability to disable validation messages
  hideValidation = input<boolean, unknown>(false, {
    transform: booleanAttribute,
  });

  // Derived label matching your required/optional format
  displayLabel = computed(() =>
    wrapLabelRequiredOrOptional(this.label(), this.required())
  );

  // Normalize options for rendering
  normalizedOptions = computed<OptionItem[]>(() => {
    const opts = this.options();
    if (opts && Array.isArray(opts)) {
      return opts;
    }
    const en = this.enumObject();
    if (en) {
      // Use object values as labels and keys as values by default
      return Object.keys(en).map((k) => ({
        value: (en as Record<string, unknown>)[k],
        label: String((en as Record<string, unknown>)[k]),
      }));
    }
    return [];
  });

  validators = VALIDATORS;

  selectForm = this.fb.group({
    value: [null],
  });

  onTouched = noop;
  onChangeSubs: Subscription[] = [];
  constructor() {
    effect(() => {
      const v = this.validatorsForValue(this.required());
      this.selectForm.controls['value'].setValidators(v);
      this.selectForm.controls['value'].updateValueAndValidity({
        emitEvent: false,
      });
    });
  }

  ngOnDestroy(): void {
    for (const sub of this.onChangeSubs) {
      sub.unsubscribe();
    }
  }

  // ControlValueAccessor
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  writeValue(value: any) {
    this.selectForm.setValue({ value }, { emitEvent: false });
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  registerOnChange(onChange: any) {
    const sub =
      this.selectForm.controls['value'].valueChanges.subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  registerOnTouched(onTouched: () => void) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.selectForm.disable();
    } else {
      this.selectForm.enable();
    }
  }

  validate() {
    if (this.selectForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.selectForm);
  }

  private validatorsForValue(isRequired: boolean): ValidatorFn[] | null {
    if (isRequired) {
      // Works for both single and multiple selection (array) to ensure non-empty value
      return [Validators.required];
    }
    return null;
  }

  get valueControl() {
    return this.selectForm.controls['value'];
  }
}
