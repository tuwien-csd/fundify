import { Action, createReducer, on } from '@ngrx/store';
import { initialState, OefosState } from '../states/oefos.state';
import {
  loadOefos,
  loadOefosFailure,
  loadOefosSuccess,
} from '../actions/oefos.actions';

const _oefosReducer = createReducer(
  initialState,
  on(loadOefos, (state) => ({
    ...state,
    loading: true,
  })),
  on(loadOefosSuccess, (state, { subjects }) => ({
    ...state,
    subjects,
    loading: false,
  })),
  on(loadOefosFailure, (state, { error }) => ({
    ...state,
    error,
    loading: false,
  }))
);

export function oefosReducer(state: OefosState | undefined, action: Action) {
  return _oefosReducer(state, action);
}
