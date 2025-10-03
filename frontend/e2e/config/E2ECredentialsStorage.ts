export type E2ECredential = {
  FILE: string;
  USERNAME: string;
  PASSWORD: string;
};

export const E2E_CREDENTIALS = {
  ADMIN: {
    FILE: 'e2e/config/.auth/admin.json',
    USERNAME: 'admin',
    PASSWORD: 'admin',
  },
  FUNDER: {
    FILE: 'e2e/config/.auth/funder.json',
    USERNAME: 'ffg',
    PASSWORD: 'ffg',
  },
  ANNOTATOR: {
    FILE: 'e2e/config/.auth/annotator.json',
    USERNAME: 'tuwien',
    PASSWORD: 'tuwien',
  },
} as const satisfies Record<string, E2ECredential>;
