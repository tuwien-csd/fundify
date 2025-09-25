import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { CareerStageEnum } from 'src/app/shared/models/enums/career-stage.enum';
import { FundingCharacteristicEnum } from 'src/app/shared/models/enums/funding-characteristic.enum';
import { FundingRegexp } from 'src/app/shared/models/enums/funding-regexp.enum';
import { FundingSchemeEnum } from 'src/app/shared/models/enums/funding-scheme.enum';
import { LegalTypeEnum } from 'src/app/shared/models/enums/legal-type.enum';
import { TargetGroupEnum } from 'src/app/shared/models/enums/target-group.enum';
import { ViewEnum } from 'src/app/shared/models/enums/view.enum';
import { PROGRAM_DETIALS_CONSTANTS } from '../../programs.constants';
import { BUTTON_LABELS } from 'src/app/shared/shared.constants';
import { Router } from '@angular/router';
import { ROUTER_LINKS } from '../../../core/router-links.constants';
import { EntryOriginEnum } from '../../../shared/models/enums/entry-origin.enum';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { ActionPermissions } from '../../../core/models/ActionPermissions';
import { Program } from '../../models/program.interface';
import {
  LOCAL_STORAGE_KEYS,
  LocalStorageService,
} from '../../../core/services/local-storage.service';
import { PermissionContext } from '../../../core/models/enums/permission-context.enum';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardTitle,
} from '@angular/material/card';
import { MatChip } from '@angular/material/chips';
import { TranslatedTextFieldComponent } from '../../../shared/components/translated-text-field/translated-text-field.component';
import { TranslatedTextsFieldComponent } from '../../../shared/components/translated-texts-field/translated-texts-field.component';
import { MultipleChoiceFieldComponent } from '../../../shared/components/multiple-choice-field/multiple-choice-field.component';
import { TextFieldComponent } from '../../../shared/components/text-field/text-field.component';
import { DateRangeFieldComponent } from '../../../shared/components/date-range-field/date-range-field.component';
import { TextsFieldComponent } from '../../../shared/components/texts-field/texts-field.component';
import { SubjectFieldComponent } from '../../../shared/components/subject-field/subject-field.component';
import { SingleChoiceFieldComponent } from '../../../shared/components/single-choice-field/single-choice-field.component';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
  selector: 'app-program-detail-edit',
  templateUrl: './program-detail-edit.component.html',
  styleUrls: ['./program-detail-edit.component.scss'],
  imports: [
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatChip,
    MatCardContent,
    FormsModule,
    ReactiveFormsModule,
    TranslatedTextFieldComponent,
    TranslatedTextsFieldComponent,
    MultipleChoiceFieldComponent,
    TextFieldComponent,
    DateRangeFieldComponent,
    TextsFieldComponent,
    SubjectFieldComponent,
    SingleChoiceFieldComponent,
    MatCardActions,
    MatButton,
    MatIcon,
    MatTooltip,
  ],
})
export class ProgramDetailEditComponent implements OnInit {
  private permissionService = inject(PermissionService);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private localStorageService = inject(LocalStorageService);

  protected readonly PROGRAM_DETAILS_CONSTANTS = PROGRAM_DETIALS_CONSTANTS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly PublicationStatusEnum = PublicationStatusEnum;

  @Input() program!: Program;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  @Output() saveAsDraft = new EventEmitter<any>();
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  @Output() publish: EventEmitter<any> = new EventEmitter<any>();
  @Output() back = new EventEmitter<void>();
  @Output() validate = new EventEmitter();
  @Output() preview = new EventEmitter<ViewEnum>();

  permissions!: ActionPermissions;
  detailsForm!: FormGroup;

  PATTERN_FOR_ACRONYM: string = FundingRegexp.ACRONYM;

  fundingSchemes: typeof FundingSchemeEnum = FundingSchemeEnum;
  legalTypes: typeof LegalTypeEnum = LegalTypeEnum;
  targetGroups: typeof TargetGroupEnum = TargetGroupEnum;
  careerStages: typeof CareerStageEnum = CareerStageEnum;
  fundingCharacteristics: typeof FundingCharacteristicEnum =
    FundingCharacteristicEnum;

  ngOnInit() {
    this.loadStagedChanges();
    this.initPermissions();
    if (!this.permissions?.canEdit) {
      this.router.navigate([ROUTER_LINKS.NOT_AUTHORIZED]);
    }
    this.initForm();
  }

  onBack(): void {
    this.back.emit();
  }

  onSaveAsDraft() {
    this.saveAsDraft.emit({
      ...this.program,
      ...this.detailsForm.getRawValue(),
    });
  }

  onPublish() {
    this.publish.emit({ ...this.program, ...this.detailsForm.getRawValue() });
  }

  onPreview() {
    this.stageChanges();
    this.preview.emit(ViewEnum.PREVIEW);
  }

  private initForm() {
    this.detailsForm = this.fb.group({
      name: [this.program.name],
      acronym: [this.program.acronym],
      programTracks: [this.program.programTracks],
      targetGroups: [this.program.targetGroups],
      careerStages: [this.program.careerStages],
      description: [this.program.description],
      characteristics: [this.program.characteristics],
      fundingScheme: [this.program.fundingScheme],
      legalType: [this.program.legalType],
      website: [this.program.website],
      subjects: [this.program.subjects],
      duration: [this.program.duration],
      funder: [this.program.funder],
    });
  }

  getStatus(): PublicationStatusEnum {
    return this.program?.status ?? PublicationStatusEnum.DRAFT;
  }

  getOwnerId(): string {
    return this.program?.funder?.id ?? '';
  }

  getOrigin(): EntryOriginEnum {
    return this.program?.entryOrigin ?? EntryOriginEnum.REFOP;
  }

  private initPermissions(): void {
    const context = this.program.id
      ? PermissionContext.EXISTING
      : PermissionContext.NEW;
    this.permissions = this.permissionService.getPermissions(
      this.getOrigin(),
      this.getStatus(),
      this.getOwnerId(),
      context
    );
  }

  private loadStagedChanges(): void {
    const stagedChanges = this.localStorageService.load(
      LOCAL_STORAGE_KEYS.STAGED_PROGRAM_CHANGES
    );
    if (stagedChanges) {
      this.program = { ...this.program, ...stagedChanges };
    }
  }

  private stageChanges(): void {
    if (this.detailsForm.dirty) {
      this.localStorageService.save(
        LOCAL_STORAGE_KEYS.STAGED_PROGRAM_CHANGES,
        this.detailsForm.getRawValue()
      );
    }
  }
}
