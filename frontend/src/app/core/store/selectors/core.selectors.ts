import { CoreState } from '../states/core.state';
import { createFeatureSelector } from '@ngrx/store';

export const selectCoreState = createFeatureSelector<CoreState>('core');
