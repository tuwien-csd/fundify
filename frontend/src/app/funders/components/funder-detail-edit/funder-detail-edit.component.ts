import {
  Component,
  computed,
  CUSTOM_ELEMENTS_SCHEMA,
  effect,
  EventEmitter,
  inject,
  input,
  Output,
  signal,
} from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { ViewEnum } from '../../../shared/models/enums/view.enum';
import { BUTTON_LABELS } from '../../../shared/shared.constants';
import { FUNDER_DETAILS_CONSTANTS } from '../../funders.constants';
import {
  FunderCreateWebModel,
  FunderWebModel,
} from '../../models/funder.interface';
import { GenericDetailsContainerComponent } from '../../../shared/components/generic-details-container/generic-details-container.component';
import { MatCardTitle } from '@angular/material/card';
import { TextFieldComponent } from '../../../shared/components/text-field/text-field.component';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import {
  TranslatedTextFieldFormFactory,
  TranslatedTextFieldV2Component,
} from '../../../shared/components/translated-text-field/v2/translated-text-field-v2/translated-text-field-v2.component';
import { FundersStore } from '../../signal/funders-store';
import { Location } from '@angular/common';
import { Router } from '@angular/router';
import { ROUTER_LINKS } from '../../../core/router-links.constants';
import { MatCheckbox } from '@angular/material/checkbox';
import {
  AddressFormComponent,
  AddressFormFactory,
} from '../../../shared/components/address-form/address-form.component';

type FunderDetailsForm = {
  name: FormArray;
  acronym: FormControl<string>;
  risId: FormControl<string | null | undefined>;
  crossRefDoi: FormControl<string | null | undefined>;
  postAddress: FormGroup;
  phone: FormControl<string | null | undefined>;
  website: FormControl<string>;
  submissionSystem: FormControl<string | null | undefined>;
  externallyAdministered: FormControl<boolean | null | undefined>;
};

@Component({
  selector: 'app-funder-detail-edit',
  templateUrl: './funder-detail-edit.component.html',
  styleUrls: ['./funder-detail-edit.component.scss'],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    GenericDetailsContainerComponent,
    MatCardTitle,
    TextFieldComponent,
    MatIcon,
    MatButton,
    TranslatedTextFieldV2Component,
    MatCheckbox,
    AddressFormComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class FunderDetailEditComponent {
  private fb = new FormBuilder();
  private transTextFieldFormFactory = inject(TranslatedTextFieldFormFactory);
  private addressFormFactory = inject(AddressFormFactory);
  private store = inject(FundersStore);
  private location = inject(Location);
  private router = inject(Router);

  protected readonly FUNDER_DETAILS_CONSTANTS = FUNDER_DETAILS_CONSTANTS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;

  funder = input<FunderWebModel | undefined | null>();
  @Output() save = new EventEmitter<void>();
  @Output() back = new EventEmitter<void>();
  @Output() validate = new EventEmitter();
  @Output() preview = new EventEmitter<ViewEnum>();

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
      this.funder()?.name
    );
  });

  addressForm = computed(() => {
    return this.addressFormFactory.buildFormGroup(this.funder()?.postAddress);
  });

  formValidSignal = signal(false);

  detailsForm = computed(() => {
    return this.fb.group<FunderDetailsForm>({
      name: this.nameFormArray(),
      acronym: this.fb.control(this.funder()?.acronym ?? '', {
        nonNullable: true,
        validators: [Validators.pattern(/\S/)],
      }),
      crossRefDoi: this.fb.control(this.funder()?.crossRefDoi),
      risId: this.fb.control(this.funder()?.risId),
      postAddress: this.addressForm(),
      phone: this.fb.control(this.funder()?.phone),
      website: this.fb.control(this.funder()?.website ?? '', {
        nonNullable: true,
        validators: [Validators.pattern(/\S/)],
      }),
      submissionSystem: this.fb.control(this.funder()?.submissionSystem),
      externallyAdministered: this.fb.control(
          this.funder()?.externallyAdministered
      ),
    });
  });

  formToFunderCreateWebModelMapper(
    form: ReturnType<typeof this.detailsForm>['value']
  ): FunderCreateWebModel {
    return {
      // form is already validated
      name: form.name,
      acronym: form.acronym ?? '',
      postAddress: form.postAddress,
      phone: form.phone ?? undefined,
      crossRefDoi: form.crossRefDoi ?? undefined,
      website: form.website ?? '',
      submissionSystem: form.submissionSystem ?? undefined,
      risId: form.risId ?? undefined,
      identifiers: undefined,
      externallyAdministered: form.externallyAdministered ?? false,
    };
  }

  formToFunderWebModelMapper(
    form: ReturnType<typeof this.detailsForm>['value']
  ): FunderWebModel {
    return {
      id: this.funder()?.id ?? '',
      name: form.name,
      acronym: form.acronym ?? '',
      postAddress: form.postAddress,
      phone: form.phone ?? undefined,
      crossRefDoi: form.crossRefDoi ?? undefined,
      website: form.website ?? '',
      submissionSystem: form.submissionSystem ?? undefined,
      risId: form.risId ?? undefined,
      identifiers: this.funder()?.identifiers ?? undefined,
      externallyAdministered: form.externallyAdministered ?? false,
    };
  }

  onBack(): void {
    this.location.back();
  }

  onSave(): void {
    if (this.funder()?.id) {
      this.updateFunder();
    } else {
      this.createFunder();
    }
  }

  private createFunder(): void {
    this.store
      .addFunder(
        this.formToFunderCreateWebModelMapper(this.detailsForm().value)
      )
      .then((response) => {
        if (response && response.id) {
          this.router.navigate([
            `${ROUTER_LINKS.INSTITUTIONS}/${ROUTER_LINKS.FUNDERS}`,
            response.id,
          ]);
        }
      });
  }

  private updateFunder(): void {
    this.store
      .updateFunder(this.formToFunderWebModelMapper(this.detailsForm().value))
      .then((response) => {
        if (response && response.id) {
          this.router.navigate([
            `${ROUTER_LINKS.INSTITUTIONS}/${ROUTER_LINKS.FUNDERS}`,
            response.id,
          ]);
        }
      });
  }
}
