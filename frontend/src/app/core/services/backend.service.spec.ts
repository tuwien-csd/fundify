import { TestBed } from '@angular/core/testing';
import { BackendService } from './backend.service';
import { AuthService } from '../auth/services/auth.service';
import { HttpRequestService } from './http-request.service';
import { of } from 'rxjs';

describe('BackendService', () => {
  let service: BackendService;
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let httpRequestServiceMock: jasmine.SpyObj<HttpRequestService>;

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', [], {
      token: jasmine.createSpy(),
      // Requests are deferred until the OAuth flow has initialized.
      authInitialized: Promise.resolve(),
    });
    const httpRequestSpy = jasmine.createSpyObj('HttpRequestService', [
      'getBackendBaseUrl',
      'getBasicOptions',
      'get',
      'post',
      'put',
      'delete',
    ]);

    TestBed.configureTestingModule({
      providers: [
        BackendService,
        { provide: AuthService, useValue: authSpy },
        { provide: HttpRequestService, useValue: httpRequestSpy },
      ],
    });

    service = TestBed.inject(BackendService);
    authServiceMock = TestBed.inject(
      AuthService
    ) as jasmine.SpyObj<AuthService>;
    httpRequestServiceMock = TestBed.inject(
      HttpRequestService
    ) as jasmine.SpyObj<HttpRequestService>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('get', () => {
    it('should call httpRequestService.get with correct parameters', async () => {
      const path = '/api/data';
      const mockResponse = { data: 'test' };
      // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
      const mockOptions = { headers: { 'Custom-Header': 'Value' } as any };
      const mockToken = 'mock-token';
      const mockBaseUrl = 'http://api.example.com';
      const mockBasicOptions = {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        headers: { Authorization: 'Bearer mock-token' } as any,
      };

      authServiceMock.token.and.returnValue(mockToken);
      httpRequestServiceMock.getBackendBaseUrl.and.returnValue(mockBaseUrl);
      httpRequestServiceMock.getBasicOptions.and.returnValue(mockBasicOptions);
      httpRequestServiceMock.get.and.returnValue(of(mockResponse));

      const response = await service.get(path, mockOptions).toPromise();
      expect(response).toEqual(mockResponse);

      expect(httpRequestServiceMock.getBackendBaseUrl).toHaveBeenCalled();
      expect(httpRequestServiceMock.getBasicOptions).toHaveBeenCalledWith(
        mockToken
      );
      expect(httpRequestServiceMock.get).toHaveBeenCalledWith(
        `${mockBaseUrl}${path}`,
        { ...mockBasicOptions, ...mockOptions }
      );
    });
  });

  describe('post', () => {
    it('should call httpRequestService.post with correct parameters', async () => {
      const path = '/api/create';
      const body = { name: 'Test' };
      const mockResponse = { id: 1, name: 'Test' };
      // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
      const mockOptions = { headers: { 'Custom-Header': 'Value' } as any };
      const mockToken = 'mock-token';
      const mockBaseUrl = 'http://api.example.com';
      const mockBasicOptions = {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        headers: { Authorization: 'Bearer mock-token' } as any,
      };

      authServiceMock.token.and.returnValue(mockToken);
      httpRequestServiceMock.getBackendBaseUrl.and.returnValue(mockBaseUrl);
      httpRequestServiceMock.getBasicOptions.and.returnValue(mockBasicOptions);
      httpRequestServiceMock.post.and.returnValue(of(mockResponse));

      const response = await service.post(path, body, mockOptions).toPromise();
      expect(response).toEqual(mockResponse);

      expect(httpRequestServiceMock.getBackendBaseUrl).toHaveBeenCalled();
      expect(httpRequestServiceMock.getBasicOptions).toHaveBeenCalledWith(
        mockToken
      );
      expect(httpRequestServiceMock.post).toHaveBeenCalledWith(
        `${mockBaseUrl}${path}`,
        body,
        { ...mockBasicOptions, ...mockOptions }
      );
    });
  });

  describe('put', () => {
    it('should call httpRequestService.put with correct parameters', async () => {
      const path = '/api/update/1';
      const body = { name: 'Updated Test' };
      const mockResponse = { id: 1, name: 'Updated Test' };
      // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
      const mockOptions = { headers: { 'Custom-Header': 'Value' } as any };
      const mockToken = 'mock-token';
      const mockBaseUrl = 'http://api.example.com';
      const mockBasicOptions = {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        headers: { Authorization: 'Bearer mock-token' } as any,
      };

      authServiceMock.token.and.returnValue(mockToken);
      httpRequestServiceMock.getBackendBaseUrl.and.returnValue(mockBaseUrl);
      httpRequestServiceMock.getBasicOptions.and.returnValue(mockBasicOptions);
      httpRequestServiceMock.put.and.returnValue(of(mockResponse));

      const response = await service.put(path, body, mockOptions).toPromise();
      expect(response).toEqual(mockResponse);

      expect(httpRequestServiceMock.getBackendBaseUrl).toHaveBeenCalled();
      expect(httpRequestServiceMock.getBasicOptions).toHaveBeenCalledWith(
        mockToken
      );
      expect(httpRequestServiceMock.put).toHaveBeenCalledWith(
        `${mockBaseUrl}${path}`,
        body,
        { ...mockBasicOptions, ...mockOptions }
      );
    });
  });

  describe('delete', () => {
    it('should call httpRequestService.delete with correct parameters', async () => {
      const path = '/api/delete/1';
      const mockResponse = { success: true };
      // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
      const mockOptions = { headers: { 'Custom-Header': 'Value' } as any };
      const mockToken = 'mock-token';
      const mockBaseUrl = 'http://api.example.com';
      const mockBasicOptions = {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
        headers: { Authorization: 'Bearer mock-token' } as any,
      };

      authServiceMock.token.and.returnValue(mockToken);
      httpRequestServiceMock.getBackendBaseUrl.and.returnValue(mockBaseUrl);
      httpRequestServiceMock.getBasicOptions.and.returnValue(mockBasicOptions);
      httpRequestServiceMock.delete.and.returnValue(of(mockResponse));

      const response = await service.delete(path, mockOptions).toPromise();
      expect(response).toEqual(mockResponse);

      expect(httpRequestServiceMock.getBackendBaseUrl).toHaveBeenCalled();
      expect(httpRequestServiceMock.getBasicOptions).toHaveBeenCalledWith(
        mockToken
      );
      expect(httpRequestServiceMock.delete).toHaveBeenCalledWith(
        `${mockBaseUrl}${path}`,
        { ...mockBasicOptions, ...mockOptions }
      );
    });
  });
});
