import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  FormControl,
  FormsModule,
  NgControl,
  ReactiveFormsModule,
} from '@angular/forms';
import { NumberFieldComponent } from './number-field.component';

describe('NumberFieldComponent', () => {
  let component: NumberFieldComponent;
  let fixture: ComponentFixture<NumberFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, NumberFieldComponent],
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
    fixture = TestBed.createComponent(NumberFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('has null control values by default', () => {
    expect(component.numberForm.value).toBeNull();
  });

  it('is valid by default', () => {
    expect(component.numberForm.valid).toEqual(true);
  });
});
