import {
  Component,
  computed,
  CUSTOM_ELEMENTS_SCHEMA,
  inject,
  signal,
} from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { BUTTON_LABELS } from '../../../shared/shared.constants';
import { GenericDetailsContainerComponent } from '../../../shared/components/generic-details-container/generic-details-container.component';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCardTitle } from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
} from '@angular/material/table';
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
  styleUrls: ['./user-permissions-edit.component.scss'],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    GenericDetailsContainerComponent,
    MatButton,
    MatIconButton,
    MatIcon,
    MatCardTitle,
    SelectFieldComponent,
    SearchSelectComponent,
    TextFieldComponent,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class UserPermissionsEditComponent {
  private fb = new FormBuilder();
  protected store = inject(UsersStore);

  protected readonly USER_PERMISSIONS_CONSTANTS = USER_PERMISSIONS_CONSTANTS;
  protected readonly USER_ROLE_OPTIONS = USER_ROLE_OPTIONS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;

  protected userOptions = computed(() =>
    this.store.keycloakUsers().map((u) => ({
      value: u.id ?? '',
      label: u.email ?? u.username ?? u.id ?? '',
    }))
  );

  protected readonly displayedColumns =
    USER_PERMISSIONS_CONSTANTS.TABLE_COLUMNS.map((column) => column.field);

  protected permissionRows = computed(() => {
    const emailByUserId = new Map(
      this.store
        .keycloakUsers()
        .map((u) => [u.id ?? '', u.email ?? u.username ?? u.id ?? ''])
    );
    return this.store.userPermissions().map((permission) => ({
      userId: permission.userId ?? '',
      email: emailByUserId.get(permission.userId ?? '') ?? permission.userId,
      roles: (permission.roles ?? []).join(', '),
      affiliationId: permission.affiliationId ?? '',
    }));
  });

  formValidSignal = signal(false);

  // Toggled off/on after a successful save to re-render the custom field
  // components, clearing their internal touched/dirty state (a plain
  // detailsForm.reset() does not reach into them).
  protected formVisible = signal(true);

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
    this.store.loadUserPermissions();
    this.formValidSignal.set(this.detailsForm.valid);
    this.detailsForm.statusChanges.subscribe((status) => {
      this.formValidSignal.set(status === 'VALID');
    });
  }

  onSave(): void {
    if (!this.detailsForm.valid) return;
    const { userId, roles, affiliationId } = this.detailsForm.value;
    this.store
      .updateUserPermissions(userId!, roles ?? [], affiliationId!)
      .then((success) => {
        if (success) {
          this.detailsForm.reset();
          this.formVisible.set(false);
          setTimeout(() => this.formVisible.set(true));
          this.store.loadUserPermissions();
        }
      });
  }

  onDelete(userId: string): void {
    if (!userId) return;
    this.store.deleteUserPermissions(userId);
  }
}
