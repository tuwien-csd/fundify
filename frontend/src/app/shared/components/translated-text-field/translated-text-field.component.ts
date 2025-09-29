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
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
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
import { LanguageEnum } from '../../models/enums/language.enum';
import { TranslatedText } from '../../models/interfaces/translated-text.interface';
import { getFormValidationErrors } from '../../utils/validator-util';
import { TRANSLATED_TEXT_LABELS, VALIDATORS } from '../../shared.constants';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { ValidationService } from '../../services/validation.service';
import { CustomValidators } from '../../validators/custom-validators';
import { MatFormField, MatLabel, MatError } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { MatSelect, MatOption } from '@angular/material/select';
import { MatIconButton, MatButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector:
    'app-translated-text-field[formControl],' +
    'app-translated-text-field[formControlName],' +
    'app-translated-text-field[ngModel]',
  templateUrl: './translated-text-field.component.html',
  styleUrls: ['./translated-text-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: TranslatedTextFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: TranslatedTextFieldComponent,
    },
  ],
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    FormsModule,
    ReactiveFormsModule,
    CdkTextareaAutosize,
    MatError,
    MatSelect,
    MatOption,
    MatIconButton,
    MatTooltip,
    MatIcon,
    MatButton,
  ],
})
export class TranslatedTextFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor, AfterViewInit
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);
  private injector = inject(Injector);

  @Input() placeholder: string = '';
  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() textLabel: string = '';
  @Input() languageLabel: string = 'Language';
  @Input() multilineText: boolean = false;
  @Input() expandEmpty: boolean = true;
  @Input() minLength: number = 0;

  translatedTextForm: UntypedFormArray;

  constants = TRANSLATED_TEXT_LABELS;
  validators = VALIDATORS;
  langCodes: LanguageEnum[] = [LanguageEnum.GERMAN, LanguageEnum.ENGLISH];
  selectedLangCodes: LanguageEnum[] = [];
  expanded: boolean = false;

  constructor() {
    this.translatedTextForm = this.fb.array([]);
  }

  ngOnInit(): void {
    if (this.required || this.expandEmpty) {
      this.addTextForm();
    }
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
          this.translatedTextForm.markAllAsTouched();
          this.translatedTextForm.updateValueAndValidity();
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.translatedTextForm.valueChanges.subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.translatedTextForm.disable();
    } else {
      this.translatedTextForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    if (value) {
      this.translatedTextForm = this.fb.array(
        value.map((x: TranslatedText) => {
          this.selectedLangCodes.push(x.language);
          return this.fb.group(
            {
              text: [x.text, this.validatorsForText()],
              language: [x.language, this.validatorsForLanguage()],
              translation: [x.translation],
            },
            {
              validators: CustomValidators.translation,
            }
          );
        })
      );
      this.updateExpanded();
    }
  }

  addTextForm(text?: string, language?: LanguageEnum) {
    const fg = this.fb.group(
      {
        text: [text, this.validatorsForText()],
        language: [language, this.validatorsForLanguage()],
        translation: [null],
      },
      { validators: CustomValidators.translation }
    );
    this.translatedTextForm.push(fg);
    this.updateExpanded();
  }

  deleteTextForm(index: number) {
    const langIndex = this.selectedLangCodes.indexOf(
      this.translatedTextForm.at(index).value.language
    );
    this.translatedTextForm.removeAt(index);
    this.selectedLangCodes.splice(langIndex, 1);
    this.updateExpanded();
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.translatedTextForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.translatedTextForm);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  updateSelectedLangCodes(event: any, index: number) {
    if (index < this.translatedTextForm.length) {
      this.selectedLangCodes[index] = event.value;
    } else {
      this.selectedLangCodes.push(event.value);
    }
  }

  private validatorsForText(): ValidatorFn | ValidatorFn[] | null {
    const validators = [];
    validators.push(Validators.minLength(this.minLength));
    if (this.required) {
      validators.push(Validators.required);
    }
    return validators;
  }

  private validatorsForLanguage(): ValidatorFn | ValidatorFn[] | null {
    if (this.required) {
      return [Validators.required];
    }
    return null;
  }

  isEmpty(): boolean {
    return this.translatedTextForm.length === 0;
  }

  private updateExpanded() {
    this.expanded = this.required || this.expandEmpty || !this.isEmpty();
  }

  getLanguageControl(i: number): UntypedFormControl {
    return this.translatedTextForm.at(i).get('language') as UntypedFormControl;
  }

  getTextControl(i: number): UntypedFormControl {
    return this.translatedTextForm.at(i).get('text') as UntypedFormControl;
  }
}
