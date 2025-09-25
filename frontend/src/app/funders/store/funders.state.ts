import { FunderWebModel } from '../models/funder.interface';
import { createEntityAdapter, EntityState } from '@ngrx/entity';
import { AsyncState } from '../../core/models/enums/async-state.enum';

export interface FundersState extends EntityState<FunderWebModel> {
  state: AsyncState;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  error: any;
}

export const adapter = createEntityAdapter<FunderWebModel>();
export const initialState: FundersState = adapter.getInitialState({
  state: AsyncState.PENDING,
  error: null,
});
