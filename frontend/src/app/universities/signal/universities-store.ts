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
import { BackendServiceV2 } from '../../core/services/backend-service-v2.service';
import { NotificationService } from '../../shared/services/notification-service.service';
import {
  UniversityCreateWebModel,
  UniversityWebModel,
} from '../models/university.interface';
import { UNIVERSITY_DETAILS_CONSTANTS } from '../universities.constants';
import { equalsIgnoreCase } from '../../shared/utils/string-utils';

export const UniversitiesStore = signalStore(
  { providedIn: 'root' },
  withEntities<UniversityWebModel>(),
  withProps(() => ({
    backendService: inject(BackendServiceV2),
    notificationService: inject(NotificationService),
  })),
  withMethods(({ backendService, notificationService, ...store }) => ({
    async addUniversity(
      universityToCreate: UniversityCreateWebModel
    ): Promise<UniversityWebModel | undefined> {
      try {
        const response = await backendService.client.POST(
          '/api/universities',
          {
            body: universityToCreate,
          }
        );
        if (response.data) {
          notificationService.success(
            UNIVERSITY_DETAILS_CONSTANTS.CREATION_SUCCESS
          );
          patchState(store, addEntity(response.data));
          return response.data;
        } else {
          notificationService.error(
            UNIVERSITY_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
          return undefined;
        }
      } catch (error) {
        console.error('Error creating the university:', error);
        notificationService.error(
          UNIVERSITY_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
        );
        return undefined;
      }
    },
    async updateUniversity(
      UniversityToUpdate: UniversityWebModel
    ): Promise<UniversityWebModel | undefined> {
      try {
        const response = await backendService.client.PUT(
          '/api/universities/{id}',
          {
            params: { path: { id: UniversityToUpdate.id } },
            body: UniversityToUpdate,
          }
        );
        if (response.data) {
          notificationService.success(
            UNIVERSITY_DETAILS_CONSTANTS.UPDATE_SUCCESS
          );
          patchState(store, setEntity(response.data));
          return response.data;
        } else {
          notificationService.error(
            UNIVERSITY_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
          return undefined;
        }
      } catch (error) {
        console.error('Error updating the university:', error);
        notificationService.error(
          UNIVERSITY_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
        );
        return undefined;
      }
    },
    async fetchAll(): Promise<void> {
      try {
        const response = await backendService.client.GET('/api/universities');
        if (response.data) {
          patchState(store, setAllEntities(response.data));
        } else {
          notificationService.error(
            UNIVERSITY_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
        }
      } catch (error) {
        console.error('Error fetching universities:', error);
      }
    },
    async fetchById(id: string): Promise<UniversityWebModel | undefined> {
      try {
        const response = await backendService.client.GET(
          '/api/universities/{id}',
          {
            params: {
              path: { id: id },
            },
          }
        );
        if (response.data) {
          const university: UniversityWebModel = response.data;
          patchState(store, addEntity(university));
          return university;
        } else {
          return undefined;
        }
      } catch (error) {
        console.error('Error fetching university:', error);
        return undefined;
      }
    },
    async deleteById(id: string): Promise<void> {
      try {
        const response = await backendService.client.DELETE(
          '/api/universities/{id}',
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
            UNIVERSITY_DETAILS_CONSTANTS.ERRORS.GENERIC_ERROR
          );
        }
      } catch (error) {
        console.error('Error deleting university:', error);
      }
    },
    getUniversityByAcronym: (acronym?: string | null) => {
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
