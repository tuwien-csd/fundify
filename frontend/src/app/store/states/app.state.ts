import { RouterReducerState } from '@ngrx/router-store';
import { CoreState } from '../../core/store';
import { FundersState } from '../../funders/store';
import { CallModuleState } from '../../calls/store';

export interface AppState {
  core: CoreState;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  router: RouterReducerState<any>;
  funders: FundersState;
  callsModule: CallModuleState;
}
