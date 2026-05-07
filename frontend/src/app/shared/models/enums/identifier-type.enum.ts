export const IdentifierTypeEnum = {
  CROSSREF_GRANTID: 'Crossref Grant ID',
  PROJECT_NUMBER: 'Project Number',
  APPLICATION_NUMBER: 'Application Number',
  ORCID: 'ORCID',
  ROR: 'ROR',
  RINGGOLD: 'Ringgold',
  RIS_SYNERGY: 'Ris Synergy',
  EU_ID: 'EU-ID',
} as const;

export type IdentifierTypeEnum =
  (typeof IdentifierTypeEnum)[keyof typeof IdentifierTypeEnum];
