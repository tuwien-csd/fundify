import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpParams } from '@angular/common/http';
import { of } from 'rxjs';
import { AnnotatedCallId, CallService } from './call.service';
import { BackendService } from '../../core/services/backend.service';
import { PublicationStatusEnum } from '../../shared/models/enums/publication-status.enum';
import { ApiPath } from '../../shared/models/enums/api-path';
import { Call } from '../models/call.interface';
import { AnnotatedCall } from '../models/annotated-call.interface';
import { CallPreview } from '../models/call-preview.interface';
import { BackendServiceV2 } from '../../core/services/backend-service-v2.service';

describe('CallService', () => {
  let service: CallService;
  let backendServiceSpy: jasmine.SpyObj<BackendService>;

  const mockBackendService = {
    client: {
      GET: jasmine.createSpy('GET').and.returnValue(
        Promise.resolve({
          data: [{ id: '1' }, { id: '2' }] as Call[],
          error: undefined,
        })
      ),
    },
  };

  beforeEach(() => {
    const spy = jasmine.createSpyObj('BackendService', [
      'get',
      'post',
      'put',
      'delete',
    ]);

    TestBed.configureTestingModule({
      providers: [
        CallService,
        { provide: BackendService, useValue: spy },
        {
          provide: BackendServiceV2,
          useValue: mockBackendService,
        },
      ],
    });

    service = TestBed.inject(CallService);
    backendServiceSpy = TestBed.inject(
      BackendService
    ) as jasmine.SpyObj<BackendService>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCalls', () => {
    it('should return an Observable of Call[]', (done) => {
      service.getCalls().subscribe((calls) => {
        expect(calls).toEqual([{ id: '1' }, { id: '2' }]);
        expect(mockBackendService.client.GET).toHaveBeenCalledWith('/api/calls');
        done();
      });
    });
  });

  describe('getCallDrafts', () => {
    it('should call getUserAffiliatedDrafts when affiliationId is provided', fakeAsync(() => {
      const affiliationId = 'aff123';
      const mockCalls: Call[] = [
        { id: '1', status: PublicationStatusEnum.DRAFT },
      ];
      backendServiceSpy.get.and.returnValue(of(mockCalls));

      let result: Call[] | undefined;
      service
        .getCallDrafts(affiliationId)
        .subscribe((calls) => (result = calls));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `calls${ApiPath.ENTITY_LIST_BY_FUNDER_APPEND_PARAMETER}${affiliationId}`,
        {
          params: new HttpParams().set(
            ApiPath.QUERY_PARAM_STATUS,
            PublicationStatusEnum.DRAFT
          ),
        }
      );
      expect(result).toEqual(mockCalls);
    }));

    it('should call getAllCallDrafts when affiliationId is not provided', fakeAsync(() => {
      const mockCalls: Call[] = [
        { id: '1', acronym: 'Draft', status: PublicationStatusEnum.DRAFT },
      ];
      backendServiceSpy.get.and.returnValue(of(mockCalls));

      let result: Call[] | undefined;
      service.getCallDrafts('').subscribe((calls) => (result = calls));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `calls`,
        {
          params: new HttpParams().set(
            ApiPath.QUERY_PARAM_STATUS,
            PublicationStatusEnum.DRAFT
          ),
        }
      );
      expect(result).toEqual(mockCalls);
    }));
  });

  describe('getCall', () => {
    it('should return an Observable of Call', fakeAsync(() => {
      const mockCall: Call = { id: '1' };
      backendServiceSpy.get.and.returnValue(of(mockCall));

      let result: Call | undefined;
      service.getCall('1').subscribe((call) => (result = call));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `calls/1`
      );
      expect(result).toEqual(mockCall);
    }));
  });

  describe('updateCall', () => {
    it('should update a call and return the updated Call', fakeAsync(() => {
      const mockCall: Call = { id: '1' };
      backendServiceSpy.put.and.returnValue(of(mockCall));

      let result: Call | undefined;
      service.updateCall(mockCall).subscribe((call) => (result = call));
      tick();

      expect(backendServiceSpy.put).toHaveBeenCalledWith(
        `calls/1`,
        mockCall
      );
      expect(result).toEqual(mockCall);
    }));
  });

  describe('addCall', () => {
    it('should add a new call and return the added Call', fakeAsync(() => {
      const mockCall: Call = { id: '1' };
      backendServiceSpy.post.and.returnValue(of(mockCall));

      let result: Call | undefined;
      service.addCall(mockCall).subscribe((call) => (result = call));
      tick();

      expect(backendServiceSpy.post).toHaveBeenCalledWith(
        `calls`,
        mockCall
      );
      expect(result).toEqual(mockCall);
    }));
  });

  describe('deleteCall', () => {
    it('should delete a call and return the deleted Call', fakeAsync(() => {
      const callId = 'call1';
      const mockCall: Call = { id: callId };
      backendServiceSpy.delete.and.returnValue(of(mockCall));

      let result: Call | undefined;
      service.deleteCall(callId).subscribe((call) => (result = call));
      tick();

      expect(backendServiceSpy.delete).toHaveBeenCalledWith(
        `calls/${callId}`
      );
      expect(result).toEqual(mockCall);
    }));
  });

  describe('removeCallFromState', () => {
    it('should remove a call from the array of calls', () => {
      const callId = 'call1';
      const calls: Call[] = [{ id: 'call1' }, { id: 'call2' }, { id: 'call3' }];

      const result = service.removeCallFromState(callId, calls);

      expect(result.length).toBe(2);
      expect(result.find((call) => call.id === callId)).toBeUndefined();
    });
  });

  describe('getAnnotatedCallsByUniversityId', () => {
    it('should return an Observable of AnnotatedCall[]', fakeAsync(() => {
      const mockAnnotatedCalls: AnnotatedCall[] = [
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        { id: '1', callPreview: { id: 'call1' } as any },
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        { id: '2', callPreview: { id: 'call2' } as any },
      ];
      backendServiceSpy.get.and.returnValue(of(mockAnnotatedCalls));

      let result: AnnotatedCall[] | undefined;
      service.getAnnotatedCalls().subscribe((calls) => (result = calls));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(`annotated-calls`);
      expect(result).toEqual(mockAnnotatedCalls);
    }));
  });

  describe('getAnnotatedCallByCallIdAndUniversityId', () => {
    it('should return an Observable of AnnotatedCall', fakeAsync(() => {
      const callId = 'call1';
      const universityId = 'uni1';
      const mockAnnotatedCall: AnnotatedCall = {
        id: '1',
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        callPreview: { id: callId } as any,
      };
      backendServiceSpy.get.and.returnValue(of(mockAnnotatedCall));

      let result: AnnotatedCall | undefined;
      service
        .getAnnotatedCallByCallIdAndUniversityId(callId, universityId)
        .subscribe((call) => (result = call));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        'annotated-calls/by-id',
        {
          params: new HttpParams()
            .set('callId', callId)
            .set('universityId', universityId),
        }
      );
      expect(result).toEqual(mockAnnotatedCall);
    }));
  });

  describe('annotateCall', () => {
    it('should update an existing annotated call', fakeAsync(() => {
      const mockAnnotatedCall: AnnotatedCall = {
        id: 'anno1',
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        callPreview: { id: 'call1' } as any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        annotation: {} as any,
      };
      const mockAnnotatedCallId: AnnotatedCallId = { value: 'anno1' };
      backendServiceSpy.put.and.returnValue(of(mockAnnotatedCallId));

      let result: AnnotatedCallId | undefined;
      service.annotateCall(mockAnnotatedCall).subscribe((id) => (result = id));
      tick();

      expect(backendServiceSpy.put).toHaveBeenCalledWith(
        'annotated-calls/anno1',
        mockAnnotatedCall.annotation
      );
      expect(result).toEqual(mockAnnotatedCallId);
    }));

    it('should create a new annotated call', fakeAsync(() => {
      const mockAnnotatedCall: AnnotatedCall = {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        callPreview: { id: 'call1' } as any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        university: { id: 'uni1' } as any,
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        annotation: {} as any,
      };
      const mockAnnotatedCallId: AnnotatedCallId = { value: 'anno1' };
      backendServiceSpy.post.and.returnValue(of(mockAnnotatedCallId));

      let result: AnnotatedCallId | undefined;
      service.annotateCall(mockAnnotatedCall).subscribe((id) => (result = id));
      tick();

      expect(backendServiceSpy.post).toHaveBeenCalledWith('annotated-calls/', {
        callId: 'call1',
        universityId: 'uni1',
        annotation: {},
      });
      expect(result).toEqual(mockAnnotatedCallId);
    }));
  });

  describe('publishAnnotatedCall', () => {
    it('should publish an annotated call and return AnnotatedCallId', fakeAsync(() => {
      const annotatedCallId = 'anno1';
      const mockAnnotatedCallId: AnnotatedCallId = { value: annotatedCallId };
      backendServiceSpy.put.and.returnValue(of(mockAnnotatedCallId));

      let result: AnnotatedCallId | undefined;
      service
        .publishAnnotatedCall(annotatedCallId)
        .subscribe((id) => (result = id));
      tick();

      expect(backendServiceSpy.put).toHaveBeenCalledWith(
        `annotated-calls/${annotatedCallId}/publish`,
        {}
      );
      expect(result).toEqual(mockAnnotatedCallId);
    }));
  });

  describe('deleteCallAnnotation', () => {
    it('should delete a call annotation and return boolean', fakeAsync(() => {
      const annotatedCallId = 'anno1';
      backendServiceSpy.delete.and.returnValue(of(true));

      let result: boolean | undefined;
      service
        .deleteCallAnnotation(annotatedCallId)
        .subscribe((success) => (result = success));
      tick();

      expect(backendServiceSpy.delete).toHaveBeenCalledWith(
        `annotated-calls/${annotatedCallId}`
      );
      expect(result).toBe(true);
    }));
  });

  describe('getCallPreview', () => {
    it('should return an Observable of CallPreview', fakeAsync(() => {
      const callId = 'call1';
      // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
      const mockCallPreview: CallPreview = { id: callId } as any;
      backendServiceSpy.get.and.returnValue(of(mockCallPreview));

      let result: CallPreview | undefined;
      service.getCallPreview(callId).subscribe((preview) => (result = preview));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `annotated-calls/preview/${callId}`
      );
      expect(result).toEqual(mockCallPreview);
    }));
  });
});
