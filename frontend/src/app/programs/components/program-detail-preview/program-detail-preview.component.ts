import { DatePipe } from '@angular/common';
import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { ViewEnum } from 'src/app/shared/models/enums/view.enum';
import { TranslatedText } from 'src/app/shared/models/interfaces/translated-text.interface';
import { PROGRAM_DETIALS_CONSTANTS } from '../../programs.constants';
import { BUTTON_LABELS } from 'src/app/shared/shared.constants';
import { StandardizedSubject } from '../../../shared/models/interfaces/standardizedSubject.interface';
import { EntryOriginEnum } from '../../../shared/models/enums/entry-origin.enum';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { ActionPermissions } from '../../../core/models/ActionPermissions';
import { Program } from '../../models/program.interface';
import { DateRange } from '../../../shared/models/interfaces/date-range.interface';
import { FundingEntityRef } from '../../../shared/models/interfaces/funding-entity-ref.interface';
import {
  LOCAL_STORAGE_KEYS,
  LocalStorageService,
} from '../../../core/services/local-storage.service';
import { PermissionContext } from '../../../core/models/enums/permission-context.enum';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';
import { ProgramValidationService } from '../../services/program-validation.service';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardTitle,
} from '@angular/material/card';
import { MatChip } from '@angular/material/chips';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
  selector: 'app-program-detail-preview',
  templateUrl: './program-detail-preview.component.html',
  styleUrls: ['./program-detail-preview.component.scss'],
  imports: [
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatChip,
    MatCardContent,
    MatCardActions,
    MatButton,
    MatIcon,
    MatTooltip,
  ],
})
export class ProgramDetailPreviewComponent implements OnInit {
  private localStorageService = inject(LocalStorageService);
  private permissionService = inject(PermissionService);
  private validationService = inject(ProgramValidationService);

  protected readonly PROGRAM_DETIALS_CONSTANTS = PROGRAM_DETIALS_CONSTANTS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly PublicationStatusEnum = PublicationStatusEnum;

  @Input() program!: Program;
  @Output() edit = new EventEmitter<ViewEnum>();
  @Output() back = new EventEmitter<void>();
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  @Output() publish = new EventEmitter<any>();

  permissions!: ActionPermissions;

  displayedColumns: string[] = ['label', 'value'];
  programDetails: { label: string; value: string }[] = [];
  isValid!: boolean;

  ngOnInit(): void {
    this.loadStagedChanges();
    this.generateProgramDetails();
    this.initPermissions();
    this.isValid = this.validationService.validate(this.program);
  }

  onBack(): void {
    this.back.emit();
  }

  onEdit(): void {
    this.edit.emit(ViewEnum.EDIT);
  }

  onPublish(): void {
    this.publish.emit(this.program);
  }

  initPermissions(): void {
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

  getOwnerId(): string {
    return this.program?.funder?.id ?? '';
  }

  getOrigin(): EntryOriginEnum {
    return this.program?.entryOrigin ?? EntryOriginEnum.REFOP;
  }

  getStatus(): PublicationStatusEnum {
    return this.program?.status ?? PublicationStatusEnum.DRAFT;
  }

  private generateProgramDetails(): void {
    const datePipe = new DatePipe('en-US');
    this.programDetails = [
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.RIS_ID_LABEL,
        value: this.program.risId ?? PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.NAME_LABEL,
        value: this.formatTranslatedText(this.program.name ?? []),
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.ACRONYM_LABEL,
        value: this.program.acronym ?? PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.PROGRAM_TRACK_LABEL,
        value: this.formatProgramTracks(),
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.TARGET_GROUPS_LABEL,
        value:
          this.program.targetGroups?.join(', ') ??
          PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.CAREER_STAGES_LABEL,
        value:
          this.program.careerStages?.join(', ') ??
          PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.PROGRAM_DATE_RANGE_LABEL,
        value:
          this.formatDateRange(this.program.duration, datePipe) ??
          PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.DESCRIPTION_LABEL,
        value: this.formatTranslatedText(this.program.description ?? []),
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.CHARACTERISTICS_LABEL,
        value:
          this.program.characteristics?.join(', ') ??
          PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.FUNDING_SCHEMES_LABEL,
        value:
          this.program.fundingScheme ?? PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.LEGAL_TYPE_LABEL,
        value: this.program.legalType ?? PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.WEBSITE_LABEL,
        value:
          this.program.website?.join(', ') ??
          PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.SUBJECTS_LABEL,
        value: this.formatStandardizedSubjects(),
      },
      {
        label: this.PROGRAM_DETIALS_CONSTANTS.FUNDER_LABEL,
        value: this.formatFunder(),
      },
    ];
  }

  private formatTranslatedText(translatedTexts: TranslatedText[]): string {
    if (!translatedTexts) return PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER;

    return translatedTexts
      .map((tt) => `${tt.text} (${tt.language})`)
      .join(', ');
  }

  private formatDateRange(
    dateRange: DateRange | undefined,
    datePipe: DatePipe
  ): string {
    if (!dateRange?.start || !dateRange?.end) {
      return this.PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER;
    }
    const formattedStartDate = datePipe.transform(
      dateRange.start,
      'dd.MM.yyyy'
    );
    const formattedEndDate = datePipe.transform(dateRange.end, 'dd.MM.yyyy');
    return `${formattedStartDate} - ${formattedEndDate}`;
  }

  private formatProgramTracks(): string {
    const programmeTracks: TranslatedText[][] =
      this.program.programTracks ?? [];
    return (
      programmeTracks
        ?.map((tracks) => this.formatTranslatedText(tracks))
        .join(', ') ?? PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER
    );
  }

  private formatFunder(): string {
    const funderRef: FundingEntityRef =
      this.program.funder ?? ({} as FundingEntityRef);
    return funderRef
      ? `${this.formatTranslatedText(funderRef.name)} (${funderRef.id})`
      : PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER;
  }

  private formatStandardizedSubjects(): string {
    const subjects: StandardizedSubject[] = this.program.subjects ?? [];
    return (
      subjects?.map((subject) => subject.title).join(', ') ??
      PROGRAM_DETIALS_CONSTANTS.PLACEHOLDER
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

  protected readonly PROGRAM_DETAILS_CONSTANTS = PROGRAM_DETIALS_CONSTANTS;
}
