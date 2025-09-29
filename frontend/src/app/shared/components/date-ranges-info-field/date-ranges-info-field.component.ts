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
  ValidatorFn,
  Validators,
  FormsModule,
  ReactiveFormsModule,
  NgControl,
  TouchedChangeEvent,
} from '@angular/forms';
import { Subscription } from 'rxjs';
import { DateInfoRange } from '../../models/interfaces/date-info-range.interface';
import { getFormValidationErrors } from '../../utils/validator-util';
import { DATE_RANGE_INFO_LABELS } from '../../shared.constants';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { DateRangeFieldComponent } from '../date-range-field/date-range-field.component';
import { TranslatedTextFieldComponent } from '../translated-text-field/translated-text-field.component';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-date-ranges-info-field',
  templateUrl: './date-ranges-info-field.component.html',
  styleUrls: ['./date-ranges-info-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: DateRangesInfoFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: DateRangesInfoFieldComponent,
    },
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    DateRangeFieldComponent,
    TranslatedTextFieldComponent,
    MatButton,
  ],
})
export class DateRangesInfoFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor
{
  private fb = inject(UntypedFormBuilder);
  private injector = inject(Injector);

  dateRangesInfoForm: UntypedFormGroup;

  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() labelRange: string = '';
  @Input() labelInfo: string = '';
  @Input() labelStage: string = '';

  constants = DATE_RANGE_INFO_LABELS;

  constructor() {
    this.dateRangesInfoForm = this.fb.group({
      dateRangesInfo: this.fb.array([]),
    });
  }

  ngOnInit(): void {
    this.label = wrapLabelRequiredOrOptional(this.label, this.required);
    this.addEmptyGroup(0);
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
          this.dateRangesInfoForm.markAllAsTouched();
          this.dateRangesInfoForm.updateValueAndValidity();
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.dateRangesInfo.valueChanges.subscribe((value) => {
      onChange(value);
    });
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.dateRangesInfoForm.disable();
    } else {
      this.dateRangesInfoForm.enable();
    }
  }

  writeValue(value: DateInfoRange[]) {
    if (value) {
      this.dateRangesInfo.clear();
      value.forEach((x: DateInfoRange, index: number) => {
        this.dateRangesInfo.push(this.createRangeInfoFormGroup(index, x));
      });
    }
  }

  createRangeInfoFormGroup(
    index: number,
    dateRangeInfo?: DateInfoRange
  ): UntypedFormGroup {
    return this.fb.group({
      duration: [dateRangeInfo?.duration, this.validatorsForRange(index)],
      description: [dateRangeInfo?.description, this.validatorsForInfo(index)],
      number: [dateRangeInfo?.number],
    });
  }

  addEmptyGroup(index: number) {
    this.dateRangesInfo.push(this.createRangeInfoFormGroup(index));
  }

  deleteGroup(index: number) {
    if (this.dateRangesInfo.length > 1) {
      this.dateRangesInfo.removeAt(index);
    }
  }

  isRangeRequired(index: number) {
    return this.required && index === 0;
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.dateRangesInfoForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.dateRangesInfoForm);
  }

  convertToFormGroup(absCtrl: AbstractControl): UntypedFormGroup {
    return absCtrl as UntypedFormGroup;
  }

  private validatorsForRange(
    index: number
  ): ValidatorFn | ValidatorFn[] | null {
    const validators = [];
    if (this.isRangeRequired(index)) {
      validators.push(Validators.required);
    }
    return validators;
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  private validatorsForInfo(index: number): ValidatorFn | ValidatorFn[] | null {
    return null;
  }

  get dateRangesInfo(): UntypedFormArray {
    return this.dateRangesInfoForm.get('dateRangesInfo') as UntypedFormArray;
  }
}
