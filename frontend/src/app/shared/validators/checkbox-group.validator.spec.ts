import { UntypedFormBuilder } from '@angular/forms';
import { TestBed } from '@angular/core/testing';
import { createCheckboxGroupValidator } from './checkbox-group.validator';

describe('dateRangeValidator', () => {
  let formBuilder: UntypedFormBuilder;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [UntypedFormBuilder],
    }).compileComponents();
  });

  beforeEach(() => {
    formBuilder = TestBed.inject(UntypedFormBuilder);
  });

  it('not valid when array is empty and at least one required ', () => {
    const myForm = formBuilder.array([]);
    const validator = createCheckboxGroupValidator(true);
    expect(validator(myForm)).not.toBeNull();
  });

  it('not valid when at least one required and nothing selected ', () => {
    const myForm = formBuilder.array([false]);
    const validator = createCheckboxGroupValidator(true);
    expect(validator(myForm)).not.toBeNull();
  });

  it('valid when at least one required and one selected ', () => {
    const myForm = formBuilder.array([true, false]);
    const validator = createCheckboxGroupValidator(true);
    expect(validator(myForm)).toBeNull();
  });

  it('valid when no required and one selected ', () => {
    const myForm = formBuilder.array([true, false]);
    const validator = createCheckboxGroupValidator(false);
    expect(validator(myForm)).toBeNull();
  });

  it('valid when no required and no selected ', () => {
    const myForm = formBuilder.array([false, false]);
    const validator = createCheckboxGroupValidator(false);
    expect(validator(myForm)).toBeNull();
  });

  it('valid when no required and empty array ', () => {
    const myForm = formBuilder.array([]);
    const validator = createCheckboxGroupValidator(false);
    expect(validator(myForm)).toBeNull();
  });
});
