import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
} from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { CallDetailComponent } from './call-detail.component';
import { ActivatedRoute, Router } from '@angular/router';
import { LocalStorageService } from '../../../core/services/local-storage.service';
import { ROUTER_LINKS } from '../../../core/router-links.constants';
import { ViewEnum } from '../../../shared/models/enums/view.enum';
import { Call } from '../../models/call.interface';
import * as fromCalls from '../../store';
import { AuthService } from '../../../core/auth/services/auth.service';

describe('CallDetailComponent', () => {
  let component: CallDetailComponent;
  let fixture: ComponentFixture<CallDetailComponent>;
  let store: MockStore;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockRouter: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockActivatedRoute: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockLocalStorageService: any;

  beforeEach(async () => {
    mockRouter = { navigate: jasmine.createSpy('navigate') };
    mockActivatedRoute = {
      snapshot: { params: { id: '1' } },
    };
    mockLocalStorageService = { remove: jasmine.createSpy('remove') };
    const authSpy = jasmine.createSpyObj('AuthService', [], {
      token: jasmine.createSpy(),
    });

    const initialState = {
      callsModule: {
        calls: {
          entities: {},
        },
      },
    };

    await TestBed.configureTestingModule({
      imports: [CallDetailComponent],
      providers: [
        provideMockStore({ initialState }),
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: AuthService, useValue: authSpy },
        { provide: LocalStorageService, useValue: mockLocalStorageService },
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
    spyOn(store, 'dispatch').and.callThrough();
    store.overrideSelector(fromCalls.callEntityExists('1'), false);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CallDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should dispatch loadCall on ngOnInit if callId exists and call is not in store', fakeAsync(() => {
    tick();
    expect(store.dispatch).toHaveBeenCalledWith(
      fromCalls.loadCall({ id: '1' })
    );
  }));

  it('should navigate correctly on goBack', fakeAsync(async () => {
    await component.goBack();
    tick();
    expect(mockRouter.navigate).toHaveBeenCalledWith([ROUTER_LINKS.FUNDINGS]);
  }));

  it('should navigate to the correct view on goTo', fakeAsync(async () => {
    await component.goTo(ViewEnum.EDIT);
    tick();
    expect(mockRouter.navigate).toHaveBeenCalledWith([ViewEnum.EDIT], {
      relativeTo: mockActivatedRoute,
    });
  }));

  it('should dispatch the correct action on saveAsDraft', fakeAsync(async () => {
    const testCall: Call = { id: '1' };
    await component.saveAsDraft(testCall);
    tick();
    expect(store.dispatch).toHaveBeenCalledWith(
      fromCalls.upsertCall({ call: testCall })
    );

    const newCall: Call = {};
    await component.saveAsDraft(newCall);
    tick();
    expect(store.dispatch).toHaveBeenCalledWith(
      fromCalls.addCall({ call: newCall })
    );
  }));
});
