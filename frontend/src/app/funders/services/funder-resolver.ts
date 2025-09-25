import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { inject } from '@angular/core';
import { FunderWebModel } from '../models/funder.interface';
import { FundersStore } from '../signal/funders-store';

export const funderResolver: ResolveFn<FunderWebModel | undefined> = async (
  route: ActivatedRouteSnapshot
) => {
  const funderStore = inject(FundersStore);
  const funderId = route.paramMap.get('id')!;
  // If the funderStore is empty, it probably has not yet been initialized. Fetch all entities before proceeding.
  if (funderStore.entities().length === 0) {
    await funderStore.fetchAll();
  }
  return funderStore.entities().find((funder) => funder.id === funderId);
};
