import { createAction, props } from '@ngrx/store';
import { StandardizedSubject } from '../../../shared/models/interfaces/standardizedSubject.interface';

export const loadOefos = createAction('[Subjects] Load Subjects');
export const loadOefosSuccess = createAction(
  '[Subjects] Load Subjects Success',
  props<{ subjects: StandardizedSubject[] }>()
);
export const loadOefosFailure = createAction(
  '[Subjects] Load Subjects Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);
