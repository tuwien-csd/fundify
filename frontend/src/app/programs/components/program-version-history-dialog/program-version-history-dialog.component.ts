import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogTitle, MatDialogContent, MatDialogActions } from '@angular/material/dialog';
import { DatePipe, NgClass } from '@angular/common';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { BackendServiceV2 } from '../../../core/services/backend-service-v2.service';
import { NotificationService } from '../../../shared/services/notification-service.service';
import { PROGRAM_DETAILS_CONSTANTS } from '../../programs.constants';
import { components } from '../../../../generated/refop-be';

type ProgramVersionWebModel = components['schemas']['ProgramVersionWebModel'];
type VersionFields = Pick<ProgramVersionWebModel, 'description' | 'duration'>;

const TRACKED_FIELDS: (keyof VersionFields)[] = ['description', 'duration'];

export interface ProgramVersionHistoryDialogData {
  programId: string;
  currentFields?: Record<string, unknown>;
}

@Component({
  selector: 'app-program-version-history-dialog',
  templateUrl: './program-version-history-dialog.component.html',
  styleUrls: ['./program-version-history-dialog.component.scss'],
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
export class ProgramVersionHistoryDialogComponent implements OnInit {
  private dialogRef = inject(MatDialogRef<ProgramVersionHistoryDialogComponent>);
  private backendService = inject(BackendServiceV2);
  private notificationService = inject(NotificationService);
  data = inject<ProgramVersionHistoryDialogData>(MAT_DIALOG_DATA);

  versions: ProgramVersionWebModel[] = [];
  changedFields: Set<string>[] = [];
  currentIndex = 0;
  loading = true;
  loadError = false;

  readonly fieldDefs: { key: keyof VersionFields; label: string; format: (v: ProgramVersionWebModel) => string }[] = [
    { key: 'description', label: 'Description', format: v => this.formatTranslatedText(v.description) },
    { key: 'duration',    label: 'Duration',    format: v => this.formatDateRange(v.duration) },
  ];

  get currentVersion(): ProgramVersionWebModel | undefined {
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
      const response = await this.backendService.client.GET('/api/programs/{id}/versions', {
        params: { path: { id: this.data.programId } },
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
    console.error('Error loading program version history:', error);
    this.loadError = true;
    this.notificationService.error(PROGRAM_DETAILS_CONSTANTS.ERRORS.VERSION_HISTORY_ERROR);
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
    const newerData: ProgramVersionWebModel | undefined =
      this.currentIndex === 0
        ? (this.data.currentFields as ProgramVersionWebModel | undefined)
        : this.versions[this.currentIndex - 1];
    return newerData ? fieldDef.format(newerData) : '—';
  }

  formatTranslatedText(texts: { text?: string; language?: string }[] | undefined): string {
    if (!texts?.length) return '—';
    return texts.map(t => `${t.text} (${t.language})`).join(', ');
  }

  formatDateRange(duration: { start?: string; end?: string } | undefined): string {
    if (!duration) return '—';
    const fmt = (s: string) => {
      const d = new Date(s);
      return `${String(d.getDate()).padStart(2, '0')}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getFullYear()).slice(-2)}`;
    };
    const start = duration.start ? fmt(duration.start) : '?';
    const end = duration.end ? fmt(duration.end) : '?';
    return `${start} – ${end}`;
  }

  onClose(): void {
    this.dialogRef.close();
  }

  private computeChangedFields(versions: ProgramVersionWebModel[], currentFields?: Record<string, unknown>): Set<string>[] {
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
