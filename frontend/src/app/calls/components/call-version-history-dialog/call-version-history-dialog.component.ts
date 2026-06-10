import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogTitle, MatDialogContent, MatDialogActions } from '@angular/material/dialog';
import { DatePipe, NgClass } from '@angular/common';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { BackendServiceV2 } from '../../../core/services/backend-service-v2.service';
import { NotificationService } from '../../../shared/services/notification-service.service';
import { CALL_DETAILS_CONSTANTS } from '../../calls.constants';
import { components } from '../../../../generated/refop-be';

type CallVersionWebModel = components['schemas']['CallVersionWebModel'];
type VersionFields = Pick<CallVersionWebModel, 'name' | 'description' | 'eligibleApplicants' | 'callStages' | 'callVolumeAmount' | 'website'>;

const TRACKED_FIELDS: (keyof VersionFields)[] = [
  'name', 'description', 'eligibleApplicants', 'callStages', 'callVolumeAmount', 'website',
];

export interface CallVersionHistoryDialogData {
  callId: string;
  currentFields?: Record<string, unknown>;
}

@Component({
  selector: 'app-call-version-history-dialog',
  templateUrl: './call-version-history-dialog.component.html',
  styleUrls: ['./call-version-history-dialog.component.scss'],
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatIconButton,
    MatIcon,
    MatProgressSpinner,
    DatePipe,
    NgClass,
  ],
})
export class CallVersionHistoryDialogComponent implements OnInit {
  private dialogRef = inject(MatDialogRef<CallVersionHistoryDialogComponent>);
  private backendService = inject(BackendServiceV2);
  private notificationService = inject(NotificationService);
  data = inject<CallVersionHistoryDialogData>(MAT_DIALOG_DATA);

  versions: CallVersionWebModel[] = [];
  changedFields: Set<string>[] = [];
  currentIndex = 0;
  loading = true;
  loadError = false;

  readonly fieldDefs: { key: keyof VersionFields; label: string; format: (v: CallVersionWebModel) => string }[] = [
    { key: 'name',               label: 'Title',              format: v => this.formatTranslatedText(v.name) },
    { key: 'description',        label: 'Description',        format: v => this.formatTranslatedText(v.description) },
    { key: 'eligibleApplicants', label: 'Eligible Applicants', format: v => this.formatTranslatedText(v.eligibleApplicants) },
    { key: 'callStages',         label: 'Deadline',           format: v => this.formatCallStages(v.callStages) },
    { key: 'callVolumeAmount',   label: 'Volume',             format: v => this.formatVolume(v.callVolumeAmount) },
    { key: 'website',            label: 'Link',               format: v => v.website?.join(', ') || '—' },
  ];

  get currentVersion(): CallVersionWebModel | undefined {
    return this.versions[this.currentIndex];
  }

  get isLatest(): boolean {
    return this.currentIndex === 0;
  }

  get isOldest(): boolean {
    return this.currentIndex === this.versions.length - 1;
  }

  get hasChanges(): boolean {
    return this.fieldDefs.some(f => this.isChanged(f.key));
  }

  async ngOnInit(): Promise<void> {
    try {
      const response = await this.backendService.client.GET('/api/calls/{id}/versions', {
        params: { path: { id: this.data.callId } },
      });
      if (response.error || !response.data) {
        this.handleLoadError(response.error);
        return;
      }
      this.versions = response.data.sort(
        (a, b) => new Date(b.versionedAt ?? 0).getTime() - new Date(a.versionedAt ?? 0).getTime()
      );
      this.changedFields = this.computeChangedFields(this.versions, this.data.currentFields);
    } catch (error) {
      this.handleLoadError(error);
    } finally {
      this.loading = false;
    }
  }

  private handleLoadError(error: unknown): void {
    console.error('Error loading call version history:', error);
    this.loadError = true;
    this.notificationService.error(CALL_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR);
  }

  goNewer(): void {
    if (!this.isLatest) this.currentIndex--;
  }

  goOlder(): void {
    if (!this.isOldest) this.currentIndex++;
  }

  isChanged(field: keyof VersionFields): boolean {
    return this.changedFields[this.currentIndex]?.has(field) ?? false;
  }

  getNewerFormattedValue(fieldDef: (typeof this.fieldDefs)[number]): string {
    const newerData: CallVersionWebModel | undefined =
      this.currentIndex === 0
        ? (this.data.currentFields as CallVersionWebModel | undefined)
        : this.versions[this.currentIndex - 1];
    return newerData ? fieldDef.format(newerData) : '—';
  }

  formatTranslatedText(texts: { text?: string; language?: string }[] | undefined): string {
    if (!texts?.length) return '—';
    return texts.map(t => `${t.text} (${t.language})`).join(', ');
  }

  formatCallStages(stages: { number?: number; duration?: { start?: string; end?: string } }[] | undefined): string {
    if (!stages?.length) return '—';
    const fmt = (s: string) => {
      const d = new Date(s);
      return `${String(d.getDate()).padStart(2, '0')}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getFullYear()).slice(-2)}`;
    };
    return stages.map(s => {
      const start = s.duration?.start ? fmt(s.duration.start) : '?';
      const end = s.duration?.end ? fmt(s.duration.end) : '?';
      return `Stage ${s.number}: ${start} – ${end}`;
    }).join(', ');
  }

  formatVolume(volume: { amount?: number; currency?: string } | undefined): string {
    if (!volume) return '—';
    return `${volume.amount ?? '?'} ${volume.currency ?? ''}`.trim();
  }

  onClose(): void {
    this.dialogRef.close();
  }

  private computeChangedFields(versions: CallVersionWebModel[], currentFields?: Record<string, unknown>): Set<string>[] {
    return versions.map((version, i) => {
      const nextState: Record<string, unknown> | undefined = i === 0 ? currentFields : versions[i - 1];
      if (!nextState) return new Set<string>();

      const changed = new Set<string>();
      for (const field of TRACKED_FIELDS) {
        if (JSON.stringify(version[field]) !== JSON.stringify(nextState[field])) {
          changed.add(field);
        }
      }
      return changed;
    });
  }
}
