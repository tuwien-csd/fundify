import { Component, computed, CUSTOM_ELEMENTS_SCHEMA, inject, signal } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Location } from '@angular/common';
import { BUTTON_LABELS } from '../../../shared/shared.constants';
import { GenericDetailsContainerComponent } from '../../../shared/components/generic-details-container/generic-details-container.component';
import { MatButton } from '@angular/material/button';
import { MatCardTitle } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { SelectFieldComponent } from '../../../shared/components/select-field/select-field.component';
import { SearchSelectComponent } from '../../../shared/components/search-select/search-select.component';
import { TextFieldComponent } from '../../../shared/components/text-field/text-field.component';
import { UsersStore } from '../../signal/users-store';
import {
  USER_PERMISSIONS_CONSTANTS,
  USER_ROLE_OPTIONS,
} from '../../users.constants';
type UserPermissionsForm = {
  userId: FormControl<string | null>;
  roles: FormControl<string[] | null>;
  affiliationId: FormControl<string>;
};

@Component({
  selector: 'app-user-permissions-edit',
  templateUrl: './user-permissions-edit.component.html',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    GenericDetailsContainerComponent,
    MatButton,
    MatCardTitle,
    MatIcon,
    SelectFieldComponent,
    SearchSelectComponent,
    TextFieldComponent,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class UserPermissionsEditComponent {
  private fb = new FormBuilder();
  protected store = inject(UsersStore);
  private location = inject(Location);

  protected readonly USER_PERMISSIONS_CONSTANTS = USER_PERMISSIONS_CONSTANTS;
  protected readonly USER_ROLE_OPTIONS = USER_ROLE_OPTIONS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;

  protected userOptions = computed(() =>
    this.store.keycloakUsers().map((u) => ({
      value: u.id ?? '',
      label: u.email ?? u.username ?? u.id ?? '',
    }))
  );

  formValidSignal = signal(false);

  detailsForm = this.fb.group<UserPermissionsForm>({
    userId: this.fb.control<string | null>(null, [Validators.required]),
    roles: this.fb.control<string[] | null>(null, [Validators.required]),
    affiliationId: this.fb.control('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  constructor() {
    this.store.loadKeycloakUsers();
    this.formValidSignal.set(this.detailsForm.valid);
    this.detailsForm.statusChanges.subscribe((status) => {
      this.formValidSignal.set(status === 'VALID');
    });
  }

  onBack(): void {
    this.location.back();
  }

  onSave(): void {
    if (!this.detailsForm.valid) return;
    const { userId, roles, affiliationId } = this.detailsForm.value;
    this.store
      .updateUserPermissions(userId!, roles ?? [], affiliationId!)
      .then((success) => {
        if (success) {
          this.detailsForm.reset();
        }
      });
  }
}
