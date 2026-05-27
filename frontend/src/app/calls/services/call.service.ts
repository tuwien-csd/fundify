import { CallAnnotation } from '../models/call-annotation.interface';
import { AnnotatedCall } from '../models/annotated-call.interface';
import { HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { BackendService } from '../../core/services/backend.service';
import { from, Observable } from 'rxjs';
import { Call } from '../models/call.interface';
import { ApiPath } from '../../shared/models/enums/api-path';
import { PublicationStatusEnum } from '../../shared/models/enums/publication-status.enum';
import { CallPreview } from '../models/call-preview.interface';
import { Vocabulary } from '../models/vocabulary.interface';
import { BackendServiceV2 } from '../../core/services/backend-service-v2.service';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class CallService {
  private backendService = inject(BackendService);
  private openApiBeClientService = inject(BackendServiceV2);

  private entityPathPart = 'calls';
  private annotatedCallPathPart = 'annotated-calls';
  private vocabularyPathPart = 'vocabularies';

  getCalls(): Observable<Call[]> {
    return from(this.openApiBeClientService.client.GET('/api/calls')).pipe(
      map(({ data, error }) => {
        if (error) throw error;

        return (data ?? []) as Call[];
      })
    );
  }

  getCallDrafts(affiliationId: string): Observable<Call[]> {
    return affiliationId
      ? this.getUserAffiliatedDrafts(affiliationId)
      : this.getAllCallDrafts();
  }

  getAllCallDrafts(): Observable<Call[]> {
    const params = new HttpParams().set(
      ApiPath.QUERY_PARAM_STATUS,
      PublicationStatusEnum.DRAFT
    );
    return this.backendService.get<Call[]>(
      `${this.entityPathPart}`,
      { params }
    );
  }

  getUserAffiliatedDrafts(affiliationId: string): Observable<Call[]> {
    const params = new HttpParams().set(
      ApiPath.QUERY_PARAM_STATUS,
      PublicationStatusEnum.DRAFT
    );
    return this.backendService.get<Call[]>(
      `${this.entityPathPart}${ApiPath.ENTITY_LIST_BY_FUNDER_APPEND_PARAMETER}${affiliationId}`,
      { params }
    );
  }

  getCall(id: string): Observable<Call> {
    return this.backendService.get<Call>(
      `${this.entityPathPart}/${id}`
    );
  }

  updateCall(call: Call): Observable<Call> {
    return this.backendService.put<Call>(
      `${this.entityPathPart}/${call.id}`,
      call
    );
  }

  addCall(call: Call): Observable<Call> {
    return this.backendService.post<Call>(
      `${this.entityPathPart}`,
      call
    );
  }

  deleteCall(callId: string): Observable<Call> {
    return this.backendService.delete<Call>(
      `${this.entityPathPart}/${callId}`
    );
  }

  removeCallFromState(callId: string, calls: Call[]): Call[] {
    return calls.filter((call) => call.id !== callId);
  }

  getAnnotatedCalls(): Observable<AnnotatedCall[]> {
    return this.backendService.get<AnnotatedCall[]>(
      `${this.annotatedCallPathPart}`
    );
  }

  getAnnotatedCallByCallIdAndUniversityId(
    callId: string,
    universityId: string
  ): Observable<AnnotatedCall> {
    const params = new HttpParams()
      .set('callId', callId)
      .set('universityId', universityId);
    return this.backendService.get<AnnotatedCall>(
      `${this.annotatedCallPathPart}${ApiPath.ENTITY_BY_ID}`,
      {
        params,
      }
    );
  }

  annotateCall(annotatedCall: AnnotatedCall): Observable<AnnotatedCallId> {
    if (annotatedCall.id) {
      return this.updateAnnotatedCall(
        annotatedCall.id,
        annotatedCall.annotation ?? ({} as CallAnnotation)
      );
    }
    return this.createAnnotatedCall(
      annotatedCall.callPreview?.id ?? '',
      annotatedCall.university?.id ?? '',
      annotatedCall.annotation ?? ({} as CallAnnotation)
    );
  }

  publishAnnotatedCall(annotatedCallId: string): Observable<AnnotatedCallId> {
    return this.backendService.put<AnnotatedCallId>(
      `${this.annotatedCallPathPart}/${annotatedCallId}/publish`,
      {}
    );
  }

  deleteCallAnnotation(annotatedCallId: string): Observable<boolean> {
    return this.backendService.delete<boolean>(
      `${this.annotatedCallPathPart}/${annotatedCallId}`
    );
  }

  getCallPreview(callId: string): Observable<CallPreview> {
    return this.backendService.get<CallPreview>(
      `${this.annotatedCallPathPart}/preview/${callId}`
    );
  }

  getVocabularies(): Observable<Vocabulary[]> {
    return this.backendService.get<Vocabulary[]>(`${this.vocabularyPathPart}`);
  }

  getVocabularyById(vocabularyId: string): Observable<Vocabulary> {
    return this.backendService.get<Vocabulary>(
      `${this.vocabularyPathPart}/${vocabularyId}`
    );
  }

  addVocabularyEntry(vocabularyId: string, entry: string): Observable<void> {
    return this.backendService.put<void>(
      `${this.vocabularyPathPart}/${vocabularyId}/entries/${entry}`,
      {}
    );
  }

  removeVocabularyEntry(
    vocabularyId: string,
    entry: string
  ): Observable<VocabularyId> {
    return this.backendService.put<VocabularyId>(
      `${this.vocabularyPathPart}/${vocabularyId}/entries/${entry}`,
      {}
    );
  }

  private createAnnotatedCall(
    callId: string,
    universityId: string,
    annotation: CallAnnotation
  ): Observable<AnnotatedCallId> {
    return this.backendService.post<AnnotatedCallId>(
      `${this.annotatedCallPathPart}/`,
      { callId, universityId, annotation }
    );
  }

  private updateAnnotatedCall(
    annotatedCallId: string,
    annotation: CallAnnotation
  ): Observable<AnnotatedCallId> {
    return this.backendService.put<AnnotatedCallId>(
      `${this.annotatedCallPathPart}/${annotatedCallId}`,
      annotation
    );
  }
}

export type VocabularyId = { value: string };
export type AnnotatedCallId = { value: string };
