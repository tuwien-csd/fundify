import { ProgramDetailEditComponent } from './program-detail-edit.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { LocalStorageService } from '../../../core/services/local-storage.service';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { PROGRAMS } from '../../../shared/mocks/mock-programs';
import { ROUTER_LINKS } from '../../../core/router-links.constants';
import { ViewEnum } from '../../../shared/models/enums/view.enum';
import { OAuthService } from 'angular-oauth2-oidc';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideNativeDateAdapter } from '@angular/material/core';
import { DateRange } from '../../../shared/models/interfaces/date-range.interface';
import { OAuthServiceMock } from '../../../testing/mocks/OAuthService.mock';

describe('ProgramDetailEditComponent', () => {
  let component: ProgramDetailEditComponent;
  let fixture: ComponentFixture<ProgramDetailEditComponent>;
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
          name: new FormControl(PROGRAMS[0].name),
          acronym: new FormControl(PROGRAMS[0].acronym),
          programTracks: new FormControl(PROGRAMS[0].programTracks),
          duration: new FormControl<DateRange>({
            start: '',
            end: '',
          }),
          targetGroups: new FormControl(PROGRAMS[0].targetGroups),
          careerStages: new FormControl(PROGRAMS[0].careerStages),
          description: new FormControl(PROGRAMS[0].description),
          characteristics: new FormControl(PROGRAMS[0].characteristics),
          fundingScheme: new FormControl(PROGRAMS[0].fundingScheme),
          legalType: new FormControl(PROGRAMS[0].legalType),
          website: new FormControl(PROGRAMS[0].website),
          subjects: new FormControl(PROGRAMS[0].subjects),
          funder: new FormControl(PROGRAMS[0].funder),
        })
      ),
    };
    mockLocalStorageService = {
      load: jasmine.createSpy('load'),
      save: jasmine.createSpy('save'),
      remove: jasmine.createSpy('remove'),
    };
    mockPermissionService = {
      getPermissions: jasmine
        .createSpy('getPermissions')
        .and.returnValue({ canEdit: false, canDelete: true }),
    };

    await TestBed.configureTestingModule({
      imports: [ProgramDetailEditComponent],
      providers: [
        provideNativeDateAdapter(),
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: mockRouter },
        { provide: FormBuilder, useValue: mockFormBuilder },
        { provide: LocalStorageService, useValue: mockLocalStorageService },
        { provide: PermissionService, useValue: mockPermissionService },
        { provide: OAuthService, useValue: OAuthServiceMock },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgramDetailEditComponent);
    component = fixture.componentInstance;
    component.program = PROGRAMS[0];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize permissions and navigate if not authorized', () => {
    mockPermissionService.getPermissions.and.returnValue({ canEdit: false });
    expect(mockPermissionService.getPermissions).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith([
      ROUTER_LINKS.NOT_AUTHORIZED,
    ]);
  });

  it('should initialize the form on ngOnInit', () => {
    expect(mockFormBuilder.group).toHaveBeenCalled();
    expect(component.detailsForm).toBeDefined();
  });

  it('should emit saveAsDraft event with form data on onSaveAsDraft', () => {
    spyOn(component.saveAsDraft, 'emit');
    component.onSaveAsDraft();
    expect(component.saveAsDraft.emit).toHaveBeenCalledWith(
      jasmine.any(Object)
    );
  });

  it(`should emit publish event with form data on onPublish`, () => {
    spyOn(component.publish, 'emit');
    component.onPublish();
    expect(component.publish.emit).toHaveBeenCalledWith(jasmine.any(Object));
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
      'stagedProgramChanges'
    );
  });

  it('should save changes to local storage on navigation to preview', () => {
    component.detailsForm.markAsDirty();
    component.onPreview();
    expect(mockLocalStorageService.save).toHaveBeenCalledWith(
      'stagedProgramChanges',
      jasmine.any(Object)
    );
  });
});
