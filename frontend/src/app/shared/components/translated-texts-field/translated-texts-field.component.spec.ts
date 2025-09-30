import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslatedTextsFieldComponent } from './translated-texts-field.component';
import { FormControl, NgControl, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

describe('TranslatedTextsFieldComponent', () => {
  let component: TranslatedTextsFieldComponent;
  let fixture: ComponentFixture<TranslatedTextsFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        TranslatedTextsFieldComponent,
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
      imports: [TranslatedTextsFieldComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslatedTextsFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('has initialized one form array element with null controls values by default', () => {
    expect(component.itemsForm.controls.length).toEqual(1);
    expect(component.itemsForm.at(0)?.get('itemText')?.value).toBeNull();
  });

  it('is valid by default', () => {
    expect(component.itemsForm.valid).toEqual(true);
  });
});
