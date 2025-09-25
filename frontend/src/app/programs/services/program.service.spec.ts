import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HttpParams } from '@angular/common/http';
import { of } from 'rxjs';
import { ProgramService } from './program.service';
import { BackendService } from '../../core/services/backend.service';
import { Program } from '../models/program.interface';
import { PublicationStatusEnum } from '../../shared/models/enums/publication-status.enum';
import { ApiPath } from '../../shared/models/enums/api-path';

describe('ProgramService', () => {
  let service: ProgramService;
  let backendServiceSpy: jasmine.SpyObj<BackendService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('BackendService', [
      'get',
      'post',
      'put',
      'delete',
    ]);

    TestBed.configureTestingModule({
      providers: [ProgramService, { provide: BackendService, useValue: spy }],
    });

    service = TestBed.inject(ProgramService);
    backendServiceSpy = TestBed.inject(
      BackendService
    ) as jasmine.SpyObj<BackendService>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getPrograms', () => {
    it('should return an Observable of Program[]', fakeAsync(() => {
      const mockPrograms: Program[] = [{ id: '1' }, { id: '2' }];
      backendServiceSpy.get.and.returnValue(of(mockPrograms));

      let result: Program[] | undefined;
      service.getPrograms().subscribe((programs) => (result = programs));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `program${ApiPath.ENTITY_LIST}`
      );
      expect(result).toEqual(mockPrograms);
    }));
  });

  describe('getProgramDrafts', () => {
    it('should call getProgramDraftsByFunder when funderId is provided', fakeAsync(() => {
      const funderId = 'funder123';
      const mockPrograms: Program[] = [
        { id: '1', status: PublicationStatusEnum.DRAFT },
      ];
      backendServiceSpy.get.and.returnValue(of(mockPrograms));

      let result: Program[] | undefined;
      service
        .getProgramDrafts(funderId)
        .subscribe((programs) => (result = programs));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `program${ApiPath.ENTITY_LIST_BY_FUNDER_APPEND_PARAMETER}${funderId}`,
        {
          params: new HttpParams().set(
            ApiPath.QUERY_PARAM_STATUS,
            PublicationStatusEnum.DRAFT
          ),
        }
      );
      expect(result).toEqual(mockPrograms);
    }));

    it('should call getAllProgramDrafts when funderId is not provided', fakeAsync(() => {
      const mockPrograms: Program[] = [
        { id: '1', status: PublicationStatusEnum.DRAFT },
      ];
      backendServiceSpy.get.and.returnValue(of(mockPrograms));

      let result: Program[] | undefined;
      service.getProgramDrafts('').subscribe((programs) => (result = programs));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `program${ApiPath.ENTITY_LIST}`,
        {
          params: new HttpParams().set(
            ApiPath.QUERY_PARAM_STATUS,
            PublicationStatusEnum.DRAFT
          ),
        }
      );
      expect(result).toEqual(mockPrograms);
    }));
  });

  describe('getProgram', () => {
    it('should return an Observable of Program', fakeAsync(() => {
      const programId = 'program1';
      const mockProgram: Program = { id: programId };
      backendServiceSpy.get.and.returnValue(of(mockProgram));

      let result: Program | undefined;
      service.getProgram(programId).subscribe((program) => (result = program));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `program${ApiPath.ENTITY_BY_ID_APPEND_PARAMETER}${programId}`
      );
      expect(result).toEqual(mockProgram);
    }));
  });

  describe('updateProgram', () => {
    it('should update a program and return the updated Program', fakeAsync(() => {
      const mockProgram: Program = { id: '1' };
      backendServiceSpy.put.and.returnValue(of(mockProgram));

      let result: Program | undefined;
      service
        .updateProgram(mockProgram)
        .subscribe((program) => (result = program));
      tick();

      expect(backendServiceSpy.put).toHaveBeenCalledWith(
        `program${ApiPath.UPDATE_ENTITY}`,
        mockProgram
      );
      expect(result).toEqual(mockProgram);
    }));
  });

  describe('addProgram', () => {
    it('should add a new program and return the added Program', fakeAsync(() => {
      const mockProgram: Program = { id: '1' };
      backendServiceSpy.post.and.returnValue(of(mockProgram));

      let result: Program | undefined;
      service
        .addProgram(mockProgram)
        .subscribe((program) => (result = program));
      tick();

      expect(backendServiceSpy.post).toHaveBeenCalledWith(
        `program${ApiPath.ADD_ENTITY}`,
        mockProgram
      );
      expect(result).toEqual(mockProgram);
    }));
  });

  describe('deleteProgram', () => {
    it('should delete a program and return the deleted Program', fakeAsync(() => {
      const programId = 'program1';
      const mockProgram: Program = { id: programId };
      backendServiceSpy.delete.and.returnValue(of(mockProgram));

      let result: Program | undefined;
      service
        .deleteProgram(programId)
        .subscribe((program) => (result = program));
      tick();

      expect(backendServiceSpy.delete).toHaveBeenCalledWith(
        `program${ApiPath.DELETE_ENTITY}${programId}`
      );
      expect(result).toEqual(mockProgram);
    }));
  });
});
