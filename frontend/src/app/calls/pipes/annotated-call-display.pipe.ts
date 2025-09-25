import { Pipe, PipeTransform } from '@angular/core';
import {
  CallAnnotationListViewElement,
  PublicationStatus,
} from '../components/call-annotation-list/call-annotation-list.component';

@Pipe({ name: 'displayCallAnnotationListViewElement' })
export class AnnotatedCallDisplayPipe implements PipeTransform {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  transform(value: CallAnnotationListViewElement, field: string): any {
    switch (field) {
      case 'name':
        return value?.name?.[0]?.text ?? '';
      case 'funder':
        return value?.funder?.[0]?.text ?? '';
      case 'partOf':
        return value?.partOf?.[0]?.text ?? '';
      case 'callStatus':
        return this.getCallStatus(value);
      case 'status':
        return value?.status ?? '';
      case 'lastUpdatedAt': {
        const iso = value?.lastUpdatedAt;
        if (!iso) return '';
        const d = new Date(iso);
        return isNaN(d.getTime()) ? iso : d.toLocaleString();
      }
      case 'lastUpdatedBy':
        return value?.lastUpdatedBy?.name ?? '';
      default:
        return '';
    }
  }

  private getCallStatus(
    value: CallAnnotationListViewElement
  ): PublicationStatus {
    const now = new Date();
    const startDate = new Date(value.callStartDate);
    const endDate = new Date(value.callEndDate);

    if (startDate > now) {
      return 'Upcoming';
    } else if (startDate <= now && endDate >= now) {
      return 'Running';
    } else {
      return 'Closed';
    }
  }
}
