import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { ProgramService } from '../services/program.service';
import * as ProgramsActions from './programs.actions';

@Injectable()
export class ProgramsEffects {
  private actions$ = inject(Actions);
  private programService = inject(ProgramService);

  // add url argument for pagination and filtering in future
  loadPrograms$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ProgramsActions.loadPrograms),
      mergeMap(() =>
        this.programService.getPrograms().pipe(
          map((programs) => ProgramsActions.loadProgramsSuccess({ programs })),
          catchError((error) =>
            of(ProgramsActions.loadProgramsFailure({ error }))
          )
        )
      )
    );
  });

  loadProgramDrafts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ProgramsActions.loadProgramDrafts),
      mergeMap((action) =>
        this.programService.getProgramDrafts(action.id).pipe(
          map((programs) => ProgramsActions.loadProgramsSuccess({ programs })),
          catchError((error) =>
            of(ProgramsActions.loadProgramsFailure({ error }))
          )
        )
      )
    );
  });

  loadProgram$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ProgramsActions.loadProgram),
      mergeMap((action) =>
        this.programService.getProgram(action.id).pipe(
          map((program) => ProgramsActions.loadProgramSuccess({ program })),
          catchError((error) =>
            of(ProgramsActions.loadProgramFailure({ error }))
          )
        )
      )
    );
  });

  addProgram$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ProgramsActions.addProgram),
      mergeMap((action) =>
        this.programService.addProgram(action.program).pipe(
          map((program) => ProgramsActions.addProgramSuccess({ program })),
          catchError((error) =>
            of(ProgramsActions.addProgramFailure({ error }))
          )
        )
      )
    );
  });

  updateProgram$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ProgramsActions.upsertProgram),
      mergeMap((action) =>
        this.programService.updateProgram(action.program).pipe(
          map((program) => ProgramsActions.upsertProgramSuccess({ program })),
          catchError((error) =>
            of(ProgramsActions.upsertProgramFailure({ error }))
          )
        )
      )
    );
  });

  deleteProgram$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ProgramsActions.deleteProgram),
      mergeMap((action) =>
        this.programService.deleteProgram(action.programId).pipe(
          map(() =>
            ProgramsActions.deleteProgramSuccess({
              programId: action.programId,
            })
          ),
          catchError((error) =>
            of(ProgramsActions.deleteProgramFailure({ error }))
          )
        )
      )
    );
  });
}
