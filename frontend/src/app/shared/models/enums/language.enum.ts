export const LanguageEnum = {
  ENGLISH: 'En',
  GERMAN: 'De',
} as const;

export type LanguageEnum = (typeof LanguageEnum)[keyof typeof LanguageEnum];
