import { Subject } from 'rxjs';

export const OAuthServiceMock = jasmine.createSpyObj(
  'OAuthService',
  [
    'initLoginFlow',
    'logOut',
    'getAccessToken',
    'getIdentityClaims',
    'hasValidAccessToken',
    'hasValidIdToken',
    'setupAutomaticSilentRefresh',
    'configure',
    'loadDiscoveryDocumentAndTryLogin',
  ],
  { events: new Subject<{ type: string }>() }
);
