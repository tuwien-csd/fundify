import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateRangeInfoFieldComponent } from './date-range-info-field.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { provideNativeDateAdapter } from '@angular/material/core';

describe('DateRangeInfoFieldComponent', () => {
  let component: DateRangeInfoFieldComponent;
  let fixture: ComponentFixture<DateRangeInfoFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        DateRangeInfoFieldComponent,
      ],
      providers: [provideNativeDateAdapter()],
    }).compileComponents();
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DateRangeInfoFieldComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DateRangeInfoFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('has null control values by default', () => {
    expect(component.rangeInfoForm.controls['range'].value).toBeNull();
    expect(component.rangeInfoForm.controls['info'].value).toBeNull();
  });

  it('is valid by default', () => {
    expect(component.rangeInfoForm.valid).toEqual(true);
  });
});
