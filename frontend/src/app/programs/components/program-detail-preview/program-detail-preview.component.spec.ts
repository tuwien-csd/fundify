import { ProgramDetailPreviewComponent } from './program-detail-preview.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LocalStorageService } from '../../../core/services/local-storage.service';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { PROGRAMS } from '../../../shared/mocks/mock-programs';
import { ViewEnum } from '../../../shared/models/enums/view.enum';
import { ProgramValidationService } from '../../services/program-validation.service';

describe('ProgramDetailPreviewComponent', () => {
  let component: ProgramDetailPreviewComponent;
  let fixture: ComponentFixture<ProgramDetailPreviewComponent>;
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

    await TestBed.configureTestingModule({
      imports: [ProgramDetailPreviewComponent],
      providers: [
        { provide: LocalStorageService, useValue: mockLocalStorageService },
        { provide: PermissionService, useValue: mockPermissionService },
        { provide: ProgramValidationService, useValue: mockValidationService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgramDetailPreviewComponent);
    component = fixture.componentInstance;
    component.program = PROGRAMS[0];
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load staged changes and generate program details on ngOnInit', () => {
    expect(mockLocalStorageService.load).toHaveBeenCalledWith(
      'stagedProgramChanges'
    );
    expect(component.programDetails.length).toBeGreaterThan(0);
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
    expect(component.publish.emit).toHaveBeenCalledWith(component.program);
  });

  it('should emit edit event with ViewEnum.EDIT on onEdit', () => {
    spyOn(component.edit, 'emit');
    component.onEdit();
    expect(component.edit.emit).toHaveBeenCalledWith(ViewEnum.EDIT);
  });
});
