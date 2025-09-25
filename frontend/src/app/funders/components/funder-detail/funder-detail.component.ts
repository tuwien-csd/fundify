import {
  Component,
  computed,
  EventEmitter,
  inject,
  input,
  Output,
} from '@angular/core';
import { ViewEnum } from '../../../shared/models/enums/view.enum';
import { PermissionService } from '../../../core/auth/services/permission.service';
import { ActionPermissions } from '../../../core/models/ActionPermissions';
import { FUNDER_DETAILS_CONSTANTS } from '../../funders.constants';
import { TranslatedText } from '../../../shared/models/interfaces/translated-text.interface';
import { EntryOriginEnum } from '../../../shared/models/enums/entry-origin.enum';
import { BUTTON_LABELS } from '../../../shared/shared.constants';
import { FunderWebModel } from '../../models/funder.interface';
import { PostAddress } from '../../../shared/models/interfaces/post-address.interface';
import { PermissionContext } from '../../../core/models/enums/permission-context.enum';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardTitle,
} from '@angular/material/card';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { ROUTER_LINKS } from '../../../core/router-links.constants';

@Component({
  selector: 'app-funder-detail-preview',
  templateUrl: './funder-detail.component.html',
  styleUrls: ['./funder-detail.component.scss'],
  imports: [
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
    MatCardActions,
    MatButton,
    MatIcon,
    RouterLink,
  ],
})
export class FunderDetailComponent {
  private permissionService = inject(PermissionService);

  protected readonly FUNDER_DETAILS_CONSTANTS = FUNDER_DETAILS_CONSTANTS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly ROUTER_LINKS = ROUTER_LINKS;

  funder = input.required<FunderWebModel>();
  @Output() edit = new EventEmitter<ViewEnum>();
  @Output() back = new EventEmitter<void>();

  permissions = computed<ActionPermissions>(() => {
    const context = this.funder().id
      ? PermissionContext.EXISTING
      : PermissionContext.NEW;
    return this.permissionService.getPermissions(
      EntryOriginEnum.REFOP,
      PublicationStatusEnum.DRAFT,
      this.getFunderId(),
      context
    );
  });

  funderDetails = computed(() => {
    return [
      {
        label: this.FUNDER_DETAILS_CONSTANTS.NAME_LABEL,
        value: this.formatTranslatedText(this.funder().name ?? []),
      },
      {
        label: this.FUNDER_DETAILS_CONSTANTS.ACRONYM_LABEL,
        value: this.funder().acronym ?? 'N/A',
      },
      {
        label: this.FUNDER_DETAILS_CONSTANTS.CROSSREF_DOI_LABEL,
        value: this.funder().crossRefDoi ?? 'N/A',
      },
      {
        label: this.FUNDER_DETAILS_CONSTANTS.WEBSITE_LABEL,
        value: this.funder().website ?? 'N/A',
      },
      {
        label: this.FUNDER_DETAILS_CONSTANTS.SUBMISSION_SYSTEM_LABEL,
        value: this.funder().submissionSystem ?? 'N/A',
      },
      {
        label: this.FUNDER_DETAILS_CONSTANTS.ADDRESS_LABEL,
        value: this.formatPostAddress(this.funder().postAddress),
      },
      {
        label: this.FUNDER_DETAILS_CONSTANTS.PHONE_LABEL,
        value: this.funder().phone ?? 'N/A',
      },
      {
        label: this.FUNDER_DETAILS_CONSTANTS.EXTERNALLY_ADMINISTERED_LABEL,
        value: this.funder().externallyAdministered ? 'Yes' : 'No',
      },
    ] satisfies { label: string; value: string }[];
  });

  onBack(): void {
    this.back.emit();
  }

  onEdit(): void {
    this.edit.emit(ViewEnum.EDIT);
  }

  getFunderId(): string {
    return this.funder().id ?? '';
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
