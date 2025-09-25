import { FundingEntityEnum } from '../models/enums/funding-entity.enum';
import { LanguageEnum } from '../models/enums/language.enum';
import { FundingEntityRef } from '../models/interfaces/funding-entity-ref.interface';

export const PROGREFS: FundingEntityRef[] = [
  {
    id: '5001',
    type: FundingEntityEnum.PROGRAM,
    name: [
      { text: 'Thematic programme', language: LanguageEnum.ENGLISH },
      { text: 'Thematisches Programm', language: LanguageEnum.GERMAN },
    ],
  },
  {
    id: '5002',
    type: FundingEntityEnum.PROGRAM,
    name: [
      { text: 'IKT der Zukunft', language: LanguageEnum.ENGLISH },
      { text: 'IKT der Zukunft', language: LanguageEnum.GERMAN },
    ],
  },
  {
    id: '5003',
    type: FundingEntityEnum.PROGRAM,
    name: [
      { text: 'ENERGIE DER ZUKUNFT', language: LanguageEnum.ENGLISH },
      { text: 'ENERGIE DER ZUKUNFT', language: LanguageEnum.GERMAN },
    ],
  },
  {
    id: '5004',
    type: FundingEntityEnum.PROGRAM,
    name: [
      { text: 'Special Research Program', language: LanguageEnum.ENGLISH },
      { text: 'Spezialforschungsbereich', language: LanguageEnum.GERMAN },
    ],
  },
  {
    id: '5005',
    type: FundingEntityEnum.PROGRAM,
    name: [
      {
        text: 'Programme for Arts-based Research',
        language: LanguageEnum.ENGLISH,
      },
      {
        text: 'Programm zur Entwicklung und Erschließung der Künste',
        language: LanguageEnum.GERMAN,
      },
    ],
  },
  {
    id: '5006',
    type: FundingEntityEnum.PROGRAM,
    name: [
      { text: 'Go!Digital', language: LanguageEnum.ENGLISH },
      { text: 'Go!Digital', language: LanguageEnum.GERMAN },
    ],
  },
  {
    id: '5007',
    type: FundingEntityEnum.PROGRAM,
    name: [
      {
        text: 'NEXT-New Exiting Transfer Projects',
        language: LanguageEnum.ENGLISH,
      },
      {
        text: 'NEXT-New Exiting Transfer Projects',
        language: LanguageEnum.GERMAN,
      },
    ],
  },
  {
    id: '5008',
    type: FundingEntityEnum.PROGRAM,
    name: [
      { text: 'Life Sciences', language: LanguageEnum.ENGLISH },
      { text: 'Life Sciences', language: LanguageEnum.GERMAN },
    ],
  },
];
