import { CallDetailPreviewComponent } from './call-detail-preview.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CALLS } from '../../../shared/mocks/mock-calls';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { LocalStorageService } from '../../../core/services/local-storage.service';
import { ViewEnum } from '../../../shared/models/enums/view.enum';
import { CallValidationService } from '../../services/call-validation.service';
import { OAuthService } from 'angular-oauth2-oidc';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideNativeDateAdapter } from '@angular/material/core';
import { OAuthServiceMock } from '../../../testing/mocks/OAuthService.mock';
import { MatDialog } from '@angular/material/dialog';
import { CallVersionHistoryDialogComponent } from '../call-version-history-dialog/call-version-history-dialog.component';

describe('CallDetailPreviewComponent', () => {
  let component: CallDetailPreviewComponent;
  let fixture: ComponentFixture<CallDetailPreviewComponent>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockLocalStorageService: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockPermissionService: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockValidationService: any;

  beforeEach(async () => {
    mockLocalStorageService = {
      load: jasmine.createSpy('load'),
      save: jasmine.createSpy('save'),
    };
    mockPermissionService = {
      getPermissions: jasmine
        .createSpy('getPermissions')
        .and.returnValue({ canEdit: true, canDelete: true }),
    };
    mockValidationService = { validate: jasmine.createSpy('validate') };
    mockValidationService.validate.and.returnValue({
      valid: true,
      message: '',
    });

    await TestBed.configureTestingModule({
      imports: [CallDetailPreviewComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideNativeDateAdapter(),
        { provide: OAuthService, useValue: OAuthServiceMock },
        { provide: LocalStorageService, useValue: mockLocalStorageService },
        { provide: PermissionService, useValue: mockPermissionService },
        { provide: CallValidationService, useValue: mockValidationService },
        { provide: MatDialog, useValue: { open: jasmine.createSpy('open') } },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CallDetailPreviewComponent);
    component = fixture.componentInstance;
    component.call = CALLS[0];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load staged changes and generate call details on ngOnInit', () => {
    expect(mockLocalStorageService.load).toHaveBeenCalledWith(
      'stagedCallChanges'
    );
    expect(component.callDetails.length).toBeGreaterThan(0);
  });

  it('should initialize permissions on ngOnInit', () => {
    expect(mockPermissionService.getPermissions).toHaveBeenCalled();
  });

  it('should emit back event on onBack', () => {
    spyOn(component.back, 'emit');
    component.onBack();
    expect(component.back.emit).toHaveBeenCalled();
  });

  it('should emit publish event on onPublish', () => {
    spyOn(component.publish, 'emit');
    component.onPublish();
    expect(component.publish.emit).toHaveBeenCalled();
  });

  it('should emit edit event with ViewEnum.EDIT on onEdit', () => {
    spyOn(component.edit, 'emit');
    component.onEdit();
    expect(component.edit.emit).toHaveBeenCalledWith(ViewEnum.EDIT);
  });

  it('should open CallVersionHistoryDialog on onShowHistory', () => {
    const dialog = TestBed.inject(MatDialog);
    component.call = { ...CALLS[0], id: 'test-id' };
    component.onShowHistory();
    expect(dialog.open).toHaveBeenCalledWith(
      CallVersionHistoryDialogComponent,
      jasmine.objectContaining({ width: '680px' })
    );
  });
});
