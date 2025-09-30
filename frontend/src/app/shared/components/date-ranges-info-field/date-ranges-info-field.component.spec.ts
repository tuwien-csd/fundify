import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateRangesInfoFieldComponent } from './date-ranges-info-field.component';
import { FormControl, NgControl, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { provideNativeDateAdapter } from '@angular/material/core';

describe('DateRangesInfoFieldComponent', () => {
  let component: DateRangesInfoFieldComponent;
  let fixture: ComponentFixture<DateRangesInfoFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        DateRangesInfoFieldComponent,
      ],
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DateRangesInfoFieldComponent],
      providers: [
        provideNativeDateAdapter(),
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
    fixture = TestBed.createComponent(DateRangesInfoFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('has initialized one form array element with null controls values by default', () => {
    expect(component.dateRangesInfo.length).toEqual(1);
    expect(component.dateRangesInfo.at(0)?.get('duration')?.value).toBeNull();
    expect(
      component.dateRangesInfo.at(0)?.get('description')?.value
    ).toBeNull();
  });

  it('is valid by default', () => {
    expect(component.dateRangesInfoForm.valid).toEqual(true);
  });
});
