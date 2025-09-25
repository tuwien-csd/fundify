import { TestBed } from '@angular/core/testing';
import {
  ReactiveFormsModule,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import { ValidationService } from './validation.service';
import { CustomValidators } from '../validators/custom-validators';
import { VALIDATORS } from '../shared.constants';

describe('ValidationService', () => {
  let service: ValidationService;
  let formBuilder: UntypedFormBuilder;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      providers: [UntypedFormBuilder],
    });
    service = TestBed.inject(ValidationService);
    formBuilder = TestBed.inject(UntypedFormBuilder);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return the correct error message for a required field', () => {
    const control = formBuilder.control(null, [Validators.required]);
    const errorMessage = service.getValidationErrorMessage(
      control,
      VALIDATORS.REQUIRED
    );
    expect(errorMessage).toBe('This field is required');
  });

  it('should return the correct error message for an invalid email address', () => {
    const control = formBuilder.control('invalid-email', [Validators.email]);
    const errorMessage = service.getValidationErrorMessage(
      control,
      VALIDATORS.EMAIL
    );
    expect(errorMessage).toBe('Invalid email address');
  });

  it('should return the correct error message for an invalid phone number', () => {
    const control = formBuilder.control(
      'invalid-phone',
      CustomValidators.phone
    );
    const errorMessage = service.getValidationErrorMessage(
      control,
      VALIDATORS.PHONE
    );
    expect(errorMessage).toBe('Invalid phone number');
  });

  it('should return the correct error message for an invalid URL', () => {
    const control = formBuilder.control('2', [CustomValidators.url]);
    const errorMessage = service.getValidationErrorMessage(
      control,
      VALIDATORS.URL
    );
    expect(errorMessage).toBe('Invalid URL');
  });

  it('should return the correct error message for a minlength validation error', () => {
    const control = formBuilder.control('min', [Validators.minLength(5)]);
    const errorMessage = service.getValidationErrorMessage(
      control,
      VALIDATORS.MIN_LENGTH
    );
    expect(errorMessage).toBe('Length should be at least 5');
  });

  it('should return the correct error message for a maxlength validation error', () => {
    const control = formBuilder.control('10 Characters', [
      Validators.maxLength(10),
    ]);
    const errorMessage = service.getValidationErrorMessage(
      control,
      VALIDATORS.MAX_LENGTH
    );
    expect(errorMessage).toBe('Length should be at most 10');
  });

  it('should return null if the control is null', () => {
    const control = null;
    const errorMessage = service.getValidationErrorMessage(
      control,
      VALIDATORS.REQUIRED
    );
    expect(errorMessage).toBeNull();
  });

  it('should return null if the control does not have the specified error', () => {
    const control = formBuilder.control('test', [Validators.required]);
    const errorMessage = service.getValidationErrorMessage(
      control,
      VALIDATORS.REQUIRED
    );
    expect(errorMessage).toBeNull();
  });
});
