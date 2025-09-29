import {
  Component,
  Input,
  OnDestroy,
  OnInit,
  inject,
  Injector,
} from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator,
  FormsModule,
  ReactiveFormsModule,
  NgControl,
  TouchedChangeEvent,
} from '@angular/forms';
import { Subscription } from 'rxjs';
import { CheckboxItemControlWrapper } from '../../utils/checkbox-item-control-wrapper';
import { CheckboxGroupControlWrapper } from '../../utils/checkbox-group-control-wrapper';
import { getFormValidationErrors } from '../../utils/validator-util';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { VALIDATORS } from '../../shared.constants';
import { ValidationService } from '../../services/validation.service';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatError } from '@angular/material/form-field';

@Component({
  selector:
    'app-multiple-choice-field[formControl],' +
    'app-multiple-choice-field[formControlName],' +
    'app-multiple-choice-field[ngModel]',
  templateUrl: './multiple-choice-field.component.html',
  styleUrls: ['./multiple-choice-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: MultipleChoiceFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: MultipleChoiceFieldComponent,
    },
  ],
  imports: [FormsModule, ReactiveFormsModule, MatCheckbox, MatError],
})
export class MultipleChoiceFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);
  private injector = inject(Injector);
  multipleChoiceForm: UntypedFormGroup;

  options: string[] = [];
  checkboxGroup: CheckboxGroupControlWrapper<string> =
    new CheckboxGroupControlWrapper();

  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() enableSelectAll: boolean = true;
  @Input() type: object = {}; // Enum

  allSelected: boolean = false;
  validators = VALIDATORS;

  constructor() {
    this.multipleChoiceForm = this.fb.group({});
  }

  ngOnInit(): void {
    this.initOptions();
    this.initCheckboxes();
    this.multipleChoiceForm = this.fb.group({
      checkboxes: this.checkboxGroup.control,
    });
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
          this.multipleChoiceForm.markAllAsTouched();
          this.multipleChoiceForm.updateValueAndValidity();
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.checkboxes.valueChanges.subscribe((values) => {
      const selected = values
        .map((isChecked: boolean, i: number) =>
          isChecked ? this.options[i] : null
        )
        .filter(Boolean);
      onChange(selected);
    });
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.multipleChoiceForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.multipleChoiceForm);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.multipleChoiceForm.disable();
    } else {
      this.multipleChoiceForm.enable();
    }
  }

  writeValue(value: string[]) {
    if (value) {
      const boolValue: boolean[] = this.options.map((option) =>
        value.includes(option)
      );
      this.checkboxes.setValue(boolValue);
    }
  }

  onChangeSelectedAll(checked: boolean) {
    this.allSelected = checked;
    this.checkboxes.setValue(this.options.map(() => checked));
    this.checkboxes.markAsTouched();
  }

  onChangeCheckbox(checked: boolean) {
    this.allSelected =
      checked && this.checkboxes.value.every((value: boolean) => value);
  }

  someSelected(): boolean {
    return (
      this.checkboxes.value.some((value: boolean) => value) && !this.allSelected
    );
  }

  private initCheckboxes() {
    const checkboxItems = this.options.map(
      (value: string) =>
        new CheckboxItemControlWrapper({ value: value, label: value })
    );
    this.checkboxGroup = new CheckboxGroupControlWrapper(
      checkboxItems,
      this.label,
      this.required
    );
  }

  private initOptions() {
    this.options = Object.values(this.type).filter((item) => {
      return isNaN(Number(item));
    });
  }

  get checkboxes(): UntypedFormArray {
    return this.multipleChoiceForm.controls['checkboxes'] as UntypedFormArray;
  }
}
