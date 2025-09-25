import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
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
} from '@angular/forms';
import { Subscription } from 'rxjs';
import { TranslatedText } from '../../models/interfaces/translated-text.interface';
import { getFormValidationErrors } from '../../utils/validator-util';
import { TRANSLATED_TEXTS_LABELS } from '../../shared.constants';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { TranslatedTextFieldComponent } from '../translated-text-field/translated-text-field.component';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-translated-texts-field',
  templateUrl: './translated-texts-field.component.html',
  styleUrls: ['./translated-texts-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: TranslatedTextsFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: TranslatedTextsFieldComponent,
    },
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    TranslatedTextFieldComponent,
    MatButton,
  ],
})
export class TranslatedTextsFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor
{
  private fb = inject(UntypedFormBuilder);

  itemsForm: UntypedFormArray;

  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() textLabel: string = '';

  constants = TRANSLATED_TEXTS_LABELS;

  constructor() {
    this.itemsForm = this.fb.array([]);
  }

  ngOnInit(): void {
    this.itemsForm = this.fb.array([
      this.fb.group({
        itemText: [null, this.validatorsForText(0)],
      }),
    ]);
    this.label = wrapLabelRequiredOrOptional(this.label, this.required);
  }

  ngOnDestroy() {
    for (const sub of this.onChangeSubs) {
      sub.unsubscribe();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];
  // eslint-disable-next-line @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onChange = (v: any) => {};

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.itemsForm.valueChanges.subscribe((values) => {
      const pureValues = values
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        .map((x: any) => x.itemText);
      onChange(pureValues);
    });
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.itemsForm.disable();
    } else {
      this.itemsForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    if (value) {
      this.itemsForm = this.fb.array(
        value.map((x: TranslatedText, index: number) => {
          return this.fb.group({
            itemText: [x, this.validatorsForText(index)],
          });
        })
      );
    }
  }

  addTextItem(index: number) {
    const fg = this.fb.group({
      itemText: [null, this.validatorsForText(index)],
    });
    this.itemsForm.push(fg);
  }

  deleteTextItem(index: number) {
    this.itemsForm.removeAt(index);
  }

  isTextItemRequired(index: number) {
    return this.required && index === 0;
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.itemsForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.itemsForm);
  }

  convertToFormGroup(absCtrl: AbstractControl): UntypedFormGroup {
    return absCtrl as UntypedFormGroup;
  }

  private validatorsForText(index: number): ValidatorFn | ValidatorFn[] | null {
    const validators = [];
    if (this.isTextItemRequired(index)) {
      validators.push(Validators.required);
    }
    return validators;
  }
}
