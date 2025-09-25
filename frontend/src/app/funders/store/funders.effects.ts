import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, exhaustMap, map, mergeMap } from 'rxjs/operators';
import * as FundersActions from './funders.actions';
import { FunderService } from '../services/funder.service';
import { ROUTER_LINKS } from '../../core/router-links.constants';
import { Router } from '@angular/router';

@Injectable()
export class FundersEffects {
  private actions$ = inject(Actions);
  private funderService = inject(FunderService);
  private router = inject(Router);

  // add url argument for pagination and filtering in future
  loadFunders$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(FundersActions.loadFunders),
      mergeMap(() =>
        this.funderService.getFunders().pipe(
          map((funders) => FundersActions.loadFundersSuccess({ funders })),
          catchError((error) =>
            of(FundersActions.loadFundersFailure({ error }))
          )
        )
      )
    );
  });

  loadFunder$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(FundersActions.loadFunder),
      mergeMap((action) =>
        this.funderService.getFunder(action.id).pipe(
          map((funder) => FundersActions.loadFunderSuccess({ funder })),
          catchError((error) => of(FundersActions.loadFunderFailure({ error })))
        )
      )
    );
  });

  addFunder$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(FundersActions.addFunder),
      exhaustMap((action) =>
        this.funderService.addFunder(action.funder).pipe(
          map((funder) => FundersActions.addFunderSuccess({ funder })),
          catchError((error) => of(FundersActions.addFunderFailure({ error })))
        )
      )
    );
  });

  updateFunder$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(FundersActions.upsertFunder),
      exhaustMap((action) =>
        this.funderService.updateFunder(action.funder).pipe(
          map((funder) => FundersActions.upsertFunderSuccess({ funder })),
          catchError((error) =>
            of(FundersActions.upsertFunderFailure({ error }))
          )
        )
      )
    );
  });

  deleteFunder$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(FundersActions.deleteFunder),
      exhaustMap((action) =>
        this.funderService.deleteFunder(action.funderId).pipe(
          map(() =>
            FundersActions.deleteFunderSuccess({ funderId: action.funderId })
          ),
          catchError((error) =>
            of(FundersActions.deleteFunderFailure({ error }))
          )
        )
      )
    );
  });

  navigateToHome$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(FundersActions.addFunderSuccess),
        map(() => this.router.navigate([ROUTER_LINKS.HOME]))
      );
    },
    { dispatch: false }
  );

  navigateToFunders$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(FundersActions.upsertFunderSuccess),
        map(() => this.router.navigate([ROUTER_LINKS.FUNDERS]))
      );
    },
    { dispatch: false }
  );
}
