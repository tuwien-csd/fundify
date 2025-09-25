import { inject } from '@angular/core';
import {
  patchState,
  signalStore,
  withHooks,
  withMethods,
  withProps,
} from '@ngrx/signals';
import {
  addEntities,
  addEntity,
  removeEntity,
  setEntity,
  withEntities,
} from '@ngrx/signals/entities';
import { CallUpdateWebModel, CallWebModel } from '../models/call.interface';
import { BackendServiceV2 } from '../../core/services/backend-service-v2.service';
import { NotificationService } from '../../shared/services/notification-service.service';
import { CALL_DETAILS_CONSTANTS } from '../calls.constants';
import { SubscriptionStatus } from '../../shared/models/enums/call-subscription.enum';

export const CallsStore = signalStore(
  { providedIn: 'root' },
  withEntities<CallWebModel>(),
  withProps(() => ({
    backendService: inject(BackendServiceV2),
    notificationService: inject(NotificationService),
  })),
  withMethods(({ backendService, notificationService, ...store }) => ({
    async addCall(
      callToCreate: CallWebModel
    ): Promise<CallWebModel | undefined> {
      try {
        const response = await backendService.client.POST('/api/call/add', {
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
        const response = await backendService.client.PUT('/api/call/update', {
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
      try {
        const response = await backendService.client.GET('/api/call');
        if (response.data) {
          patchState(store, addEntities(response.data));
        } else {
          notificationService.error(
            CALL_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
        }
      } catch (error) {
        console.error('Error fetching calls:', error);
      }
    },
    async fetchById(id: string): Promise<CallWebModel | undefined> {
      try {
        const response = await backendService.client.GET(
          '/api/call/detail/{id}',
          {
            params: {
              path: { id: id },
            },
          }
        );
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
        const response = await backendService.client.DELETE(
          '/api/call/delete/{id}',
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
          '/api/call/{id}/subscriptions',
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
