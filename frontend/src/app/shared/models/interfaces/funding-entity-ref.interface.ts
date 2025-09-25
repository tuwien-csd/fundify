import { FundingEntityEnum } from '../enums/funding-entity.enum';
import { TranslatedText } from './translated-text.interface';

export interface FundingEntityRef {
  id: string;
  type: FundingEntityEnum;
  name: TranslatedText[];
}
