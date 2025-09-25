import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of } from 'rxjs';
import { SubjectService } from './subject.service';
import { BackendService } from './backend.service';
import { StandardizedSubject } from '../../shared/models/interfaces/standardizedSubject.interface';

describe('SubjectService', () => {
  let service: SubjectService;
  let backendServiceSpy: jasmine.SpyObj<BackendService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('BackendService', ['get']);

    TestBed.configureTestingModule({
      providers: [SubjectService, { provide: BackendService, useValue: spy }],
    });

    service = TestBed.inject(SubjectService);
    backendServiceSpy = TestBed.inject(
      BackendService
    ) as jasmine.SpyObj<BackendService>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('fetchSubjectsFromBackend', () => {
    it('should fetch, sort, and store subjects', fakeAsync(() => {
      const mockSubjects: StandardizedSubject[] = [
        { level: 1, code: '2', title: 'Subject B' },
        { level: 1, code: '1', title: 'Subject A' },
        { level: 1, code: '3', title: 'Subject C' },
      ];
      backendServiceSpy.get.and.returnValue(of(mockSubjects));

      let result: StandardizedSubject[] = [];
      service
        .fetchSubjectsFromBackend()
        .subscribe((subjects) => (result = subjects));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith('oefos');
      expect(result).toBeDefined();
      expect(result?.length).toBe(3);
      expect(result?.[0].code).toBe('1');
      expect(result?.[1].code).toBe('2');
      expect(result?.[2].code).toBe('3');

      // Check if the subjects are stored internally
      expect(service.getSubjects()).toEqual(result);
    }));

    it('should handle empty response', fakeAsync(() => {
      backendServiceSpy.get.and.returnValue(of([]));

      let result: StandardizedSubject[] | undefined;
      service
        .fetchSubjectsFromBackend()
        .subscribe((subjects) => (result = subjects));
      tick();

      expect(result).toEqual([]);
      expect(service.getSubjects()).toEqual([]);
    }));
  });

  describe('getSubjects', () => {
    it('should return stored subjects', fakeAsync(() => {
      const mockSubjects: StandardizedSubject[] = [
        { level: 1, code: '1', title: 'Subject A' },
        { level: 1, code: '2', title: 'Subject B' },
      ];
      backendServiceSpy.get.and.returnValue(of(mockSubjects));

      service.fetchSubjectsFromBackend().subscribe();
      tick();

      const result = service.getSubjects();
      expect(result).toEqual(mockSubjects);
    }));

    it('should return empty array if no subjects fetched', () => {
      const result = service.getSubjects();
      expect(result).toEqual([]);
    });
  });

  describe('compareCodes', () => {
    it('should correctly compare codes', () => {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
      const compareCodes = (service as any).compareCodes.bind(service);

      expect(compareCodes('1', '2')).toBeLessThan(0);
      expect(compareCodes('2', '1')).toBeGreaterThan(0);
      expect(compareCodes('1', '1')).toBe(0);
      expect(compareCodes('170', '2')).toBeLessThan(0);
      expect(compareCodes('11', '2')).toBeLessThan(0);
      expect(compareCodes('2', '13')).toBeGreaterThan(0);
    });
  });
});
