import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProgramsComponent } from './programs.component';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { PROGRAMS } from '../shared/mocks/mock-programs';
import { MatTableDataSource } from '@angular/material/table';
import { PermissionService } from '../core/auth/services/permission.service';
import { MatSort } from '@angular/material/sort';
import { AuthService } from '../core/auth/services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { signal } from '@angular/core';

describe('ProgramsComponent', () => {
  let component: ProgramsComponent;
  let fixture: ComponentFixture<ProgramsComponent>;
  let store: MockStore;
  let mockPermissionService: jasmine.SpyObj<PermissionService>;

  const initialState = {
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

    fixture = TestBed.createComponent(ProgramsComponent);
    component = fixture.componentInstance;
    component.dataSource = signal(new MatTableDataSource(PROGRAMS));
    component.sort = new MatSort();
    component.dataSource().sort = component.sort;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should filter the programs when applyFilter is called', () => {
    component.dataSource = signal(new MatTableDataSource(PROGRAMS));
    const event = { target: { value: 'test' } } as unknown as Event;
    component.changeFilterValue(event);
    expect(component.dataSource().filter).toBe(
      '{"searchTerm":"test","hideClosedCalls":false}'
    );
  });
});
