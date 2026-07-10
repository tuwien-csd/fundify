import { TestBed } from '@angular/core/testing';
import { UsersStore } from './users-store';
import { BackendServiceV2 } from '../../core/services/backend-service-v2.service';
import { NotificationService } from '../../shared/services/notification-service.service';
import { components } from '../../../generated/refop-be';

type RegistrationRequest = components['schemas']['RegistrationRequest'];

const REQUESTS: RegistrationRequest[] = [
  { id: 'req-1', name: 'Jane', email: 'jane@funder.org', kindOfInstitution: 'Funder' },
  { id: 'req-2', name: 'John', email: 'john@uni.org', kindOfInstitution: 'Research Institute' },
];

describe('UsersStore - registration requests', () => {
  let getSpy: jasmine.Spy;
  let deleteSpy: jasmine.Spy;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;

  function configure(): InstanceType<typeof UsersStore> {
    getSpy = jasmine.createSpy('GET');
    deleteSpy = jasmine.createSpy('DELETE');
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
      'success',
      'error',
    ]);

    TestBed.configureTestingModule({
      providers: [
        { provide: BackendServiceV2, useValue: { client: { GET: getSpy, DELETE: deleteSpy } } },
        { provide: NotificationService, useValue: notificationServiceSpy },
      ],
    });

    return TestBed.inject(UsersStore);
  }

  it('loadRegistrationRequests stores the fetched requests', async () => {
    const store = configure();
    getSpy.and.returnValue(Promise.resolve({ data: REQUESTS }));

    await store.loadRegistrationRequests();

    expect(getSpy).toHaveBeenCalledWith('/api/registration-requests');
    expect(store.registrationRequests()).toEqual(REQUESTS);
  });

  it('deleteRegistrationRequest removes the request from state on 204', async () => {
    const store = configure();
    getSpy.and.returnValue(Promise.resolve({ data: REQUESTS }));
    deleteSpy.and.returnValue(Promise.resolve({ response: { status: 204 } }));
    await store.loadRegistrationRequests();

    const result = await store.deleteRegistrationRequest('req-1');

    expect(result).toBeTrue();
    expect(deleteSpy).toHaveBeenCalledWith('/api/registration-requests/{id}', {
      params: { path: { id: 'req-1' } },
    });
    expect(store.registrationRequests().map((r) => r.id)).toEqual(['req-2']);
  });

  it('deleteRegistrationRequest keeps state and notifies on non-204', async () => {
    const store = configure();
    getSpy.and.returnValue(Promise.resolve({ data: REQUESTS }));
    deleteSpy.and.returnValue(Promise.resolve({ response: { status: 500 } }));
    await store.loadRegistrationRequests();

    const result = await store.deleteRegistrationRequest('req-1');

    expect(result).toBeFalse();
    expect(store.registrationRequests().length).toBe(2);
    expect(notificationServiceSpy.error).toHaveBeenCalled();
  });
});
