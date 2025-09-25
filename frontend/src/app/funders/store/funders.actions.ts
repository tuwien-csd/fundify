import { createAction, props } from '@ngrx/store';
import { FunderWebModel } from '../models/funder.interface';

/****************************************************************** */
/***** LOAD FUNDERS **** */
/****************************************************************** */
export const loadFunders = createAction(
  '[Funders] Load Funders',
  props<{ skipIfPresent: boolean }>()
);

export const loadFundersSuccess = createAction(
  '[Funders] Load Funders Success',
  props<{ funders: FunderWebModel[] }>()
);
export const loadFundersFailure = createAction(
  '[Funders] Load Funders Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

/****************************************************************** */
/***** LOAD INDIVIDUAL FUNDER **** */
/****************************************************************** */
export const loadFunder = createAction(
  '[Funder Edit/View Component] Load Funder',
  props<{ id: string }>()
);

export const loadFunderSuccess = createAction(
  '[Funder Effect] Load Funder Success',
  props<{ funder: FunderWebModel }>()
);

export const loadFunderFailure = createAction(
  '[Funder Effect] Load Funder Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

/****************************************************************** */
/***** ADD INDIVIDUAL FUNDER **** */
/****************************************************************** */
export const addFunder = createAction(
  '[Funder Detail Component] Add Funder',
  props<{ funder: FunderWebModel }>()
);
export const addFunderSuccess = createAction(
  '[Funder Effect] Add Funder Success',
  props<{ funder: FunderWebModel }>()
);
export const addFunderFailure = createAction(
  '[Funder Effect] Add Funder Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

/****************************************************************** */
/***** UPDATE INDIVIDUAL FUNDER **** */
/****************************************************************** */
export const upsertFunder = createAction(
  '[Funder Edit Component] Upsert Funder',
  props<{ funder: FunderWebModel }>()
);
export const upsertFunderSuccess = createAction(
  '[Funder Edit Component] Upsert Funder Success',
  props<{ funder: FunderWebModel }>()
);
export const upsertFunderFailure = createAction(
  '[Funder Edit Component] Upsert Funder Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

/****************************************************************** */
/***** DELETE INDIVIDUAL FUNDER **** */
/****************************************************************** */
export const deleteFunder = createAction(
  '[Funders Component] Delete Funder',
  props<{ funderId: string }>()
);
export const deleteFunderSuccess = createAction(
  '[Funders Component] Delete Funder Success',
  props<{ funderId: string }>()
);
export const deleteFunderFailure = createAction(
  '[Funders Component] Delete Funder Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const clearFunders = createAction('[Funder/API] Clear Funders');
