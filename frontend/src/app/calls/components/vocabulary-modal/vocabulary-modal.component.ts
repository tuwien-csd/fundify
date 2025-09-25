import { Component, EventEmitter, inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,
} from '@angular/material/dialog';
import { CdkScrollable } from '@angular/cdk/scrolling';
import {
  MatChipListbox,
  MatChipOption,
  MatChipRemove,
} from '@angular/material/chips';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-vocabulary-modal',
  templateUrl: './vocabulary-modal.component.html',
  styleUrls: ['./vocabulary-modal.component.scss'],
  imports: [
    MatDialogTitle,
    CdkScrollable,
    MatDialogContent,
    MatChipListbox,
    MatChipOption,
    MatChipRemove,
    MatIcon,
    MatFormField,
    MatLabel,
    MatInput,
    FormsModule,
    MatDialogActions,
    MatButton,
    MatDialogClose,
  ],
})
export class VocabularyModalComponent {
  data = inject(MAT_DIALOG_DATA);

  entryAdded = new EventEmitter<string>();
  entryRemoved = new EventEmitter<string>();
  newEntry: string = '';

  addEntry() {
    const entry = this.newEntry.trim();
    if (entry && !this.data.entries.includes(entry)) {
      this.data.entries = [...this.data.entries, entry];
      this.entryAdded.emit(entry);
    }
    this.newEntry = '';
  }

  removeEntry(entry: string) {
    this.data.entries = this.data.entries.filter((e: string) => e !== entry);
    this.entryRemoved.emit(entry);
  }
}
