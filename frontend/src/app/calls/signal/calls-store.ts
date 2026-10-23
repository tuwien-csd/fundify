import { inject } from '@angular/core';
import {
  patchState,
  signalStore,
  withHooks,
  withMethods,
  withProps,
  withState,
} from '@ngrx/signals';
import {
  addEntities,
  addEntity,
  removeEntity,
  setEntity,
  withEntities,
} from '@ngrx/signals/entities';
import {
  CallUpdateWebModel,
  CallCreateWebModel,
  CallWebModel,
} from '../models/call.interface';
import { BackendServiceV2 } from '../../core/services/backend-service-v2.service';
import { NotificationService } from '../../shared/services/notification-service.service';
import { CALL_DETAILS_CONSTANTS } from '../calls.constants';
import { SubscriptionStatus } from '../../shared/models/enums/call-subscription.enum';

export const CallsStore = signalStore(
  { providedIn: 'root' },
  withEntities<CallWebModel>(),
  withState({ isLoading: false }),
  withProps(() => ({
    backendService: inject(BackendServiceV2),
    notificationService: inject(NotificationService),
  })),
  withMethods(({ backendService, notificationService, ...store }) => ({
    async addCall(
      callToCreate: CallCreateWebModel
    ): Promise<CallWebModel | undefined> {
      try {
        const response = await backendService.client.POST('/api/calls', {
          body: callToCreate,
        });
        if (response.data) {
          notificationService.success(CALL_DETAILS_CONSTANTS.CREATION_SUCCESS);
          patchState(store, addEntity(response.data));
          return response.data;
        } else {
          notificationService.error(
            CALL_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
          return undefined;
        }
      } catch (error) {
        console.error('Error creating the call:', error);
        notificationService.error(CALL_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR);
        return undefined;
      }
    },
    async updateCall(
      callToUpdate: CallUpdateWebModel
    ): Promise<CallWebModel | undefined> {
      try {
        const response = await backendService.client.PUT('/api/calls/{id}', {
          params: { path: { id: callToUpdate.id } },
          body: callToUpdate,
        });
        if (response.data) {
          notificationService.success(CALL_DETAILS_CONSTANTS.UPDATE_SUCCESS);
          patchState(store, setEntity(response.data));
          return response.data;
        } else {
          notificationService.error(
            CALL_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
          return undefined;
        }
      } catch (error) {
        console.error('Error updating the call:', error);
        notificationService.error(CALL_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR);
        return undefined;
      }
    },
    async fetchAll(): Promise<void> {
      patchState(store, { isLoading: true });
      try {
        const response = await backendService.client.GET('/api/calls');
        if (response.data) {
          patchState(store, addEntities(response.data));
        } else {
          notificationService.error(
            CALL_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
        }
      } catch (error) {
        console.error('Error fetching calls:', error);
      } finally {
        patchState(store, { isLoading: false });
      }
    },
    async fetchById(id: string): Promise<CallWebModel | undefined> {
      try {
        const response = await backendService.client.GET('/api/calls/{id}', {
          params: {
            path: { id: id },
          },
        });
        if (response.data) {
          const call: CallWebModel = response.data;
          patchState(store, addEntity(call));
          return call;
        } else {
          return undefined;
        }
      } catch (error) {
        console.error('Error fetching call:', error);
        return undefined;
      }
    },
    async deleteById(id: string): Promise<void> {
      try {
        const response = await backendService.client.DELETE('/api/calls/{id}', {
          params: {
            path: { id: id },
          },
        });
        if (response.response.status === 204) {
          patchState(store, removeEntity(id));
        } else {
          notificationService.error(
            CALL_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
        }
      } catch (error) {
        console.error('Error deleting call:', error);
      }
    },
    async changeSubscriptionById(
      id: string,
      newStatus: SubscriptionStatus
    ): Promise<void> {
      try {
        const response = await backendService.client.PATCH(
          '/api/calls/{id}/subscriptions',
          {
            params: {
              path: { id: id },
            },
            body: newStatus,
          }
        );
        if (response.data) {
          if (newStatus === 'SUBSCRIBED') {
            notificationService.success(CALL_DETAILS_CONSTANTS.CALL_SUBSCRIBED);
          } else {
            notificationService.success(
              CALL_DETAILS_CONSTANTS.CALL_UNSUBSCRIBED
            );
          }
          patchState(store, setEntity(response.data));
        } else {
          notificationService.error(
            CALL_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
          return undefined;
        }
      } catch (error) {
        console.error('Error deleting call:', error);
      }
    },
  })),
  withHooks({
    onInit: (store) => {
      store.fetchAll();
    },
  })
);
