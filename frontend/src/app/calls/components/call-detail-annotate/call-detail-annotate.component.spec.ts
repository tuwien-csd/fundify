import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CallDetailAnnotateComponent } from './call-detail-annotate.component';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { MatDialog } from '@angular/material/dialog';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';
import { AuthService } from '../../../core/auth/services/auth.service';

describe('CallDetailAnnotateComponent', () => {
  let component: CallDetailAnnotateComponent;
  let fixture: ComponentFixture<CallDetailAnnotateComponent>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', [], {
      userAffiliationId: jasmine.createSpy(),
    });
    await TestBed.configureTestingModule({
      imports: [CallDetailAnnotateComponent],
      providers: [
        { provide: AuthService, useValue: authSpy },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { params: { id: '123' } } },
        },
        { provide: Router, useValue: { navigate: () => {} } },
        {
          provide: Store,
          useValue: { select: () => of(), dispatch: () => {} },
        },
        { provide: MatDialog, useValue: {} },
        {
          provide: FormBuilder,
          useValue: { group: () => {}, array: () => {} },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CallDetailAnnotateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
