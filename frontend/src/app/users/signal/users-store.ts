import { inject } from '@angular/core';
import { patchState, signalStore, withMethods, withProps, withState } from '@ngrx/signals';
import { BackendServiceV2 } from '../../core/services/backend-service-v2.service';
import { NotificationService } from '../../shared/services/notification-service.service';
import { USER_PERMISSIONS_CONSTANTS } from '../users.constants';
import { components } from '../../../generated/refop-be';

type KeycloakUser = components['schemas']['KeycloakUser'];
type UserPermissionHolder = components['schemas']['UserPermissionHolder'];
type RegistrationRequest = components['schemas']['RegistrationRequest'];

export const UsersStore = signalStore(
  { providedIn: 'root' },
  withState({
    keycloakUsers: [] as KeycloakUser[],
    userPermissions: [] as UserPermissionHolder[],
    registrationRequests: [] as RegistrationRequest[],
  }),
  withProps(() => ({
    backendService: inject(BackendServiceV2),
    notificationService: inject(NotificationService),
  })),
  withMethods(({ backendService, notificationService, ...store }) => ({
    async loadKeycloakUsers(): Promise<void> {
      try {
        const response = await backendService.client.GET('/api/users');
        if (response.data) {
          patchState(store, { keycloakUsers: response.data });
        }
      } catch (error) {
        console.error('Error loading Keycloak users:', error);
      }
    },
    async loadUserPermissions(): Promise<void> {
      try {
        const response = await backendService.client.GET(
          '/api/users/permissions'
        );
        if (response.data) {
          patchState(store, { userPermissions: response.data });
        }
      } catch (error) {
        console.error('Error loading user permissions:', error);
      }
    },
    async loadRegistrationRequests(): Promise<void> {
      try {
        const response = await backendService.client.GET(
          '/api/registration-requests'
        );
        if (response.data) {
          patchState(store, { registrationRequests: response.data });
        }
      } catch (error) {
        console.error('Error loading registration requests:', error);
      }
    },
    async deleteRegistrationRequest(id: string): Promise<boolean> {
      try {
        const response = await backendService.client.DELETE(
          '/api/registration-requests/{id}',
          {
            params: { path: { id } },
          }
        );
        if (response.response.status === 204) {
          patchState(store, {
            registrationRequests: store
              .registrationRequests()
              .filter((request) => request.id !== id),
          });
          return true;
        } else {
          notificationService.error(
            USER_PERMISSIONS_CONSTANTS.ERRORS.REGISTRATION_DELETE_ERROR
          );
          return false;
        }
      } catch (error) {
        console.error('Error deleting registration request:', error);
        notificationService.error(
          USER_PERMISSIONS_CONSTANTS.ERRORS.REGISTRATION_DELETE_ERROR
        );
        return false;
      }
    },
    async updateUserPermissions(
      userId: string,
      roles: string[],
      affiliationId: string
    ): Promise<boolean> {
      try {
        const response = await backendService.client.PUT(
          '/api/users/{id}/permissions',
          {
            params: { path: { id: userId } },
            body: { roles: roles as never, affiliationId },
          }
        );
        if (response.data) {
          notificationService.success(USER_PERMISSIONS_CONSTANTS.UPDATE_SUCCESS);
          return true;
        } else {
          notificationService.error(
            USER_PERMISSIONS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
          return false;
        }
      } catch (error) {
        console.error('Error updating user permissions:', error);
        notificationService.error(
          USER_PERMISSIONS_CONSTANTS.ERRORS.GENERIC_ERROR
        );
        return false;
      }
    },
    async createUserAndUpdatePermissions(
      email: string,
      firstName: string,
      lastName: string,
      roles: string[],
      affiliationId: string
    ): Promise<boolean> {
      try {
        const response = await backendService.client.POST('/api/users', {
          body: { email, firstName, lastName, roles: roles as never, affiliationId },
        });
        if (response.data) {
          notificationService.success(USER_PERMISSIONS_CONSTANTS.UPDATE_SUCCESS);
          return true;
        } else {
          notificationService.error(
            USER_PERMISSIONS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
          return false;
        }
      } catch (error) {
        console.error('Error creating user / updating permissions:', error);
        notificationService.error(
          USER_PERMISSIONS_CONSTANTS.ERRORS.GENERIC_ERROR
        );
        return false;
      }
    },
    async deleteUserPermissions(userId: string): Promise<boolean> {
      try {
        const response = await backendService.client.DELETE(
          '/api/users/{id}/permissions',
          {
            params: { path: { id: userId } },
          }
        );
        if (response.response.status === 204) {
          patchState(store, {
            userPermissions: store
              .userPermissions()
              .filter((permission) => permission.userId !== userId),
          });
          notificationService.success(USER_PERMISSIONS_CONSTANTS.DELETE_SUCCESS);
          return true;
        } else {
          notificationService.error(
            USER_PERMISSIONS_CONSTANTS.ERRORS.DELETE_ERROR
          );
          return false;
        }
      } catch (error) {
        console.error('Error deleting user permissions:', error);
        notificationService.error(
          USER_PERMISSIONS_CONSTANTS.ERRORS.DELETE_ERROR
        );
        return false;
      }
    },
  }))
);
