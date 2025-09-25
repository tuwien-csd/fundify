import { Call } from '../../calls/models/call.interface';
import { CallTypeEnum } from '../models/enums/call-type.enum';
import { CareerStageEnum } from '../models/enums/career-stage.enum';
import { CurrencyEnum } from '../models/enums/currency.enum';
import { DecisionProcessEnum } from '../models/enums/decision-process.enum';
import { FundingCharacteristicEnum } from '../models/enums/funding-characteristic.enum';
import { FundingEntityEnum } from '../models/enums/funding-entity.enum';
import { FundingSchemeEnum } from '../models/enums/funding-scheme.enum';
import { LanguageEnum } from '../models/enums/language.enum';
import { LegalTypeEnum } from '../models/enums/legal-type.enum';
import { ModeOfSubmissionEnum } from '../models/enums/mode-of-submission.enum';
import { TargetGroupEnum } from '../models/enums/target-group.enum';

import { OEFOS } from './mock-oefos';
import { AnswerYnEnum } from '../models/enums/answer-yn.enum';

export const CALLS: Call[] = [
  {
    id: '21',
    funder: {
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
    applicationLanguages: [LanguageEnum.GERMAN],
    risId: 'https://fwf.ac.at/calls/call/1',
    fundingType: CallTypeEnum.CALL,
    partOf: {
      id: '5001',
      type: FundingEntityEnum.PROGRAM,
      name: [
        { text: 'Thematic programme', language: LanguageEnum.ENGLISH },
        { text: 'Thematisches Programm', language: LanguageEnum.GERMAN },
      ],
    },
    website: [new URL('https://fwf.ac.at/call1')],
    name: [{ text: 'TAI-2021', language: LanguageEnum.ENGLISH }],
    acronym: 'TAI-2021',
    description: [
      {
        text:
          'With the 1000 Ideas Programme, the FWF supports the promotion of completely new, daring ' +
          'or particularly original research ideas that lie outside the current scientific understanding. The key ' +
          'aim is to investigate future-oriented research topics with high scientific and transformative potential.',
        language: LanguageEnum.ENGLISH,
      },
      { text: 'TODO de', language: LanguageEnum.GERMAN },
    ],
    targetGroups: [TargetGroupEnum.UNIVERSITY, TargetGroupEnum.GOVERNMENT],
    targetGroupDetails: [
      {
        text:
          'Researchers and scientists at universities and non-university research institutions in ' +
          'Vienna, including those moving to Vienna',
        language: LanguageEnum.ENGLISH,
      },
    ],
    careerStages: [
      CareerStageEnum.DOC_STUDENT,
      CareerStageEnum.EARLY_CAREER_RESEARCHERS,
      CareerStageEnum.ESTABLISHED_CAREER_RESEARCHERS,
      CareerStageEnum.EXPERT,
    ],
    thematicOrientations: [
      [
        { text: 'Computer Science', language: LanguageEnum.ENGLISH },
        { text: 'Informatik', language: LanguageEnum.GERMAN },
      ],
      [
        { text: 'Information management', language: LanguageEnum.ENGLISH },
        { text: 'Informationsmanagement', language: LanguageEnum.GERMAN },
      ],
    ],
    eligibleApplicants: [
      {
        text:
          "A project's core team may include up to three Principal Investigators (PIs). " +
          'One PI must be the designated Principal Investigator and Coordinator (PI&C). He/she should be an expert in an ' +
          'area relevant to the topic of the proposal. The PI&C must have an affiliation at a Viennese research institution, ' +
          'which will serve as a legal contract partner. Included are researcher moving to Vienna who will have a Viennese ' +
          'affiliation in case of funding. The PI&C must have an excellent scientific track record an a proven capability to ' +
          'manage projects.',
        language: LanguageEnum.ENGLISH,
      },
    ],
    contacts: [
      {
        name: 'Contact Name',
        email: 'contact@email.com',
        phone: '+43 000 0001',
      },
    ],
    callStages: [
      {
        duration: {
          start: 'Dec 08 2019 07:44:57',
          end: 'Apr 22 2022 00:00:00 GMT',
        },
        description: [
          { text: 'Submit abstract...', language: LanguageEnum.ENGLISH },
          { text: 'Abstract einreichen...', language: LanguageEnum.GERMAN },
        ],
      },
    ],
    inkindDetails: [
      {
        text: 'The inkind regulations are not clear...',
        language: LanguageEnum.ENGLISH,
      },
    ],
    overheadDetails: [
      {
        text:
          'The FWF pays a maximum of 20% overhead. Overhead is calculated as 20% of the direct costs ' +
          'of the project. Please note, FWF offers the 20% overhead lump sum to compensate for all administration costs. ' +
          'That means that flat fees for payroll or project accounting charged to the project accounts are non-eligible ' +
          'direct costs. The total funding volume is calculated as direct plus indirect costs. Applicants are required ' +
          'to comply with the overhead regulations of the involved institutions.',
        language: LanguageEnum.ENGLISH,
      },
    ],
    decisionProcessDetails: [
      {
        text:
          'The scientific content of proposal is reviewed by 3 independent experts in an area ' +
          'relevant to the topic of the proposal',
        language: LanguageEnum.ENGLISH,
      },
      { text: 'TODO...', language: LanguageEnum.GERMAN },
    ],
    reportingPeriodDetails: [
      {
        text: 'Every half a year the report should be submitted',
        language: LanguageEnum.ENGLISH,
      },
    ],
    projectStartDetails: [
      { text: 'latest project start 19.09.22', language: LanguageEnum.ENGLISH },
      { text: 'letzter Projektstart 19.09.22', language: LanguageEnum.GERMAN },
    ],
    dmpGuidelines: new URL('https://fwf.ac.at/calls/dmp/guidelines'),
    subjects: [OEFOS[0], OEFOS[7]],
    minProjectDuration: { months: 12, days: 0 },
    minProjectVolume: { amount: 10000, currency: CurrencyEnum.EUR },
    maxProjectDuration: { months: 36, days: 0 },
    maxProjectVolume: { amount: 300000, currency: CurrencyEnum.EUR },
    callVolumeAmount: { amount: 1000000, currency: CurrencyEnum.EUR },
    fullyFunded: AnswerYnEnum.YES,
    minInkind: 50,
    maxOverhead: 25.0,
    callVolumeProjects: 10,
    characteristics: [
      FundingCharacteristicEnum.BILATERAL_PROGRAMME,
      FundingCharacteristicEnum.SCIENTIFIC_PROGRAMME,
    ],
    legalType: LegalTypeEnum.P_26,
    fundingScheme: FundingSchemeEnum.GRANT,
    decisionProcess: [DecisionProcessEnum.JURY],
    submissionModes: [
      ModeOfSubmissionEnum.OFFLINE,
      ModeOfSubmissionEnum.ONLINE_PARTLY,
    ],
  },
  {
    id: '23',
    funder: {
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
    risId: 'https://fwf.ac.at/calls/call/2',
    website: [new URL('https://fwf.ac.at/call2')],
    name: [{ text: 'P-Einzelprojekt', language: LanguageEnum.ENGLISH }],
    acronym: 'P-Einz',
    description: [
      {
        text:
          'Support funding of individual research in the area of non-profit oriented scholarly/scientific ' +
          'research for researchers of any discipline working in Austria',
        language: LanguageEnum.ENGLISH,
      },
    ],
  },
  {
    id: '20',
    funder: {
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
    jointCallPartner: [
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
    ],
    risId: 'https://fwf.ac.at/calls/call/3',
    name: [{ text: 'TAI-2020', language: LanguageEnum.ENGLISH }],
    acronym: 'TAI-2020',
    description: [
      {
        text:
          'With the 1000 Ideas Programme, the FWF supports the promotion of completely new, daring ' +
          'or particularly original research ideas that lie outside the current scientific understanding. The key ' +
          'aim is to investigate future-oriented research topics with high scientific and transformative potential.',
        language: LanguageEnum.ENGLISH,
      },
    ],
    callStages: [
      {
        duration: {
          start: 'Sep 09 2019 07:44:57',
          end: 'Oct 22 2022 00:00:00 GMT',
        },
        description: [
          { text: 'Submit abstract...', language: LanguageEnum.ENGLISH },
          { text: 'Abstract einreichen...', language: LanguageEnum.GERMAN },
        ],
      },
      {
        duration: {
          start: 'Oct 22 2022 00:00:00 GMT',
          end: 'Nov 22 2022 00:00:00 GMT',
        },
        description: [
          { text: 'Submit final proposal...', language: LanguageEnum.ENGLISH },
          { text: 'Antrag einreichen...', language: LanguageEnum.GERMAN },
        ],
      },
    ],
  },
];
