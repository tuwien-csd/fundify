import {
  Component,
  inject,
  Injectable,
  input,
  Input,
  OnInit,
} from '@angular/core';
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatInput, MatLabel } from '@angular/material/input';
import { SharedModule } from '../../../../shared.module';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { ValidationService } from '../../../../services/validation.service';
import {
  TRANSLATED_TEXT_LABELS,
  VALIDATORS,
} from '../../../../shared.constants';
import { LanguageEnum } from '../../../../models/enums/language.enum';
import { wrapLabelRequiredOrOptional } from '../../../../utils/display-util';
import { TranslatedText } from '../../../../models/interfaces/translated-text.interface';

export type TranslatedTextForm = {
  text: FormControl<string | null | undefined>;
  language: FormControl<LanguageEnum | null | undefined>;
  translation: FormControl<string | null | undefined>;
};

@Component({
  selector: 'app-translated-text-field-v2',
  imports: [
    CdkTextareaAutosize,
    MatButton,
    MatError,
    MatFormField,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatOption,
    MatSelect,
    MatTooltip,
    SharedModule,
  ],
  templateUrl: './translated-text-field-v2.component.html',
  styleUrl: './translated-text-field-v2.component.scss',
})
export class TranslatedTextFieldV2Component implements OnInit {
  formFactory = inject(TranslatedTextFieldFormFactory);
  validationService = inject(ValidationService);

  @Input() placeholder: string = '';
  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() textLabel: string = '';
  @Input() languageLabel: string = 'Language';
  @Input() multilineText: boolean = false;
  @Input() expandEmpty: boolean = true;
  @Input() minLength: number = 0;

  inputFormGroupArray =
    input.required<FormArray<FormGroup<TranslatedTextForm>>>();

  constants = TRANSLATED_TEXT_LABELS;
  validators = VALIDATORS;
  langCodes = Object.values(LanguageEnum);

  get selectedLangCodes(): LanguageEnum[] {
    return this.inputFormGroupArray()
      .controls.map((it) => it.value.language)
      .filter((it) => it !== null && it !== undefined);
  }

  expanded: boolean = false;

  ngOnInit(): void {
    if (this.required || this.expandEmpty) {
      if (this.isEmpty()) {
        this.addTextForm();
      }
    }
    this.updateExpanded();
    this.label = wrapLabelRequiredOrOptional(this.label, this.required);
  }

  addTextForm() {
    this.formFactory.addFormGroup(this.inputFormGroupArray(), this.required);
    this.updateExpanded();
  }

  deleteTextForm(index: number) {
    this.inputFormGroupArray().removeAt(index);
    this.updateExpanded();
  }

  isEmpty(): boolean {
    return this.inputFormGroupArray().length === 0;
  }

  private updateExpanded() {
    this.expanded = this.required || this.expandEmpty || !this.isEmpty();
  }
}

@Injectable({ providedIn: 'root' })
export class TranslatedTextFieldFormFactory {
  private fb = new FormBuilder();

  /**
   * Creates a FormArray of FormGroups for translated text fields.
   */
  createFormArray(
    required: boolean,
    input?: TranslatedText[]
  ): FormArray<FormGroup<TranslatedTextForm>> {
    const formGroups = (input ?? []).map((it) => {
      return this.buildFormGroup(required, it.text, it.language);
    });
    return this.fb.array<FormGroup<TranslatedTextForm>>(formGroups);
  }

  /**
   * Adds a new FormGroup to the provided FormArray.
   * Mutates the input FormArray.
   * @param formArray
   * @param required
   */
  addFormGroup(
    formArray: FormArray<FormGroup<TranslatedTextForm>>,
    required: boolean = false
  ) {
    const fg = this.buildFormGroup(required);
    formArray.push(fg);
  }

  private buildFormGroup(
    required: boolean = false,
    text: string = '',
    language: LanguageEnum | null = null
  ) {
    return this.fb.group<TranslatedTextForm>({
      text: new FormControl<string>(text, {
        nonNullable: true,
        validators: this.getValidatorsForText(required),
      }),
      language: new FormControl<LanguageEnum | null>(
        language,
        required ? [Validators.required, Validators.nullValidator] : []
      ),
      translation: new FormControl<string | null>('o'), //TODO 13.08.2025: Translation is not shown in the UI. What is the purpose of this field?
    });
  }

  private getValidatorsForText(
    required: boolean,
    minLength = 0
  ): ValidatorFn | ValidatorFn[] | null {
    const validators = [];
    validators.push(Validators.minLength(minLength));
    validators.push(Validators.pattern(/\S/));
    if (required) {
      validators.push(Validators.required);
    }
    return validators;
  }
}
