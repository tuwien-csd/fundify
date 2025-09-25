import { LanguageEnum } from '../enums/language.enum';
import { TranslationEnum } from '../enums/translation-enum';

export interface TranslatedText {
  text: string;
  language: LanguageEnum;
  translation?: TranslationEnum;
}
