import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ContactFormComponent } from './contact-form.component';
import { ContactFormService } from '../contact-form.service';

describe('ContactFormComponent', () => {
  let fixture: ComponentFixture<ContactFormComponent>;
  let component: ContactFormComponent;
  let submitSpy: jasmine.Spy;

  beforeEach(async () => {
    submitSpy = jasmine
      .createSpy('submitForm')
      .and.returnValue(Promise.resolve(true));

    await TestBed.configureTestingModule({
      imports: [ContactFormComponent],
      providers: [
        {
          provide: ContactFormService,
          useValue: { submitForm: submitSpy, isPending: () => false },
        },
      ],
    })
      // Strip the template so the test does not depend on child components.
      .overrideComponent(ContactFormComponent, {
        set: { template: '', imports: [], schemas: [CUSTOM_ELEMENTS_SCHEMA] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(ContactFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('requires both the given name and the surname', () => {
    const { firstName, lastName } = component.contactForm.controls;

    expect(firstName.valid).toBeFalse();
    expect(lastName.valid).toBeFalse();

    firstName.setValue('Jane');
    lastName.setValue('Doe');

    expect(firstName.valid).toBeTrue();
    expect(lastName.valid).toBeTrue();
  });

  it('submits the given name and surname as separate fields', async () => {
    component.contactForm.setValue({
      subject: 'Account please',
      category: 'registration',
      firstName: 'Jane',
      lastName: 'Doe',
      email: 'jane@funder.org',
      kindOfInstitution: null,
      message: 'I would like an account.',
    });

    expect(component.contactForm.valid).toBeTrue();

    component.onSubmit();
    await Promise.resolve();

    expect(submitSpy).toHaveBeenCalled();
    const submitted = submitSpy.calls.mostRecent().args[0];
    expect(submitted.firstName).toBe('Jane');
    expect(submitted.lastName).toBe('Doe');
    expect(submitted.name).toBeUndefined();
  });

  it('does not submit an incomplete form', () => {
    component.contactForm.controls.firstName.setValue('Jane');

    component.onSubmit();

    expect(submitSpy).not.toHaveBeenCalled();
  });
});
