import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { DateRangeFieldComponent } from './date-range-field.component';
import { provideNativeDateAdapter } from '@angular/material/core';

describe('DateRangeFieldComponent', () => {
  let component: DateRangeFieldComponent;
  let fixture: ComponentFixture<DateRangeFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        DateRangeFieldComponent,
      ],
    }).compileComponents();
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DateRangeFieldComponent],
      providers: [provideNativeDateAdapter()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DateRangeFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('has null control values by default', () => {
    expect(component.rangeForm.controls['start'].value).toBeNull();
    expect(component.rangeForm.controls['end'].value).toBeNull();
  });

  it('is valid by default', () => {
    expect(component.rangeForm.valid).toEqual(true);
  });
});
