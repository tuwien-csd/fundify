export const TranslationEnum = {
  ORIGINAL: 'o',
  TRANSLATION: 't',
} as const;

export type TranslationEnum =
  (typeof TranslationEnum)[keyof typeof TranslationEnum];
