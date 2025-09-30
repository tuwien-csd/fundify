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
  UntypedFormBuilder,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  Validator,
  ValidatorFn,
  Validators,
  UntypedFormArray,
  FormsModule,
  ReactiveFormsModule,
  NgControl,
  TouchedChangeEvent,
} from '@angular/forms';
import { Subscription } from 'rxjs';
import { Contact } from '../../models/interfaces/contact.interface';
import { getFormValidationErrors } from '../../utils/validator-util';
import { CONTACT_LABELS, VALIDATORS } from '../../shared.constants';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { ValidationService } from '../../services/validation.service';
import { CustomValidators } from '../../validators/custom-validators';
import { MatFormField, MatLabel, MatError } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';

@Component({
  selector:
    'app-contact-field[formControl],' +
    'app-contact-field[formControlName],' +
    'app-contact-field[ngModel]',
  templateUrl: './contact-field.component.html',
  styleUrls: ['./contact-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: ContactFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: ContactFieldComponent,
    },
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatError,
    MatButton,
  ],
})
export class ContactFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor, AfterViewInit
{
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);
  injector = inject(Injector);

  contactForm: UntypedFormGroup;

  @Input() required: boolean = false;
  @Input() minLength: number = 0;

  constants = CONTACT_LABELS;
  validators = VALIDATORS;
  label: string = '';

  constructor() {
    this.contactForm = this.fb.group({
      contacts: this.fb.array([]),
    });
  }

  ngOnInit(): void {
    this.label = wrapLabelRequiredOrOptional(
      this.constants.TITLE,
      this.required
    );
    this.addContact();
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
          this.contactForm.markAllAsTouched();
          this.contactForm.updateValueAndValidity();
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any) {
    const sub = this.contacts.valueChanges.subscribe(onChange);
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.contactForm.disable();
    } else {
      this.contactForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any) {
    if (value) {
      this.contacts.clear();
      value.forEach((contact: Contact) => {
        this.contacts.push(this.createContactGroup(contact));
      });
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl) {
    if (this.contactForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.contactForm);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  createContactGroup(contact?: any): UntypedFormGroup {
    return this.fb.group({
      name: [contact?.name || null, this.validatorsForName()],
      email: [contact?.email || null, this.validatorsForEmail()],
      phone: [contact?.phone || null, this.validatorsForPhone()],
    });
  }

  addContact() {
    this.contacts.push(this.createContactGroup());
  }

  removeContact(index: number) {
    if (this.contacts.length > 1) {
      this.contacts.removeAt(index);
    }
  }

  private validatorsForEmail(): ValidatorFn | ValidatorFn[] | null {
    const validatorFns = [];
    validatorFns.push(Validators.email);
    if (this.required) {
      validatorFns.push(Validators.required);
    }
    return validatorFns;
  }

  private validatorsForPhone(): ValidatorFn | ValidatorFn[] | null {
    const validatorFns: ValidatorFn[] = [];
    validatorFns.push(CustomValidators.phone);
    return validatorFns;
  }

  private validatorsForName(): ValidatorFn | ValidatorFn[] | null {
    const validatorFns = [];
    validatorFns.push(Validators.minLength(this.minLength));
    if (this.required) {
      validatorFns.push(Validators.required);
    }
    return validatorFns;
  }

  get contacts(): UntypedFormArray {
    return this.contactForm.get('contacts') as UntypedFormArray;
  }

  displayContacts(): string[] {
    const contacts = this.contactForm.get('contacts')?.value || [];
    return contacts.map(
      // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
      (contact: any, index: number) =>
        `Contact ${index + 1}:\nName: ${contact.name}\nEmail: ${contact.email}\nPhone: ${contact.phone}`
    );
  }
}
