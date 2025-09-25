import { FunderDisplayPipe } from './funder-display.pipe';
import { FunderWebModel } from '../models/funder.interface';
import { LanguageEnum } from 'src/app/shared/models/enums/language.enum';
import { TranslationEnum } from '../../shared/models/enums/translation-enum';

describe('FunderDisplayPipe', () => {
  let pipe: FunderDisplayPipe;

  beforeEach(() => {
    pipe = new FunderDisplayPipe();
  });

  it('should return empty string for unknown field', () => {
    const funder: FunderWebModel = {
      id: '1',
      acronym: 'F1',
      risId: 'id',
      name: [
        {
          text: 'Funder 1',
          language: LanguageEnum.ENGLISH,
          translation: TranslationEnum.ORIGINAL,
        },
      ],
      website: 'https://funder1.com',
    };
    const result = pipe.transform(funder, 'unknownField');
    expect(result).toBe('');
  });

  it('should return the acronym for the "acronym" field', () => {
    const funder: FunderWebModel = {
      id: '1',
      acronym: 'F1',
      risId: 'id',
      name: [
        {
          text: 'Funder 1',
          language: LanguageEnum.ENGLISH,
          translation: TranslationEnum.ORIGINAL,
        },
      ],
      website: 'https://funder1.com',
    };
    const result = pipe.transform(funder, 'acronym');
    expect(result).toBe('F1');
  });

  it('should return the name for the "name" field', () => {
    const funder: FunderWebModel = {
      id: '1',
      acronym: 'F1',
      risId: 'id',
      name: [
        {
          text: 'Funder 1',
          language: LanguageEnum.ENGLISH,
          translation: TranslationEnum.ORIGINAL,
        },
      ],
      website: 'https://funder1.com',
    };
    const result = pipe.transform(funder, 'name');
    expect(result).toBe('Funder 1');
  });

  it('should return the website for the "website" field', () => {
    const funder: FunderWebModel = {
      id: '1',
      acronym: 'F1',
      risId: 'id',
      name: [
        {
          text: 'Funder 1',
          language: LanguageEnum.ENGLISH,
          translation: TranslationEnum.ORIGINAL,
        },
      ],
      website: 'https://funder1.com',
    };
    const result = pipe.transform(funder, 'website');
    expect(result).toBe('https://funder1.com');
  });
});
