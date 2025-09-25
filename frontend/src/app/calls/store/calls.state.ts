import { createEntityAdapter, Dictionary, EntityState } from '@ngrx/entity';
import { AsyncState } from '../../core/models/enums/async-state.enum';
import { Call } from '../models/call.interface';
import { AnnotatedCall } from '../models/annotated-call.interface';
import { Vocabulary } from '../models/vocabulary.interface';

export interface CallModuleState {
  calls: CallsState;
  annotatedCalls: AnnotatedCallsState;
}

export interface CallsState extends EntityState<Call> {
  state: AsyncState;
  error: string | null;
  vocabularies: Dictionary<Vocabulary>;
}

export interface AnnotatedCallsState extends EntityState<AnnotatedCall> {
  state: AsyncState;
  error: string | null;
}

export const callsAdapter = createEntityAdapter<Call>();

export function selectCallId(a: AnnotatedCall) {
  return a.callPreview?.id ?? '';
}
export const annotatedCallsAdapter = createEntityAdapter<AnnotatedCall>({
  selectId: selectCallId,
});

export const initialAnnotatedCallState = annotatedCallsAdapter.getInitialState({
  state: AsyncState.PENDING,
  error: null,
} as AnnotatedCallsState);

export const initialCallState: CallsState = callsAdapter.getInitialState({
  state: AsyncState.PENDING,
  error: null,
  vocabularies: {},
});
