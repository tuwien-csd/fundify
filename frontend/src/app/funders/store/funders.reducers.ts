import { createReducer, on } from '@ngrx/store';
import * as FundersActions from './funders.actions';
import { adapter, initialState } from './funders.state';
import { AsyncState } from '../../core/models/enums/async-state.enum';

export const fundersReducer = createReducer(
  initialState,

  on(FundersActions.loadFunders, (state, { skipIfPresent }) => {
    return skipIfPresent && state.state !== AsyncState.PENDING
      ? { ...state }
      : { ...state, state: AsyncState.LOADING };
  }),
  on(FundersActions.loadFundersSuccess, (state, { funders }) => {
    return adapter.setAll(funders, {
      ...state,
      state: AsyncState.SUCCESS,
      error: null,
    });
  }),
  on(FundersActions.loadFundersFailure, (state, { error }) => ({
    ...state,
    state: AsyncState.ERROR,
    error,
  })),

  on(
    FundersActions.loadFunder,
    FundersActions.addFunder,
    FundersActions.upsertFunder,
    FundersActions.deleteFunder,
    // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
    (state, _) => {
      return {
        ...state,
        state: AsyncState.LOADING,
      };
    }
  ),
  on(
    FundersActions.loadFunderSuccess,
    FundersActions.addFunderSuccess,
    (state, { funder }) => {
      return adapter.addOne(funder, {
        ...state,
        state: AsyncState.SUCCESS,
        error: null,
      });
    }
  ),
  on(FundersActions.upsertFunderSuccess, (state, { funder }) => {
    return adapter.upsertOne(funder, {
      ...state,
      state: AsyncState.SUCCESS,
      error: null,
    });
  }),
  on(FundersActions.deleteFunderSuccess, (state, { funderId }) => {
    return adapter.removeOne(funderId, {
      ...state,
      state: AsyncState.SUCCESS,
    });
  }),
  on(
    FundersActions.loadFunderFailure,
    FundersActions.addFunderFailure,
    FundersActions.upsertFunderFailure,
    FundersActions.deleteFunderFailure,
    (state, { error }) => ({
      ...state,
      state: AsyncState.ERROR,
      error,
    })
  ),
  on(FundersActions.clearFunders, (state) =>
    adapter.removeAll({
      ...state,
      state: AsyncState.PENDING,
    })
  )
);
