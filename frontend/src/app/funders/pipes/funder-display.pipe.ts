import { Pipe, PipeTransform } from '@angular/core';
import { FunderWebModel } from '../models/funder.interface';

@Pipe({ name: 'displayFunderField' })
export class FunderDisplayPipe implements PipeTransform {
  transform(value: FunderWebModel, field: string): string {
    switch (field) {
      case 'acronym':
        return value[field] || '';
      case 'name':
        return value?.name?.[0]?.text || '';
      case 'website':
        return value?.website || '';
      default:
        return '';
    }
  }
}
