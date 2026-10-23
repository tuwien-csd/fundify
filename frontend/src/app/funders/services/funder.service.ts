import { inject, Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BackendService } from '../../core/services/backend.service';
import { FunderWebModel } from '../models/funder.interface';
import { FundingEntityRef } from '../../shared/models/interfaces/funding-entity-ref.interface';
import { ApiPath } from '../../shared/models/enums/api-path';

@Injectable({
  providedIn: 'root',
})
export class FunderService {
  private backendService = inject(BackendService);

  private entityPathPart = 'funders';

  getFunders(): Observable<FunderWebModel[]> {
    return this.backendService.get<FunderWebModel[]>(
      `${this.entityPathPart}`
    );
  }

  getFunderReferences(): Observable<FundingEntityRef[]> {
    return this.backendService.get<FundingEntityRef[]>(
      `${this.entityPathPart}${ApiPath.REFERENCE_LIST}`
    );
  }

  getFunder(id: string): Observable<FunderWebModel> {
    return this.backendService.get<FunderWebModel>(
      `${this.entityPathPart}/${id}`
    );
  }

  addFunder(funder: FunderWebModel): Observable<FunderWebModel> {
    return this.backendService.post<FunderWebModel>(
      `${this.entityPathPart}`,
      funder
    );
  }

  updateFunder(funder: FunderWebModel): Observable<FunderWebModel> {
    return this.backendService.put<FunderWebModel>(
      `${this.entityPathPart}/${funder.id}`,
      funder
    );
  }

  deleteFunder(funderId: string): Observable<FunderWebModel> {
    return this.backendService.delete<FunderWebModel>(
      `${this.entityPathPart}/${funderId}`
    );
  }

  search(searchText: string): Observable<FundingEntityRef[]> {
    const params = new HttpParams().set(
      ApiPath.QUERY_PARAM_SEARCH_TERM,
      searchText
    );
    return this.backendService.get<FundingEntityRef[]>(
      `${this.entityPathPart}${ApiPath.REFERENCE_LIST_SEARCH_ADD_QUERY_PARAM}`,
      { params }
    );
  }
}
