import { getRouterSelectors, RouterReducerState } from '@ngrx/router-store';
import { createFeatureSelector, createSelector } from '@ngrx/store';

export const selectRouter = createFeatureSelector<RouterReducerState>('router');

export const {
  selectCurrentRoute,
  selectFragment,
  selectQueryParams,
  selectQueryParam,
  selectRouteParams,
  selectRouteParam,
  selectRouteData,
  selectUrl,
  selectTitle,
} = getRouterSelectors();

export const selectPageMode = createSelector(selectUrl, (url) => {
  if (url.includes('/edit')) {
    return 'edit';
  }
  if (url.includes('/preview')) {
    return 'preview';
  }
  if (url.includes('/annotate')) {
    return 'annotate';
  }
  return null;
});
