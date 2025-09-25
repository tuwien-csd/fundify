import { createReducer, on } from '@ngrx/store';
import {
  annotatedCallsAdapter,
  callsAdapter,
  initialAnnotatedCallState,
  initialCallState,
} from './calls.state';
import { AsyncState } from '../../core/models/enums/async-state.enum';
import * as CallsActions from './calls.actions';
import { VocabularyTypeEnum } from '../models/vocabulary-type.enum';

export const callsReducer = createReducer(
  initialCallState,
  on(
    CallsActions.loadCalls,
    CallsActions.loadCallDrafts,
    CallsActions.loadAnnotatedCalls,
    (state, { skipIfPresent }) => {
      return skipIfPresent && state.state !== AsyncState.PENDING
        ? { ...state }
        : { ...state, state: AsyncState.LOADING };
    }
  ),
  on(
    CallsActions.loadCallsSuccess,
    CallsActions.loadCallDraftsSuccess,
    (state, { calls }) => {
      return callsAdapter.upsertMany(calls, {
        ...state,
        state: AsyncState.SUCCESS,
        error: null,
      });
    }
  ),
  on(
    CallsActions.loadCallsFailure,
    CallsActions.loadCallDraftsFailure,
    (state, { error }) => ({
      ...state,
      state: AsyncState.ERROR,
      error,
    })
  ),
  on(
    CallsActions.loadCall,
    CallsActions.addCall,
    CallsActions.upsertCall,
    CallsActions.deleteCall,
    // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
    (state, _) => {
      return {
        ...state,
        state: AsyncState.LOADING,
      };
    }
  ),
  on(
    CallsActions.loadCallSuccess,
    CallsActions.addCallSuccess,
    (state, { call }) => {
      return callsAdapter.addOne(call, {
        ...state,
        state: AsyncState.SUCCESS,
        error: null,
      });
    }
  ),
  on(CallsActions.upsertCallSuccess, (state, { call }) => {
    return callsAdapter.upsertOne(call, {
      ...state,
      state: AsyncState.SUCCESS,
      error: null,
    });
  }),
  on(CallsActions.deleteCallSuccess, (state, { callId }) => {
    return callsAdapter.removeOne(callId, {
      ...state,
      state: AsyncState.SUCCESS,
    });
  }),
  on(
    CallsActions.loadCallFailure,
    CallsActions.addCallFailure,
    CallsActions.upsertCallFailure,
    CallsActions.deleteCallFailure,
    (state, { error }) => ({
      ...state,
      state: AsyncState.ERROR,
      error,
    })
  ),
  on(CallsActions.clearCalls, (state) =>
    callsAdapter.removeAll({
      ...state,
      state: AsyncState.PENDING,
    })
  ),
  on(CallsActions.loadVocabularies, (state) => ({
    ...state,
    state: AsyncState.LOADING,
  })),
  on(CallsActions.loadVocabulariesSuccess, (state, { vocabularies }) => ({
    ...state,
    state: AsyncState.SUCCESS,
    vocabularies,
  })),
  on(CallsActions.loadVocabulariesFailure, (state, { error }) => ({
    ...state,
    state: AsyncState.ERROR,
    error,
  })),
  on(CallsActions.addVocabularyEntry, (state) => ({
    ...state,
    state: AsyncState.LOADING,
  })),
  on(CallsActions.addVocabularyEntrySuccess, (state) => ({
    ...state,
    state: AsyncState.SUCCESS,
  })),
  on(CallsActions.addVocabularyEntryFailure, (state, { error }) => ({
    ...state,
    state: AsyncState.ERROR,
    error,
  })),
  on(CallsActions.loadVocabularyByIdSuccess, (state, { vocabulary }) => ({
    ...state,
    vocabularies: {
      ...state.vocabularies,
      [VocabularyTypeEnum[vocabulary.type]]: vocabulary,
    },
  })),
  on(CallsActions.loadVocabularyByIdFailure, (state, { error }) => ({
    ...state,
    error,
  }))
);

export const annotatedCallsReducer = createReducer(
  initialAnnotatedCallState,
  on(CallsActions.loadAnnotatedCalls, (state, { skipIfPresent }) => {
    return skipIfPresent //TODO: ANGULAR_MIGRATION: Verify if this can be safely removed:      && state.state !== AsyncState.PENDING
      ? { ...state }
      : { ...state, state: AsyncState.LOADING };
  }),
  on(CallsActions.loadAnnotatedCallsSuccess, (state, { annotatedCalls }) => {
    return annotatedCallsAdapter.upsertMany(annotatedCalls, {
      ...state,
      state: AsyncState.SUCCESS,
      errror: null,
    });
  }),
  on(CallsActions.loadAnnotatedCallsFailure, (state, { error }) => ({
    ...state,
    state: AsyncState.ERROR,
    error,
  })),
  on(CallsActions.loadAnnotatedCall, (state) => ({
    ...state,
    state: AsyncState.LOADING,
  })),
  on(CallsActions.loadAnnotatedCallSuccess, (state, { annotatedCall }) => {
    return annotatedCallsAdapter.upsertOne(annotatedCall, {
      ...state,
      state: AsyncState.SUCCESS,
      error: null,
    });
  }),
  on(CallsActions.loadAnnotatedCallFailure, (state, { error }) => ({
    ...state,
    state: AsyncState.ERROR,
    error,
  })),
  on(CallsActions.annotateCall, (state) => ({
    ...state,
    state: AsyncState.LOADING,
  })),
  on(CallsActions.annotateCallSuccess, (state, { annotatedCall }) => {
    return annotatedCallsAdapter.upsertOne(annotatedCall, {
      ...state,
      state: AsyncState.SUCCESS,
      error: null,
    });
  }),
  on(CallsActions.annotateCallFailure, (state, { error }) => ({
    ...state,
    state: AsyncState.ERROR,
    error,
  })),
  on(CallsActions.deleteCallAnnotationSuccess, (state, { callId }) => {
    return annotatedCallsAdapter.removeOne(callId, {
      ...state,
      state: AsyncState.SUCCESS,
    });
  }),
  on(CallsActions.deleteCallAnnotationFailure, (state, { error }) => ({
    ...state,
    state: AsyncState.ERROR,
    error,
  })),
  on(CallsActions.publishAnnotatedCall, (state) => ({
    ...state,
    state: AsyncState.LOADING,
  })),
  on(CallsActions.publishAnnotatedCallSuccess, (state, { annotatedCall }) => {
    return annotatedCallsAdapter.upsertOne(annotatedCall, {
      ...state,
      state: AsyncState.SUCCESS,
      error: null,
    });
  }),
  on(CallsActions.publishAnnotatedCallFailure, (state, { error }) => ({
    ...state,
    state: AsyncState.ERROR,
    error,
  })),
  on(CallsActions.saveAndPublishAnnotatedCall, (state) => ({
    ...state,
    state: AsyncState.LOADING,
  })),
  on(
    CallsActions.saveAndPublishAnnotatedCallSuccess,
    (state, { annotatedCall }) => {
      return annotatedCallsAdapter.upsertOne(annotatedCall, {
        ...state,
        state: AsyncState.SUCCESS,
        error: null,
      });
    }
  ),
  on(CallsActions.saveAndPublishAnnotatedCallFailure, (state, { error }) => ({
    ...state,
    error,
  })),
  on(CallsActions.clearAnnotatedCall, (state) => ({
    ...state,
    annotatedCall: null,
  }))
);
