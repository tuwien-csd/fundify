import { UniversityReference } from './university-reference.interface';
import { VocabularyTypeEnum } from './vocabulary-type.enum';

export type VocabularyId = {
  value: string;
};

export type VocabularyEntryEvent = {
  id: string;
  entry: string;
};

export interface Vocabulary {
  id: string;
  type: VocabularyTypeEnum;
  university: UniversityReference;
  entries: string[];
}
