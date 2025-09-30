import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubjectFieldComponent } from './subject-field.component';
import { RouterTestingModule } from '@angular/router/testing';
import {
  UntypedFormBuilder,
  ReactiveFormsModule,
  FormControl,
  NgControl,
} from '@angular/forms';
import { SubjectService } from '../../../core/services/subject.service';
import { OEFOS } from '../../mocks/mock-oefos';

describe('SubjectFieldComponent', () => {
  describe('SubjectFieldComponent Initialization', () => {
    let subjectServiceSpy;
    let component: SubjectFieldComponent;
    let fixture: ComponentFixture<SubjectFieldComponent>;

    beforeEach(() => {
      subjectServiceSpy = jasmine.createSpyObj('SubjectService', [
        'getSubjects',
      ]);
      subjectServiceSpy.getSubjects.and.returnValue(OEFOS);

      TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          ReactiveFormsModule,
          SubjectFieldComponent,
        ],
        providers: [
          UntypedFormBuilder,
          { provide: SubjectService, useValue: subjectServiceSpy },
          {
            provide: NgControl,
            useValue: {
              control: new FormControl(),
            },
          },
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      fixture = TestBed.createComponent(SubjectFieldComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('initializes subjects correctly', () => {
      expect(component.subjects).toEqual(OEFOS);
    });

    it('has nothing selected by default', () => {
      expect(component.selection.value).toEqual(
        component.subjects.map(() => false)
      );
    });

    it('has collapsed state by default', () => {
      expect(component.expanded.every((val: boolean) => !val)).toBeTruthy();
    });
  });

  describe(' SubjectFieldComponent modification with @Input type', () => {
    let subjectServiceSpy;
    let component: SubjectFieldComponent;
    let fixture: ComponentFixture<SubjectFieldComponent>;

    beforeEach(() => {
      subjectServiceSpy = jasmine.createSpyObj('SubjectService', [
        'getSubjects',
      ]);
      subjectServiceSpy.getSubjects.and.returnValue(OEFOS);

      TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          ReactiveFormsModule,
          SubjectFieldComponent,
        ],
        providers: [
          UntypedFormBuilder,
          { provide: SubjectService, useValue: subjectServiceSpy },
          {
            provide: NgControl,
            useValue: {
              control: new FormControl(),
            },
          },
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      fixture = TestBed.createComponent(SubjectFieldComponent);
      component = fixture.componentInstance;
      component.required = true;
      fixture.detectChanges();
    });

    it('is invalid when no subject is selected (default)', () => {
      expect(component.tableForm.valid).toEqual(false);
    });

    it('is valid when one subject is selected', () => {
      const selectedRow: number = 1;
      component.selection.controls[selectedRow].setValue(true);
      expect(component.tableForm.valid).toEqual(true);
    });
  });
});
