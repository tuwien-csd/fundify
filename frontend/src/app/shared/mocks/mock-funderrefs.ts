import { FundingEntityEnum } from '../models/enums/funding-entity.enum';
import { LanguageEnum } from '../models/enums/language.enum';
import { FundingEntityRef } from '../models/interfaces/funding-entity-ref.interface';

export const FUNDERREFS: FundingEntityRef[] = [
  {
    id: '11',
    type: FundingEntityEnum.FUNDER,
    name: [
      { text: 'Austrian Science Fund', language: LanguageEnum.ENGLISH },
      {
        text: 'Fonds zur Förderung der wissenschaftlichen Forschung',
        language: LanguageEnum.GERMAN,
      },
    ],
  },
  {
    id: '12',
    type: FundingEntityEnum.FUNDER,
    name: [
      {
        text: 'Austrian Research Promotion Agency',
        language: LanguageEnum.ENGLISH,
      },
      {
        text: 'Österreichische Forschungsförderungsgesellschaft mbH',
        language: LanguageEnum.GERMAN,
      },
    ],
  },
  {
    id: '13',
    type: FundingEntityEnum.FUNDER,
    name: [
      {
        text: 'Vienna Science and Technology Fund',
        language: LanguageEnum.ENGLISH,
      },
      {
        text: 'Wiener Wissenschafts-, Forschungs- und Technologiefonds',
        language: LanguageEnum.GERMAN,
      },
    ],
  },
  {
    id: '14',
    type: FundingEntityEnum.FUNDER,
    name: [
      {
        text: 'Christian Doppler Research Association',
        language: LanguageEnum.ENGLISH,
      },
      {
        text: 'Christian Doppler Forschungsgesellschaft',
        language: LanguageEnum.GERMAN,
      },
    ],
  },
  {
    id: '16',
    type: FundingEntityEnum.FUNDER,
    name: [
      { text: 'Ludwig Boltzmann Society', language: LanguageEnum.ENGLISH },
      { text: 'Ludwig Boltzmann Gesellschaft', language: LanguageEnum.GERMAN },
    ],
  },
  {
    id: '17',
    type: FundingEntityEnum.FUNDER,
    name: [
      { text: 'Klima- und Energiefonds', language: LanguageEnum.ENGLISH },
      { text: 'Klima- und Energiefonds', language: LanguageEnum.GERMAN },
    ],
  },
];
