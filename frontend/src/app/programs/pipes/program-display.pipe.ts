import { Program } from '../models/program.interface';
import { Pipe, PipeTransform } from '@angular/core';
import { PublicationStatus } from '../../calls/components/call-annotation-list/call-annotation-list.component';
import { PublicationStatusEnum } from '../../shared/models/enums/publication-status.enum';

@Pipe({ name: 'displayProgramField' })
export class ProgramDisplayPipe implements PipeTransform {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  transform(value: Program, field: string): any {
    switch (field) {
      case 'acronym':
        return value[field] ?? '';
      case 'name':
        return value?.name?.[0]?.text ?? '';
      case 'website':
        return value?.website?.[0]?.toString() ?? '';
      case 'funder':
        return value?.funder?.name?.[0]?.text ?? '';
      case 'status':
        return this.getProgramStatus(value);
      default:
        return '';
    }
  }

  private getProgramStatus(program: Program): PublicationStatus {
    if (program.status === PublicationStatusEnum.DRAFT) {
      return 'Draft';
    }

    const now = new Date();
    const startDate = program.duration?.start ?? new Date(0);
    const endDate = program.duration?.end ?? new Date(2100, 1, 1);

    if (startDate > now) {
      return 'Upcoming';
    } else if (startDate <= now && endDate >= now) {
      return 'Running';
    } else {
      return 'Closed';
    }
  }
}
