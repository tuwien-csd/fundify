import { createEntityAdapter, EntityState } from '@ngrx/entity';
import { Program } from '../models/program.interface';
import { AsyncState } from '../../core/models/enums/async-state.enum';

export interface ProgramsState extends EntityState<Program> {
  state: AsyncState;
  error: string | null;
}

export const adapter = createEntityAdapter<Program>();
export const initialState: ProgramsState = adapter.getInitialState({
  state: AsyncState.PENDING,
  error: null,
});
