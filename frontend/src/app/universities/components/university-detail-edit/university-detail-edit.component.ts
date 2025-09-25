import {
  Component,
  computed,
  CUSTOM_ELEMENTS_SCHEMA,
  effect,
  inject,
  input,
  signal,
} from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { BUTTON_LABELS } from '../../../shared/shared.constants';
import {
  TranslatedTextFieldFormFactory,
  TranslatedTextFieldV2Component,
} from '../../../shared/components/translated-text-field/v2/translated-text-field-v2/translated-text-field-v2.component';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { ROUTER_LINKS } from '../../../core/router-links.constants';
import { UniversitiesStore } from '../../signal/universities-store';
import { UNIVERSITY_DETAILS_CONSTANTS } from '../../universities.constants';
import {
  UniversityCreateWebModel,
  UniversityWebModel,
} from '../../models/university.interface';
import { GenericDetailsContainerComponent } from '../../../shared/components/generic-details-container/generic-details-container.component';
import { MatButton } from '@angular/material/button';
import { MatCardTitle } from '@angular/material/card';
import { TextFieldComponent } from '../../../shared/components/text-field/text-field.component';
import { MatIcon } from '@angular/material/icon';
import {
  AddressFormComponent,
  AddressFormFactory,
} from '../../../shared/components/address-form/address-form.component';

type UniversityDetailsForm = {
  name: FormArray;
  risId: FormControl<string | null | undefined>;
  acronym: FormControl<string | null | undefined>;
  emailDomain: FormControl<string | null | undefined>;
  website: FormControl<string>;
  submissionSystem: FormControl<string | null | undefined>;
  postAddress: FormGroup;
  phone: FormControl<string | null | undefined>;
  crossRefDoi: FormControl<string | null | undefined>;
};

@Component({
  selector: 'app-university-detail-edit',
  templateUrl: './university-detail-edit.component.html',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    GenericDetailsContainerComponent,
    MatButton,
    MatCardTitle,
    MatIcon,
    TextFieldComponent,
    TranslatedTextFieldV2Component,
    AddressFormComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class UniversityDetailEditComponent {
  private fb = new FormBuilder();
  private transTextFieldFormFactory = inject(TranslatedTextFieldFormFactory);
  private addressFormFactory = inject(AddressFormFactory);
  private store = inject(UniversitiesStore);
  private location = inject(Location);
  private router = inject(Router);

  protected readonly UNIVERSITY_DETAILS_CONSTANTS =
    UNIVERSITY_DETAILS_CONSTANTS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;

  university = input<UniversityWebModel | undefined | null>();

  constructor() {
    effect(() => {
      const formGroup = this.detailsForm();
      this.formValidSignal.set(formGroup.valid);

      formGroup.statusChanges.subscribe((status) => {
        this.formValidSignal.set(status === 'VALID');
      });
    });
  }

  nameFormArray = computed(() => {
    return this.transTextFieldFormFactory.createFormArray(
      true,
      this.university()?.name
    );
  });

  addressForm = computed(() => {
    return this.addressFormFactory.buildFormGroup(
      this.university()?.postAddress
    );
  });
  formValidSignal = signal(false);

  detailsForm = computed(() => {
    return this.fb.group<UniversityDetailsForm>({
      name: this.nameFormArray(),
      risId: this.fb.control(this.university()?.risId),
      acronym: this.fb.control(this.university()?.acronym),
      emailDomain: this.fb.control(this.university()?.emailDomain),
      website: this.fb.control(this.university()?.website ?? '', {
        nonNullable: true,
      }),
      submissionSystem: this.fb.control(this.university()?.submissionSystem),
      postAddress: this.addressForm(),
      phone: this.fb.control(this.university()?.phone),
      crossRefDoi: this.fb.control(this.university()?.crossRefDoi),
    });
  });

  formToUniversityCreateWebModelMapper(
    form: ReturnType<typeof this.detailsForm>['value']
  ): UniversityCreateWebModel {
    return {
      // form is already validated
      name: form.name,
      acronym: form.acronym ?? undefined,
      emailDomain: form.emailDomain ?? undefined,
      website: form.website ?? '',
      risId: form.risId ?? undefined,
      submissionSystem: form.submissionSystem ?? undefined,
      postAddress: form.postAddress,
      phone: form.phone ?? undefined,
      crossRefDoi: form.crossRefDoi ?? undefined,
    };
  }

  formToUniversityWebModelMapper(
    form: ReturnType<typeof this.detailsForm>['value']
  ): UniversityWebModel {
    return {
      id: this.university()?.id ?? '',
      name: form.name,
      acronym: form.acronym ?? undefined,
      website: form.website ?? undefined,
      emailDomain: form.emailDomain ?? undefined,
      risId: this.university()?.risId ?? undefined,
      submissionSystem: form.submissionSystem ?? undefined,
      postAddress: form.postAddress,
      phone: form.phone ?? undefined,
      crossRefDoi: form.crossRefDoi ?? undefined,
    };
  }

  onBack(): void {
    this.location.back();
  }

  onSave(): void {
    if (this.university()?.id) {
      this.updateUniversity();
    } else {
      this.createUniversity();
    }
  }

  private createUniversity(): void {
    this.store
      .addUniversity(
        this.formToUniversityCreateWebModelMapper(this.detailsForm().value)
      )
      .then((response) => {
        if (response && response.id) {
          this.router.navigate([
            `${ROUTER_LINKS.INSTITUTIONS}/${ROUTER_LINKS.UNIVERSITIES}`,
            response.id,
          ]);
        }
      });
  }

  private updateUniversity(): void {
    this.store
      .updateUniversity(
        this.formToUniversityWebModelMapper(this.detailsForm().value)
      )
      .then((response) => {
        if (response && response.id) {
          this.router.navigate([
            `${ROUTER_LINKS.INSTITUTIONS}/${ROUTER_LINKS.UNIVERSITIES}`,
            response.id,
          ]);
        }
      });
  }
}
