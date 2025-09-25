import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { OAuthService } from 'angular-oauth2-oidc';
import { Subject } from 'rxjs';
import { User } from '../models/user.interface';
import { UserRoleEnum } from '../models/user-role.enum';
import { OAuthServiceMock } from '../../../testing/mocks/OAuthService.mock';
import { ConfigService } from '../../services/config.service';
import { ApplicationRef } from '@angular/core';

describe('AuthService', () => {
  let service: AuthService;
  let oAuthServiceMock: jasmine.SpyObj<OAuthService>;
  let oAuthEventsSubject: Subject<{ type: string }>;
  let configServiceMock: jasmine.SpyObj<ConfigService>;
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  let fetchSpy: jasmine.Spy;

  const mockUser: User = {
    id: '123',
    name: 'Test User',
    emailDomain: 'example.com',
    roles: [UserRoleEnum.ADMIN],
  };

  beforeEach(async () => {
    oAuthEventsSubject = OAuthServiceMock.events;

    const cfgSpy = jasmine.createSpyObj(
      'ConfigService',
      ['initializeApp', 'getEnvironment'],
      {
        $config: new Subject<{
          authUrl: string;
          authClient: string;
          authScope: string;
          env: string;
        }>(),
      }
    );

    TestBed.configureTestingModule({
      providers: [
        { provide: OAuthService, useValue: OAuthServiceMock },
        { provide: ConfigService, useValue: cfgSpy },
      ],
    });

    service = TestBed.inject(AuthService);
    oAuthServiceMock = TestBed.inject(
      OAuthService
    ) as jasmine.SpyObj<OAuthService>;
    configServiceMock = TestBed.inject(
      ConfigService
    ) as jasmine.SpyObj<ConfigService>;

    // Mock fetch to return user details (including roles and affiliation) when called by the AuthService
    fetchSpy = spyOn(window as unknown as Window, 'fetch').and.returnValue(
      Promise.resolve({
        ok: true,
        json: () =>
          Promise.resolve({
            id: '123',
            affiliationId: 'aff123',
            roles: [UserRoleEnum.ADMIN],
          }),
      } as unknown as Response)
    );

    oAuthServiceMock.getIdentityClaims.and.returnValue({
      sub: mockUser.id,
      preferred_username: mockUser.name,
      email: `test@${mockUser.emailDomain}`,
      roles: mockUser.roles,
    });

    oAuthServiceMock.getAccessToken.and.returnValue('mock-token');

    oAuthServiceMock.hasValidAccessToken?.and.returnValue(true);
    oAuthServiceMock.hasValidIdToken?.and.returnValue(true);

    // Emit config so AuthService completes its setup and subscribes to OAuth events
    configServiceMock.$config.next({
      authUrl: 'https://auth.example.com/',
      authClient: 'test-client',
      authScope: 'openid profile email',
      env: 'test',
    });
    await TestBed.inject(ApplicationRef).whenStable();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Computed properties', () => {
    it('should return correct user', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_received' });
      tick();
      expect(service.user()).toEqual(mockUser);
    }));

    it('should return correct token', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_received' });
      tick();
      expect(service.token()).toBe('mock-token');
    }));

    it('should return correct username', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_received' });
      tick();
      expect(service.username()).toBe(mockUser.name);
    }));

    it('should return correct userAffiliationId', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_received' });
      tick();
      expect(service.userAffiliationId()).toBe('aff123');
    }));

    it('should return correct roles', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_received' });
      tick();
      expect(service.roles()).toEqual(mockUser.roles);
    }));

    it('should return correct isAuthenticated status', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_received' });
      tick();
      expect(service.isAuthenticated()).toBeTrue();
    }));

    it('should return correct isAdmin status', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_received' });
      tick();
      expect(service.isAdmin()).toBeTrue();
    }));

    it('should return correct isFunder status', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_received' });
      tick();
      expect(service.isFunder()).toBeFalse();
    }));
  });

  describe('login', () => {
    it('should call oAuthService.initLoginFlow', () => {
      service.login();
      expect(oAuthServiceMock.initLoginFlow).toHaveBeenCalled();
    });
  });

  describe('logout', () => {
    it('should call oAuthService.logOut', () => {
      service.logout();
      expect(oAuthServiceMock.logOut).toHaveBeenCalled();
    });
  });

  describe('hasAnyRole', () => {
    it('should return true if user has any of the expected roles', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_received' });
      tick();
      expect(service.hasAnyRole([UserRoleEnum.ADMIN])).toBeTrue();
    }));

    it('should return false if user does not have any of the expected roles', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_received' });
      tick();
      expect(service.hasAnyRole([UserRoleEnum.FUNDER])).toBeFalse();
    }));
  });

  describe('OAuthService events', () => {
    it('should update state when token_received event is emitted', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_received' });
      tick();
      expect(service.user()).toEqual(mockUser);
      expect(service.token()).toEqual('mock-token');
    }));

    it('should update state when token_refreshed event is emitted', fakeAsync(() => {
      oAuthEventsSubject.next({ type: 'token_refreshed' });
      tick();
      expect(service.user()).toEqual(mockUser);
      expect(service.token()).toEqual('mock-token');
    }));

    it('should not update state for other event types', fakeAsync(() => {
      const initialUser = service.user();
      const initialToken = service.token();
      oAuthEventsSubject.next({ type: 'other_event' });
      tick();
      expect(service.user()).toEqual(initialUser);
      expect(service.token()).toEqual(initialToken);
    }));
  });
});
