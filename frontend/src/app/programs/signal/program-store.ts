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
  updateEntity,
  withEntities,
} from '@ngrx/signals/entities';
import { BackendServiceV2 } from '../../core/services/backend-service-v2.service';
import { NotificationService } from '../../shared/services/notification-service.service';
import { ProgramWebModel } from '../models/program.interface';
import { PROGRAM_DETAILS_CONSTANTS } from '../programs.constants';

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
            PROGRAM_DETAILS_CONSTANTS.ERRORS.FETCH_ALL_ERROR
          );
        }
      } catch (error) {
        console.error('Error fetching funders:', error);
      }
    },
    async create(program: ProgramWebModel) {
      //TODO: Make paths actually RESTful
      const { data, error } = await backendService.client.POST(
        '/api/program/add',
        {
          body: program,
        }
      );
      if (data) {
        patchState(store, addEntity(data));
        notificationService.success(
          PROGRAM_DETAILS_CONSTANTS.MESSAGES.CREATE.SUCCESS
        );
        return data;
      } else {
        console.error('Unexpected error while creating program: ', error);
        notificationService.error(
          PROGRAM_DETAILS_CONSTANTS.MESSAGES.CREATE.ERROR
        );
        return null;
      }
    },
    async update(program: ProgramWebModel) {
      //TODO: Make paths actually RESTful
      const { data, error } = await backendService.client.PUT(
        '/api/program/update',
        {
          body: program,
        }
      );
      if (data) {
        patchState(store, updateEntity({ id: data.id, changes: data }));
        notificationService.success(
          PROGRAM_DETAILS_CONSTANTS.MESSAGES.UPDATE.SUCCESS
        );
        return data;
      } else {
        console.error('Unexpected error while creating program: ', error);
        notificationService.error(
          PROGRAM_DETAILS_CONSTANTS.MESSAGES.UPDATE.ERROR
        );
        return null;
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
            PROGRAM_DETAILS_CONSTANTS.MESSAGES.DELETE.SUCCESS
          );
        } else {
          notificationService.error(
            PROGRAM_DETAILS_CONSTANTS.MESSAGES.DELETE.ERROR
          );
        }
      } catch (error) {
        console.error(
          `Unexpected error while trying to delete program with id '${programId}': ${error}`
        );
      }
    },
    findById(programId: string) {
      return store.entities().find((it) => it.id === programId);
    },
  })),
  withHooks({
    onInit: (store) => {
      //Fetch all programs when the store initializes
      store.fetchAll();
    },
  })
);
