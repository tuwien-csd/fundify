import { inject, Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import { catchError, concatMap, map, switchMap } from 'rxjs/operators';
import { CallService } from '../services/call.service';
import * as CallsActions from './calls.actions';
import { removeVocabularyEntrySuccess } from './calls.actions';
import { Dictionary } from '@ngrx/entity';
import { Vocabulary } from '../models/vocabulary.interface';
import { VocabularyTypeEnum } from '../models/vocabulary-type.enum';
import { UniversityReference } from '../models/university-reference.interface';
import { PublicationStatusEnum } from '../../shared/models/enums/publication-status.enum';
import { AuthService } from '../../core/auth/services/auth.service';
import { UniversitiesStore } from '../../universities/signal/universities-store';
import { AnnotatedCall } from '../models/annotated-call.interface';

@Injectable()
export class CallsEffects {
  private actions$ = inject(Actions);
  private callService = inject(CallService);
  private authService = inject(AuthService);
  private universitiesStore = inject(UniversitiesStore);

  loadCalls$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.loadCalls),
      switchMap(() =>
        this.callService.getCalls().pipe(
          map((calls) => CallsActions.loadCallsSuccess({ calls })),
          catchError((error) => of(CallsActions.loadCallsFailure({ error })))
        )
      )
    );
  });

  loadCallDrafts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.loadCallDrafts),
      switchMap((action) =>
        this.callService.getCallDrafts(action.id).pipe(
          map((calls) => CallsActions.loadCallsSuccess({ calls })),
          catchError((error) => of(CallsActions.loadCallsFailure({ error })))
        )
      )
    );
  });

  loadCall$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.loadCall),
      switchMap((action) =>
        this.callService.getCall(action.id).pipe(
          map((call) => CallsActions.loadCallSuccess({ call })),
          catchError((error) => of(CallsActions.loadCallFailure({ error })))
        )
      )
    );
  });

  addCall$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.addCall),
      concatMap((action) =>
        this.callService.addCall(action.call).pipe(
          map((call) => CallsActions.addCallSuccess({ call })),
          catchError((error) => of(CallsActions.addCallFailure({ error })))
        )
      )
    );
  });

  updateCall$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.upsertCall),
      switchMap((action) =>
        this.callService.updateCall(action.call).pipe(
          map((call) => CallsActions.upsertCallSuccess({ call })),
          catchError((error) => of(CallsActions.upsertCallFailure({ error })))
        )
      )
    );
  });

  deleteCall$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.deleteCall),
      switchMap((action) =>
        this.callService.deleteCall(action.callId).pipe(
          map(() => CallsActions.deleteCallSuccess({ callId: action.callId })),
          catchError((error) => of(CallsActions.deleteCallFailure({ error })))
        )
      )
    );
  });

  loadAnnotatedCalls$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.loadAnnotatedCalls),
      switchMap(() =>
        this.callService.getAnnotatedCalls().pipe(
          map((annotatedCalls) =>
            CallsActions.loadAnnotatedCallsSuccess({ annotatedCalls })
          ),
          catchError((error) =>
            of(CallsActions.loadAnnotatedCallsFailure({ error }))
          )
        )
      )
    );
  });

  loadAnnotatedCall$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.loadAnnotatedCall),
      switchMap((action) =>
        this.callService
          .getAnnotatedCallByCallIdAndUniversityId(
            action.callId,
            action.universityId
          )
          .pipe(
            map((annotatedCall) =>
              CallsActions.loadAnnotatedCallSuccess({ annotatedCall })
            ),
            catchError((error) =>
              error.status === 404
                ? this.handleAnnotatedCallNotFound(
                    action.callId,
                    action.universityId
                  )
                : of(
                    CallsActions.loadAnnotatedCallFailure({
                      error: error.message,
                    })
                  )
            )
          )
      )
    );
  });

  private handleAnnotatedCallNotFound(callId: string, universityId: string) {
    return this.callService.getCallPreview(callId).pipe(
      map((callPreview) =>
        CallsActions.loadAnnotatedCallSuccess({
          annotatedCall: {
            callPreview: callPreview,
            university: { id: universityId } as UniversityReference,
          },
        })
      ),
      catchError((error) =>
        of(CallsActions.loadAnnotatedCallFailure({ error }))
      )
    );
  }

  annotateCall$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.annotateCall),
      switchMap((action) => {
        // Setting the university ID here is quite hacky and should be refactored once we move away from ngrx
        const currentUserUniversity =
          this.universitiesStore.getUniversityByAcronym(
            this.authService.userAffiliationId()
          );
        const updatedInput: AnnotatedCall = {
          ...action.annotatedCall,
          university: {
            id: currentUserUniversity?.id ?? '',
            acronym: currentUserUniversity?.acronym ?? '',
            emailDomain: currentUserUniversity?.emailDomain ?? '',
          },
        };

        return this.callService.annotateCall(updatedInput).pipe(
          map((annotatedCallId) => {
            return CallsActions.annotateCallSuccess({
              annotatedCall: {
                ...action.annotatedCall,
                id: annotatedCallId.value,
                status: PublicationStatusEnum.DRAFT,
              },
            });
          }),
          catchError((error) => of(CallsActions.annotateCallFailure({ error })))
        );
      })
    );
  });

  publishAnnotatedCall$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.publishAnnotatedCall),
      switchMap((action) =>
        this.callService.publishAnnotatedCall(action.annotatedCall.id!).pipe(
          // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
          map((_) =>
            CallsActions.publishAnnotatedCallSuccess({
              annotatedCall: {
                ...action.annotatedCall,
                status: PublicationStatusEnum.PUBLISHED,
              },
            })
          ),
          catchError((error) =>
            of(CallsActions.publishAnnotatedCallFailure({ error }))
          )
        )
      )
    );
  });

  deleteAnnotation$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.deleteCallAnnotation),
      switchMap((action) =>
        this.callService.deleteCallAnnotation(action.annotatedCallId).pipe(
          // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
          map((_) =>
            CallsActions.deleteCallAnnotationSuccess({ callId: action.callId })
          ),
          catchError((error) =>
            of(CallsActions.deleteCallAnnotationFailure({ error }))
          )
        )
      )
    );
  });

  saveAndPublishAnnotatedCall$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.saveAndPublishAnnotatedCall),
      switchMap((action) => {
        const currentUserUniversity =
          this.universitiesStore.getUniversityByAcronym(
            this.authService.userAffiliationId()
          );
        const updatedInput: AnnotatedCall = {
          ...action.annotatedCall,
          university: {
            id: currentUserUniversity?.id ?? '',
            acronym: currentUserUniversity?.acronym ?? '',
            emailDomain: currentUserUniversity?.emailDomain ?? '',
          },
        };
        return this.callService.annotateCall(updatedInput).pipe(
          switchMap((annotatedCallId) =>
            this.callService.publishAnnotatedCall(annotatedCallId.value).pipe(
              map((annotatedCallId) =>
                CallsActions.publishAnnotatedCallSuccess({
                  annotatedCall: {
                    ...action.annotatedCall,
                    id: annotatedCallId.value,
                    status: PublicationStatusEnum.PUBLISHED,
                  },
                })
              ),
              catchError((error) =>
                of(CallsActions.publishAnnotatedCallFailure({ error }))
              )
            )
          ),
          catchError((error) => of(CallsActions.annotateCallFailure({ error })))
        );
      })
    );
  });

  loadVocabularies$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.loadVocabularies),
      switchMap(() =>
        this.callService.getVocabularies().pipe(
          map((vocabularies) =>
            CallsActions.loadVocabulariesSuccess({
              vocabularies: vocabularies.reduce((acc, vocabulary) => {
                acc[VocabularyTypeEnum[vocabulary.type]] = vocabulary;
                return acc;
              }, {} as Dictionary<Vocabulary>),
            })
          ),
          catchError((error) =>
            of(CallsActions.loadVocabulariesFailure({ error }))
          )
        )
      )
    );
  });

  addEntry$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.addVocabularyEntry),
      concatMap((action) =>
        this.callService
          .addVocabularyEntry(action.vocabularyId, action.entry)
          .pipe(
            map(() =>
              CallsActions.addVocabularyEntrySuccess({
                vocabularyId: action.vocabularyId,
                entry: action.entry,
              })
            ),
            catchError((error) =>
              of(CallsActions.addVocabularyEntryFailure({ error }))
            )
          )
      )
    );
  });

  removeEntry$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(CallsActions.removeVocabularyEntry),
      concatMap((action) =>
        this.callService
          .removeVocabularyEntry(action.vocabularyId, action.entry)
          .pipe(
            map((vocabularyId) =>
              removeVocabularyEntrySuccess({
                vocabularyId: vocabularyId.value,
                entry: action.entry,
              })
            ),
            catchError((error) =>
              of(CallsActions.removeVocabularyEntryFailure({ error }))
            )
          )
      )
    );
  });

  refreshVocabulary$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        CallsActions.addVocabularyEntrySuccess,
        CallsActions.removeVocabularyEntrySuccess
      ),
      switchMap((action) =>
        this.callService.getVocabularyById(action.vocabularyId).pipe(
          map((vocabulary) =>
            CallsActions.loadVocabularyByIdSuccess({ vocabulary })
          ),
          catchError((error) =>
            of(CallsActions.loadVocabularyByIdFailure({ error }))
          )
        )
      )
    );
  });
}
