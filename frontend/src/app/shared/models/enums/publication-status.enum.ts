export const PublicationStatusEnum = {
  DRAFT: 'DRAFT',
  PUBLISHED: 'PUBLISHED',
} as const;

export type PublicationStatusEnum =
  (typeof PublicationStatusEnum)[keyof typeof PublicationStatusEnum];
