import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';

import { catchError, map, switchMap } from 'rxjs/operators';
import { SubjectService } from '../../services/subject.service';
import * as oefosActions from '../actions/oefos.actions';
import { of } from 'rxjs';

@Injectable()
export class OefosEffects {
  private actions$ = inject(Actions);
  private subjectService = inject(SubjectService);

  loadOefos$ = createEffect(() =>
    this.actions$.pipe(
      ofType(oefosActions.loadOefos),
      switchMap(() =>
        this.subjectService.fetchSubjectsFromBackend().pipe(
          map((subjects) => oefosActions.loadOefosSuccess({ subjects })),
          catchError((error) => of(oefosActions.loadOefosFailure({ error })))
        )
      )
    )
  );
}
