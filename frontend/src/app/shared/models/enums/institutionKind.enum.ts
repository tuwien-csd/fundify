export const InstitutionKindEnum = {
  FUNDER: 'Funder',
  RESEARCH_INSTITUTE: 'Research Institute',
  OTHER: 'Other',
} as const;

export type InstitutionKindEnum =
  (typeof InstitutionKindEnum)[keyof typeof InstitutionKindEnum];
