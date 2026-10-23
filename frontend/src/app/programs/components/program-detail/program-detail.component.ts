import { DatePipe } from '@angular/common';
import {
  Component,
  EventEmitter,
  inject,
  input,
  OnInit,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ProgramVersionHistoryDialogComponent, ProgramVersionHistoryDialogData } from '../program-version-history-dialog/program-version-history-dialog.component';
import { ViewEnum } from 'src/app/shared/models/enums/view.enum';
import { TranslatedText } from 'src/app/shared/models/interfaces/translated-text.interface';
import { PROGRAM_DETAILS_CONSTANTS } from '../../programs.constants';
import { BUTTON_LABELS } from 'src/app/shared/shared.constants';
import { EntryOriginEnum } from '../../../shared/models/enums/entry-origin.enum';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { ActionPermissions } from '../../../core/models/ActionPermissions';
import { ProgramWebModel } from '../../models/program.interface';
import { DateRange } from '../../../shared/models/interfaces/date-range.interface';
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
import { ApiModels } from '../../../shared/models/backend-api-models';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProgramStore } from '../../signal/program-store';
import { ROUTER_LINKS } from '../../../core/router-links.constants';

@Component({
  selector: 'app-program-detail',
  templateUrl: './program-detail.component.html',
  styleUrls: ['./program-detail.component.scss'],
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
    RouterLink,
  ],
})
export class ProgramDetailComponent implements OnInit {
  private readonly permissionService = inject(PermissionService);
  private readonly validationService = inject(ProgramValidationService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly programStore = inject(ProgramStore);
  private readonly dialog = inject(MatDialog);

  protected readonly CONSTANTS = PROGRAM_DETAILS_CONSTANTS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly PublicationStatusEnum = PublicationStatusEnum;
  program = input.required<ProgramWebModel>();
  @Output() edit = new EventEmitter<ViewEnum>();
  @Output() back = new EventEmitter<void>();
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  @Output() publish = new EventEmitter<any>();

  permissions!: ActionPermissions;

  displayedColumns: string[] = ['label', 'value'];
  programDetails: { label: string; value: string }[] = [];
  isValid!: boolean;

  ngOnInit(): void {
    this.generateProgramDetails();
    this.initPermissions();
    this.isValid = this.validationService.validate(this.program());
  }

  onEdit(): void {
    this.router.navigate(['./edit'], { relativeTo: this.route });
  }

  onShowHistory(): void {
    const data: ProgramVersionHistoryDialogData = {
      programId: this.program().id!,
      currentFields: {
        description: this.program().description,
        duration: this.program().duration,
      },
    };
    this.dialog.open(ProgramVersionHistoryDialogComponent, { width: '680px', data });
  }

  onPublish(): void {
    this.programStore
      .update({
        ...this.program(),
        status: PublicationStatusEnum.PUBLISHED,
      })
      .then((updatedProgram) => {
        if (updatedProgram?.id) {
          this.router.navigate([
            `${ROUTER_LINKS.FUNDINGS}/${ROUTER_LINKS.PROGRAMS}`,
            updatedProgram.id,
          ]);
        }
      });
    this.publish.emit(this.program());
  }

  initPermissions(): void {
    const context = this.program().id
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
    return this.program()?.funder?.acronym ?? '';
  }

  getOrigin(): EntryOriginEnum {
    return this.program()?.entryOrigin ?? EntryOriginEnum.REFOP;
  }

  getStatus(): PublicationStatusEnum {
    return this.program()?.status ?? PublicationStatusEnum.DRAFT;
  }

  private generateProgramDetails(): void {
    const datePipe = new DatePipe('en-US');
    this.programDetails = [
      {
        label: this.CONSTANTS.RIS_ID_LABEL,
        value: this.program().risId ?? PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CONSTANTS.NAME_LABEL,
        value: this.formatTranslatedText(this.program().name ?? []),
      },
      {
        label: this.CONSTANTS.ACRONYM_LABEL,
        value: this.program().acronym ?? PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CONSTANTS.PROGRAM_TRACK_LABEL,
        value: this.formatProgramTracks(),
      },
      {
        label: this.CONSTANTS.TARGET_GROUPS_LABEL,
        value:
          this.program().targetGroups?.join(', ') ??
          PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CONSTANTS.CAREER_STAGES_LABEL,
        value:
          this.program().careerStages?.join(', ') ??
          PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CONSTANTS.PROGRAM_DATE_RANGE_LABEL,
        value:
          this.formatDateRange(this.program().duration, datePipe) ??
          PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CONSTANTS.DESCRIPTION_LABEL,
        value: this.formatTranslatedText(this.program().description ?? []),
      },
      {
        label: this.CONSTANTS.CHARACTERISTICS_LABEL,
        value:
          this.program().characteristics?.join(', ') ??
          PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CONSTANTS.FUNDING_SCHEMES_LABEL,
        value:
          this.program().fundingScheme ?? PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CONSTANTS.LEGAL_TYPE_LABEL,
        value:
          this.program().legalType ?? PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CONSTANTS.WEBSITE_LABEL,
        value:
          this.program().website?.join(', ') ??
          PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CONSTANTS.SUBJECTS_LABEL,
        value: this.formatStandardizedSubjects(),
      },
      {
        label: this.CONSTANTS.FUNDER_LABEL,
        value: this.formatFunder(),
      },
    ];
  }

  private formatTranslatedText(translatedTexts: TranslatedText[]): string {
    if (!translatedTexts) return PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER;

    return translatedTexts
      .map((tt) => `${tt.text} (${tt.language})`)
      .join(', ');
  }

  private formatDateRange(
    dateRange: DateRange | undefined,
    datePipe: DatePipe
  ): string {
    if (!dateRange?.start || !dateRange?.end) {
      return this.CONSTANTS.PLACEHOLDER;
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
      this.program().programTracks ?? [];
    return (
      programmeTracks
        ?.map((tracks) => this.formatTranslatedText(tracks))
        .join(', ') ?? PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER
    );
  }

  private formatFunder(): string {
    const funderRef: ApiModels['FunderRefWebModel'] =
      this.program().funder ?? ({} as ApiModels['FunderRefWebModel']);
    return funderRef
      ? `${this.formatTranslatedText(funderRef.name!)} (${funderRef.id})`
      : PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER;
  }

  private formatStandardizedSubjects(): string {
    const subjects = this.program().subjects ?? [];
    return (
      subjects?.map((subject) => subject.title).join(', ') ??
      PROGRAM_DETAILS_CONSTANTS.PLACEHOLDER
    );
  }

  protected readonly PROGRAM_DETAILS_CONSTANTS = PROGRAM_DETAILS_CONSTANTS;
}
