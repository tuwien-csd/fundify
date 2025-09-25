import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PermissionService } from '../../../core/auth/services/permission.service';
import { ViewEnum } from '../../../shared/models/enums/view.enum';
import { LocalStorageService } from '../../../core/services/local-storage.service';
import { FUNDERS } from '../../../shared/mocks/mock-funders';
import { Subject } from 'rxjs';
import { OAuthService } from 'angular-oauth2-oidc';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { Component } from '@angular/core';
import { FunderWebModel } from '../../models/funder.interface';
import { FunderDetailComponent } from './funder-detail.component';

@Component({
  template: ` <app-funder-detail-preview
    [funder]="funder"
  ></app-funder-detail-preview>`,
  standalone: true,
  imports: [FunderDetailComponent],
})
class TestHostComponent {
  funder: FunderWebModel = FUNDERS[0];
}

describe('FunderDetailComponent', () => {
  let component: FunderDetailComponent;
  let fixture: ComponentFixture<TestHostComponent>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockLocalStorageService: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockPermissionService: any;

  beforeEach(async () => {
    const oAuthSpy = jasmine.createSpyObj(
      'OAuthService',
      ['initLoginFlow', 'logOut', 'getAccessToken', 'getIdentityClaims'],
      { events: new Subject<{ type: string }>() }
    );

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
      imports: [TestHostComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: LocalStorageService, useValue: mockLocalStorageService },
        { provide: PermissionService, useValue: mockPermissionService },
        { provide: OAuthService, useValue: oAuthSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => 'mockValue',
              },
            },
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestHostComponent);
    component = fixture.debugElement.children[0].componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize permissions on ngOnInit', () => {
    expect(mockPermissionService.getPermissions).toHaveBeenCalled();
  });

  it('should emit back event on onBack', () => {
    spyOn(component.back, 'emit');
    component.onBack();
    expect(component.back.emit).toHaveBeenCalled();
  });

  it('should emit edit event with ViewEnum.EDIT on onEdit', () => {
    spyOn(component.edit, 'emit');
    component.onEdit();
    expect(component.edit.emit).toHaveBeenCalledWith(ViewEnum.EDIT);
  });
});
