import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { FundingEntityRef } from '../../../shared/models/interfaces/funding-entity-ref.interface';
import { ViewEnum } from 'src/app/shared/models/enums/view.enum';
import { Contact } from 'src/app/shared/models/interfaces/contact.interface';
import { Duration } from 'src/app/shared/models/interfaces/duration.interface';
import { MonetaryNumber } from 'src/app/shared/models/interfaces/monetary-number.interface';
import { TranslatedText } from 'src/app/shared/models/interfaces/translated-text.interface';
import { CALL_DETAILS_CONSTANTS } from '../../calls.constants';
import { BUTTON_LABELS } from 'src/app/shared/shared.constants';
import { StandardizedSubject } from '../../../shared/models/interfaces/standardizedSubject.interface';
import { DateInfoRange } from 'src/app/shared/models/interfaces/date-info-range.interface';
import { DateRange } from 'src/app/shared/models/interfaces/date-range.interface';
import { DatePipe } from '@angular/common';
import { EntryOriginEnum } from '../../../shared/models/enums/entry-origin.enum';
import { ActionPermissions } from '../../../core/models/ActionPermissions';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { Call } from '../../models/call.interface';
import { IdentifierTypeEnum } from '../../../shared/models/enums/identifier-type.enum';
import {
  LOCAL_STORAGE_KEYS,
  LocalStorageService,
} from '../../../core/services/local-storage.service';
import { PermissionContext } from '../../../core/models/enums/permission-context.enum';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';
import { CallValidationService } from '../../services/call-validation.service';
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
  selector: 'app-call-detail-preview',
  templateUrl: './call-detail-preview.component.html',
  styleUrls: ['./call-detail-preview.component.scss'],
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
export class CallDetailPreviewComponent implements OnInit {
  private localStorageService = inject(LocalStorageService);
  private permissionService = inject(PermissionService);
  private validationService = inject(CallValidationService);

  protected readonly CALL_DETAILS_CONSTANTS = CALL_DETAILS_CONSTANTS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly PublicationStatusEnum = PublicationStatusEnum;

  @Input() call!: Call;
  @Output() edit = new EventEmitter<ViewEnum>();
  @Output() back = new EventEmitter<void>();
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  @Output() publish = new EventEmitter<any>();

  permissions!: ActionPermissions;

  callDetails: { label: string; value: string; href?: string }[] = [];
  isValid!: boolean;

  ngOnInit(): void {
    this.loadStagedChanges();
    this.generateCallDetails();
    this.initPermissions();
    this.isValid = this.validationService.validate(this.call);
  }

  onBack(): void {
    this.back.emit();
  }

  onEdit(): void {
    this.edit.emit(ViewEnum.EDIT);
  }

  onPublish(): void {
    this.publish.emit(this.call);
  }

  private initPermissions(): void {
    const context = this.call.id
      ? PermissionContext.EXISTING
      : PermissionContext.NEW;
    this.permissions = this.permissionService.getPermissions(
      this.getEntryOrigin(),
      this.getStatus(),
      this.getOwnerId(),
      context
    );
  }

  getOwnerId(): string {
    return this.call.callOwner?.acronym ?? '';
  }

  getEntryOrigin(): EntryOriginEnum {
    return this.call.entryOrigin ?? EntryOriginEnum.REFOP;
  }

  getStatus(): PublicationStatusEnum {
    return this.call.status ?? PublicationStatusEnum.DRAFT;
  }

  private generateCallDetails(): void {
    this.callDetails = [
      {
        label: this.CALL_DETAILS_CONSTANTS.APPlICATION_LANGUAGE_LABEL,
        value:
          this.call.applicationLanguages?.join(', ') ??
          CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.RIS_ID_LABEL,
        value: this.call.risId ?? CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      ...(this.getEuId() ? [{
        label: this.CALL_DETAILS_CONSTANTS.EU_ID_LABEL,
        value: this.getEuId()!,
      }] : []),
      {
        label: this.CALL_DETAILS_CONSTANTS.PART_OF_LABEL,
        value: this.formatFundingRef(this.call.partOf),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.NAME_LABEL,
        value: this.formatTranslatedText(this.call.name),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.ACRONYM_LABEL,
        value: this.call.acronym ?? CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.TARGET_GROUP_LABEL,
        value:
          this.call.targetGroups?.join(', ') ??
          CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.TARGET_GROUP_SPECIFIED_LABEL,
        value: this.formatTranslatedText(this.call.targetGroupDetails),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.CAREER_STAGE_LABEL,
        value:
          this.call.careerStages?.join(', ') ??
          CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.THEMATIC_ORIENTATIONS_LABEL,
        value: this.formatTranslatedTextList(this.call.thematicOrientations),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.DESCRIPTION_LABEL,
        value: this.formatTranslatedText(this.call.description),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.ELIGIBLE_APPLICANTS_LABEL,
        value: this.formatTranslatedText(this.call.eligibleApplicants),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.INKIND_DETAILS_LABEL,
        value: this.formatTranslatedText(this.call.inkindDetails),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.OVERHEAD_DETAILS_LABEL,
        value: this.formatTranslatedText(this.call.overheadDetails),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.DECISION_PROCESS_DETAILS_LABEL,
        value: this.formatTranslatedText(this.call.decisionProcessDetails),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.REPORTING_PERIODS_LABEL,
        value: this.formatTranslatedText(this.call.reportingPeriodDetails),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.STAGES_LABEL,
        value: this.formatCallStages(new DatePipe('en-US')),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.DMP_REQUIRED_LABEL,
        value: this.call.dmpRequired ?? CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.DMP_GUIDELINES_LABEL,
        value:
          this.call.dmpGuidelines?.toString() ??
          CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.PROJECT_START_DETAILS_LABEL,
        value: this.formatTranslatedText(this.call.projectStartDetails),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.WEBSITE_LABEL,
        value:
          this.call.website?.join(', ') ?? CALL_DETAILS_CONSTANTS.PLACEHOLDER,
        href: this.call.website?.[0]?.toString(),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.CONTACTS_LABEL,
        value: this.formatContacts(this.call.contacts),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.FUNDING_TYPE_LABEL,
        value: this.call.fundingType ?? CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.FUNDING_SCHEMES_LABEL,
        value: this.call.fundingScheme ?? CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.LEGAL_TYPE_LABEL,
        value: this.call.legalType ?? CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.ELIGIBLE_APPLICANTS_SCOPE_LABEL,
        value:
          this.call.eligibleApplicantsScope ??
          CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
       {
         label: this.CALL_DETAILS_CONSTANTS.ELIGIBLE_APPLICANTS_REGION_LABEL,
         value:
           this.call.eligibleApplicantsRegions?.join(', ') ??
           CALL_DETAILS_CONSTANTS.PLACEHOLDER,
       },
       {
         label: this.CALL_DETAILS_CONSTANTS.ELIGIBLE_TARGET_REGIONS_LABEL,
         value:
           this.call.eligibleTargetRegions?.join(', ') ??
           CALL_DETAILS_CONSTANTS.PLACEHOLDER,
       },
       {
         label: this.CALL_DETAILS_CONSTANTS.ELIGIBLE_SOURCE_REGIONS_LABEL,
         value:
           this.call.eligibleSourceRegions?.join(', ') ??
           CALL_DETAILS_CONSTANTS.PLACEHOLDER,
       },
       {
         label: this.CALL_DETAILS_CONSTANTS.MODE_OF_SUBMISSION_LABEL,
         value:
           this.call.submissionModes?.join(', ') ??
           CALL_DETAILS_CONSTANTS.PLACEHOLDER,
       },
      {
        label: this.CALL_DETAILS_CONSTANTS.CHARACTERISTICS_LABEL,
        value:
          this.call.characteristics?.join(', ') ??
          CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.DECISION_PROCESS_LABEL,
        value:
          this.call.decisionProcess?.join(', ') ??
          CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.SUBJECT_LABEL,
        value: this.formatStandardizedSubjects(),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.MIN_PROJECT_DURATION_LABEL,
        value: this.formatDuration(this.call.minProjectDuration),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.MIN_PROJECT_VOLUME_LABEL,
        value: this.formatMonetaryNumber(this.call.minProjectVolume),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.MAX_PROJECT_DURATION_LABEL,
        value: this.formatDuration(this.call.maxProjectDuration),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.MAX_PROJECT_VOLUME_LABEL,
        value: this.formatMonetaryNumber(this.call.maxProjectVolume),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.CALL_VOLUME_AMOUNT_LABEL,
        value: this.formatMonetaryNumber(this.call.callVolumeAmount),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.CALL_VOLUME_PROJCETS_LABEL,
        value:
          this.call.callVolumeProjects?.toString() ??
          CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.FULLY_FUNDED_LABEL,
        value: this.call.fullyFunded ?? CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.MIN_INKIND_LABEL,
        value:
          this.call.minInkind?.toString() ?? CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.MAX_OVERHEAD_LABEL,
        value:
          this.call.maxOverhead?.toString() ??
          CALL_DETAILS_CONSTANTS.PLACEHOLDER,
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.MAIN_FUNDER_LABEL,
        value: this.formatFundingRef(this.call.funder),
      },
      {
        label: this.CALL_DETAILS_CONSTANTS.JOINT_CALL_PARTNER_LABEL,
        value: this.formatJointCallPartners(),
      },
    ];
  }

  private getEuId(): string | undefined {
    return this.call.identifiers?.find(
      (i) => i.type === IdentifierTypeEnum.EU_ID
    )?.value;
  }

  private formatDateRange(
    dateRange: DateRange | undefined,
    datePipe: DatePipe
  ): string {
    if (!dateRange?.start || !dateRange?.end) {
      return this.CALL_DETAILS_CONSTANTS.PLACEHOLDER;
    }
    const formattedStartDate = datePipe.transform(
      dateRange.start,
      'dd.MM.yyyy'
    );
    const formattedEndDate = datePipe.transform(dateRange.end, 'dd.MM.yyyy');
    return `${formattedStartDate} - ${formattedEndDate}`;
  }

  private formatDuration(duration: Duration | undefined): string {
    if (!duration) {
      return this.CALL_DETAILS_CONSTANTS.PLACEHOLDER;
    }
    return `${this.CALL_DETAILS_CONSTANTS.DURATION_SUB_LABELS[0]}: ${duration.months || 0}, ${this.CALL_DETAILS_CONSTANTS.DURATION_SUB_LABELS[1]}: ${duration.days || 0}`;
  }

  private formatMonetaryNumber(
    monetaryNumber: MonetaryNumber | undefined
  ): string {
    if (!monetaryNumber || monetaryNumber.amount == null) {
      return this.CALL_DETAILS_CONSTANTS.PLACEHOLDER;
    }
    return monetaryNumber.amount.toLocaleString('de-DE') + ' ' + monetaryNumber.currency;
  }

  private formatTranslatedTextList(
    translatedTexts: TranslatedText[][] | undefined
  ): string {
    return translatedTexts
      ? translatedTexts.map((tt) => this.formatTranslatedText(tt)).join(', ')
      : this.CALL_DETAILS_CONSTANTS.PLACEHOLDER;
  }

  private formatTranslatedText(
    translatedTexts: TranslatedText[] | undefined
  ): string {
    if (!translatedTexts) return CALL_DETAILS_CONSTANTS.PLACEHOLDER;

    return translatedTexts
      .map((tt) => `${tt.text} (${tt.language})`)
      .join(', ');
  }

  private formatContacts(contacts: Contact[] | undefined): string {
    return contacts
      ? contacts.map((contact) => this.formatContact(contact)).join(', ')
      : this.CALL_DETAILS_CONSTANTS.PLACEHOLDER;
  }

  private formatContact(contact: Contact): string {
    return `${contact.name} (${contact.email}, ${contact.phone})`;
  }

  private formatCallStages(datePipe: DatePipe): string {
    const callstages: DateInfoRange[] = this.call.callStages ?? [];
    let displayString = '';
    let index = 0;
    if (!callstages || callstages.length === 0) {
      return this.CALL_DETAILS_CONSTANTS.PLACEHOLDER;
    }
    callstages.forEach((dateInfoRanges: DateInfoRange) => {
      const dateRange = this.formatDateRange(dateInfoRanges.duration, datePipe);
      const info = this.formatTranslatedText(dateInfoRanges.description);
      displayString += `${this.CALL_DETAILS_CONSTANTS.CALL_STAGE_LABEL} ${index + 1}\n`;
      displayString += `${this.CALL_DETAILS_CONSTANTS.CALL_STAGE_DATE_RANGE_LABEL}: ${dateRange}\n`;
      displayString += `${this.CALL_DETAILS_CONSTANTS.CALL_STAGE_DETAILS_LABEL}: ${info}\n`;
      displayString += '\n';
      index++;
    });
    return displayString;
  }

  private formatStandardizedSubjects(): string {
    const subjects: StandardizedSubject[] = this.call.subjects ?? [];
    return (
      subjects?.map((subject) => subject.title).join(', ') ??
      CALL_DETAILS_CONSTANTS.PLACEHOLDER
    );
  }

  private formatFundingRef(funderRef: FundingEntityRef | undefined): string {
    return funderRef
      ? `${this.formatTranslatedText(funderRef.name)}`
      : CALL_DETAILS_CONSTANTS.PLACEHOLDER;
  }

  private formatJointCallPartners(): string {
    const funderRefs: FundingEntityRef[] = this.call.jointCallPartner ?? [];
    if (!funderRefs || funderRefs.length === 0) {
      return this.CALL_DETAILS_CONSTANTS.PLACEHOLDER;
    }
    return funderRefs.map((ref) => this.formatFundingRef(ref)).join(', ');
  }

  private loadStagedChanges(): void {
    const stagedChanges = this.localStorageService.load(
      LOCAL_STORAGE_KEYS.STAGED_CALL_CHANGES
    );
    if (stagedChanges) {
      this.call = { ...this.call, ...stagedChanges };
    }
  }
}
