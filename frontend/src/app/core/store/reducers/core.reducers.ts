import { ActionReducerMap } from '@ngrx/store';
import { CoreState } from '../states/core.state';
import { oefosReducer } from './oefos.reducers';

export const coreReducers: ActionReducerMap<CoreState> = {
  oefos: oefosReducer,
};
