import { DatePipe } from '@angular/common';
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
import { MatDialog } from '@angular/material/dialog';
import {
  ConfirmDialogComponent,
  ConfirmDialogData,
} from '../../../shared/components/confirm-dialog/confirm-dialog.component';
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
import { UsersStore } from '../../signal/users-store';
import { UniversitiesStore } from '../../../universities/signal/universities-store';
import { NotificationService } from '../../../shared/services/notification-service.service';
import {
  REGISTRATION_REQUESTS_CONSTANTS,
  USER_PERMISSIONS_CONSTANTS,
  USER_ROLE_OPTIONS,
} from '../../users.constants';

// Pragmatic email shape check; the backend (@Email) is the source of truth.
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
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
    DatePipe,
    FormsModule,
    ReactiveFormsModule,
    GenericDetailsContainerComponent,
    MatButton,
    MatIconButton,
    MatIcon,
    MatCardTitle,
    SelectFieldComponent,
    SearchSelectComponent,
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
  private universitiesStore = inject(UniversitiesStore);
  private notificationService = inject(NotificationService);
  private dialog = inject(MatDialog);

  protected readonly USER_PERMISSIONS_CONSTANTS = USER_PERMISSIONS_CONSTANTS;
  protected readonly REGISTRATION_REQUESTS_CONSTANTS =
    REGISTRATION_REQUESTS_CONSTANTS;
  protected readonly USER_ROLE_OPTIONS = USER_ROLE_OPTIONS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;

  protected userOptions = computed(() =>
    this.store.keycloakUsers().map((u) => ({
      value: u.id ?? '',
      label: u.email ?? u.username ?? u.id ?? '',
    }))
  );

  // Affiliation is stored as the university acronym (see getUniversityByAcronym).
  // Universities without an acronym cannot be a valid affiliation, so skip them.
  protected affiliationOptions = computed(() =>
    this.universitiesStore
      .entities()
      .filter((u) => !!u.acronym)
      .map((u) => {
        const name = u.name?.[0]?.text;
        return {
          value: u.acronym!,
          label: name ? `${u.acronym} — ${name}` : u.acronym!,
        };
      })
  );

  // Ids of users that already exist in Keycloak. Used to tell apart an existing
  // selection (the control holds a Keycloak id) from a freshly typed email.
  private knownUserIds = computed(
    () => new Set(this.store.keycloakUsers().map((u) => u.id ?? ''))
  );

  protected readonly displayedColumns =
    USER_PERMISSIONS_CONSTANTS.TABLE_COLUMNS.map((column) => column.field);

  protected readonly displayedRegistrationColumns =
    REGISTRATION_REQUESTS_CONSTANTS.TABLE_COLUMNS.map((column) => column.field);

  protected registrationRows = computed(() => this.store.registrationRequests());

  // Set when the form is prefilled from a registration request, so the request
  // can be removed once the corresponding user has been created.
  private pendingRegistrationId = signal<string | null>(null);

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
    this.store.loadRegistrationRequests();
    this.formValidSignal.set(this.detailsForm.valid);
    this.detailsForm.statusChanges.subscribe((status) => {
      this.formValidSignal.set(status === 'VALID');
    });
  }

  onSave(): void {
    if (!this.detailsForm.valid) return;
    const { userId, roles, affiliationId } = this.detailsForm.value;
    const selected = userId!;

    // An existing selection carries a Keycloak user id; anything else is a
    // freshly typed email for which the backend provisions a new account.
    const isExistingUser = this.knownUserIds().has(selected);

    if (!isExistingUser && !EMAIL_PATTERN.test(selected)) {
      this.notificationService.error(
        USER_PERMISSIONS_CONSTANTS.ERRORS.INVALID_EMAIL
      );
      return;
    }

    const save = isExistingUser
      ? this.store.updateUserPermissions(selected, roles ?? [], affiliationId!)
      : this.store.createUserAndUpdatePermissions(
          selected,
          roles ?? [],
          affiliationId!
        );

    save.then((success) => {
      if (success) {
        this.detailsForm.reset();
        this.formVisible.set(false);
        setTimeout(() => this.formVisible.set(true));
        this.store.loadUserPermissions();
        // A new email may have provisioned an account: refresh the picker list.
        if (!isExistingUser) {
          this.store.loadKeycloakUsers();
        }
        // If this creation fulfilled a registration request, remove it now that
        // the account exists. Left untouched on failure so nothing is lost.
        const registrationId = this.pendingRegistrationId();
        if (registrationId) {
          this.store.deleteRegistrationRequest(registrationId);
        }
      }
      this.pendingRegistrationId.set(null);
    });
  }

  onDelete(userId: string): void {
    if (!userId) return;
    const data: ConfirmDialogData = {
      title: USER_PERMISSIONS_CONSTANTS.DELETE_CONFIRM.TITLE,
      message: USER_PERMISSIONS_CONSTANTS.DELETE_CONFIRM.MESSAGE,
      confirmLabel: USER_PERMISSIONS_CONSTANTS.DELETE_CONFIRM.CONFIRM_LABEL,
    };
    this.dialog
      .open(ConfirmDialogComponent, { width: '400px', data })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) {
          this.store.deleteUserPermissions(userId);
        }
      });
  }

  // Prefills the creation form with the requester's email and a role derived
  // from the institution type; the admin then completes affiliation before
  // submitting. The request is deleted on success.
  onCreateUserFromRequest(request: {
    id?: string;
    email?: string;
    kindOfInstitution?: string;
  }): void {
    if (!request.email) return;
    this.pendingRegistrationId.set(request.id ?? null);
    this.detailsForm.reset();
    this.detailsForm.controls.userId.setValue(request.email);
    const role = this.roleForInstitution(request.kindOfInstitution);
    if (role) {
      this.detailsForm.controls.roles.setValue([role]);
    }
  }

  // Maps the institution type captured on the request to a default role.
  // 'Other' (and anything unrecognised) is left for the admin to choose.
  private roleForInstitution(kindOfInstitution?: string): string | null {
    switch ((kindOfInstitution ?? '').trim().toLowerCase()) {
      case 'funder':
        return 'FUNDER';
      case 'research institute':
        return 'ANNOTATOR';
      default:
        return null;
    }
  }

  onRejectRequest(id: string | undefined): void {
    if (!id) return;
    const data: ConfirmDialogData = {
      title: REGISTRATION_REQUESTS_CONSTANTS.REJECT_CONFIRM.TITLE,
      message: REGISTRATION_REQUESTS_CONSTANTS.REJECT_CONFIRM.MESSAGE,
      confirmLabel: REGISTRATION_REQUESTS_CONSTANTS.REJECT_CONFIRM.CONFIRM_LABEL,
    };
    this.dialog
      .open(ConfirmDialogComponent, { width: '400px', data })
      .afterClosed()
      .subscribe((confirmed) => {
        if (confirmed) {
          this.store.deleteRegistrationRequest(id);
        }
      });
  }
}
