import {
  Component,
  computed,
  inject,
  Injector,
  Input,
  OnDestroy,
  OnInit,
  signal,
  AfterViewInit,
} from '@angular/core';
import {
  AbstractControl,
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
import { FundingEntityRef } from '../../models/interfaces/funding-entity-ref.interface';
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
import { FundersStore } from '../../../funders/signal/funders-store';

@Component({
  selector:
    'app-funder-search[formControl],' + 'app-funder-search[formControlName]',
  templateUrl: './funder-search.component.html',
  styleUrls: ['./funder-search.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: FunderSearchComponent,
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
export class FunderSearchComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor, AfterViewInit
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);
  private injector = inject(Injector);

  constants = FUNDING_SEARCH_LABELS;
  validators = VALIDATORS;

  @Input() placeholder: string = '';
  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() multiSelect: boolean = false;
  @Input() showOnlyExternalFunders: boolean = false;

  readonly fundersStore = inject(FundersStore);

  form: FormGroup = this.fb.group({
    search: [''],
    fundingEntityRefs: this.fb.array([], {
      validators: this.validatorsForFunder(),
    }),
  });
  fundingEntityRefs: FundingEntityRef[] = [];

  searchValue = signal('');
  filteredEntities = computed(() => {
    const funderEntities = this.showOnlyExternalFunders
      ? this.fundersStore.externallyManagedFunders()
      : this.fundersStore.entities();

    return funderEntities.filter((ent) => {
      const searchValue = this.searchValue();
      if (!searchValue) return true; // if no search value, return all
      const name = ent.name?.[0]?.text.toLowerCase();
      return name.includes(searchValue);
    });
  });

  ngOnInit() {
    this.label = wrapLabelRequiredOrOptional(this.label, this.required);

    // initialize disabled state based on current selection
    this.applySearchDisabledState(this.fundingEntityFormArray.length);
    this.fundingEntityFormArray.valueChanges.subscribe((arr) => {
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

  private touchedChangeSub?: Subscription;

  ngAfterViewInit() {
    // Workaround for propagating touched state to all nested child controls:
    // as markAllAsTouched is not propagated to the child controls
    // we subscribe to the TouchedChangeEvent and mark all controls as touched.
    this.touchedChangeSub = this.injector
      .get(NgControl)
      .control!.events.subscribe((event) => {
        if (event instanceof TouchedChangeEvent) {
          this.form.markAllAsTouched();
          this.form.updateValueAndValidity();
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.fundingEntityFormArray.valueChanges
      .pipe(map((value) => (this.multiSelect ? value : value[0])))
      .subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  private applySearchDisabledState(selectionLen: number): void {
    const shouldDisable = !this.multiSelect && selectionLen > 0;
    const control = this.search;
    if (shouldDisable && control.enabled) {
      control.disable({ emitEvent: false });
    } else if (!shouldDisable && control.disabled) {
      control.enable({ emitEvent: false });
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.form.disable();
    } else {
      this.form.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    if (value) {
      const values = this.multiSelect ? value : [value];
      values.forEach((fundingEntityRef: FundingEntityRef) => {
        this.fundingEntityFormArray.push(this.fb.control(fundingEntityRef));
      });
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.form.valid) {
      return null;
    }
    return getFormValidationErrors(this.form);
  }

  private validatorsForFunder(): ValidatorFn[] {
    const validators = [];
    if (this.required) {
      validators.push(Validators.required);
    }
    return validators;
  }

  displayFn(funder: FundingEntityRef): string {
    return funder?.name?.[0]?.text ?? '';
  }

  setSelectedFunder() {
    if (this.fundingEntityFormArray.value.length > 0) {
      this.search.setValue('');
      return;
    }
    const selectedValue = this.search.value;
    this.fundingEntityFormArray.push(this.fb.control(selectedValue));
    this.search.setValue('');
  }

  removeFunder(index: number) {
    this.fundingEntityFormArray.removeAt(index);
    this.search.setValue(null, { emitEvent: true });
  }

  get search(): FormControl {
    return this.form.get('search') as FormControl;
  }

  get fundingEntityFormArray(): FormArray {
    return this.form.get('fundingEntityRefs') as FormArray;
  }
}
