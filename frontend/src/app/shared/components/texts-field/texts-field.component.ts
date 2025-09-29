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
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormGroup,
  Validator,
  ValidatorFn,
  Validators,
  FormsModule,
  ReactiveFormsModule,
  NgControl,
  TouchedChangeEvent,
} from '@angular/forms';
import { Subscription } from 'rxjs';
import { getFormValidationErrors } from '../../utils/validator-util';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { TextFieldComponent } from '../text-field/text-field.component';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector:
    'app-texts-field[formControl],' +
    'app-texts-field[formControlName],' +
    'app-texts-field[ngModel]',
  templateUrl: './texts-field.component.html',
  styleUrls: ['./texts-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: TextsFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: TextsFieldComponent,
    },
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    TextFieldComponent,
    MatIconButton,
    MatIcon,
  ],
})
export class TextsFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor, AfterViewInit
{
  private fb = inject(UntypedFormBuilder);
  private injector = inject(Injector);

  textsForm: UntypedFormArray;

  @Input() required: boolean = false;
  @Input() label: string = '';
  @Input() textLabel: string = '';
  @Input() textPattern: string | RegExp = '';

  constructor() {
    this.textsForm = this.fb.array([]);
  }

  ngOnInit(): void {
    this.textsForm = this.fb.array([
      this.fb.group({
        text: [null, this.validatorsForText(0)],
      }),
    ]);
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
          this.textsForm.markAllAsTouched();
          this.textsForm.updateValueAndValidity();
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];
  // eslint-disable-next-line @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onChange = (v: any) => {};

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.textsForm.valueChanges.subscribe((values) => {
      const pureValues = values
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        .map((x: any) => x.text);
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
      this.textsForm.disable();
    } else {
      this.textsForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    if (value) {
      this.textsForm = this.fb.array(
        value.map((x: string, index: number) => {
          return this.fb.group({
            text: [x, this.validatorsForText(index)],
          });
        })
      );
    }
  }

  addTextItem(index: number) {
    const fg = this.fb.group({
      text: [null, this.validatorsForText(index)],
    });
    this.textsForm.push(fg);
  }

  deleteTextItem(index: number) {
    this.textsForm.removeAt(index);
  }

  isTextItemRequired(index: number) {
    return this.required && index === 0;
  }

  getTextItemLabel(index: number) {
    return this.textLabel + ' ' + (index + 1);
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.textsForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.textsForm);
  }

  convertToFormGroup(absCtrl: AbstractControl): UntypedFormGroup {
    return absCtrl as UntypedFormGroup;
  }

  private validatorsForText(index: number): ValidatorFn | ValidatorFn[] | null {
    const validators = [];
    if (this.isTextItemRequired(index)) {
      validators.push(Validators.required);
      validators.push(Validators.pattern(this.textPattern));
    }
    return validators;
  }
}
