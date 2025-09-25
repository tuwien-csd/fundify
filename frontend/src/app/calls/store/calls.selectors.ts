import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  annotatedCallsAdapter,
  CallModuleState,
  callsAdapter,
} from './calls.state';
import { VocabularyTypeEnum } from '../models/vocabulary-type.enum';

export const selectCallModuleState =
  createFeatureSelector<CallModuleState>('callsModule');

// Calls

export const selectCallsState = createSelector(
  selectCallModuleState,
  (state) => state.calls
);

export const selectAllCalls = createSelector(
  selectCallsState,
  callsAdapter.getSelectors().selectAll
);
export const selectAllCallEntities = createSelector(
  selectCallsState,
  callsAdapter.getSelectors().selectEntities
);

export const callEntityExists = (id: string) =>
  createSelector(selectAllCallEntities, (entities) => !!entities[id]);

export const selectCallById = (id: string) =>
  createSelector(selectAllCallEntities, (entities) => entities[id]);

// Annotated Call

export const selectAnnotatedCallsState = createSelector(
  selectCallModuleState,
  (state) => state.annotatedCalls
);

export const selectAllAnnotatedCalls = createSelector(
  selectAnnotatedCallsState,
  annotatedCallsAdapter.getSelectors().selectAll
);

export const selectAllAnnotatedCallEntities = createSelector(
  selectAnnotatedCallsState,
  annotatedCallsAdapter.getSelectors().selectEntities
);

export const annotatedCallEntityExists = (callId: string) =>
  createSelector(
    selectAllAnnotatedCallEntities,
    (entities) => !!entities[callId]
  );

export const selectAnnotatedCallByCallId = (callId: string) =>
  createSelector(
    selectAllAnnotatedCallEntities,
    (entities) => entities[callId]
  );

// Vocabularies
export const selectVocabularies = createSelector(
  selectCallsState,
  (state) => state.vocabularies
);

export const selectVocabulary = (type: VocabularyTypeEnum) =>
  createSelector(selectVocabularies, (vocabularies) => {
    return vocabularies[type];
  });
