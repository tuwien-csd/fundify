import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpParams } from '@angular/common/http';
import { of } from 'rxjs';
import { FunderService } from './funder.service';
import { BackendService } from '../../core/services/backend.service';
import { FunderWebModel } from '../models/funder.interface';
import { FundingEntityRef } from '../../shared/models/interfaces/funding-entity-ref.interface';
import { ApiPath } from '../../shared/models/enums/api-path';
import { LanguageEnum } from '../../shared/models/enums/language.enum';
import { TranslationEnum } from '../../shared/models/enums/translation-enum';

describe('FunderService', () => {
  let service: FunderService;
  let backendServiceSpy: jasmine.SpyObj<BackendService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('BackendService', [
      'get',
      'post',
      'put',
      'delete',
    ]);

    TestBed.configureTestingModule({
      providers: [FunderService, { provide: BackendService, useValue: spy }],
    });

    service = TestBed.inject(FunderService);
    backendServiceSpy = TestBed.inject(
      BackendService
    ) as jasmine.SpyObj<BackendService>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getFunders', () => {
    it('should return an Observable of Funder[]', fakeAsync(() => {
      const mockFunders: FunderWebModel[] = [
        {
          id: '1',
          risId: 'id',
          name: [
            {
              text: 'Funder 1',
              language: LanguageEnum.ENGLISH,
              translation: TranslationEnum.ORIGINAL,
            },
          ],
          website: 'https://funder1.com',
        },
        {
          id: '1',
          risId: 'id',
          name: [
            {
              text: 'Funder 1',
              language: LanguageEnum.ENGLISH,
              translation: TranslationEnum.ORIGINAL,
            },
          ],
          website: 'https://funder1.com',
        },
      ];
      backendServiceSpy.get.and.returnValue(of(mockFunders));

      let result: FunderWebModel[] | undefined;
      service.getFunders().subscribe((funders) => (result = funders));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `funders`
      );
      expect(result).toEqual(mockFunders);
    }));
  });

  describe('getFunderReferences', () => {
    it('should return an Observable of FundingEntityRef[]', fakeAsync(() => {
      const mockRefs: FundingEntityRef[] = [
        { id: '1' },
        { id: '2' },
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
      ] as any[];
      backendServiceSpy.get.and.returnValue(of(mockRefs));

      let result: FundingEntityRef[] | undefined;
      service.getFunderReferences().subscribe((refs) => (result = refs));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `funders${ApiPath.REFERENCE_LIST}`
      );
      expect(result).toEqual(mockRefs);
    }));
  });

  describe('getFunder', () => {
    it('should return an Observable of Funder', fakeAsync(() => {
      const mockFunder: FunderWebModel = {
        id: '1',
        name: [],
        risId: '',
        website: '',
      };
      backendServiceSpy.get.and.returnValue(of(mockFunder));

      let result: FunderWebModel | undefined;
      service.getFunder('1').subscribe((funder) => (result = funder));
      tick();

      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `funders/1`
      );
      expect(result).toEqual(mockFunder);
    }));
  });

  describe('addFunder', () => {
    it('should add a new funder and return the added Funder', fakeAsync(() => {
      const mockFunder: FunderWebModel = {
        id: '1',
        risId: 'id',
        name: [
          {
            text: 'Funder 1',
            language: LanguageEnum.ENGLISH,
            translation: TranslationEnum.ORIGINAL,
          },
        ],
        website: 'https://funder1.com',
      };
      backendServiceSpy.post.and.returnValue(of(mockFunder));

      let result: FunderWebModel | undefined;
      service.addFunder(mockFunder).subscribe((funder) => (result = funder));
      tick();

      expect(backendServiceSpy.post).toHaveBeenCalledWith(
        `funders`,
        mockFunder
      );
      expect(result).toEqual(mockFunder);
    }));
  });

  describe('updateFunder', () => {
    it('should update a funder and return the updated Funder', fakeAsync(() => {
      const mockFunder: FunderWebModel = {
        id: '1',
        risId: 'id',
        name: [
          {
            text: 'Funder 1',
            language: LanguageEnum.ENGLISH,
            translation: TranslationEnum.ORIGINAL,
          },
        ],
        website: 'https://funder1.com',
      };
      backendServiceSpy.put.and.returnValue(of(mockFunder));

      let result: FunderWebModel | undefined;
      service.updateFunder(mockFunder).subscribe((funder) => (result = funder));
      tick();

      expect(backendServiceSpy.put).toHaveBeenCalledWith(
        `funders/1`,
        mockFunder
      );
      expect(result).toEqual(mockFunder);
    }));
  });

  describe('deleteFunder', () => {
    it('should delete a funder and return the deleted Funder', fakeAsync(() => {
      const funderId = 'funder1';
      const mockFunder: FunderWebModel = {
        id: '1',
        risId: 'id',
        name: [
          {
            text: 'Funder 1',
            language: LanguageEnum.ENGLISH,
            translation: TranslationEnum.ORIGINAL,
          },
        ],
        website: 'https://funder1.com',
      };
      backendServiceSpy.delete.and.returnValue(of(mockFunder));

      let result: FunderWebModel | undefined;
      service.deleteFunder(funderId).subscribe((funder) => (result = funder));
      tick();

      expect(backendServiceSpy.delete).toHaveBeenCalledWith(
        `funders/${funderId}`
      );
      expect(result).toEqual(mockFunder);
    }));
  });

  describe('search', () => {
    it('should search funders and return FundingEntityRef[]', fakeAsync(() => {
      const searchText = 'test';
      const mockRefs: FundingEntityRef[] = [
        { id: '1' },
        { id: '2' },
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
      ] as any[];
      backendServiceSpy.get.and.returnValue(of(mockRefs));

      let result: FundingEntityRef[] | undefined;
      service.search(searchText).subscribe((refs) => (result = refs));
      tick();

      const expectedParams = new HttpParams().set(
        ApiPath.QUERY_PARAM_SEARCH_TERM,
        searchText
      );
      expect(backendServiceSpy.get).toHaveBeenCalledWith(
        `funders${ApiPath.REFERENCE_LIST_SEARCH_ADD_QUERY_PARAM}`,
        { params: expectedParams }
      );
      expect(result).toEqual(mockRefs);
    }));
  });
});
