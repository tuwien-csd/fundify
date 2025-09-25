import { AppEnvironment } from './appenvironment.type';

export const environment = {
  production: true,
  backendUrl: `${window.location.origin}/api/`,
  backendBaseUrl: `${window.location.origin}/`,
} satisfies AppEnvironment;
