import { createFeatureSelector, createSelector } from '@ngrx/store';
import { adapter, ProgramsState } from './programs.state';

export const selectProgramsState =
  createFeatureSelector<ProgramsState>('programs');

const { selectAll, selectEntities } = adapter.getSelectors();

export const selectAllPrograms = createSelector(selectProgramsState, selectAll);
export const selectAllProgramEntities = createSelector(
  selectProgramsState,
  selectEntities
);

export const entityExists = (id: string) =>
  createSelector(selectAllProgramEntities, (entities) => !!entities[id]);

export const selectProgramById = (id: string) =>
  createSelector(selectAllProgramEntities, (entities) => entities[id]);
