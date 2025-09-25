import { TranslatedText } from '../../shared/models/interfaces/translated-text.interface';
import { ParentReference } from './parent-reference.interface';
import { TargetGroupEnum } from '../../shared/models/enums/target-group.enum';
import { FundingCharacteristicEnum } from '../../shared/models/enums/funding-characteristic.enum';
import { CallStagePreview } from './call-stage-preview.interface';

export interface CallPreview {
  id: string;
  registrationDate: Date;
  lastSync: Date;
  name: TranslatedText[];
  partOf: ParentReference;
  funder: ParentReference;
  targetGroups: TargetGroupEnum[];
  characteristics: FundingCharacteristicEnum[];
  callStages: CallStagePreview[];
}
