import { inject } from '@angular/core';
import {
  patchState,
  signalStore,
  withHooks,
  withMethods,
  withProps,
} from '@ngrx/signals';
import {
  removeEntity,
  setAllEntities,
  withEntities,
} from '@ngrx/signals/entities';
import { BackendServiceV2 } from '../../core/services/backend-service-v2.service';
import { NotificationService } from '../../shared/services/notification-service.service';
import { ProgramWebModel } from '../models/program.interface';
import { PROGRAM_DETIALS_CONSTANTS } from '../programs.constants';

export const ProgramStore = signalStore(
  { providedIn: 'root' },
  withEntities<ProgramWebModel>(),
  withProps(() => ({
    backendService: inject(BackendServiceV2),
    notificationService: inject(NotificationService),
  })),
  withMethods(({ backendService, notificationService, ...store }) => ({
    async fetchAll(): Promise<void> {
      try {
        const response = await backendService.client.GET(
          '/api/program/detail/list'
        );
        if (response.data) {
          patchState(store, setAllEntities(response.data));
        } else {
          notificationService.error(
            PROGRAM_DETIALS_CONSTANTS.ERRORS.FETCH_ALL_ERROR
          );
        }
      } catch (error) {
        console.error('Error fetching funders:', error);
      }
    },
    async delete(programId: string): Promise<void> {
      try {
        const { response } = await backendService.client.DELETE(
          '/api/program/delete/{id}',
          {
            params: {
              path: {
                id: programId,
              },
            },
          }
        );
        if (response.ok) {
          patchState(store, removeEntity(programId));
          notificationService.success(
            PROGRAM_DETIALS_CONSTANTS.MESSAGES.DELETE.SUCCESS
          );
        } else {
          notificationService.error(
            PROGRAM_DETIALS_CONSTANTS.MESSAGES.DELETE.ERROR
          );
        }
      } catch (error) {
        console.error(
          `Unexpected error while trying to delete program with id '${programId}': ${error}`
        );
      }
      return;
    },
  })),
  withHooks({
    onInit: (store) => {
      //Fetch all programs when the store initializes
      store.fetchAll();
    },
  })
);
