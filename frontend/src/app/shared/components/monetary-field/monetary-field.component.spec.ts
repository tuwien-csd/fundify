import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonetaryFieldComponent } from './monetary-field.component';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

describe('MonetaryFieldComponent', () => {
  let component: MonetaryFieldComponent;
  let fixture: ComponentFixture<MonetaryFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        MonetaryFieldComponent,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MonetaryFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('has null control values by default', () => {
    expect(component.monetaryForm.controls['amount'].value).toBeNull();
    expect(component.monetaryForm.controls['currency'].value).toBeNull();
  });

  it('is valid by default', () => {
    expect(component.monetaryForm.valid).toEqual(true);
  });
});
