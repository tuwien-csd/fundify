import { createAction, props } from '@ngrx/store';
import { Program } from '../models/program.interface';

/****************************************************************** */
/***** LOAD PROGRAMS **** */
/****************************************************************** */
export const loadPrograms = createAction(
  '[Programs] Load Programs',
  props<{ skipIfPresent: boolean }>()
);
export const loadProgramDrafts = createAction(
  '[Programs] Load Program Drafts',
  props<{ skipIfPresent: boolean; id: string }>()
);
export const loadProgramsSuccess = createAction(
  '[Programs] Load Programs Success',
  props<{ programs: Program[] }>()
);
export const loadProgramDraftsSuccess = createAction(
  '[Programs] Load Program Drafts Success',
  props<{ programs: Program[] }>()
);
export const loadProgramsFailure = createAction(
  '[Programs] Load Programs Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);
export const loadProgramDraftsFailure = createAction(
  '[Programs] Load Program Drafts Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

/****************************************************************** */
/***** LOAD INDIVIDUAL PROGRAM **** */
/****************************************************************** */
export const loadProgram = createAction(
  '[Program Edit/View Component] Load Program',
  props<{ id: string }>()
);

export const loadProgramSuccess = createAction(
  '[Program Effect] Load Program Success',
  props<{ program: Program }>()
);

export const loadProgramFailure = createAction(
  '[Program Effect] Load Program Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

/****************************************************************** */
/***** ADD INDIVIDUAL PROGRAM **** */
/****************************************************************** */
export const addProgram = createAction(
  '[Program Detail Component] Add Program',
  props<{ program: Program }>()
);
export const addProgramSuccess = createAction(
  '[Program Effect] Add Program Success',
  props<{ program: Program }>()
);
export const addProgramFailure = createAction(
  '[Program Effect] Add Program Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

/****************************************************************** */
/***** UPDATE INDIVIDUAL PROGRAM **** */
/****************************************************************** */
export const upsertProgram = createAction(
  '[Program Edit Component] Upsert Program',
  props<{ program: Program }>()
);
export const upsertProgramSuccess = createAction(
  '[Program Edit Component] Upsert Program Success',
  props<{ program: Program }>()
);
export const upsertProgramFailure = createAction(
  '[Program Edit Component] Upsert Program Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

/****************************************************************** */
/***** DELETE INDIVIDUAL PROGRAM **** */
/****************************************************************** */
export const deleteProgram = createAction(
  '[Programs Component] Delete Program',
  props<{ programId: string }>()
);
export const deleteProgramSuccess = createAction(
  '[Programs Component] Delete Program Success',
  props<{ programId: string }>()
);
export const deleteProgramFailure = createAction(
  '[Programs Component] Delete Program Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const clearPrograms = createAction('[Program/API] Clear Programs');
