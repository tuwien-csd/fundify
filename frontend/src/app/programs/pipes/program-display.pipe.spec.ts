import { LanguageEnum } from '../../shared/models/enums/language.enum';
import { ProgramDisplayPipe } from './program-display.pipe';
import { Program } from '../models/program.interface';
import { TranslatedText } from 'src/app/shared/models/interfaces/translated-text.interface';
import { EntryOriginEnum } from 'src/app/shared/models/enums/entry-origin.enum';
import { FundingEntityEnum } from '../../shared/models/enums/funding-entity.enum';
import { FundingEntityRef } from '../../shared/models/interfaces/funding-entity-ref.interface';
import { TranslationEnum } from '../../shared/models/enums/translation-enum';

describe('ProgramDisplayPipe', () => {
  let pipe: ProgramDisplayPipe;
  let urls: URL[];
  let name: TranslatedText[];
  let funder: FundingEntityRef;

  beforeEach(() => {
    pipe = new ProgramDisplayPipe();
    urls = [new URL('https://program1.com')];
    name = [{ text: 'Program 1', language: LanguageEnum.ENGLISH }];
    funder = {
      id: '1',
      name: [
        {
          text: 'Funder 1',
          language: LanguageEnum.ENGLISH,
          translation: TranslationEnum.ORIGINAL,
        },
      ],
      type: FundingEntityEnum.FUNDER,
    };
  });

  it('should return empty string for unknown field', () => {
    const program: Program = {
      id: '1',
      name: name,
      website: urls,
      entryOrigin: EntryOriginEnum.REFOP,
    };
    const result = pipe.transform(program, 'unknownField');
    expect(result).toEqual('');
  });

  it('should return empty string for undefined value', () => {
    const program: Program = {
      id: '1',
      name: name,
      website: undefined,
      entryOrigin: EntryOriginEnum.REFOP,
    };
    const result = pipe.transform(program, 'website');
    expect(result).toEqual('');
  });

  it('should return the acronym for the "acronym" field', () => {
    const program: Program = {
      id: '1',
      name: name,
      acronym: 'P1',
      website: urls,
      entryOrigin: EntryOriginEnum.REFOP,
    };
    const result = pipe.transform(program, 'acronym');
    expect(result).toEqual('P1');
  });

  it('should return the name for the "name" field', () => {
    const program: Program = {
      id: '1',
      name: name,
      website: urls,
      entryOrigin: EntryOriginEnum.REFOP,
    };
    const result = pipe.transform(program, 'name');
    expect(result).toEqual('Program 1');
  });

  it('should return the first website for the "website" field', () => {
    const program: Program = {
      id: '1',
      name: name,
      website: urls,
      entryOrigin: EntryOriginEnum.REFOP,
    };
    const result = pipe.transform(program, 'website');
    expect(result).toEqual(urls[0].toString());
  });

  it('should return the english name for "funder" field', () => {
    const program: Program = {
      id: '1',
      name: name,
      funder: funder,
    } as unknown as Program;
    const result = pipe.transform(program, 'funder');
    expect(result).toEqual('Funder 1');
  });
});
