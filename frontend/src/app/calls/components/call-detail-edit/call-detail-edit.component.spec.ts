import { CallDetailEditComponent } from './call-detail-edit.component';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { LocalStorageService } from '../../../core/services/local-storage.service';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { CALLS } from '../../../shared/mocks/mock-calls';
import { ROUTER_LINKS } from '../../../core/router-links.constants';
import { ViewEnum } from '../../../shared/models/enums/view.enum';
import { OAuthService } from 'angular-oauth2-oidc';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideNativeDateAdapter } from '@angular/material/core';
import { OAuthServiceMock } from '../../../testing/mocks/OAuthService.mock';
import { FORM_STATUS_MESSAGES } from '../../../shared/shared.constants';

describe('CallDetailEditComponent', () => {
  let component: CallDetailEditComponent;
  let fixture: ComponentFixture<CallDetailEditComponent>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockRouter: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockFormBuilder: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockLocalStorageService: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockPermissionService: any;

  beforeEach(async () => {
    mockRouter = { navigate: jasmine.createSpy('navigate') };
    mockFormBuilder = {
      group: jasmine.createSpy('group').and.returnValue(
        new FormGroup({
          id: new FormControl(CALLS[0].id),
          funder: new FormControl(CALLS[0].funder),
          applicationLanguages: new FormControl(CALLS[0].applicationLanguages),
          risId: new FormControl(CALLS[0].risId),
          fundingType: new FormControl(CALLS[0].fundingType),
          partOf: new FormControl(CALLS[0].partOf),
          website: new FormControl(CALLS[0].website),
          name: new FormControl(CALLS[0].name),
          acronym: new FormControl(CALLS[0].acronym),
          description: new FormControl(CALLS[0].description),
          targetGroups: new FormControl(CALLS[0].targetGroups),
          targetGroupDetails: new FormControl(CALLS[0].targetGroupDetails),
          careerStages: new FormControl(CALLS[0].careerStages),
          thematicOrientations: new FormControl(CALLS[0].thematicOrientations),
          eligibleApplicants: new FormControl(CALLS[0].eligibleApplicants),
          contacts: new FormControl(CALLS[0].contacts),
          callStages: new FormControl(CALLS[0].callStages),
          inkindDetails: new FormControl(CALLS[0].inkindDetails),
          overheadDetails: new FormControl(CALLS[0].overheadDetails),
          decisionProcessDetails: new FormControl(
            CALLS[0].decisionProcessDetails
          ),
          reportingPeriodDetails: new FormControl(
            CALLS[0].reportingPeriodDetails
          ),
          projectStartDetails: new FormControl(CALLS[0].projectStartDetails),
          dmpGuidelines: new FormControl(CALLS[0].dmpGuidelines),
          subjects: new FormControl(CALLS[0].subjects),
          minProjectDuration: new FormControl(CALLS[0].minProjectDuration),
          minProjectVolume: new FormControl(CALLS[0].minProjectVolume),
          maxProjectDuration: new FormControl(CALLS[0].maxProjectDuration),
          maxProjectVolume: new FormControl(CALLS[0].maxProjectVolume),
          callVolumeAmount: new FormControl(CALLS[0].callVolumeAmount),
          fullyFunded: new FormControl(CALLS[0].fullyFunded),
          minInkind: new FormControl(CALLS[0].minInkind),
          maxOverhead: new FormControl(CALLS[0].maxOverhead),
          callVolumeProjects: new FormControl(CALLS[0].callVolumeProjects),
          characteristics: new FormControl(CALLS[0].characteristics),
          legalType: new FormControl(CALLS[0].legalType),
          fundingScheme: new FormControl(CALLS[0].fundingScheme),
          decisionProcess: new FormControl(CALLS[0].decisionProcess),
          submissionModes: new FormControl(CALLS[0].submissionModes),
          eligibleApplicantsScope: new FormControl(
            CALLS[0].eligibleApplicantsScope
          ),
          eligibleApplicantsRegions: new FormControl(
            CALLS[0].eligibleApplicantsRegions
          ),
          jointCallPartner: new FormControl(CALLS[0].jointCallPartner),
          dmpRequired: new FormControl(CALLS[0].dmpRequired),
        })
      ),
    };
    mockLocalStorageService = {
      load: jasmine.createSpy('load'),
      save: jasmine.createSpy('save'),
    };
    mockPermissionService = {
      getPermissions: jasmine
        .createSpy('getPermissions')
        .and.returnValue({ canEdit: true, canDelete: true }),
    };

    await TestBed.configureTestingModule({
      imports: [CallDetailEditComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideNativeDateAdapter(),
        { provide: OAuthService, useValue: OAuthServiceMock },
        { provide: Router, useValue: mockRouter },
        { provide: FormBuilder, useValue: mockFormBuilder },
        { provide: LocalStorageService, useValue: mockLocalStorageService },
        { provide: PermissionService, useValue: mockPermissionService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CallDetailEditComponent);
    component = fixture.componentInstance;
    component.call = CALLS[0];
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize permissions and navigate if not authorized', () => {
    mockPermissionService.getPermissions.and.returnValue({ canEdit: false });

    component.ngOnInit();

    expect(mockPermissionService.getPermissions).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith([
      ROUTER_LINKS.NOT_AUTHORIZED,
    ]);
  });

  it('should initialize the form on ngOnInit', () => {
    expect(mockFormBuilder.group).toHaveBeenCalled();
    expect(component.detailsForm).toBeDefined();
  });

  it('should emit save event with form data on onSaveAsDraft', () => {
    spyOn(component.saveAsDraft, 'emit');

    // Make the form valid
    component.detailsForm.setErrors(null);
    Object.keys(component.detailsForm.controls).forEach((key) => {
      component.detailsForm.controls[key].setErrors(null);
    });

    component.onSaveAsDraft();

    expect(component.saveAsDraft.emit).toHaveBeenCalledWith(
      jasmine.any(Object)
    );
    expect(component.submissionErrorMsg).toBe('');
  });

  it('should not emit save event and show error when form is invalid on onSaveAsDraft', () => {
    spyOn(component.saveAsDraft, 'emit');

    // Make form invalid
    component.detailsForm.setErrors({ invalid: true });

    component.onSaveAsDraft();

    expect(component.saveAsDraft.emit).not.toHaveBeenCalled();
    expect(component.submissionErrorMsg).toBe(
      FORM_STATUS_MESSAGES.VALIDATION_ERROR
    );
  });

  it('should emit publish event on onPublish', () => {
    spyOn(component.publish, 'emit');

    // Make the form valid by patching its state
    component.detailsForm.setErrors(null); // Clear form-level errors

    // Mark all controls as valid
    Object.keys(component.detailsForm.controls).forEach((key) => {
      component.detailsForm.controls[key].setErrors(null);
    });

    component.onPublish();

    expect(component.publish.emit).toHaveBeenCalled();
    expect(component.submissionErrorMsg).toBe('');
  });

  it('should not emit publish event and show error when form is invalid on onPublish', () => {
    spyOn(component.publish, 'emit');
    component.detailsForm.markAllAsTouched =
      jasmine.createSpy('markAllAsTouched');

    // Make form invalid
    Object.defineProperty(component.detailsForm, 'valid', {
      get: () => false,
      configurable: true,
    });

    component.onPublish();

    expect(component.detailsForm.markAllAsTouched).toHaveBeenCalled();
    expect(component.publish.emit).not.toHaveBeenCalled();
    expect(component.submissionErrorMsg).toBe(
      FORM_STATUS_MESSAGES.VALIDATION_ERROR
    );
  });

  it('should emit back event on onBack', () => {
    spyOn(component.back, 'emit');
    component.onBack();
    expect(component.back.emit).toHaveBeenCalled();
  });

  it('should emit preview event with ViewEnum.PREVIEW on onPreview', () => {
    spyOn(component.preview, 'emit');
    component.onPreview();
    expect(component.preview.emit).toHaveBeenCalledWith(ViewEnum.PREVIEW);
  });

  it('should load staged changes from local storage on ngOnInit', () => {
    expect(mockLocalStorageService.load).toHaveBeenCalledWith(
      'stagedCallChanges'
    );
  });

  it('should save changes to local storage on navigation to preview', () => {
    component.detailsForm.markAsDirty();

    component.onPreview();

    expect(mockLocalStorageService.save).toHaveBeenCalledWith(
      'stagedCallChanges',
      jasmine.any(Object)
    );
  });
});
