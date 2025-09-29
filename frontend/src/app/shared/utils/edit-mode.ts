export const EditMode = {
  CREATE: 'CREATE',
  EDIT: 'EDIT',
} as const;

export type EditMode = (typeof EditMode)[keyof typeof EditMode];
