import { TestBed } from '@angular/core/testing';
import { PermissionService } from './permission.service';
import { EntryOriginEnum } from '../../../shared/models/enums/entry-origin.enum';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';
import { ActionPermissions } from '../../models/ActionPermissions';
import { PermissionContext } from '../../models/enums/permission-context.enum';
import { AuthService } from './auth.service';

describe('PermissionService', () => {
  let service: PermissionService;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  const ADMIN_PERMISSIONS: ActionPermissions = {
    canEdit: true,
    canDelete: true,
  };
  const DRAFT_OWNER_PERMISSIONS: ActionPermissions = {
    canEdit: true,
    canDelete: true,
  };
  const READONLY_PERMISSIONS: ActionPermissions = {
    canEdit: false,
    canDelete: false,
  };

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', [
      'isAdmin',
      'isFunder',
      'userAffiliationId',
    ]);
    await TestBed.configureTestingModule({
      providers: [{ provide: AuthService, useValue: mockAuthService }],
    }).compileComponents();

    service = TestBed.inject(PermissionService);
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  describe('getPermissions', () => {
    it('should return READONLY_PERMISSIONS for published entries', () => {
      const permissions = service.getPermissions(
        EntryOriginEnum.ENDPOINT,
        PublicationStatusEnum.PUBLISHED,
        '1234'
      );
      expect(permissions).toEqual(READONLY_PERMISSIONS);
    });

    it('should return DRAFT_OWNER_PERMISSIONS for new entries', () => {
      const permissions = service.getPermissions(
        EntryOriginEnum.REFOP,
        PublicationStatusEnum.DRAFT,
        '1234',
        PermissionContext.NEW
      );
      expect(permissions).toEqual(DRAFT_OWNER_PERMISSIONS);
    });

    it('should return ADMIN_PERMISSIONS for admin users', () => {
      mockAuthService.isAdmin.and.returnValue(true);
      const permissions = service.getPermissions(
        EntryOriginEnum.REFOP,
        PublicationStatusEnum.DRAFT,
        '1234'
      );
      expect(permissions).toEqual(ADMIN_PERMISSIONS);
    });

    it('should return DRAFT_OWNER_PERMISSIONS for draft owners', () => {
      mockAuthService.userAffiliationId.and.returnValue('1234');
      mockAuthService.isFunder.and.returnValue(true);
      const permissions = service.getPermissions(
        EntryOriginEnum.REFOP,
        PublicationStatusEnum.DRAFT,
        '1234'
      );
      expect(permissions).toEqual(DRAFT_OWNER_PERMISSIONS);
    });

    it('should return READONLY_PERMISSIONS for non-owners', () => {
      const permissions = service.getPermissions(
        EntryOriginEnum.REFOP,
        PublicationStatusEnum.DRAFT,
        '1234'
      );
      expect(permissions).toEqual(READONLY_PERMISSIONS);
    });
  });
});
