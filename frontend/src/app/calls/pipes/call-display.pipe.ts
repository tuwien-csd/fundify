import { Pipe, PipeTransform } from '@angular/core';
import { Call } from '../models/call.interface';
import { PublicationStatusEnum } from '../../shared/models/enums/publication-status.enum';
import { DateInfoRange } from '../../shared/models/interfaces/date-info-range.interface';
import { PublicationStatus } from '../components/call-annotation-list/call-annotation-list.component';

@Pipe({ name: 'displayCallField' })
export class CallDisplayPipe implements PipeTransform {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  transform(value: Call, field: string): any {
    switch (field) {
      case 'acronym':
        return value?.acronym ?? '';
      case 'name':
        return value?.name?.[0]?.text ?? '';
      case 'funder':
        return value?.funder?.name?.[0]?.text ?? '';
      case 'partOf':
        return value?.partOf?.name?.[0]?.text ?? '';
      case 'status':
        return this.getCallStatus(value);
      case 'lastUpdatedAt': {
        const iso = value?.lastUpdatedAt;
        if (!iso) return '';
        const d = new Date(iso);
        return isNaN(d.getTime()) ? iso : d.toLocaleString();
      }
      default:
        return '';
    }
  }

  private getCallStatus(call: Call): PublicationStatus {
    if (call.status === PublicationStatusEnum.DRAFT) {
      return 'Draft';
    }

    const now = new Date();
    const startDate = this.getCallStartDate(call.callStages ?? []);
    const endDate = this.getCallEndDate(call.callStages ?? []);

    if (startDate > now) {
      return 'Upcoming';
    } else if (startDate <= now && endDate >= now) {
      return 'Running';
    } else {
      return 'Closed';
    }
  }

  private getCallStartDate(callStages: DateInfoRange[]): Date {
    if (callStages.length === 0) return new Date(0);
    return new Date(callStages[0].duration.start!);
  }

  private getCallEndDate(callStages: DateInfoRange[]): Date {
    if (callStages.length === 0) return new Date(2100, 0, 1);
    return new Date(
      callStages[callStages.length - 1].duration.end ?? new Date(2100, 1, 1)
    );
  }
}
