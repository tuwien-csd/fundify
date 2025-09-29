import { inject } from '@angular/core';
import {
  patchState,
  signalStore,
  withHooks,
  withMethods,
  withProps,
} from '@ngrx/signals';
import {
  addEntity,
  removeEntity,
  setAllEntities,
  setEntity,
  withEntities,
} from '@ngrx/signals/entities';
import {
  FunderCreateWebModel,
  FunderWebModel,
} from '../models/funder.interface';
import { BackendServiceV2 } from '../../core/services/backend-service-v2.service';
import { NotificationService } from '../../shared/services/notification-service.service';
import { FUNDER_DETAILS_CONSTANTS } from '../funders.constants';
import { equalsIgnoreCase } from '../../shared/utils/string-utils';

export const FundersStore = signalStore(
  { providedIn: 'root' },
  withEntities<FunderWebModel>(),
  withProps(() => ({
    backendService: inject(BackendServiceV2),
    notificationService: inject(NotificationService),
  })),
  withMethods(({ backendService, notificationService, ...store }) => ({
    async addFunder(
      funderToCreate: FunderCreateWebModel
    ): Promise<FunderWebModel | undefined> {
      try {
        const response = await backendService.client.POST('/api/funder/add', {
          body: funderToCreate,
        });
        if (response.data) {
          notificationService.success(
            FUNDER_DETAILS_CONSTANTS.CREATION_SUCCESS
          );
          patchState(store, addEntity(response.data));
          return response.data;
        } else {
          notificationService.error(
            FUNDER_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
          return undefined;
        }
      } catch (error) {
        console.error('Error creating the funder:', error);
        notificationService.error(
          FUNDER_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
        );
        return undefined;
      }
    },
    async updateFunder(
      funderToUpdate: FunderWebModel
    ): Promise<FunderWebModel | undefined> {
      try {
        const response = await backendService.client.PUT('/api/funder/update', {
          body: funderToUpdate,
        });
        if (response.data) {
          notificationService.success(FUNDER_DETAILS_CONSTANTS.UPDATE_SUCCESS);
          patchState(store, setEntity(response.data));
          return response.data;
        } else {
          notificationService.error(
            FUNDER_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
          return undefined;
        }
      } catch (error) {
        console.error('Error updating the funder:', error);
        notificationService.error(
          FUNDER_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
        );
        return undefined;
      }
    },
    async fetchAll(): Promise<void> {
      try {
        const response = await backendService.client.GET(
          '/api/funder/detail/list'
        );
        if (response.data) {
          patchState(store, setAllEntities(response.data));
        } else {
          notificationService.error(
            FUNDER_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
        }
      } catch (error) {
        console.error('Error fetching funders:', error);
      }
    },
    async fetchById(id: string): Promise<FunderWebModel | undefined> {
      try {
        const response = await backendService.client.GET(
          '/api/funder/detail/{id}',
          {
            params: {
              path: { id: id },
            },
          }
        );
        if (response.data) {
          const funder: FunderWebModel = response.data;
          patchState(store, addEntity(funder));
          return funder;
        } else {
          return undefined;
        }
      } catch (error) {
        console.error('Error fetching funder:', error);
        return undefined;
      }
    },
    async deleteById(id: string): Promise<void> {
      try {
        const response = await backendService.client.DELETE(
          '/api/funder/delete/{id}',
          {
            params: {
              path: { id: id },
            },
          }
        );
        if (response.response.status === 204) {
          patchState(store, removeEntity(id));
        } else {
          notificationService.error(
            FUNDER_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
        }
      } catch (error) {
        console.error('Error deleting funder:', error);
      }
    },
    externallyManagedFunders: () => {
      return store.entities().filter((it) => !!it.externallyAdministered);
    },
    getFunderByAcronym: (acronym?: string | null) => {
      return store
        .entities()
        .find((it) => equalsIgnoreCase(it.acronym, acronym));
    },
  })),
  withHooks({
    onInit: (store) => {
      //Fetch all funders when the store initializes
      store.fetchAll();
    },
  })
);
