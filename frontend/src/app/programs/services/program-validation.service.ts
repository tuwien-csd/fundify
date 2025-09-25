import { Injectable } from '@angular/core';
import { ProgramWebModel } from '../models/program.interface';

@Injectable({
  providedIn: 'root',
})
export class ProgramValidationService {
  // checks if all required fields are filled
  validate(program: ProgramWebModel): boolean {
    return (
      !!program.name?.length &&
      !!program.targetGroups?.length &&
      !!program.subjects?.length &&
      !!program.description?.length &&
      !!program.characteristics?.length &&
      !!program.website?.length &&
      !!program.fundingScheme &&
      !!program.legalType &&
      !!program.funder
    );
  }
}
