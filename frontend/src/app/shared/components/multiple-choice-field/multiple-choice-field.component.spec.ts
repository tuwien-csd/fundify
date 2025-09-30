import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultipleChoiceFieldComponent } from './multiple-choice-field.component';
import { RouterTestingModule } from '@angular/router/testing';
import { FormControl, NgControl, ReactiveFormsModule } from '@angular/forms';

describe('MultipleChoiceFieldComponent', () => {
  let component: MultipleChoiceFieldComponent;
  let fixture: ComponentFixture<MultipleChoiceFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        MultipleChoiceFieldComponent,
      ],
      providers: [
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
    fixture = TestBed.createComponent(MultipleChoiceFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('has empty array control value by default', () => {
    expect(component.multipleChoiceForm.controls['checkboxes'].value).toEqual(
      []
    );
  });
});

describe('MultipleChoiceFieldComponent with @Input type', () => {
  let component: MultipleChoiceFieldComponent;
  let fixture: ComponentFixture<MultipleChoiceFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        MultipleChoiceFieldComponent,
      ],
      providers: [
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
    fixture = TestBed.createComponent(MultipleChoiceFieldComponent);
    component = fixture.componentInstance;
    component.type = { TEST: 'Test' };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('assigns correct options when given type', () => {
    expect(component.options).toEqual(['Test']);
  });

  it('has boolean default=false array control values according to options when given type', () => {
    expect(component.multipleChoiceForm.controls['checkboxes'].value).toEqual([
      false,
    ]);
  });
});
