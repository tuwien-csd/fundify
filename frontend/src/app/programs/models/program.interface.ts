import { CareerStageEnum } from 'src/app/shared/models/enums/career-stage.enum';
import { EntryOriginEnum } from 'src/app/shared/models/enums/entry-origin.enum';
import { FundingCharacteristicEnum } from 'src/app/shared/models/enums/funding-characteristic.enum';
import { FundingSchemeEnum } from 'src/app/shared/models/enums/funding-scheme.enum';
import { LegalTypeEnum } from 'src/app/shared/models/enums/legal-type.enum';
import { TargetGroupEnum } from 'src/app/shared/models/enums/target-group.enum';
import { DateRange } from 'src/app/shared/models/interfaces/date-range.interface';
import { TranslatedText } from 'src/app/shared/models/interfaces/translated-text.interface';
import { FundingEntityRef } from '../../shared/models/interfaces/funding-entity-ref.interface';
import { Identifier } from '../../shared/models/interfaces/identifier.interface';
import { StandardizedSubject } from '../../shared/models/interfaces/standardizedSubject.interface';
import { PublicationStatusEnum } from '../../shared/models/enums/publication-status.enum';
import { components } from '../../../generated/refop-be';

export interface Program {
  id?: string;
  entryOrigin: EntryOriginEnum;
  status?: PublicationStatusEnum;
  registrationDate?: string;
  lastSync?: string;
  //mandatory fields
  risId?: string;
  name?: TranslatedText[];
  targetGroups?: TargetGroupEnum[];
  subjects?: StandardizedSubject[];
  description?: TranslatedText[];
  characteristics?: FundingCharacteristicEnum[];
  website?: URL[];
  fundingScheme?: FundingSchemeEnum;
  legalType?: LegalTypeEnum;
  funder?: FundingEntityRef;
  //optional fields
  acronym?: string;
  identifiers?: Identifier[]; // not implemented in V1
  programTracks?: TranslatedText[][];
  careerStages?: CareerStageEnum[];
  duration?: DateRange; // startDate and endDate as of Anforderungstemplate v1
}

export type ProgramWebModel = components['schemas']['ProgramWebModel'];
