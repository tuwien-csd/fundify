import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ContactFieldComponent } from './contact-field.component';
import {
  FormBuilder,
  FormControl,
  NgControl,
  ReactiveFormsModule,
} from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('ContactFieldComponent', () => {
  describe('ContactFieldComponent without @Input', () => {
    let component: ContactFieldComponent;
    let fixture: ComponentFixture<ContactFieldComponent>;

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          ReactiveFormsModule,
          BrowserAnimationsModule,
          MatInputModule,
          MatFormFieldModule,
          MatButtonModule,
          ContactFieldComponent,
        ],
        providers: [
          FormBuilder,
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
      fixture = TestBed.createComponent(ContactFieldComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should have at least one contact form', () => {
      expect(component.contacts.length).toBeGreaterThanOrEqual(1);
    });

    it('should add a new contact form', () => {
      const initialContactsLength = component.contacts.length;
      component.addContact();
      expect(component.contacts.length).toEqual(initialContactsLength + 1);
    });

    it('should remove a contact form', () => {
      component.addContact();
      const initialContactsLength = component.contacts.length;
      component.removeContact(initialContactsLength - 1);
      expect(component.contacts.length).toEqual(initialContactsLength - 1);
    });

    it('should not remove the last contact form', () => {
      const initialContactsLength = component.contacts.length;
      component.removeContact(0);
      expect(component.contacts.length).toEqual(initialContactsLength);
    });

    it('should update the contactForm value when writing a new value', () => {
      const newValue = [
        {
          name: 'John Doe',
          email: 'john@example.com',
          phone: '1234567890',
        },
        {
          name: 'Jane Doe',
          email: 'jane@example.com',
          phone: '0987654321',
        },
      ];
      component.writeValue(newValue);
      expect(component.contactForm.value).toEqual({ contacts: newValue });
    });

    it('should validate the form correctly', () => {
      const invalidValue = [
        {
          name: '',
          email: 'invalid-email',
          phone: 'invalid-phone',
        },
      ];
      component.writeValue(invalidValue);
      const validationResult = component.validate(component.contactForm);
      expect(validationResult).not.toBeNull();
    });
  });

  describe('ContactFieldComponent with required @Input', () => {
    let component: ContactFieldComponent;
    let fixture: ComponentFixture<ContactFieldComponent>;

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          ReactiveFormsModule,
          BrowserAnimationsModule,
          MatInputModule,
          MatFormFieldModule,
          MatButtonModule,
          ContactFieldComponent,
        ],
        providers: [
          FormBuilder,
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
      fixture = TestBed.createComponent(ContactFieldComponent);
      component = fixture.componentInstance;
      component.required = true;
      component.minLength = 3;
      fixture.detectChanges();
    });

    it('should mark the name field as required', () => {
      const nameControl = component.contacts.at(0)?.get('name');
      nameControl?.setValue('');
      expect(nameControl?.valid).toBeFalse();
    });

    it('should set the form state correctly when calling setDisabledState', () => {
      component.setDisabledState(true);
      expect(component.contactForm.disabled).toBeTrue();

      component.setDisabledState(false);
      expect(component.contactForm.enabled).toBeTrue();
    });

    it('should validate the email field pattern correctly', () => {
      const emailControl = component.contacts.at(0).get('email');
      emailControl?.setValue('invalid-email');
      expect(emailControl?.valid).toBeFalse();

      emailControl?.setValue('john@example.com');
      expect(emailControl?.valid).toBeTrue();
    });

    it('should validate the phone field pattern correctly', () => {
      const phoneControl = component.contacts.at(0).get('phone');
      phoneControl?.setValue('invalid-phone');
      expect(phoneControl?.valid).toBeFalse();

      phoneControl?.setValue('1234567890');
      expect(phoneControl?.valid).toBeTrue();
    });

    it('should validate the name field minimum length correctly', () => {
      const nameControl = component.contacts.at(0).get('name');
      nameControl?.setValue('A');
      expect(nameControl?.valid).toBeFalse();

      nameControl?.setValue('John');
      expect(nameControl?.valid).toBeTrue();
    });
  });
});
