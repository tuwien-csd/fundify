import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VocabularyModalComponent } from './vocabulary-modal.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

describe('VocabularyModalComponent', () => {
  let component: VocabularyModalComponent;
  let fixture: ComponentFixture<VocabularyModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VocabularyModalComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VocabularyModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
