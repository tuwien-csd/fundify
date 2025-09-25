export const EntryOriginEnum = {
  REFOP: 'REFOP',
  ENDPOINT: 'ENDPOINT',
} as const;

export type EntryOriginEnum =
  (typeof EntryOriginEnum)[keyof typeof EntryOriginEnum];
