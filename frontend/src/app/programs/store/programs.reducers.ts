import { createReducer, on } from '@ngrx/store';
import { adapter, initialState } from './programs.state';
import { AsyncState } from '../../core/models/enums/async-state.enum';
import * as ProgramsActions from './programs.actions';

export const programsReducer = createReducer(
  initialState,

  on(ProgramsActions.loadPrograms, (state, { skipIfPresent }) => {
    return skipIfPresent && state.state !== AsyncState.PENDING
      ? { ...state }
      : { ...state, state: AsyncState.LOADING };
  }),
  on(ProgramsActions.loadProgramDrafts, (state, { skipIfPresent }) => {
    return skipIfPresent && state.state !== AsyncState.PENDING
      ? { ...state }
      : { ...state, state: AsyncState.LOADING };
  }),
  on(
    ProgramsActions.loadProgramsSuccess,
    ProgramsActions.loadProgramDraftsSuccess,
    (state, { programs }) => {
      return adapter.upsertMany(programs, {
        ...state,
        state: AsyncState.SUCCESS,
        error: null,
      });
    }
  ),
  on(
    ProgramsActions.loadProgramsFailure,
    ProgramsActions.loadProgramDraftsFailure,
    (state, { error }) => ({
      ...state,
      state: AsyncState.ERROR,
      error,
    })
  ),

  on(
    ProgramsActions.loadProgram,
    ProgramsActions.addProgram,
    ProgramsActions.upsertProgram,
    ProgramsActions.deleteProgram,
    // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
    (state, _) => {
      return {
        ...state,
        state: AsyncState.LOADING,
      };
    }
  ),
  on(
    ProgramsActions.loadProgramSuccess,
    ProgramsActions.addProgramSuccess,
    (state, { program }) => {
      return adapter.addOne(program, {
        ...state,
        state: AsyncState.SUCCESS,
        error: null,
      });
    }
  ),
  on(ProgramsActions.upsertProgramSuccess, (state, { program }) => {
    return adapter.upsertOne(program, {
      ...state,
      state: AsyncState.SUCCESS,
      error: null,
    });
  }),
  on(ProgramsActions.deleteProgramSuccess, (state, { programId }) => {
    return adapter.removeOne(programId, {
      ...state,
      state: AsyncState.SUCCESS,
    });
  }),
  on(
    ProgramsActions.loadProgramFailure,
    ProgramsActions.addProgramFailure,
    ProgramsActions.upsertProgramFailure,
    ProgramsActions.deleteProgramFailure,
    (state, { error }) => ({
      ...state,
      state: AsyncState.ERROR,
      error,
    })
  ),
  on(ProgramsActions.clearPrograms, (state) =>
    adapter.removeAll({
      ...state,
      state: AsyncState.PENDING,
    })
  )
);
