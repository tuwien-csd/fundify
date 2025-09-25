import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
  ValidationErrors,
} from '@angular/forms';
import { TestBed } from '@angular/core/testing';
import { collectFormGroupErrors } from './validator-util';
import { getFormValidationErrors } from './validator-util';

describe('validatorUtil', () => {
  describe('collectFormGroupErrors', () => {
    let formBuilder: UntypedFormBuilder;

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        providers: [UntypedFormBuilder],
      }).compileComponents();
    });

    beforeEach(() => {
      formBuilder = TestBed.inject(UntypedFormBuilder);
    });

    it('when control has errors then return errors under control name property', () => {
      const myForm = formBuilder.group({
        myControl: [null, Validators.required],
      });
      myForm.get('myControl')?.setValue(null);
      expect(myForm.get('myControl')?.hasError('required')).toBeTruthy();
      const errors = collectFormGroupErrors(myForm) ?? {}; // errors can be null
      expect(errors['myControl']).toBeTruthy();
    });

    it('when control and group have no errors then return null', () => {
      const myForm = formBuilder.group({
        myControl: [null],
      });
      const errors = collectFormGroupErrors(myForm);
      expect(errors).toBeNull();
    });
  });

  describe('getFormValidationErrors', () => {
    let formBuilder: UntypedFormBuilder;

    beforeEach(() => {
      formBuilder = new UntypedFormBuilder();
    });

    it('should return an empty object when no errors are present', () => {
      const form: UntypedFormGroup = formBuilder.group({
        name: ['', Validators.required],
        address: formBuilder.group({
          street: ['', Validators.required],
          city: ['', Validators.required],
        }),
      });

      form.setValue({
        name: 'John Doe',
        address: {
          street: '123 Main St',
          city: 'New York',
        },
      });

      const errors: ValidationErrors = getFormValidationErrors(form);
      expect(Object.keys(errors).length).toBe(0);
    });

    it('should return all validation errors for a nested form', () => {
      const form: UntypedFormGroup = formBuilder.group({
        name: ['', Validators.required],
        address: formBuilder.group({
          street: ['', Validators.required],
          city: ['', Validators.required],
        }),
      });

      form.setValue({
        name: '',
        address: {
          street: '',
          city: '',
        },
      });

      const errors: ValidationErrors = getFormValidationErrors(form);
      expect(Object.keys(errors).length).toBe(3);
      expect(errors).toEqual({
        name: 'required: true',
        'address.street': 'required: true',
        'address.city': 'required: true',
      });
    });

    it('should return all validation errors for a form with FormArray', () => {
      const form: UntypedFormGroup = formBuilder.group({
        name: ['', Validators.required],
        hobbies: formBuilder.array([
          formBuilder.control('', Validators.required),
          formBuilder.control('', Validators.required),
        ]),
      });

      form.setValue({
        name: '',
        hobbies: ['', ''],
      });

      const errors: ValidationErrors = getFormValidationErrors(form);
      expect(Object.keys(errors).length).toBe(3);
      expect(errors).toEqual({
        name: 'required: true',
        'hobbies[0]': 'required: true',
        'hobbies[1]': 'required: true',
      });
    });
  });
});
