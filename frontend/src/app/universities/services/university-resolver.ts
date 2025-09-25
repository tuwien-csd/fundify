import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { inject } from '@angular/core';
import { UniversityWebModel } from '../models/university.interface';
import { UniversitiesStore } from '../signal/universities-store';

export const universityResolver: ResolveFn<
  UniversityWebModel | undefined
> = async (route: ActivatedRouteSnapshot) => {
  const universityStore = inject(UniversitiesStore);
  const universityId = route.paramMap.get('id')!;
  if (universityStore.entities().length === 0) {
    await universityStore.fetchAll();
  }
  return universityStore
    .entities()
    .find((university) => university.id === universityId);
};
