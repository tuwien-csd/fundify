import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DurationFieldComponent } from './duration-field.component';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule, NgControl, FormControl } from '@angular/forms';

describe('DurationFieldComponent', () => {
  let component: DurationFieldComponent;
  let fixture: ComponentFixture<DurationFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        DurationFieldComponent,
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

  beforeEach(() => {
    fixture = TestBed.createComponent(DurationFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('has null control values by default', () => {
    expect(component.durationForm.controls['months'].value).toBeNull();
    expect(component.durationForm.controls['days'].value).toBeNull();
  });

  it('is valid by default', () => {
    expect(component.durationForm.valid).toEqual(true);
  });
});
