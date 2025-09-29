import {
  Component,
  Input,
  OnDestroy,
  OnInit,
  inject,
  Injector,
  AfterViewInit,
} from '@angular/core';
import { SubjectService } from '../../../core/services/subject.service';
import {
  AbstractControl,
  ControlValueAccessor,
  UntypedFormArray,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  FormsModule,
  ReactiveFormsModule,
  NgControl,
  TouchedChangeEvent,
} from '@angular/forms';
import {
  MatTableDataSource,
  MatTable,
  MatColumnDef,
  MatHeaderCellDef,
  MatHeaderCell,
  MatCellDef,
  MatCell,
  MatHeaderRowDef,
  MatHeaderRow,
  MatRowDef,
  MatRow,
} from '@angular/material/table';
import { Subscription } from 'rxjs';
import { createCheckboxGroupValidator } from '../../validators/checkbox-group.validator';
import { getFormValidationErrors } from '../../utils/validator-util';
import { SUBJECT_LABELS, VALIDATORS } from '../../shared.constants';
import { wrapLabelRequiredOrOptional } from '../../utils/display-util';
import { ValidationService } from '../../services/validation.service';
import { StandardizedSubject } from '../../models/interfaces/standardizedSubject.interface';
import {
  MatFormField,
  MatLabel,
  MatSuffix,
  MatError,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatCheckbox } from '@angular/material/checkbox';

@Component({
  selector:
    'app-subject-field[formControl],' +
    'app-subject-field[formControlName],' +
    'app-subject-field[ngModel]',
  templateUrl: './subject-field.component.html',
  styleUrls: ['./subject-field.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: SubjectFieldComponent,
    },
    {
      provide: NG_VALIDATORS,
      multi: true,
      useExisting: SubjectFieldComponent,
    },
  ],
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatIcon,
    MatSuffix,
    MatTable,
    MatColumnDef,
    MatHeaderCellDef,
    MatHeaderCell,
    MatCheckbox,
    MatCellDef,
    MatCell,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatError,
  ],
})
export class SubjectFieldComponent
  implements OnInit, OnDestroy, Validator, ControlValueAccessor, AfterViewInit
{
  private subjectService = inject(SubjectService);
  private fb = inject(UntypedFormBuilder);
  validationService = inject(ValidationService);
  private injector = inject(Injector);

  @Input() required = false;
  @Input() label = '';

  constants = SUBJECT_LABELS;

  subjects: StandardizedSubject[] = [];
  dataSource: MatTableDataSource<StandardizedSubject> =
    new MatTableDataSource<StandardizedSubject>([]);
  tableForm: UntypedFormGroup;

  displayedColumns = ['select', 'code', 'title', 'expand'];
  allSelected = false;
  allExpanded = false;
  expanded: boolean[] = [];

  validators = VALIDATORS;

  constructor() {
    this.tableForm = this.fb.group({});
  }

  ngOnInit() {
    this.subjects = this.subjectService.getSubjects();
    this.dataSource.data = this.subjects;
    this.tableForm = this.fb.group({
      selection: this.fb.array([], createCheckboxGroupValidator(this.required)),
    });
    this.subjects.forEach((subject) => {
      this.selection.push(new UntypedFormControl(false));
      if (subject.level === 1) {
        this.expanded.push(this.allExpanded);
      }
    });
    this.label = wrapLabelRequiredOrOptional(this.label, this.required);
  }

  ngOnDestroy() {
    this.touchedChangeSub?.unsubscribe();
    for (const sub of this.onChangeSubs) {
      sub.unsubscribe();
    }
  }

  private touchedChangeSub?: Subscription;

  ngAfterViewInit() {
    // Workaround for propagating touched state to all nested child controls:
    // as markAllAsTouched is not propagated to the child controls
    // we subscribe to the TouchedChangeEvent and mark all controls as touched.
    this.touchedChangeSub = this.injector
      .get(NgControl)
      .control!.events.subscribe((event) => {
        if (event instanceof TouchedChangeEvent) {
          this.tableForm.markAllAsTouched();
          this.tableForm.updateValueAndValidity();
        }
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  onTouched: Function = () => {};

  onChangeSubs: Subscription[] = [];

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnChange(onChange: any): void {
    const sub = this.selection.valueChanges.subscribe((values: boolean[]) => {
      const selected = values
        .map((isChecked: boolean, i: number) =>
          isChecked ? this.subjects[i] : null
        )
        .filter(Boolean);
      onChange(selected);
    });
    this.onChangeSubs.push(sub);
  }

  // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  registerOnTouched(onTouched: Function) {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean) {
    if (disabled) {
      this.tableForm.disable();
    } else {
      this.tableForm.enable();
    }
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  validate(control: AbstractControl): ValidationErrors | null {
    if (this.tableForm.valid) {
      return null;
    }
    return getFormValidationErrors(this.tableForm);
  }

  writeValue(value: StandardizedSubject[]) {
    if (value) {
      const boolValue = this.subjects.map((subject) =>
        value.some((s) => s.code === subject.code)
      );
      this.selection.setValue(boolValue);
    }
  }

  applyFilter(event: Event) {
    this.allExpanded = true;
    this.expanded.fill(true);
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  toggleExpandAll() {
    this.allExpanded = !this.allExpanded;
    this.expanded = this.expanded.map(() => this.allExpanded);
  }

  toggleExpand(subject: StandardizedSubject) {
    if (this.isParent(subject)) {
      const parentIndex = +subject.code[0] - 1;
      const expandedRowStates: boolean[] = this.expanded;
      expandedRowStates[parentIndex] = !expandedRowStates[parentIndex];
      this.expanded = expandedRowStates;
    }
  }

  private isExpanded(subject: StandardizedSubject) {
    return this.expanded[+subject.code[0] - 1];
  }

  getExpandedIcon(subject: StandardizedSubject) {
    return this.isExpanded(subject) ? 'expand_less' : 'expand_more';
  }

  isParent(subject: StandardizedSubject) {
    return subject.level === 1;
  }

  onChangeSelectedAll(checked: boolean) {
    this.allSelected = checked;
    this.selection.setValue(this.subjects.map(() => checked));
    this.selection.markAsTouched();
  }

  onChangeSelection(checked: boolean, subject: StandardizedSubject) {
    if (subject.level === 1) {
      this.subjects
        .filter((s) => s.level === 2 && s.code.startsWith(subject.code))
        .forEach((s) => this.getSelectionControl(s).setValue(checked));
    }
    if (subject.level === 2) {
      const parentSubject = this.subjects.find(
        (s) => s.level === 1 && s.code === subject.code[0]
      );
      if (parentSubject) {
        const level2Subjects = this.subjects.filter(
          (s) => s.level === 2 && s.code.startsWith(subject.code[0])
        );
        const allUnchecked = level2Subjects.every(
          (s) => !this.getSelectionControl(s).value
        );
        this.getSelectionControl(parentSubject).setValue(!allUnchecked);
      }
    }
  }

  someSelected(): boolean {
    return (
      this.selection.value.some((value: boolean) => value) && !this.allSelected
    );
  }

  getSelectionControl(subject: StandardizedSubject): UntypedFormControl {
    const index = this.subjects.indexOf(subject);
    return this.selection.at(index) as UntypedFormControl;
  }

  get selection(): UntypedFormArray {
    return this.tableForm.get('selection') as UntypedFormArray;
  }

  isHidden(subject: StandardizedSubject): boolean {
    const parentIndex = +subject.code[0] - 1;
    return subject.level === 2 && !this.expanded[parentIndex];
  }
}
