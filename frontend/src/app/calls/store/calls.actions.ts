import { createAction, props } from '@ngrx/store';
import { Call } from '../models/call.interface';
import { AnnotatedCall } from '../models/annotated-call.interface';
import { Dictionary } from '@ngrx/entity';
import { Vocabulary } from '../models/vocabulary.interface';

/****************************************************************** */
/***** CALLS **** */
/****************************************************************** */
export const loadCalls = createAction(
  '[Calls] Load Calls',
  props<{ skipIfPresent: boolean }>()
);
export const loadCallDrafts = createAction(
  '[Calls] Load Call Drafts',
  props<{ skipIfPresent: boolean; id: string }>()
);
export const loadCallsSuccess = createAction(
  '[Calls] Load Calls Success',
  props<{ calls: Call[] }>()
);
export const loadCallDraftsSuccess = createAction(
  '[Calls] Load Call Drafts Success',
  props<{ calls: Call[] }>()
);
export const loadCallsFailure = createAction(
  '[Calls] Load Calls Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);
export const loadCallDraftsFailure = createAction(
  '[Calls] Load Call Drafts Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const clearCalls = createAction('[Call/API] Clear Calls');

/****************************************************************** */
/***** CALL **** */
/****************************************************************** */
export const loadCall = createAction(
  '[Call Edit/View Component] Load Call',
  props<{ id: string }>()
);

export const loadCallSuccess = createAction(
  '[Call Effect] Load Call Success',
  props<{ call: Call }>()
);

export const loadCallFailure = createAction(
  '[Call Effect] Load Call Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const addCall = createAction(
  '[Call Detail Component] Add Call',
  props<{ call: Call }>()
);
export const addCallSuccess = createAction(
  '[Call Effect] Add Call Success',
  props<{ call: Call }>()
);
export const addCallFailure = createAction(
  '[Call Effect] Add Call Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const upsertCall = createAction(
  '[Call Edit Component] Upsert Call',
  props<{ call: Call }>()
);
export const upsertCallSuccess = createAction(
  '[Call Edit Component] Upsert Call Success',
  props<{ call: Call }>()
);
export const upsertCallFailure = createAction(
  '[Call Edit Component] Upsert Call Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const deleteCall = createAction(
  '[Calls Component] Delete Call',
  props<{ callId: string }>()
);
export const deleteCallSuccess = createAction(
  '[Calls Component] Delete Call Success',
  props<{ callId: string }>()
);
export const deleteCallFailure = createAction(
  '[Calls Component] Delete Call Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

/****************************************************************** */
/***** ANNOTATED CALLS **** */
/****************************************************************** */
export const loadAnnotatedCalls = createAction(
  '[Call Annotation List Component] Load Annotated Calls',
  props<{ skipIfPresent: boolean }>()
);

export const loadAnnotatedCallsSuccess = createAction(
  '[Call Effect] Load Annotated Calls Success',
  props<{ annotatedCalls: AnnotatedCall[] }>()
);

export const loadAnnotatedCallsFailure = createAction(
  '[Call Effect] Load Annotated Calls Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

/****************************************************************** */
/***** ANNOTATED CALL **** */
/****************************************************************** */
export const loadAnnotatedCall = createAction(
  '[Call Detail Annotate Component] Load Annotated Call',
  props<{ callId: string; universityId: string }>()
);

export const loadAnnotatedCallSuccess = createAction(
  '[Call Effect] Load Annotated Call Success',
  props<{ annotatedCall: AnnotatedCall }>()
);

export const loadAnnotatedCallFailure = createAction(
  '[Call Effect] Load Annotated Call Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const annotateCall = createAction(
  '[Call Detail Annotate Component] Annotate Call',
  props<{ annotatedCall: AnnotatedCall }>()
);

export const annotateCallSuccess = createAction(
  '[Call Effect] Annotate Call Success',
  props<{ annotatedCall: AnnotatedCall }>()
);

export const annotateCallFailure = createAction(
  '[Call Effect] Annotate Call Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const deleteCallAnnotation = createAction(
  '[CallAnnotationList Component] Delete Call Annotation',
  props<{ annotatedCallId: string; callId: string }>()
);
export const deleteCallAnnotationSuccess = createAction(
  '[CallAnnotationList Component] Delete Call Annotation Success',
  props<{ callId: string }>()
);
export const deleteCallAnnotationFailure = createAction(
  '[CallAnnotationList Component] Delete Call Annotation Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const publishAnnotatedCall = createAction(
  '[Call Detail Annotate Component] Publish Annotation',
  props<{ annotatedCall: AnnotatedCall }>()
);

export const publishAnnotatedCallSuccess = createAction(
  '[Call Effect] Publish Annotation Success',
  props<{ annotatedCall: AnnotatedCall }>()
);

export const publishAnnotatedCallFailure = createAction(
  '[Call Effect] Publish Annotation Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const saveAndPublishAnnotatedCall = createAction(
  '[Call Detail Annotate Component] Save and Publish Annotation',
  props<{ annotatedCall: AnnotatedCall }>()
);

export const saveAndPublishAnnotatedCallSuccess = createAction(
  '[Call Detail Annotate Component] Save and Publish Annotation Success',
  props<{ annotatedCall: AnnotatedCall }>()
);

export const saveAndPublishAnnotatedCallFailure = createAction(
  '[Call Detail Annotate Component] Save and Publish Annotation Error',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const clearAnnotatedCall = createAction(
  '[Call/API] Clear Annotated Call'
);

/****************************************************************** */
/***** VOCABULARIES **** */
/****************************************************************** */

export const loadVocabularyById = createAction(
  '[Call Effect] Load Vocabulary',
  props<{ vocabularyId: string }>()
);

export const loadVocabularyByIdSuccess = createAction(
  '[Call Effect] Load Vocabulary Success',
  props<{ vocabulary: Vocabulary }>()
);

export const loadVocabularyByIdFailure = createAction(
  '[Call Effect] Load Vocabulary Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const loadVocabularies = createAction(
  '[Call Detail Annotate Component] Load Vocabularies'
);

export const loadVocabulariesSuccess = createAction(
  '[Call Effect] Load Vocabularies Success',
  props<{ vocabularies: Dictionary<Vocabulary> }>()
);

export const loadVocabulariesFailure = createAction(
  '[Call Effect] Load Vocabularies Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const addVocabularyEntry = createAction(
  '[Call Detail Annotate Component] Add Vocabulary Entry',
  props<{ vocabularyId: string; entry: string }>()
);

export const addVocabularyEntrySuccess = createAction(
  '[Call Effect] Add Vocabulary Entry Success',
  props<{ vocabularyId: string; entry: string }>()
);

export const addVocabularyEntryFailure = createAction(
  '[Call Effect] Add Vocabulary Entry Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const removeVocabularyEntry = createAction(
  '[Call Detail Annotate Component] Remove Keyword',
  props<{ vocabularyId: string; entry: string }>()
);

export const removeVocabularyEntrySuccess = createAction(
  '[Call Effect] Remove Keyword Success',
  props<{ vocabularyId: string; entry: string }>()
);

export const removeVocabularyEntryFailure = createAction(
  '[Call Effect] Remove Keyword Failure',
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  props<{ error: any }>()
);

export const clearVocabularies = createAction('[Call/API] Clear Vocabularies');
