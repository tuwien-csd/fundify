import { Component, inject, input, OnInit } from '@angular/core';
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
import { PROGRAM_DETAILS_CONSTANTS } from '../../programs.constants';
import {
  BUTTON_LABELS,
  FORM_STATUS_MESSAGES,
} from 'src/app/shared/shared.constants';
import { EntryOriginEnum } from '../../../shared/models/enums/entry-origin.enum';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { ActionPermissions } from '../../../core/models/ActionPermissions';
import { ProgramWebModel } from '../../models/program.interface';
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
import { ProgramStore } from '../../signal/program-store';
import { EditMode } from '../../../shared/utils/edit-mode';
import { FundersStore } from '../../../funders/signal/funders-store';
import { Router, RouterLink } from '@angular/router';
import { ROUTER_LINKS } from '../../../core/router-links.constants';
import { MatError } from '@angular/material/form-field';

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
    RouterLink,
    MatError,
  ],
})
export class ProgramDetailEditComponent implements OnInit {
  private permissionService = inject(PermissionService);
  private fb = inject(FormBuilder);
  private readonly programsStore = inject(ProgramStore);
  private readonly fundersStore = inject(FundersStore);
  private readonly router = inject(Router);

  protected readonly PROGRAM_DETAILS_CONSTANTS = PROGRAM_DETAILS_CONSTANTS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly PublicationStatusEnum = PublicationStatusEnum;

  mode = input.required<EditMode>();
  program = input.required<ProgramWebModel | null | undefined>();

  permissions!: ActionPermissions;
  detailsForm!: FormGroup;

  PATTERN_FOR_ACRONYM: string = FundingRegexp.ACRONYM;

  fundingSchemes: typeof FundingSchemeEnum = FundingSchemeEnum;
  legalTypes: typeof LegalTypeEnum = LegalTypeEnum;
  targetGroups: typeof TargetGroupEnum = TargetGroupEnum;
  careerStages: typeof CareerStageEnum = CareerStageEnum;
  fundingCharacteristics: typeof FundingCharacteristicEnum =
    FundingCharacteristicEnum;

  submissionErrorMsg: string = '';

  ngOnInit() {
    this.initPermissions();
    if (!this.permissions?.canEdit) {
      this.router.navigate([ROUTER_LINKS.NOT_AUTHORIZED]);
    }
    this.initForm();
  }

  onSaveAsDraft() {
    if (this.detailsForm.invalid) {
      this.detailsForm.markAllAsTouched();
      this.submissionErrorMsg = FORM_STATUS_MESSAGES.VALIDATION_ERROR;
      return;
    }
    const formValue = this.detailsForm.getRawValue();
    formValue.status = PublicationStatusEnum.DRAFT;
    this.createOrUpdateProgram(formValue).then((createdProgram) => {
      if (createdProgram?.id) this.navigateToProgramDetails(createdProgram.id);
    });
  }

  onPublish() {
    if (this.detailsForm.invalid) {
      this.detailsForm.markAllAsTouched();
      this.submissionErrorMsg = FORM_STATUS_MESSAGES.VALIDATION_ERROR;
      return;
    }
    const formValue = this.detailsForm.getRawValue();
    formValue.status = PublicationStatusEnum.PUBLISHED;
    this.createOrUpdateProgram(formValue).then((createdProgram) => {
      if (createdProgram?.id) this.navigateToProgramDetails(createdProgram.id);
    });
  }

  navigateToProgramDetails(programId: string) {
    this.router.navigate([
      `${ROUTER_LINKS.FUNDINGS}/${ROUTER_LINKS.PROGRAMS}`,
      programId,
    ]);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: Fix the type when we have typed forms
  private createOrUpdateProgram(formValue: any) {
    const currentUserFunder = this.fundersStore.getFunderByAcronym(
      this.permissionService.getUserAffiliationId()
    );

    switch (this.mode()) {
      case EditMode.CREATE:
        formValue.funder = currentUserFunder;
        return this.programsStore.create(formValue);
      case EditMode.EDIT:
        return this.programsStore.update({ ...this.program(), ...formValue });
    }
  }

  private initForm() {
    this.detailsForm = this.fb.group({
      name: [this.program()?.name],
      acronym: [this.program()?.acronym],
      programTracks: [this.program()?.programTracks],
      targetGroups: [this.program()?.targetGroups],
      careerStages: [this.program()?.careerStages],
      description: [this.program()?.description],
      characteristics: [this.program()?.characteristics],
      fundingScheme: [this.program()?.fundingScheme],
      legalType: [this.program()?.legalType],
      website: [this.program()?.website],
      subjects: [this.program()?.subjects],
      duration: [this.program()?.duration],
      funder: [this.program()?.funder],
    });
  }

  getStatus(): PublicationStatusEnum {
    return this.program()?.status ?? PublicationStatusEnum.DRAFT;
  }

  getOwnerId(): string {
    return this.program()?.funder?.acronym ?? '';
  }

  getOrigin(): EntryOriginEnum {
    return this.program()?.entryOrigin ?? EntryOriginEnum.REFOP;
  }

  private initPermissions(): void {
    const context = this.program()?.id
      ? PermissionContext.EXISTING
      : PermissionContext.NEW;
    this.permissions = this.permissionService.getPermissions(
      this.getOrigin(),
      this.getStatus(),
      this.getOwnerId(),
      context
    );
  }
}
