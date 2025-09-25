import { FunderWebModel } from '../../funders/models/funder.interface';
import { LanguageEnum } from '../models/enums/language.enum';

export const FUNDERS: FunderWebModel[] = [
  {
    id: '11',
    acronym: 'FWF',
    risId: 'testID1',
    name: [
      {
        text: 'Fonds zur Förderung der wissenschaftlichen Forschung',
        language: LanguageEnum.GERMAN,
        translation: 'o',
      },
    ],
    website: 'https://www.fwf.ac.at',
  },
  {
    id: '12',
    acronym: 'FFG',
    risId: 'testID2',
    name: [
      {
        text: 'Österreichische Forschungsförderungsgesellschaft mbH',
        language: LanguageEnum.GERMAN,
        translation: 'o',
      },
    ],
    website: 'https://www.ffg.at',
  },
  {
    id: '13',
    acronym: 'WWTF',
    risId: 'testID3',
    name: [
      {
        text: 'Wiener Wissenschafts-, Forschungs- und Technologiefonds',
        language: LanguageEnum.GERMAN,
        translation: 'o',
      },
    ],
    website: 'https://www.wwtf.at',
  },
  {
    id: '14',
    acronym: 'CDG',
    risId: 'testID4',
    name: [
      {
        text: 'Christian Doppler Forschungsgesellschaft',
        language: LanguageEnum.GERMAN,
        translation: 'o',
      },
    ],
    website: 'https://www.cdg.ac.at',
  },
  {
    id: '15',
    acronym: 'ÖFG',
    risId: 'testID5',
    name: [
      {
        text: 'Österreichische Forschungsgemeinschaft',
        language: LanguageEnum.GERMAN,
        translation: 'o',
      },
    ],
    website: 'https://www.oefg.at',
  },
  {
    id: '16',
    acronym: 'LBG',
    risId: 'testID6',
    name: [
      {
        text: 'Ludwig Boltzmann Gesellschaft',
        language: LanguageEnum.GERMAN,
        translation: 'o',
      },
    ],
    website: 'https://www.lbg.ac.at',
  },
  {
    id: '17',
    acronym: 'KLIEN',
    risId: 'testID7',
    name: [
      {
        text: 'Klima- und Energiefonds',
        language: LanguageEnum.GERMAN,
        translation: 'o',
      },
    ],
    website: 'https://www.klimafonds.gv.at',
  },
];
