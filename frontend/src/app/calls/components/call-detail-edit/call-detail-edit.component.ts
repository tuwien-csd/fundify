import {
  Component,
  computed,
  effect,
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
  Validators,
} from '@angular/forms';
import { AnswerYnEnum } from 'src/app/shared/models/enums/answer-yn.enum';
import { AustrianStateEnum } from 'src/app/shared/models/enums/austrian-state.enum';
import { CallTypeEnum } from 'src/app/shared/models/enums/call-type.enum';
import { CareerStageEnum } from 'src/app/shared/models/enums/career-stage.enum';
import { DecisionProcessEnum } from 'src/app/shared/models/enums/decision-process.enum';
import { FundingCharacteristicEnum } from 'src/app/shared/models/enums/funding-characteristic.enum';
import { FundingRegexp } from 'src/app/shared/models/enums/funding-regexp.enum';
import { FundingSchemeEnum } from 'src/app/shared/models/enums/funding-scheme.enum';
import { LanguageEnum } from 'src/app/shared/models/enums/language.enum';
import { LegalTypeEnum } from 'src/app/shared/models/enums/legal-type.enum';
import { ModeOfSubmissionEnum } from 'src/app/shared/models/enums/mode-of-submission.enum';
import { RegionalScopeEnum } from 'src/app/shared/models/enums/regional-scope.enum';
import { TargetGroupEnum } from 'src/app/shared/models/enums/target-group.enum';
import { ViewEnum } from 'src/app/shared/models/enums/view.enum';
import { CALL_DETAILS_CONSTANTS } from '../../calls.constants';
import {
  BUTTON_LABELS,
  FORM_STATUS_MESSAGES,
} from 'src/app/shared/shared.constants';
import { ROUTER_LINKS } from 'src/app/core/router-links.constants';
import { Router } from '@angular/router';
import { EntryOriginEnum } from '../../../shared/models/enums/entry-origin.enum';
import { ActionPermissions } from '../../../core/models/ActionPermissions';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { Call } from '../../models/call.interface';
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
import { SingleChoiceFieldComponent } from '../../../shared/components/single-choice-field/single-choice-field.component';
import { MultipleChoiceFieldComponent } from '../../../shared/components/multiple-choice-field/multiple-choice-field.component';
import { TranslatedTextFieldComponent } from '../../../shared/components/translated-text-field/translated-text-field.component';
import { TextFieldComponent } from '../../../shared/components/text-field/text-field.component';
import { FundingSearchComponent } from '../../../shared/components/funding-search/funding-search.component';
import { TranslatedTextsFieldComponent } from '../../../shared/components/translated-texts-field/translated-texts-field.component';
import { MonetaryFieldComponent } from '../../../shared/components/monetary-field/monetary-field.component';
import { NumberFieldComponent } from '../../../shared/components/number-field/number-field.component';
import { DurationFieldComponent } from '../../../shared/components/duration-field/duration-field.component';
import { SubjectFieldComponent } from '../../../shared/components/subject-field/subject-field.component';
import { DateRangesInfoFieldComponent } from '../../../shared/components/date-ranges-info-field/date-ranges-info-field.component';
import { ContactFieldComponent } from '../../../shared/components/contact-field/contact-field.component';
import { UrlFieldComponent } from '../../../shared/components/url-field/url-field.component';
import { TextsFieldComponent } from '../../../shared/components/texts-field/texts-field.component';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { ProgramStore } from '../../../programs/signal/program-store';
import { FundersStore } from '../../../funders/signal/funders-store';
import { AuthService } from '../../../core/auth/services/auth.service';
import { FundingEntityRef } from '../../../shared/models/interfaces/funding-entity-ref.interface';
import { TranslatedText } from '../../../shared/models/interfaces/translated-text.interface';
import { FunderSearchComponent } from '../../../shared/components/funder-search/funder-search.component';
import { MatError } from '@angular/material/form-field';

@Component({
  selector: 'app-call-detail-edit',
  templateUrl: './call-detail-edit.component.html',
  styleUrls: ['./call-detail-edit.component.scss'],
  imports: [
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatChip,
    MatCardContent,
    FormsModule,
    ReactiveFormsModule,
    SingleChoiceFieldComponent,
    MultipleChoiceFieldComponent,
    TranslatedTextFieldComponent,
    TextFieldComponent,
    FundingSearchComponent,
    TranslatedTextsFieldComponent,
    MonetaryFieldComponent,
    NumberFieldComponent,
    DurationFieldComponent,
    SubjectFieldComponent,
    DateRangesInfoFieldComponent,
    ContactFieldComponent,
    UrlFieldComponent,
    TextsFieldComponent,
    MatCardActions,
    MatButton,
    MatIcon,
    MatTooltip,
    FunderSearchComponent,
    MatError,
  ],
})
export class CallDetailEditComponent implements OnInit {
  private permissionService = inject(PermissionService);
  private router = inject(Router);
  private fb = inject(FormBuilder);
  private localStorageService = inject(LocalStorageService);
  readonly programStore = inject(ProgramStore);
  protected readonly authService = inject(AuthService);
  readonly fundersStore = inject(FundersStore);

  protected readonly CALL_DETAILS_CONSTANTS = CALL_DETAILS_CONSTANTS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly ROUTER_LINKS = ROUTER_LINKS;
  protected readonly PublicationStatusEnum = PublicationStatusEnum;

  @Input() call!: Call;
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

  eligibleApplicantsScopes: typeof RegionalScopeEnum = RegionalScopeEnum;
  fundingTypes: typeof CallTypeEnum = CallTypeEnum;
  fundingSchemes: typeof FundingSchemeEnum = FundingSchemeEnum;
  legalTypes: typeof LegalTypeEnum = LegalTypeEnum;
  binaryChoice: typeof AnswerYnEnum = AnswerYnEnum;
  targetGroups: typeof TargetGroupEnum = TargetGroupEnum;
  careerStages: typeof CareerStageEnum = CareerStageEnum;
  applicationLanguages: typeof LanguageEnum = LanguageEnum;
  eligibleApplicantsRegions: typeof AustrianStateEnum = AustrianStateEnum;
  modesOfSubmission: typeof ModeOfSubmissionEnum = ModeOfSubmissionEnum;
  decisionProcesses: typeof DecisionProcessEnum = DecisionProcessEnum;
  fundingCharacteristics: typeof FundingCharacteristicEnum =
    FundingCharacteristicEnum;

  submissionErrorMsg: string = '';

  funderRef = computed(() => {
    const affiliationId = this.authService.userAffiliationId();
    const funders = this.fundersStore.entities();
    if (!affiliationId || !funders?.length) return undefined;
    return funders.find(
      (f) => f.acronym?.toUpperCase() === affiliationId.toUpperCase()
    ) as FundingEntityRef | undefined;
  });
  funderRefDisplay = computed(() => this.formatFunder(this.funderRef()));

  constructor() {
    effect(() => {
      const funderRef = this.funderRef();
      if (this.authService.isFunder())
        this.detailsForm.patchValue(
          { funder: funderRef },
          { emitEvent: false }
        );
    });
  }

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
    if (this.detailsForm.invalid) {
      this.detailsForm.markAllAsTouched();
      this.submissionErrorMsg = FORM_STATUS_MESSAGES.VALIDATION_ERROR;
      return;
    }
    this.saveAsDraft.emit({
      ...this.call,
      ...this.detailsForm.getRawValue(),
    });
  }

  onPublish() {
    if (this.detailsForm.invalid) {
      this.detailsForm.markAllAsTouched();
      this.submissionErrorMsg = FORM_STATUS_MESSAGES.VALIDATION_ERROR;
      return;
    }
    this.publish.emit({
      ...this.call,
      ...this.detailsForm.getRawValue(),
    });
  }

  onPreview() {
    this.stageChanges();
    this.preview.emit(ViewEnum.PREVIEW);
  }

  private initForm() {
    this.detailsForm = this.fb.group({
      applicationLanguages: [
        this.call.applicationLanguages,
        Validators.required,
      ],
      partOf: [this.call.partOf],
      jointCallPartner: [this.call.jointCallPartner],
      name: [this.call.name],
      acronym: [this.call.acronym],
      targetGroups: [this.call.targetGroups],
      targetGroupDetails: [this.call.targetGroupDetails],
      careerStages: [this.call.careerStages],
      thematicOrientations: [this.call.thematicOrientations],
      description: [this.call.description],
      eligibleApplicants: [this.call.eligibleApplicants],
      inkindDetails: [this.call.inkindDetails],
      overheadDetails: [this.call.overheadDetails],
      decisionProcessDetails: [this.call.decisionProcessDetails],
      reportingPeriodDetails: [this.call.reportingPeriodDetails],
      callStages: [this.call.callStages],
      dmpGuidelines: [this.call.dmpGuidelines],
      projectStartDetails: [this.call.projectStartDetails],
      website: [this.call.website],
      contacts: [this.call.contacts],
      fundingType: [this.call.fundingType],
      fundingScheme: [this.call.fundingScheme],
      legalType: [this.call.legalType],
      eligibleApplicantsScope: [this.call.eligibleApplicantsScope],
      dmpRequired: [this.call.dmpRequired],
      eligibleApplicantsRegions: [this.call.eligibleApplicantsRegions],
      submissionModes: [this.call.submissionModes],
      characteristics: [this.call.characteristics],
      decisionProcess: [this.call.decisionProcess],
      subjects: [this.call.subjects],
      minProjectDuration: [this.call.minProjectDuration],
      minProjectVolume: [this.call.minProjectVolume],
      maxProjectDuration: [this.call.maxProjectDuration],
      maxProjectVolume: [this.call.maxProjectVolume],
      callVolumeAmount: [this.call.callVolumeAmount],
      callVolumeProjects: [this.call.callVolumeProjects],
      fullyFunded: [this.call.fullyFunded],
      minInkind: [this.call.minInkind],
      maxOverhead: [this.call.maxOverhead],
      funder: [this.call.funder],
    });
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

  private loadStagedChanges(): void {
    const stagedChanges = this.localStorageService.load(
      LOCAL_STORAGE_KEYS.STAGED_CALL_CHANGES
    );
    if (stagedChanges) {
      this.call = { ...this.call, ...stagedChanges };
    }
  }

  private stageChanges(): void {
    if (this.detailsForm.dirty) {
      this.localStorageService.save(
        'stagedCallChanges',
        this.detailsForm.getRawValue()
      );
    }
  }

  // display current user affiliation for FUNDER users
  private formatFunder(funder: FundingEntityRef | undefined): string {
    return funder
      ? `${this.formatTranslatedText(funder.name)} (${funder.id})`
      : CALL_DETAILS_CONSTANTS.PLACEHOLDER;
  }

  private formatTranslatedText(
    translatedTexts: TranslatedText[] | undefined
  ): string {
    if (!translatedTexts) return CALL_DETAILS_CONSTANTS.PLACEHOLDER;

    return translatedTexts
      .map((tt) => `${tt.text} (${tt.language})`)
      .join(', ');
  }
}
