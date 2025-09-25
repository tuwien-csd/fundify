import { StandardizedSubject } from '../../../shared/models/interfaces/standardizedSubject.interface';

export interface OefosState {
  subjects: StandardizedSubject[];
  loading: boolean;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  error: any;
}

export const initialState: OefosState = {
  subjects: [],
  loading: false,
  error: null,
};
