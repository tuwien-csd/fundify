import {
  Component,
  computed,
  inject,
  Injector,
  input,
  OnDestroy,
  OnInit,
  signal,
  AfterViewInit,
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
    // Filter by search value
    const filtered = this.choices.filter((item) => {
      if (!item) return false;

      const searchValue = this.searchValue();
      if (!searchValue) return true;
      return item.toLowerCase().includes(searchValue.toLowerCase());
    });

    // Sort alphabetically by display text
    return filtered.sort((a, b) => {
      const textA = a.toLowerCase();
      const textB = b.toLowerCase();
      return textA.localeCompare(textB);
    });
  });

  ngOnInit() {
    this.choices = Object.values(this.type()).filter((item) => {
      return isNaN(Number(item));
    });
    this.applySearchDisabledState(this.selectionsFormArray.length);
    this.selectionsFormArray.valueChanges.subscribe((arr) => {
      this.applySearchDisabledState(arr?.length ?? 0);
    });

    (this.form.get('search') as FormControl).valueChanges.subscribe((it) =>
      this.searchValue.set(it)
    );
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
      const values = this.multiSelect() ? value : [value];
      const selectedItems = this.choices.filter((item) =>
        values.includes(item)
      );
      selectedItems.forEach((item: string) => {
        this.selectionsFormArray.push(this.fb.control(item));
      });
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

  isItemSelected(item: string): boolean {
    const selectedItems: string[] = this.selectionsFormArray.value || [];
    return selectedItems.some((selected) => selected === item);
  }

  setSelectedItem() {
    if (!this.multiSelect() && this.selectionsFormArray.value.length > 0) {
      this.search.setValue('');
      return;
    }
    const selectedValue = this.search.value;
    if (selectedValue) {
      this.selectionsFormArray.push(this.fb.control(selectedValue));
      this.search.setValue('');
    }
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

  private applySearchDisabledState(selectionLen: number): void {
    const shouldDisable = !this.multiSelect() && selectionLen > 0;
    const control = this.search;
    if (shouldDisable && control.enabled) {
      control.disable({ emitEvent: false });
    } else if (!shouldDisable && control.disabled) {
      control.enable({ emitEvent: false });
    }
  }
}
