import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { inject } from '@angular/core';
import { ProgramStore } from '../signal/program-store';
import { ProgramWebModel } from '../models/program.interface';

export const programResolver: ResolveFn<ProgramWebModel | undefined> = async (
  route: ActivatedRouteSnapshot
) => {
  const programStore = inject(ProgramStore);
  const programId = route.paramMap.get('id')!;
  // If the store is empty, it probably has not yet been initialized. Fetch all entities before proceeding.
  if (programStore.entities().length === 0) {
    await programStore.fetchAll();
  }
  return programStore.findById(programId);
};
