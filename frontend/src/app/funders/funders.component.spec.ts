import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FundersComponent } from './funders.component';
import { provideMockStore } from '@ngrx/store/testing';
import { AuthService } from '../core/auth/services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { computed, signal } from '@angular/core';
import { UserRoleEnum } from '../core/auth/models/user-role.enum';

describe('FundersComponent', () => {
  let component: FundersComponent;
  let fixture: ComponentFixture<FundersComponent>;

  const mockUserRoles = signal([UserRoleEnum.ADMIN]);

  const mockAuthService = {
    mockUserRoles: mockUserRoles,
    isAdmin: computed(() => mockUserRoles().includes(UserRoleEnum.ADMIN)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FundersComponent],
      providers: [
        provideMockStore(),
        { provide: AuthService, useValue: mockAuthService },
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
    fixture = TestBed.createComponent(FundersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });
});
