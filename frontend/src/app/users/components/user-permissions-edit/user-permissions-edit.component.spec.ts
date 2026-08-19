import {
  ComponentFixture,
  TestBed,
  fakeAsync,
  flushMicrotasks,
  tick,
} from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { UserPermissionsEditComponent } from './user-permissions-edit.component';
import { UsersStore } from '../../signal/users-store';
import { UniversitiesStore } from '../../../universities/signal/universities-store';
import { NotificationService } from '../../../shared/services/notification-service.service';

describe('UserPermissionsEditComponent - registration requests', () => {
  let fixture: ComponentFixture<UserPermissionsEditComponent>;
  let component: UserPermissionsEditComponent;
  let storeMock: {
    registrationRequests: () => unknown[];
    keycloakUsers: () => unknown[];
    userPermissions: () => unknown[];
    loadKeycloakUsers: jasmine.Spy;
    loadUserPermissions: jasmine.Spy;
    loadRegistrationRequests: jasmine.Spy;
    createUserAndUpdatePermissions: jasmine.Spy;
    updateUserPermissions: jasmine.Spy;
    deleteUserPermissions: jasmine.Spy;
    deleteRegistrationRequest: jasmine.Spy;
  };
  let dialogMock: { open: jasmine.Spy };
  // Controls what the confirmation dialog resolves to when closed.
  let dialogResult: boolean;

  const KEYCLOAK_USERS = [{ id: 'user-1', email: 'existing@uni.org' }];
  const USER_PERMISSIONS = [
    { userId: 'user-1', roles: ['ANNOTATOR', 'FUNDER'], affiliationId: 'TUW' },
  ];

  beforeEach(async () => {
    dialogResult = true;
    dialogMock = {
      open: jasmine
        .createSpy('open')
        .and.callFake(() => ({ afterClosed: () => of(dialogResult) })),
    };

    storeMock = {
      registrationRequests: () => [],
      keycloakUsers: () => KEYCLOAK_USERS,
      userPermissions: () => USER_PERMISSIONS,
      loadKeycloakUsers: jasmine.createSpy('loadKeycloakUsers'),
      loadUserPermissions: jasmine.createSpy('loadUserPermissions'),
      loadRegistrationRequests: jasmine.createSpy('loadRegistrationRequests'),
      createUserAndUpdatePermissions: jasmine
        .createSpy('createUserAndUpdatePermissions')
        .and.returnValue(Promise.resolve(true)),
      updateUserPermissions: jasmine
        .createSpy('updateUserPermissions')
        .and.returnValue(Promise.resolve(true)),
      deleteUserPermissions: jasmine.createSpy('deleteUserPermissions'),
      deleteRegistrationRequest: jasmine
        .createSpy('deleteRegistrationRequest')
        .and.returnValue(Promise.resolve(true)),
    };

    await TestBed.configureTestingModule({
      imports: [UserPermissionsEditComponent],
      providers: [
        { provide: UsersStore, useValue: storeMock },
        { provide: UniversitiesStore, useValue: { entities: () => [] } },
        { provide: MatDialog, useValue: dialogMock },
        {
          provide: NotificationService,
          useValue: jasmine.createSpyObj('NotificationService', [
            'success',
            'error',
          ]),
        },
      ],
    })
      // Strip the template so the test does not depend on child components.
      .overrideComponent(UserPermissionsEditComponent, {
        set: { template: '', imports: [], schemas: [CUSTOM_ELEMENTS_SCHEMA] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(UserPermissionsEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('loads registration requests on init', () => {
    expect(storeMock.loadRegistrationRequests).toHaveBeenCalled();
  });

  it('prefills email and maps Funder -> FUNDER role', () => {
    component.onCreateUserFromRequest({
      id: 'req-1',
      email: 'jane@funder.org',
      kindOfInstitution: 'Funder',
    });

    expect(component['detailsForm'].controls.userId.value).toBe('jane@funder.org');
    expect(component['detailsForm'].controls.roles.value).toEqual(['FUNDER']);
  });

  it('maps Research Institute -> ANNOTATOR role (case insensitive)', () => {
    component.onCreateUserFromRequest({
      id: 'req-2',
      email: 'john@uni.org',
      kindOfInstitution: 'research institute',
    });

    expect(component['detailsForm'].controls.roles.value).toEqual(['ANNOTATOR']);
  });

  it('leaves roles empty for Other / unknown institution', () => {
    component.onCreateUserFromRequest({
      id: 'req-3',
      email: 'x@other.org',
      kindOfInstitution: 'Other',
    });

    expect(component['detailsForm'].controls.userId.value).toBe('x@other.org');
    expect(component['detailsForm'].controls.roles.value).toBeNull();
  });

  it('reject delegates to the store after the dialog is confirmed', () => {
    dialogResult = true;
    component.onRejectRequest('req-1');

    expect(dialogMock.open).toHaveBeenCalled();
    expect(storeMock.deleteRegistrationRequest).toHaveBeenCalledWith('req-1');
  });

  it('reject does nothing when the dialog is cancelled', () => {
    dialogResult = false;
    component.onRejectRequest('req-1');

    expect(dialogMock.open).toHaveBeenCalled();
    expect(storeMock.deleteRegistrationRequest).not.toHaveBeenCalled();
  });

  it('reject is a no-op without an id (no dialog opened)', () => {
    component.onRejectRequest(undefined);

    expect(dialogMock.open).not.toHaveBeenCalled();
    expect(storeMock.deleteRegistrationRequest).not.toHaveBeenCalled();
  });

  it('delete deletes the user after the dialog is confirmed', () => {
    dialogResult = true;
    component.onDelete('user-1');

    expect(dialogMock.open).toHaveBeenCalled();
    expect(storeMock.deleteUserPermissions).toHaveBeenCalledWith('user-1');
  });

  it('delete does nothing when the dialog is cancelled', () => {
    dialogResult = false;
    component.onDelete('user-1');

    expect(dialogMock.open).toHaveBeenCalled();
    expect(storeMock.deleteUserPermissions).not.toHaveBeenCalled();
  });

  it('delete is a no-op without a userId (no dialog opened)', () => {
    component.onDelete('');

    expect(dialogMock.open).not.toHaveBeenCalled();
    expect(storeMock.deleteUserPermissions).not.toHaveBeenCalled();
  });

  it('creating a user from a request deletes the request on success', fakeAsync(() => {
    component.onCreateUserFromRequest({
      id: 'req-1',
      email: 'jane@funder.org',
      kindOfInstitution: 'Funder',
    });
    component['detailsForm'].controls.affiliationId.setValue('andamp');

    component.onSave();
    flushMicrotasks();
    tick();

    expect(storeMock.createUserAndUpdatePermissions).toHaveBeenCalledWith(
      'jane@funder.org',
      ['FUNDER'],
      'andamp'
    );
    expect(storeMock.deleteRegistrationRequest).toHaveBeenCalledWith('req-1');
  }));

  it('a plain create (no request) does not delete any request', fakeAsync(() => {
    component['detailsForm'].controls.userId.setValue('manual@x.org');
    component['detailsForm'].controls.roles.setValue(['ANNOTATOR']);
    component['detailsForm'].controls.affiliationId.setValue('andamp');

    component.onSave();
    flushMicrotasks();
    tick();

    expect(storeMock.createUserAndUpdatePermissions).toHaveBeenCalled();
    expect(storeMock.deleteRegistrationRequest).not.toHaveBeenCalled();
  }));

  it('prefills roles and affiliation when an existing user is selected', () => {
    component['detailsForm'].controls.userId.setValue('user-1');

    expect(component['detailsForm'].controls.roles.value).toEqual([
      'ANNOTATOR',
      'FUNDER',
    ]);
    expect(component['detailsForm'].controls.affiliationId.value).toBe('TUW');
  });

  it('clears the prefill when the selection changes to a new email', () => {
    component['detailsForm'].controls.userId.setValue('user-1');
    component['detailsForm'].controls.userId.setValue('new@uni.org');

    expect(component['detailsForm'].controls.roles.value).toBeNull();
    expect(component['detailsForm'].controls.affiliationId.value).toBe('');
  });
});
