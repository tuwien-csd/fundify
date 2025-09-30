import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, NgControl, ReactiveFormsModule } from '@angular/forms';
import { TextFieldComponent } from '../text-field/text-field.component';

describe('TextFieldComponent', () => {
  let component: TextFieldComponent;
  let fixture: ComponentFixture<TextFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, TextFieldComponent],
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
    fixture = TestBed.createComponent(TextFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set the validators for text control on init', () => {
    const validatorsSpy = spyOn(component.text, 'setValidators');
    component.ngOnInit();
    expect(validatorsSpy).toHaveBeenCalled();
  });

  it('should return null if the textForm is valid', () => {
    expect(component.validate(component.textForm)).toBeNull();
  });

  it('should return errors if the textForm is invalid', () => {
    component.required = true;
    component.ngOnInit();
    component.text.setValue('');
    const errors = component.validate(component.textForm);
    expect(errors).not.toBeNull();
  });
});
