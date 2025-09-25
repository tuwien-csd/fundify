import { IdentifierTypeEnum } from '../enums/identifier-type.enum';

export interface Identifier {
  value: string;
  type: IdentifierTypeEnum;
}
