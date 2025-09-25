import { LanguageEnum } from '../../shared/models/enums/language.enum';
import { TranslationEnum } from '../../shared/models/enums/translation-enum';
import { CallDisplayPipe } from './call-display.pipe';
import { Call } from '../models/call.interface';
import { TranslatedText } from 'src/app/shared/models/interfaces/translated-text.interface';
import { FundingEntityRef } from 'src/app/shared/models/interfaces/funding-entity-ref.interface';
import { FundingEntityEnum } from 'src/app/shared/models/enums/funding-entity.enum';

describe('CallDisplayPipe', () => {
  let pipe: CallDisplayPipe;
  let name: TranslatedText[];
  let funderRef: FundingEntityRef;
  let programRef: FundingEntityRef;
  let risId: string;

  beforeEach(() => {
    pipe = new CallDisplayPipe();
    name = [
      {
        text: 'Call 1',
        language: LanguageEnum.ENGLISH,
        translation: TranslationEnum.ORIGINAL,
      },
    ];
    funderRef = {
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
    programRef = {
      id: '2',
      name: [
        {
          text: 'Program 1',
          language: LanguageEnum.ENGLISH,
          translation: TranslationEnum.ORIGINAL,
        },
      ],
      type: FundingEntityEnum.PROGRAM,
    };
    risId = 'C1';
  });

  it('should return empty string for unknown field', () => {
    const call: Call = { id: '1', name: name, funder: funderRef, risId: risId };
    const result = pipe.transform(call, 'unknownField');
    expect(result).toEqual('');
  });

  it('should return the english name for "funder" field', () => {
    const call: Call = { id: '1', name: name, funder: funderRef, risId: risId };
    const result = pipe.transform(call, 'name');
    expect(result).toEqual('Call 1');
  });

  it('should return the acronym for the "acronym" field', () => {
    const call: Call = {
      id: '1',
      name: name,
      funder: funderRef,
      risId: risId,
      acronym: 'C1',
    };
    const result = pipe.transform(call, 'acronym');
    expect(result).toEqual('C1');
  });

  it('should return the funder english name for the "funder" field', () => {
    const call: Call = { id: '1', name: name, funder: funderRef, risId: risId };
    const result = pipe.transform(call, 'funder');
    expect(result).toEqual('Funder 1');
  });

  it('should return the partOf english name for the "partOf" field', () => {
    const call: Call = {
      id: '1',
      name: name,
      funder: funderRef,
      risId: risId,
      partOf: programRef,
    };
    const result = pipe.transform(call, 'partOf');
    expect(result).toEqual('Program 1');
  });
});
