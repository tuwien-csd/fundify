import { Pipe, PipeTransform } from '@angular/core';
import { UniversityWebModel } from '../models/university.interface';

@Pipe({ name: 'displayUniversityField' })
export class UniversityDisplayPipe implements PipeTransform {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  transform(value: UniversityWebModel, field: string): string {
    switch (field) {
      case 'acronym':
        return value[field] || '';
      case 'name':
        return value?.name?.[0]?.text || '';
      case 'website':
        return value?.website || '';
      case 'emailDomain':
        return value?.emailDomain || '';
      default:
        return '';
    }
  }
}
