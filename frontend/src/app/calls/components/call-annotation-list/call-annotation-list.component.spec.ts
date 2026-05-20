import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CallAnnotationListComponent } from './call-annotation-list.component';
import { Store } from '@ngrx/store';
import { of } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { FormBuilder } from '@angular/forms';
import { AuthService } from '../../../core/auth/services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { Call } from '../../models/call.interface';
import { AnnotatedCall } from '../../models/annotated-call.interface';
import { CallAnnotationListViewElement } from './call-annotation-list.component';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';

describe('CallAnnotationListComponent', () => {
  let component: CallAnnotationListComponent;
  let fixture: ComponentFixture<CallAnnotationListComponent>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', [], {
      userAffiliationId: jasmine.createSpy(),
    });
    await TestBed.configureTestingModule({
      imports: [CallAnnotationListComponent],
      providers: [
        { provide: FormBuilder },
        {
          provide: Store,
          useValue: { select: () => of(), dispatch: () => {} },
        },
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

    fixture = TestBed.createComponent(CallAnnotationListComponent);
    component = fixture.componentInstance;
    component.dataSource = new MatTableDataSource();
    component.sort = new MatSort();
    component.dataSource.sort = component.sort;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should exclude draft calls from the annotation list view', () => {
    const calls: Call[] = [
      { id: 'published-1', status: PublicationStatusEnum.PUBLISHED },
      { id: 'draft-1', status: PublicationStatusEnum.DRAFT },
      { id: 'published-2', status: PublicationStatusEnum.PUBLISHED },
    ];

    type WithMerge = { mergeToListViewElements: (calls: Call[], annotatedCalls: AnnotatedCall[]) => CallAnnotationListViewElement[] };
    const result = (component as unknown as WithMerge).mergeToListViewElements(calls, []);

    expect(result.length).toBe(2);
    expect(result.map((e) => e.callId)).toEqual(['published-1', 'published-2']);
  });
});
