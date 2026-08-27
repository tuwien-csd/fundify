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
import { TextFieldComponent } from '../../../shared/components/text-field/text-field.component';
import { UsersStore } from '../../signal/users-store';
import { UniversitiesStore } from '../../../universities/signal/universities-store';
import { FundersStore } from '../../../funders/signal/funders-store';
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
  firstName: FormControl<string>;
  lastName: FormControl<string>;
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
  private universitiesStore = inject(UniversitiesStore);
  private fundersStore = inject(FundersStore);
  private notificationService = inject(NotificationService);
  private dialog = inject(MatDialog);

  protected readonly USER_PERMISSIONS_CONSTANTS = USER_PERMISSIONS_CONSTANTS;
  protected readonly REGISTRATION_REQUESTS_CONSTANTS =
    REGISTRATION_REQUESTS_CONSTANTS;
  protected readonly USER_ROLE_OPTIONS = USER_ROLE_OPTIONS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;

  protected userOptions = computed(() =>
    this.store.keycloakUsers().map((u) => {
      const identifier = u.email ?? u.username ?? u.id ?? '';
      const name = [u.firstName, u.lastName].filter(Boolean).join(' ');
      return {
        value: u.id ?? '',
        // The name is part of the label so the picker can be searched by it too.
        label: name ? `${name} — ${identifier}` : identifier,
      };
    })
  );

  // Affiliation is stored as the institution acronym and matched case-insensitively
  // against both universities and funders (see BasePermissionServiceImpl), so both
  // are offered here. Institutions without an acronym cannot be a valid affiliation,
  // so they are skipped; an acronym shared by a university and a funder is listed once.
  protected affiliationOptions = computed(() => {
    const institutions = [
      ...this.universitiesStore.entities(),
      ...this.fundersStore.entities(),
    ];
    const optionsByAcronym = new Map<string, { value: string; label: string }>();
    for (const institution of institutions) {
      const acronym = institution.acronym;
      if (!acronym) continue;
      const key = acronym.toLowerCase();
      if (optionsByAcronym.has(key)) continue;
      const name = institution.name?.[0]?.text;
      optionsByAcronym.set(key, {
        value: acronym,
        label: name ? `${acronym} — ${name}` : acronym,
      });
    }
    return [...optionsByAcronym.values()].sort((a, b) =>
      a.label.localeCompare(b.label)
    );
  });

  // Ids of users that already exist in Keycloak. Used to tell apart an existing
  // selection (the control holds a Keycloak id) from a freshly typed email.
  private knownUserIds = computed(
    () => new Set(this.store.keycloakUsers().map((u) => u.id ?? ''))
  );

  // Current permissions per user id, used to prefill the form when an existing
  // user is selected.
  private permissionsByUserId = computed(
    () =>
      new Map(
        this.store
          .userPermissions()
          .map((permission) => [permission.userId ?? '', permission])
      )
  );

  // Keycloak users per id. Names live in Keycloak only (there is no copy in our
  // own DB), so both the table and the read-only form fields read them from here.
  private usersByUserId = computed(
    () => new Map(this.store.keycloakUsers().map((u) => [u.id ?? '', u]))
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
    const usersByUserId = this.usersByUserId();
    return this.store
      .userPermissions()
      .map((permission) => {
        const user = usersByUserId.get(permission.userId ?? '');
        return {
          userId: permission.userId ?? '',
          email:
            user?.email ?? user?.username ?? user?.id ?? permission.userId,
          firstName: user?.firstName ?? '',
          lastName: user?.lastName ?? '',
          roles: (permission.roles ?? []).join(', '),
          affiliationId: permission.affiliationId ?? '',
        };
      })
      .sort(
        (a, b) =>
          a.affiliationId.localeCompare(b.affiliationId) ||
          (a.email ?? '').localeCompare(b.email ?? '')
      );
  });

  formValidSignal = signal(false);

  // Toggled off/on after a successful save to re-render the custom field
  // components, clearing their internal touched/dirty state (a plain
  // detailsForm.reset() does not reach into them).
  protected formVisible = signal(true);

  detailsForm = this.fb.group<UserPermissionsForm>({
    userId: this.fb.control<string | null>(null, [Validators.required]),
    firstName: this.fb.control('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    lastName: this.fb.control('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
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
    this.detailsForm.controls.userId.valueChanges.subscribe((userId) =>
      this.prefillFromSelectedUser(userId)
    );
  }

  // Selecting an existing user shows their current roles and affiliation as the
  // starting point, so the admin edits from the actual state instead of a blank
  // form. Anything else (a cleared field, a freshly typed email) starts empty.
  private prefillFromSelectedUser(userId: string | null): void {
    const permission = userId
      ? this.permissionsByUserId().get(userId)
      : undefined;
    this.detailsForm.controls.roles.setValue(
      permission?.roles?.length ? [...permission.roles] : null
    );
    this.detailsForm.controls.affiliationId.setValue(
      permission?.affiliationId ?? ''
    );

    // Given name and surname are set once, when the account is provisioned, and
    // cannot be changed afterwards: for an existing user they are shown as-is and
    // disabled. Disabling also keeps them out of form.value and out of validity,
    // so the required validators never block a roles/affiliation update.
    const existingUser = userId ? this.usersByUserId().get(userId) : undefined;
    const { firstName, lastName } = this.detailsForm.controls;
    if (existingUser) {
      firstName.setValue(existingUser.firstName ?? '');
      lastName.setValue(existingUser.lastName ?? '');
      firstName.disable({ emitEvent: false });
      lastName.disable({ emitEvent: false });
    } else {
      firstName.setValue('');
      lastName.setValue('');
      firstName.enable({ emitEvent: false });
      lastName.enable({ emitEvent: false });
    }
  }

  onSave(): void {
    if (!this.detailsForm.valid) return;
    const { userId, firstName, lastName, roles, affiliationId } =
      this.detailsForm.value;
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
          firstName ?? '',
          lastName ?? '',
          roles ?? [],
          affiliationId!
        );

    save.then((success) => {
      if (success) {
        this.detailsForm.reset();
        // reset() does not clear the disabled state, so the next entry would
        // otherwise start with locked (and therefore unfillable) name fields.
        this.detailsForm.controls.firstName.enable({ emitEvent: false });
        this.detailsForm.controls.lastName.enable({ emitEvent: false });
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
    firstName?: string;
    lastName?: string;
    email?: string;
    kindOfInstitution?: string;
  }): void {
    if (!request.email) return;
    this.pendingRegistrationId.set(request.id ?? null);
    this.detailsForm.reset();
    // Setting userId runs the prefill, which enables the name controls because a
    // requester's email is never a known Keycloak id. The names the requester
    // gave are filled in afterwards and stay editable until the account exists.
    this.detailsForm.controls.userId.setValue(request.email);
    this.detailsForm.controls.firstName.setValue(request.firstName ?? '');
    this.detailsForm.controls.lastName.setValue(request.lastName ?? '');
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
