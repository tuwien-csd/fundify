import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SingleChoiceFieldComponent } from './single-choice-field.component';
import { RouterTestingModule } from '@angular/router/testing';
import { FormControl, NgControl, ReactiveFormsModule } from '@angular/forms';

describe('SingleChoiceFieldComponent', () => {
  let component: SingleChoiceFieldComponent;
  let fixture: ComponentFixture<SingleChoiceFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        SingleChoiceFieldComponent,
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
    fixture = TestBed.createComponent(SingleChoiceFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('has null control value by default', () => {
    expect(component.singleChoiceForm.controls['value'].value).toBeNull();
  });
});
