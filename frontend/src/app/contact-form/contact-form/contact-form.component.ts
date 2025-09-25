import {
  ChangeDetectionStrategy,
  Component,
  inject,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { TextFieldComponent } from '../../shared/components/text-field/text-field.component';
import {
  CONTACT_FORM_CATEGORIES,
  CONTACT_FORM_CONSTANTS,
  ContactFormCategories,
} from '../contact-form-constants';
import { SingleChoiceFieldComponent } from '../../shared/components/single-choice-field/single-choice-field.component';
import { InstitutionKindEnum } from '../../shared/models/enums/institutionKind.enum';
import { TextAreaFieldComponent } from '../../shared/components/text-area-field/text-area-field.component';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { SelectFieldComponent } from '../../shared/components/select-field/select-field.component';
import { ContactFormService, TicketCreate } from '../contact-form.service';
import { MatProgressSpinner } from '@angular/material/progress-spinner';

type ContactForm = {
  subject: FormControl<string>;
  category: FormControl<ContactFormCategories | null>;
  name: FormControl<string>;
  email: FormControl<string>;
  kindOfInstitution: FormControl<typeof InstitutionKindEnum | null>;
  message: FormControl<string>;
};

@Component({
  selector: 'app-contact-form',
  imports: [
    ReactiveFormsModule,
    SingleChoiceFieldComponent,
    TextFieldComponent,
    TextAreaFieldComponent,
    MatButton,
    MatIcon,
    SelectFieldComponent,
    MatProgressSpinner,
  ],
  template: `
    @if (formSubmissionWasSuccessful()) {
      <div
        class="flex flex-col items-center justify-center h-full px-4 text-center"
        role="status"
        aria-live="polite"
      >
        <!-- Success Icon -->
        <mat-icon class="mb-6" aria-hidden="true"> check_circle </mat-icon>

        <!-- Success Title -->
        <div class="text-3xl font-bold mb-3">
          {{ CONTACT_FORM_CONSTANTS.SUCCESS_MESSAGE.TITLE }}
        </div>

        <!-- Success Body -->
        <p class="text-lg text-gray-600 max-w-prose">
          {{ CONTACT_FORM_CONSTANTS.SUCCESS_MESSAGE.BODY }}
        </p>
      </div>
    } @else {
      <form [formGroup]="contactForm">
        <app-text-field
          formControlName="subject"
          [label]="CONTACT_FORM_CONSTANTS.SUBJECT.LABEL"
          [textLabel]="CONTACT_FORM_CONSTANTS.SUBJECT.LABEL"
          [placeholder]="CONTACT_FORM_CONSTANTS.SUBJECT.PLACEHOLDER"
          [required]="true"
        />
        <app-select-field
          formControlName="category"
          [label]="CONTACT_FORM_CONSTANTS.CATEGORY.LABEL"
          [textLabel]="CONTACT_FORM_CONSTANTS.CATEGORY.LABEL"
          [options]="CONTACT_FORM_CATEGORIES"
          [required]="true"
        />

        <app-text-field
          formControlName="name"
          [label]="CONTACT_FORM_CONSTANTS.NAME.LABEL"
          [textLabel]="CONTACT_FORM_CONSTANTS.NAME.LABEL"
          [placeholder]="CONTACT_FORM_CONSTANTS.NAME.PLACEHOLDER"
          [required]="true"
        />
        <app-text-field
          formControlName="email"
          [label]="CONTACT_FORM_CONSTANTS.EMAIL.LABEL"
          [textLabel]="CONTACT_FORM_CONSTANTS.EMAIL.LABEL"
          [placeholder]="CONTACT_FORM_CONSTANTS.EMAIL.PLACEHOLDER"
          [required]="true"
        />

        <div>
          <app-single-choice-field
            formControlName="kindOfInstitution"
            [required]="false"
            [type]="InstitutionKindEnum"
            [label]="CONTACT_FORM_CONSTANTS.KIND_OF_INSTITUTION.LABEL"
          />
        </div>
        <app-text-area-field
          [label]="CONTACT_FORM_CONSTANTS.MESSAGE.LABEL"
          [textLabel]="CONTACT_FORM_CONSTANTS.MESSAGE.LABEL"
          [required]="true"
          [placeholder]="CONTACT_FORM_CONSTANTS.MESSAGE.PLACEHOLDER"
          [maxlength]="messageMaxLength"
          formControlName="message"
        />
        @if (this.contactFormService.isPending()) {
          <div class="w-full flex flex-row justify-center">
            <mat-progress-spinner
              class="button-spinner"
              mode="indeterminate"
              [diameter]="50"
              [strokeWidth]="3"
            />
          </div>
        } @else {
          <button
            (click)="onSubmit()"
            [disabled]="!this.contactForm.valid"
            mat-raised-button
            class="form-item-button"
            color="primary"
          >
            <mat-icon>send</mat-icon>
            {{ CONTACT_FORM_CONSTANTS.SUBMIT_BUTTON.LABEL }}
          </button>
        }
      </form>
    }
  `,
  styles: ``,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContactFormComponent {
  private readonly fb = new FormBuilder();
  readonly messageMaxLength = 2000;
  readonly contactFormService = inject(ContactFormService);
  formSubmissionWasSuccessful = signal(false);
  contactForm: FormGroup<ContactForm> = this.fb.group<ContactForm>({
    subject: new FormControl('', {
      validators: [Validators.required],
      nonNullable: true,
    }),
    category: new FormControl(null, {
      validators: [Validators.required],
    }),
    name: new FormControl('', {
      validators: [Validators.required],
      nonNullable: true,
    }),
    email: new FormControl('', {
      validators: [Validators.required, Validators.email],
      nonNullable: true,
    }),
    kindOfInstitution: new FormControl(null),
    message: new FormControl('', {
      validators: [
        Validators.required,
        Validators.maxLength(this.messageMaxLength),
      ],
      nonNullable: true,
    }),
  });

  onSubmit() {
    if (this.contactForm.valid) {
      this.contactFormService
        .submitForm(this.contactForm.value as TicketCreate) //if the form is valid, the cast should not cause any issues
        .then((isSuccess) => {
          if (isSuccess) this.formSubmissionWasSuccessful.set(true);
        });
    }
  }

  protected readonly CONTACT_FORM_CONSTANTS = CONTACT_FORM_CONSTANTS;
  protected readonly InstitutionKindEnum = InstitutionKindEnum;
  protected readonly CONTACT_FORM_CATEGORIES = CONTACT_FORM_CATEGORIES;
}
