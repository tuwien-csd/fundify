import { Component, computed, inject, input } from '@angular/core';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { ActionPermissions } from '../../../core/models/ActionPermissions';
import { TranslatedText } from '../../../shared/models/interfaces/translated-text.interface';
import { EntryOriginEnum } from '../../../shared/models/enums/entry-origin.enum';
import { BUTTON_LABELS } from '../../../shared/shared.constants';
import { PostAddress } from '../../../shared/models/interfaces/post-address.interface';
import { PermissionContext } from '../../../core/models/enums/permission-context.enum';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';
import { ROUTER_LINKS } from '../../../core/router-links.constants';
import { UNIVERSITY_DETAILS_CONSTANTS } from '../../universities.constants';
import { UniversityWebModel } from '../../models/university.interface';
import { MatButton } from '@angular/material/button';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardTitle,
} from '@angular/material/card';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-university-detail-preview',
  templateUrl: './university-detail.component.html',
  styleUrls: ['./university-detail.component.scss'],
  imports: [
    MatButton,
    MatCard,
    MatCardActions,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatIcon,
    RouterLink,
  ],
})
export class UniversityDetailComponent {
  private permissionService = inject(PermissionService);

  protected readonly UNIVERSITY_DETAILS_CONSTANTS =
    UNIVERSITY_DETAILS_CONSTANTS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly ROUTER_LINKS = ROUTER_LINKS;

  university = input.required<UniversityWebModel>();

  permissions = computed<ActionPermissions>(() => {
    const context = this.university().id
      ? PermissionContext.EXISTING
      : PermissionContext.NEW;
    return this.permissionService.getPermissions(
      EntryOriginEnum.REFOP,
      PublicationStatusEnum.DRAFT,
      this.getUniversityId(),
      context
    );
  });
  universityDetails = computed(() => {
    return [
      {
        label: this.UNIVERSITY_DETAILS_CONSTANTS.NAME_LABEL,
        value: this.formatTranslatedText(this.university().name ?? []),
      },
      {
        label: this.UNIVERSITY_DETAILS_CONSTANTS.ACRONYM_LABEL,
        value: this.university().acronym ?? 'N/A',
      },
      {
        label: this.UNIVERSITY_DETAILS_CONSTANTS.CROSSREF_DOI_LABEL,
        value: this.university().crossRefDoi ?? 'N/A',
      },
      {
        label: this.UNIVERSITY_DETAILS_CONSTANTS.MAIL_DOMAIN_LABEL,
        value: this.university().emailDomain ?? 'N/A',
      },
      {
        label: this.UNIVERSITY_DETAILS_CONSTANTS.WEBSITE_LABEL,
        value: this.university().website ?? 'N/A',
      },
      {
        label: this.UNIVERSITY_DETAILS_CONSTANTS.SUBMISSION_SYSTEM_LABEL,
        value: this.university().submissionSystem ?? 'N/A',
      },
      {
        label: this.UNIVERSITY_DETAILS_CONSTANTS.ADDRESS_LABEL,
        value: this.formatPostAddress(this.university().postAddress),
      },
      {
        label: this.UNIVERSITY_DETAILS_CONSTANTS.PHONE_LABEL,
        value: this.university().phone ?? 'N/A',
      },
    ] satisfies { label: string; value: string }[];
  });

  getUniversityId(): string {
    return this.university().id ?? '';
  }

  private formatTranslatedText(translatedTexts: TranslatedText[]): string {
    if (!translatedTexts) return 'N/A';

    return translatedTexts
      .map((tt) => `${tt.text} (${tt.language})`)
      .join(', ');
  }

  private formatPostAddress(postAddress: PostAddress | undefined): string {
    if (!postAddress) return 'N/A';

    return `${postAddress.streetLine}, ${postAddress.city}, ${postAddress.postalCode}, ${postAddress.countryCode}`;
  }
}
