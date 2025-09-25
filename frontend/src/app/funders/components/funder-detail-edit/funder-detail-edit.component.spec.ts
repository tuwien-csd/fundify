import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FunderDetailEditComponent } from './funder-detail-edit.component';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { LocalStorageService } from '../../../core/services/local-storage.service';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { OAuthService } from 'angular-oauth2-oidc';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { FundersStore } from '../../signal/funders-store';

describe('FunderDetailEditComponent', () => {
  let component: FunderDetailEditComponent;
  let fixture: ComponentFixture<FunderDetailEditComponent>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockLocalStorageService: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockPermissionService: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockRouter: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockFundersStore: any;

  beforeEach(async () => {
    const oAuthSpy = jasmine.createSpyObj(
      'OAuthService',
      ['initLoginFlow', 'logOut', 'getAccessToken', 'getIdentityClaims'],
      { events: new Subject<{ type: string }>() }
    );

    mockFundersStore = jasmine.createSpyObj('FundersStore', ['addFunder']);
    mockFundersStore.addFunder.and.returnValue(Promise.resolve({ id: 1 }));

    mockLocalStorageService = {
      load: jasmine.createSpy('load'),
      save: jasmine.createSpy('save'),
    };
    mockPermissionService = {
      getPermissions: jasmine.createSpy('getPermissions'),
    };
    mockRouter = { navigate: jasmine.createSpy('navigate') };

    await TestBed.configureTestingModule({
      imports: [FunderDetailEditComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: LocalStorageService, useValue: mockLocalStorageService },
        { provide: PermissionService, useValue: mockPermissionService },
        { provide: Router, useValue: mockRouter },
        { provide: OAuthService, useValue: oAuthSpy },
        { provide: FundersStore, useValue: mockFundersStore },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FunderDetailEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize the form as a computed signal', () => {
    expect(component.detailsForm()).toBeDefined();
  });

  it('should call store.addFunder with form data on onSave', () => {
    component.detailsForm();
    component.onSave();
    expect(mockFundersStore.addFunder).toHaveBeenCalledWith(
      jasmine.any(Object)
    );
  });
});
