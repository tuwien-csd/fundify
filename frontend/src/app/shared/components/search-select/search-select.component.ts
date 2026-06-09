import {
  AfterViewInit,
  Component,
  computed,
  inject,
  Injector,
  input,
  OnDestroy,
  OnInit,
  signal,
} from '@angular/core';
import {
  ControlValueAccessor,
  FormArray,
  FormControl,
  FormGroup,
  FormsModule,
  NG_VALUE_ACCESSOR,
  NgControl,
  ReactiveFormsModule,
  TouchedChangeEvent,
  UntypedFormBuilder,
  Validator,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { map } from 'rxjs/operators';
import { Subscription } from 'rxjs';
import { getFormValidationErrors } from '../../utils/validator-util';
import { FUNDING_SEARCH_LABELS, VALIDATORS } from '../../shared.constants';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { ValidationService } from '../../services/validation.service';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import {
  MatAutocomplete,
  MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatOption } from '@angular/material/select';
import {
  MatChip,
  MatChipListbox,
  MatChipRemove,
} from '@angular/material/chips';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector:
    'app-search-select[formControl],' + 'app-search-select[formControlName]',
  templateUrl: './search-select.component.html',
  styleUrls: ['./search-select.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: SearchSelectComponent,
    },
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormField,
    MatInput,
    MatAutocompleteTrigger,
    MatAutocomplete,
    MatOption,
    MatError,
    MatChipListbox,
    MatChip,
    MatIcon,
    MatChipRemove,
  ],
})
export class SearchSelectComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor, AfterViewInit
{
  type = input<object>([]);
  /** Optional structured options: label is displayed, value is stored in the form control. */
  options = input<{ value: string; label: string }[] | null>(null);
  placeholder = input<string>('');
  required = input<boolean>(false);
  label = input<string>('');
  multiSelect = input<boolean>(true);

  private fb = inject(UntypedFormBuilder);
  private validationService = inject(ValidationService);
  private injector = inject(Injector);
  private choices: string[] = [];
  private touchedChangeSub?: Subscription;
  private onChangeSubs: Subscription[] = [];

  private readonly labelToValue = computed(() => {
    const opts = this.options();
    return opts ? new Map(opts.map((o) => [o.label, o.value])) : null;
  });

  private readonly valueToLabel = computed(() => {
    const opts = this.options();
    return opts ? new Map(opts.map((o) => [o.value, o.label])) : null;
  });

  protected readonly VALIDATORS = VALIDATORS;
  protected readonly FUNDING_SEARCH_LABELS = FUNDING_SEARCH_LABELS;
  protected form: FormGroup = this.fb.group({
    search: [''],
    selections: this.fb.array([], {
      validators: this.validatorsForSelection(),
    }),
  });

  protected searchValue = signal('');
  protected displayedLabel = computed(() =>
    wrapLabelRequiredOrOptional(this.label(), this.required())
  );
  protected filteredItems = computed(() => {
    const searchValue = this.searchValue();
    const opts = this.options();

    if (opts) {
      return opts
        .filter(
          (o) =>
            !searchValue ||
            o.label.toLowerCase().includes(searchValue.toLowerCase())
        )
        .sort((a, b) => a.label.localeCompare(b.label))
        .map((o) => o.label);
    }

    const filtered = this.choices.filter((item) => {
      if (!item) return false;
      if (!searchValue) return true;
      return item.toLowerCase().includes(searchValue.toLowerCase());
    });
    return filtered.sort((a, b) => a.toLowerCase().localeCompare(b.toLowerCase()));
  });

  ngOnInit() {
    if (!this.options()) {
      this.choices = Object.values(this.type()).filter((item) => {
        return isNaN(Number(item));
      });
    }
    this.applySearchDisabledState();
    this.selectionsFormArray.valueChanges.subscribe(() => {
      this.applySearchDisabledState();
    });

    (this.form.get('search') as FormControl).valueChanges.subscribe((it) => {
      this.searchValue.set(it);
      if (!this.multiSelect() && !it) {
        this.selectionsFormArray.clear();
      }
    });
  }

  ngOnDestroy() {
    this.touchedChangeSub?.unsubscribe();
    for (const sub of this.onChangeSubs) {
      sub.unsubscribe();
    }
  }

  ngAfterViewInit() {
    this.touchedChangeSub = this.injector
      .get(NgControl)
      .control!.events.subscribe((event) => {
        if (event instanceof TouchedChangeEvent) {
          this.form.markAllAsTouched();
          this.form.updateValueAndValidity();
        }
      });
  }

  registerOnChange(onChange: (value: string | string[]) => void) {
    const sub = this.selectionsFormArray.valueChanges
      .pipe(map((value: string[]) => (this.multiSelect() ? value : value[0])))
      .subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  registerOnTouched(onTouched: () => void) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.form.disable();
    } else {
      this.form.enable();
    }
  }

  writeValue(value: string | string[]) {
    this.selectionsFormArray.clear();
    if (value) {
      const values: string[] = this.multiSelect() ? (value as string[]) : [value as string];
      if (this.options()) {
        values.forEach((v) => this.selectionsFormArray.push(this.fb.control(v)));
        if (!this.multiSelect() && values[0]) {
          const v2l = this.valueToLabel();
          const label = v2l ? (v2l.get(values[0]) ?? values[0]) : values[0];
          this.search.setValue(label, { emitEvent: false });
        }
      } else {
        const selectedItems = this.choices.filter((item) => values.includes(item));
        selectedItems.forEach((item: string) => {
          this.selectionsFormArray.push(this.fb.control(item));
        });
      }
    } else if (!this.multiSelect()) {
      this.search.setValue('', { emitEvent: false });
    }
  }

  validate() {
    if (this.form.valid) {
      return null;
    }
    return getFormValidationErrors(this.form);
  }

  onTouched: () => void = () => {};

  displayFn(item: string): string {
    return item ?? '';
  }

  isItemSelected(label: string): boolean {
    const selectedValues: string[] = this.selectionsFormArray.value || [];
    const l2v = this.labelToValue();
    const valueToCheck = l2v ? (l2v.get(label) ?? label) : label;
    return selectedValues.some((v) => v === valueToCheck);
  }

  setSelectedItem() {
    const selectedLabel = this.search.value;
    if (!selectedLabel) return;
    const l2v = this.labelToValue();
    const storedValue = l2v ? (l2v.get(selectedLabel) ?? selectedLabel) : selectedLabel;
    if (!this.multiSelect()) {
      this.selectionsFormArray.clear();
      this.selectionsFormArray.push(this.fb.control(storedValue));
      // label stays in the input via mat-autocomplete displayWith
    } else {
      this.selectionsFormArray.push(this.fb.control(storedValue));
      this.search.setValue('');
    }
  }

  protected getChipLabel(value: string): string {
    const v2l = this.valueToLabel();
    return v2l ? (v2l.get(value) ?? value) : value;
  }

  removeItem(index: number) {
    this.selectionsFormArray.removeAt(index);
    this.search.setValue('', { emitEvent: true });
  }

  getValidationErrors(form: FormGroup, validatorName: string) {
    return this.validationService.getValidationErrorMessage(
      form,
      validatorName
    );
  }

  get search(): FormControl {
    return this.form.get('search') as FormControl;
  }

  get selectionsFormArray(): FormArray {
    return this.form.get('selections') as FormArray;
  }

  private validatorsForSelection(): ValidatorFn[] {
    const validatorList = [];
    if (this.required()) {
      validatorList.push(Validators.required);
    }
    return validatorList;
  }

  private applySearchDisabledState(): void {
    const control = this.search;
    if (control.disabled) {
      control.enable({ emitEvent: false });
    }
  }
}
