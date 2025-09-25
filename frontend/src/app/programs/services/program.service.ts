import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Program } from '../models/program.interface';
import { HttpParams } from '@angular/common/http';
import { BackendService } from '../../core/services/backend.service';
import { ApiPath } from '../../shared/models/enums/api-path';
import { PublicationStatusEnum } from '../../shared/models/enums/publication-status.enum';

@Injectable({
  providedIn: 'root',
})
export class ProgramService {
  private backendService = inject(BackendService);

  private entityPathPart = 'program';

  getPrograms(): Observable<Program[]> {
    return this.backendService.get<Program[]>(
      `${this.entityPathPart}${ApiPath.ENTITY_LIST}`
    );
  }

  getProgramDrafts(funderId: string): Observable<Program[]> {
    return funderId
      ? this.getProgramDraftsByFunder(funderId)
      : this.getAllProgramDrafts();
  }

  getAllProgramDrafts(): Observable<Program[]> {
    const params = new HttpParams().set(
      ApiPath.QUERY_PARAM_STATUS,
      PublicationStatusEnum.DRAFT
    );
    return this.backendService.get<Program[]>(
      `${this.entityPathPart}${ApiPath.ENTITY_LIST}`,
      { params }
    );
  }

  getProgramDraftsByFunder(funderId: string): Observable<Program[]> {
    const params = new HttpParams().set(
      ApiPath.QUERY_PARAM_STATUS,
      PublicationStatusEnum.DRAFT
    );
    return this.backendService.get<Program[]>(
      `${this.entityPathPart}${ApiPath.ENTITY_LIST_BY_FUNDER_APPEND_PARAMETER}${funderId}`,
      { params }
    );
  }

  getProgram(id: string): Observable<Program> {
    return this.backendService.get<Program>(
      `${this.entityPathPart}${ApiPath.ENTITY_BY_ID_APPEND_PARAMETER}${id}`
    );
  }

  updateProgram(program: Program): Observable<Program> {
    return this.backendService.put<Program>(
      `${this.entityPathPart}${ApiPath.UPDATE_ENTITY}`,
      program
    );
  }

  addProgram(program: Program): Observable<Program> {
    return this.backendService.post<Program>(
      `${this.entityPathPart}${ApiPath.ADD_ENTITY}`,
      program
    );
  }

  deleteProgram(programId: string): Observable<Program> {
    return this.backendService.delete<Program>(
      `${this.entityPathPart}${ApiPath.DELETE_ENTITY}${programId}`
    );
  }
}
