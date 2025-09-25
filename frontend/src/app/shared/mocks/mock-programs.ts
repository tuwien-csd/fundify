import { Program } from 'src/app/programs/models/program.interface';
import { CareerStageEnum } from '../models/enums/career-stage.enum';
import { EntryOriginEnum } from '../models/enums/entry-origin.enum';
import { FundingCharacteristicEnum } from '../models/enums/funding-characteristic.enum';
import { FundingEntityEnum } from '../models/enums/funding-entity.enum';
import { FundingSchemeEnum } from '../models/enums/funding-scheme.enum';
import { LanguageEnum } from '../models/enums/language.enum';
import { LegalTypeEnum } from '../models/enums/legal-type.enum';
import { TargetGroupEnum } from '../models/enums/target-group.enum';
import { OEFOS } from './mock-oefos';
import { PublicationStatusEnum } from '../models/enums/publication-status.enum';
import { TranslationEnum } from '../models/enums/translation-enum';

export const PROGRAMS: Program[] = [
  {
    id: '5001',
    status: PublicationStatusEnum.DRAFT,
    funder: {
      id: '12',
      type: FundingEntityEnum.FUNDER,
      name: [
        {
          text: 'Austrian Research Promotion Agency',
          language: LanguageEnum.ENGLISH,
          translation: TranslationEnum.ORIGINAL,
        },
        {
          text: 'Österreichische Forschungsförderungsgesellschaft mbH',
          language: LanguageEnum.GERMAN,
          translation: TranslationEnum.TRANSLATION,
        },
      ],
    },
    risId: 'https://ffg.ac.at/progs/prog/5001',
    name: [
      {
        text: 'Thematic programme',
        language: LanguageEnum.ENGLISH,
        translation: TranslationEnum.ORIGINAL,
      },
      {
        text: 'Thematisches Programm',
        language: LanguageEnum.GERMAN,
        translation: TranslationEnum.TRANSLATION,
      },
    ],
    acronym: 'TP',
    programTracks: [
      [
        {
          text: 'Innovation',
          language: LanguageEnum.ENGLISH,
          translation: TranslationEnum.ORIGINAL,
        },
        {
          text: 'Innovation',
          language: LanguageEnum.GERMAN,
          translation: TranslationEnum.TRANSLATION,
        },
      ],
    ],
    duration: { start: 'Dec 08 2016 07:44:57' },
    description: [
      {
        text: 'Thematic Thematic programme is intended to bring the innovation from research into the business',
        language: LanguageEnum.ENGLISH,
        translation: TranslationEnum.ORIGINAL,
      },
      {
        text: 'Thematisches Programm - Innovations in der Wirtschaft bringen',
        language: LanguageEnum.GERMAN,
        translation: TranslationEnum.TRANSLATION,
      },
    ],
    targetGroups: [TargetGroupEnum.UNIVERSITY, TargetGroupEnum.COMPANY],
    careerStages: [
      CareerStageEnum.DOC_STUDENT,
      CareerStageEnum.EARLY_CAREER_RESEARCHERS,
      CareerStageEnum.ESTABLISHED_CAREER_RESEARCHERS,
      CareerStageEnum.EXPERT,
    ],
    characteristics: [FundingCharacteristicEnum.NATIONAL_PROGRAMME],
    fundingScheme: FundingSchemeEnum.GRANT,
    legalType: LegalTypeEnum.P_27,
    website: [new URL('https://ffg.ac.at/progs/prog/5001')],
    subjects: [OEFOS[0], OEFOS[7]],
    entryOrigin: EntryOriginEnum.ENDPOINT,
  },
  {
    id: '5002',
    status: PublicationStatusEnum.DRAFT,
    entryOrigin: EntryOriginEnum.REFOP,
    characteristics: [FundingCharacteristicEnum.NATIONAL_PROGRAMME],
    fundingScheme: FundingSchemeEnum.GRANT,
    legalType: LegalTypeEnum.P_27,
    subjects: [OEFOS[0], OEFOS[7]],
    funder: {
      id: '12',
      type: FundingEntityEnum.FUNDER,
      name: [
        {
          text: 'Austrian Research Promotion Agency',
          language: LanguageEnum.ENGLISH,
          translation: TranslationEnum.ORIGINAL,
        },
        {
          text: 'Österreichische Forschungsförderungsgesellschaft mbH',
          language: LanguageEnum.GERMAN,
          translation: TranslationEnum.TRANSLATION,
        },
      ],
    },
    risId: 'https://ffg.ac.at/progs/prog/5002',

    name: [
      {
        text: 'IKT der Zukunft',
        language: LanguageEnum.ENGLISH,
        translation: TranslationEnum.ORIGINAL,
      },
      {
        text: 'IKT der Zukunft',
        language: LanguageEnum.GERMAN,
        translation: TranslationEnum.TRANSLATION,
      },
    ],
    acronym: 'IKT-ZUKUNFT',
    description: [
      {
        text: 'Future',
        language: LanguageEnum.ENGLISH,
        translation: TranslationEnum.ORIGINAL,
      },
      {
        text: 'Zukunft',
        language: LanguageEnum.GERMAN,
        translation: TranslationEnum.TRANSLATION,
      },
    ],
    duration: {
      start: 'Dec 08 2019 07:44:57',
      end: 'Apr 22 2022 00:00:00 GMT',
    },
    targetGroups: [TargetGroupEnum.UNIVERSITY, TargetGroupEnum.COMPANY],
    website: [new URL('https://ffg.ac.at/progs/prog/5002')],
  },
  {
    id: '5003',
    status: PublicationStatusEnum.PUBLISHED,
    entryOrigin: EntryOriginEnum.REFOP,
    characteristics: [FundingCharacteristicEnum.NATIONAL_PROGRAMME],
    fundingScheme: FundingSchemeEnum.GRANT,
    legalType: LegalTypeEnum.P_27,
    subjects: [OEFOS[0], OEFOS[7]],
    funder: {
      id: '12',
      type: FundingEntityEnum.FUNDER,
      name: [
        {
          text: 'Austrian Research Promotion Agency',
          language: LanguageEnum.ENGLISH,
          translation: TranslationEnum.ORIGINAL,
        },
        {
          text: 'Österreichische Forschungsförderungsgesellschaft mbH',
          language: LanguageEnum.GERMAN,
          translation: TranslationEnum.TRANSLATION,
        },
      ],
    },
    risId: 'https://ffg.ac.at/progs/prog/5003',

    name: [
      {
        text: 'ENERGIE der Zukunft',
        language: LanguageEnum.ENGLISH,
        translation: TranslationEnum.ORIGINAL,
      },
      {
        text: 'ENERGIE der Zukunft',
        language: LanguageEnum.GERMAN,
        translation: TranslationEnum.TRANSLATION,
      },
    ],
    acronym: 'ENERGIE-ZUKUNFT',
    description: [
      {
        text: 'Future',
        language: LanguageEnum.ENGLISH,
        translation: TranslationEnum.ORIGINAL,
      },
      {
        text: 'Zukunft',
        language: LanguageEnum.GERMAN,
        translation: TranslationEnum.TRANSLATION,
      },
    ],
    duration: {
      start: 'Dec 08 2019 07:44:57',
      end: 'Apr 22 2022 00:00:00 GMT',
    },
    targetGroups: [TargetGroupEnum.UNIVERSITY, TargetGroupEnum.COMPANY],
    website: [new URL('https://ffg.ac.at/progs/prog/5003')],
  },
  {
    id: '5004',
    status: PublicationStatusEnum.DRAFT,
    entryOrigin: EntryOriginEnum.REFOP,
    characteristics: [FundingCharacteristicEnum.NATIONAL_PROGRAMME],
    description: [
      {
        text: 'Special Research Program',
        language: LanguageEnum.ENGLISH,
        translation: TranslationEnum.ORIGINAL,
      },
      {
        text: 'Spezialforschungsbereich',
        language: LanguageEnum.GERMAN,
        translation: TranslationEnum.TRANSLATION,
      },
    ],
    fundingScheme: FundingSchemeEnum.GRANT,
    legalType: LegalTypeEnum.P_27,
    subjects: [OEFOS[0], OEFOS[7]],
    funder: {
      id: '11',
      type: FundingEntityEnum.FUNDER,
      name: [
        {
          text: 'Austrian Science Fund',
          language: LanguageEnum.ENGLISH,
          translation: TranslationEnum.ORIGINAL,
        },
        {
          text: 'Fonds zur Förderung der wissenschaftlichen Forschung',
          language: LanguageEnum.GERMAN,
          translation: TranslationEnum.TRANSLATION,
        },
      ],
    },
    risId: 'https://fwf.ac.at/progs/prog/1',
    name: [
      {
        text: 'Special Research Program',
        language: LanguageEnum.ENGLISH,
        translation: TranslationEnum.ORIGINAL,
      },
      {
        text: 'Spezialforschungsbereich',
        language: LanguageEnum.GERMAN,
        translation: TranslationEnum.TRANSLATION,
      },
    ],
    acronym: 'SFB',
    targetGroups: [TargetGroupEnum.UNIVERSITY],
    careerStages: [CareerStageEnum.DOC_STUDENT],
    website: [new URL('https://fwf.ac.at/progs/prog/1')],
  },
];
