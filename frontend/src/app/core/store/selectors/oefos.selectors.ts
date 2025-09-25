import { createSelector } from '@ngrx/store';
import { selectCoreState } from './core.selectors';

export const selectOefosState = createSelector(
  selectCoreState,
  (core) => core.oefos
);

export const selectOefos = createSelector(
  selectOefosState,
  (oefosState) => oefosState.subjects
);

export const selectOefosLoading = createSelector(
  selectOefosState,
  (oefosState) => oefosState.loading
);

export const selectOefosError = createSelector(
  selectOefosState,
  (oefosState) => oefosState.error
);
