import {
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  inject,
} from '@angular/core';
import {
  ControlValueAccessor,
  FormArray,
  FormBuilder,
  FormControl,
  FormGroup,
  NG_VALUE_ACCESSOR,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import {
  Vocabulary,
  VocabularyEntryEvent,
} from '../../models/vocabulary.interface';
import { ENTER } from '@angular/cdk/keycodes';
import { Observable, Subject, Subscription } from 'rxjs';
import {
  debounceTime,
  distinctUntilChanged,
  map,
  startWith,
  switchMap,
  takeUntil,
} from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { VocabularyModalComponent } from '../vocabulary-modal/vocabulary-modal.component';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import {
  MatAutocompleteTrigger,
  MatAutocomplete,
} from '@angular/material/autocomplete';
import {
  MatChipInput,
  MatChipGrid,
  MatChipRow,
  MatChipRemove,
} from '@angular/material/chips';
import { MatOption } from '@angular/material/select';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatIcon } from '@angular/material/icon';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-vocabulary-form-field[formControlName]',
  templateUrl: './vocabulary-form-field.component.html',
  styleUrls: ['./vocabulary-form-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: VocabularyFormFieldComponent,
      multi: true,
    },
  ],
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    FormsModule,
    MatAutocompleteTrigger,
    MatChipInput,
    ReactiveFormsModule,
    MatAutocomplete,
    MatOption,
    MatIconButton,
    MatTooltip,
    MatIcon,
    MatChipGrid,
    MatChipRow,
    MatChipRemove,
    AsyncPipe,
  ],
})
export class VocabularyFormFieldComponent
  implements OnInit, ControlValueAccessor, OnDestroy
{
  private fb = inject(FormBuilder);
  dialog = inject(MatDialog);

  @Input() label: string = '';
  @Input() vocabulary!: Vocabulary;
  @Output() addEntryToVocabulary: EventEmitter<VocabularyEntryEvent> =
    new EventEmitter<VocabularyEntryEvent>();
  @Output() removeEntryFromVocabulary: EventEmitter<VocabularyEntryEvent> =
    new EventEmitter<VocabularyEntryEvent>();

  separatorKeysCodes: number[] = [ENTER];
  entryCtrl!: FormControl;
  filteredOptions!: Observable<string[]>;
  entriesForm!: FormGroup<{ entries: FormArray }>;

  vocabularyUpdates$ = new Subject<string[]>();
  private destroy$ = new Subject<void>();

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  private onChange: Function = () => {};

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  private onTouched: Function = () => {};

  ngOnInit() {
    this.vocabularyUpdates$.next(this.vocabulary.entries);
    this.entriesForm = this.fb.group({
      entries: this.fb.array([]),
    });
    this.entryCtrl = this.fb.control('');
    this.filteredOptions = this.entryCtrl.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((entry) =>
        this.vocabularyUpdates$.pipe(
          startWith(this.vocabulary.entries),
          map((entries) => (entry ? this._filter(entry) : entries))
        )
      )
    );
  }

  manageDialog() {
    const vocabularyConfig = {
      title: `${this.label} Vocabulary`,
      inputLabel: `Add a ${this.label}`,
      entries: this.vocabulary.entries,
    };
    const dialogRef = this.dialog.open(VocabularyModalComponent, {
      width: '600px',
      data: vocabularyConfig,
    });
    dialogRef.componentInstance.entryAdded
      .pipe(takeUntil(this.destroy$))
      .subscribe((entry) => {
        this.vocabularyUpdates$.next([...this.vocabulary.entries, entry]);
        this.addEntryToVocabulary.emit({
          id: this.vocabulary.id,
          entry: entry,
        });
      });
    dialogRef.componentInstance.entryRemoved
      .pipe(takeUntil(this.destroy$))
      .subscribe((entry) => {
        this.vocabularyUpdates$.next(
          this.vocabulary.entries.filter((e) => e !== entry)
        );
        this.removeEntryFromVocabulary.emit({
          id: this.vocabulary.id,
          entry: entry,
        });
      });
  }

  private onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any): void {
    this.onChangeSubs.push(this.entries.valueChanges.subscribe(onChange));
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: any): void {
    this.onTouched = onTouched;
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  writeValue(value: any): void {
    if (value) {
      this.entries.clear();
      value.forEach((entry: string) =>
        this.entries.push(this.fb.control(entry), { emitEvent: false })
      );
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.onChangeSubs.forEach((sub) => sub.unsubscribe());
  }

  add(value: string): void {
    if (!value) {
      return;
    }
    const trimmedValue = value.trim();
    if (!trimmedValue || this.entries.value.includes(trimmedValue)) {
      return;
    }
    this.entries.push(this.fb.control(trimmedValue));
    this.entryCtrl.setValue('');
    // emit event to update vocabulary, if it is not already in the list
    if (!this.vocabulary.entries.includes(trimmedValue)) {
      this.addEntryToVocabulary.emit({
        id: this.vocabulary.id,
        entry: trimmedValue,
      });
    }
  }

  remove(index: number) {
    if (index > -1) {
      this.entries.removeAt(index);
    }
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    return this.vocabulary.entries.filter((entry) =>
      entry.toLowerCase().includes(filterValue)
    );
  }

  get entries(): FormArray {
    return this.entriesForm.get('entries') as FormArray;
  }
}
