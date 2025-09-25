import { Injectable } from '@angular/core';
import { Call } from '../models/call.interface';

@Injectable({
  providedIn: 'root',
})
export class CallValidationService {
  // checks if all required fields are filled
  // more validation rules (e.g. max > min, language validation) might be necessary in future
  validate(call: Call): boolean {
    return (
      !!call.name?.length &&
      !!call.targetGroups?.length &&
      !!call.subjects?.length &&
      !!call.characteristics?.length &&
      !!call.fundingScheme &&
      !!call.legalType &&
      !!call.minProjectVolume &&
      !!call.maxProjectVolume &&
      !!call.fullyFunded &&
      !!call.minInkind &&
      !!call.maxOverhead &&
      !!call.minProjectDuration &&
      !!call.maxProjectDuration &&
      !!call.callStages?.length &&
      !!call.applicationLanguages?.length &&
      !!call.submissionModes?.length &&
      !!call.contacts?.length &&
      !!call.funder
    );
  }
}
