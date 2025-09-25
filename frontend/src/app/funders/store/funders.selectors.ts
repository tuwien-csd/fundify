import { createFeatureSelector, createSelector } from '@ngrx/store';
import { adapter, FundersState } from './funders.state';

export const selectFundersState =
  createFeatureSelector<FundersState>('funders');

const { selectAll, selectEntities } = adapter.getSelectors();

export const selectAllFunders = createSelector(selectFundersState, selectAll);
export const selectAllEntities = createSelector(
  selectFundersState,
  selectEntities
);

export const entityExists = (id: string) =>
  createSelector(selectAllEntities, (entities) => !!entities[id]);

export const selectFunderById = (id: string) =>
  createSelector(selectAllEntities, (entities) => entities[id]);
