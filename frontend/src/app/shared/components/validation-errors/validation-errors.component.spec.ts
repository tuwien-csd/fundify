import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ValidationErrorsComponent } from './validation-errors.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('ValidationErrorsComponent', () => {
  let component: ValidationErrorsComponent;
  let fixture: ComponentFixture<ValidationErrorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        ValidationErrorsComponent,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ValidationErrorsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should display error messages', () => {
    component.errors = {
      nameControl: ['required'],
      emailControl: ['required', 'email'],
    };
    fixture.detectChanges();

    const errorElements: DebugElement[] = fixture.debugElement.queryAll(
      By.css('ul ul li')
    );
    expect(errorElements.length).toEqual(3);
  });

  it('should format keys correctly', () => {
    expect(component.formatKey('nameControl')).toEqual('Name');
    expect(component.formatKey('emailControl')).toEqual('Email');
    expect(component.formatKey('customPropertyControl')).toEqual(
      'Custom Property'
    );
  });
});
