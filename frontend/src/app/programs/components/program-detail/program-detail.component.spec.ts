import { ProgramDetailComponent } from './program-detail.component';
import {
  ComponentFixture,
  fakeAsync,
  TestBed,
  tick,
} from '@angular/core/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { LocalStorageService } from '../../../core/services/local-storage.service';
import * as fromPrograms from '../../store';
import { loadProgram } from '../../store';
import { ROUTER_LINKS } from '../../../core/router-links.constants';
import { ViewEnum } from '../../../shared/models/enums/view.enum';
import { Program } from '../../models/program.interface';
import { AppState } from '../../../store';
import { AuthService } from '../../../core/auth/services/auth.service';
import { Subject } from 'rxjs';
import { OAuthService } from 'angular-oauth2-oidc';

describe('ProgramDetailComponent', () => {
  let component: ProgramDetailComponent;
  let fixture: ComponentFixture<ProgramDetailComponent>;
  let store: MockStore<AppState>;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockRouter: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockActivatedRoute: any;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let mockLocalStorageService: any;

  beforeEach(async () => {
    const oAuthSpy = jasmine.createSpyObj(
      'OAuthService',
      ['initLoginFlow', 'logOut', 'getAccessToken', 'getIdentityClaims'],
      { events: new Subject<{ type: string }>() }
    );
    mockRouter = { navigate: jasmine.createSpy('navigate') };
    mockActivatedRoute = {
      snapshot: { params: { id: '1' } },
    };
    mockLocalStorageService = { remove: jasmine.createSpy('remove') };
    const authSpy = jasmine.createSpyObj('AuthService', [], {
      token: jasmine.createSpy(),
    });

    const initialState = {
      programs: {
        entities: {},
      },
    };

    await TestBed.configureTestingModule({
      imports: [ProgramDetailComponent],
      providers: [
        provideMockStore({ initialState }),
        { provide: OAuthService, useValue: oAuthSpy },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: LocalStorageService, useValue: mockLocalStorageService },
        { provide: AuthService, useValue: authSpy },
      ],
    }).compileComponents();

    store = TestBed.inject(MockStore);
    store.overrideSelector(fromPrograms.entityExists('1'), false);
    spyOn(store, 'dispatch').and.callThrough();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgramDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set programId from route parameters on ngOnInit', () => {
    expect(component.programId).toBe('1');
  });

  it('should dispatch loadProgram on ngOnInit if programId exists and program is not in store', fakeAsync(() => {
    fixture = TestBed.createComponent(ProgramDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    tick();

    expect(store.dispatch).toHaveBeenCalledWith(loadProgram({ id: '1' }));
  }));

  it('should not dispatch loadProgram if programId is empty', () => {
    mockActivatedRoute.snapshot.params['id'] = '';
    fixture.detectChanges();
    expect(store.dispatch).not.toHaveBeenCalledWith(loadProgram({ id: '' }));
  });

  it('should navigate back to the programs list on goBack', async () => {
    await component.goBack();
    expect(mockRouter.navigate).toHaveBeenCalledWith([
      ROUTER_LINKS.FUNDINGS,
      ROUTER_LINKS.PROGRAMS,
    ]);
  });

  it('should navigate to the specified view on goTo', async () => {
    const editView = ViewEnum.EDIT;
    await component.goTo(editView);
    expect(mockRouter.navigate).toHaveBeenCalledWith([editView], {
      relativeTo: mockActivatedRoute,
    });

    const previewView = ViewEnum.PREVIEW;
    await component.goTo(previewView);
    expect(mockRouter.navigate).toHaveBeenCalledWith([previewView], {
      relativeTo: mockActivatedRoute,
    });
  });

  it('should remove item from localStorage on ngOnDestroy', () => {
    component.ngOnDestroy();
    expect(mockLocalStorageService.remove).toHaveBeenCalledWith(
      'stagedProgramChanges'
    );
  });

  it('should dispatch correct action on saveAsDraft', async () => {
    const testProgram: Program = { id: '1' };
    await component.saveAsDraft(testProgram);
    expect(store.dispatch).toHaveBeenCalledWith(
      fromPrograms.upsertProgram({ program: testProgram })
    );

    const newProgram: Program = { id: '' };
    await component.saveAsDraft(newProgram);
    expect(store.dispatch).toHaveBeenCalledWith(
      fromPrograms.addProgram({ program: newProgram })
    );
  });

  it('should dispatch correct action on publish', async () => {
    const testProgram: Program = { id: '1' };
    await component.publish(testProgram);
    expect(store.dispatch).toHaveBeenCalledWith(
      fromPrograms.upsertProgram({ program: testProgram })
    );

    const newProgram: Program = { id: '' };
    await component.publish(newProgram);
    expect(store.dispatch).toHaveBeenCalledWith(
      fromPrograms.addProgram({ program: newProgram })
    );
  });
});
