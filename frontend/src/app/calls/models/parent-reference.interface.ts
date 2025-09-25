import { TranslatedText } from '../../shared/models/interfaces/translated-text.interface';

export interface ParentReference {
  id: string;
  acronym: string;
  name: TranslatedText[];
}
