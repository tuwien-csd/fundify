import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProgramsComponent } from './programs.component';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import * as fromPrograms from './store';
import { PROGRAMS } from '../shared/mocks/mock-programs';
import { MatTableDataSource } from '@angular/material/table';
import { PermissionService } from '../core/auth/services/permission.service';
import { MatSort } from '@angular/material/sort';
import { AuthService } from '../core/auth/services/auth.service';
import { ActivatedRoute } from '@angular/router';

describe('ProgramsComponent', () => {
  let component: ProgramsComponent;
  let fixture: ComponentFixture<ProgramsComponent>;
  let store: MockStore;
  let mockPermissionService: jasmine.SpyObj<PermissionService>;

  const initialState = {
    program: fromPrograms.initialState,
    core: {},
  };

  beforeEach(async () => {
    mockPermissionService = jasmine.createSpyObj('PermissionService', [
      'canEdit',
      'canDelete',
    ]);

    mockPermissionService.canEdit.and.returnValue(true);
    mockPermissionService.canDelete.and.returnValue(true);

    const authSpy = jasmine.createSpyObj('AuthService', [], {
      userAffiliationId: jasmine.createSpy(),
      roles: jasmine.createSpy().and.returnValue([]),
    });

    await TestBed.configureTestingModule({
      imports: [ProgramsComponent],
      providers: [
        provideMockStore({ initialState }),
        { provide: PermissionService, useValue: mockPermissionService },
        { provide: AuthService, useValue: authSpy },
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

    store = TestBed.inject(MockStore);
    store.overrideSelector(fromPrograms.selectAllPrograms, PROGRAMS);

    fixture = TestBed.createComponent(ProgramsComponent);
    component = fixture.componentInstance;
    component.dataSource = new MatTableDataSource(PROGRAMS);
    component.sort = new MatSort();
    component.dataSource.sort = component.sort;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should dispatch loadPrograms action on init', () => {
    const action = fromPrograms.loadPrograms({ skipIfPresent: false });
    const dispatchSpy = spyOn(store, 'dispatch');
    component.ngOnInit();
    expect(dispatchSpy).toHaveBeenCalledWith(action);
  });

  it('should set up MatTableDataSource correctly', () => {
    store.overrideSelector(fromPrograms.selectAllPrograms, PROGRAMS);
    store.refreshState();
    fixture.detectChanges();
    expect(component.dataSource.data).toEqual(PROGRAMS);
  });

  it('should filter the programs when applyFilter is called', () => {
    component.dataSource = new MatTableDataSource(PROGRAMS);
    const event = { target: { value: 'test' } } as unknown as Event;
    component.changeFilterValue(event);
    expect(component.dataSource.filter).toBe(
      '{"searchTerm":"test","hideClosedCalls":false}'
    );
  });

  it('should dispatch deleteProgram action on onDelete', () => {
    const dispatchSpy = spyOn(store, 'dispatch');
    component.onDelete('programId');
    expect(dispatchSpy).toHaveBeenCalledWith(
      fromPrograms.deleteProgram({ programId: 'programId' })
    );
  });
});
