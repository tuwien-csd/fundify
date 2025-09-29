import { inject, Injectable } from '@angular/core';
import { EntryOriginEnum } from '../../../shared/models/enums/entry-origin.enum';
import { ActionPermissions } from '../../models/ActionPermissions';
import { PermissionContext } from '../../models/enums/permission-context.enum';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';
import { AuthService } from './auth.service';
import { equalsIgnoreCase } from '../../../shared/utils/string-utils';

const ADMIN_PERMISSIONS: ActionPermissions = { canEdit: true, canDelete: true };
const DRAFT_OWNER_PERMISSIONS: ActionPermissions = {
  canEdit: true,
  canDelete: true,
};
const READONLY_PERMISSIONS: ActionPermissions = {
  canEdit: false,
  canDelete: false,
};

@Injectable({
  providedIn: 'root',
})
export class PermissionService {
  authService = inject(AuthService);

  getUserAffiliationId() {
    return this.authService.userAffiliationId();
  }
  getPermissions(
    origin: EntryOriginEnum,
    status: PublicationStatusEnum,
    orgunitId: string,
    context?: PermissionContext
  ): ActionPermissions {
    if (origin === EntryOriginEnum.ENDPOINT) {
      return READONLY_PERMISSIONS;
    }
    // if the entry is newly created, the entry is a draft, independent of user
    if (context === PermissionContext.NEW) {
      return DRAFT_OWNER_PERMISSIONS;
    }
    // if the user is an admin, the user has full permissions (same as draft owner)
    if (this.authService.isAdmin()) {
      return ADMIN_PERMISSIONS;
    }
    // if the user is the owner of the call, the user has full permissions
    if (
      equalsIgnoreCase(
        this.authService.userAffiliationId(),
        orgunitId?.toLowerCase()
      )
    ) {
      return DRAFT_OWNER_PERMISSIONS;
    }
    // if the user is not the owner of the draft, the entry is readonly
    return READONLY_PERMISSIONS;
  }

  canEdit(
    origin: EntryOriginEnum,
    status: PublicationStatusEnum,
    funderId: string,
    context?: PermissionContext
  ): boolean {
    return this.getPermissions(origin, status, funderId, context).canEdit;
  }

  canDelete(
    origin: EntryOriginEnum,
    status: PublicationStatusEnum,
    funderId: string,
    context?: PermissionContext
  ): boolean {
    return this.getPermissions(origin, status, funderId, context).canDelete;
  }
}
