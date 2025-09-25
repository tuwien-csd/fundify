// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { AppEnvironment } from './appenvironment.type';

export const environment = {
  production: false,
  backendUrl: 'http://localhost:8080/api/',
  backendBaseUrl: 'http://localhost:8080/',
} satisfies AppEnvironment;
