import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VocabularyFormFieldComponent } from './vocabulary-form-field.component';
import { MatDialog } from '@angular/material/dialog';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { Vocabulary } from '../../models/vocabulary.interface';

describe('VocabularyFormFieldComponent', () => {
  let component: VocabularyFormFieldComponent;
  let fixture: ComponentFixture<VocabularyFormFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MatAutocompleteModule,
        ReactiveFormsModule,
        VocabularyFormFieldComponent,
      ],
      providers: [
        FormBuilder,
        {
          provide: MatDialog,
          useValue: [{ open: () => {} }],
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VocabularyFormFieldComponent);
    component = fixture.componentInstance;
    component.vocabulary = { entries: ['entry1'] } as Vocabulary;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
