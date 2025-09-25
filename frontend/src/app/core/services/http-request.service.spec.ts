import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { HttpRequestService, RequestOptions } from './http-request.service';
import {
  HttpHeaders,
  HttpErrorResponse,
  provideHttpClient,
  withInterceptorsFromDi,
} from '@angular/common/http';
import { environment } from '../../../environments/environment';

describe('HttpRequestService', () => {
  let service: HttpRequestService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        HttpRequestService,
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(HttpRequestService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getBackendBaseUrl', () => {
    it('should return the correct backend URL', () => {
      expect(service.getBackendBaseUrl()).toBe(environment.backendUrl);
    });
  });

  describe('getBasicOptions', () => {
    it('should return basic options without token', () => {
      const options = service.getBasicOptions();
      expect(options.headers.get('Content-Type')).toBe('application/json');
      expect(options.headers.has('Authorization')).toBeFalse();
    });

    it('should return basic options with token', () => {
      const token = 'test-token';
      const options = service.getBasicOptions(token);
      expect(options.headers.get('Content-Type')).toBe('application/json');
      expect(options.headers.get('Authorization')).toBe(`Bearer ${token}`);
    });
  });

  describe('HTTP methods', () => {
    const testUrl = 'https://api.example.com/test';
    const testData = { id: 1, name: 'Test' };

    it('should send a GET request', () => {
      service.get(testUrl).subscribe((data) => expect(data).toEqual(testData));

      const req = httpMock.expectOne(testUrl);
      expect(req.request.method).toBe('GET');
      req.flush(testData);
    });

    it('should send a POST request', () => {
      service
        .post(testUrl, testData)
        .subscribe((data) => expect(data).toEqual(testData));

      const req = httpMock.expectOne(testUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(testData);
      req.flush(testData);
    });

    it('should send a PUT request', () => {
      service
        .put(testUrl, testData)
        .subscribe((data) => expect(data).toEqual(testData));

      const req = httpMock.expectOne(testUrl);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(testData);
      req.flush(testData);
    });

    it('should send a DELETE request', () => {
      service
        .delete(testUrl)
        .subscribe((data) => expect(data).toEqual(testData));

      const req = httpMock.expectOne(testUrl);
      expect(req.request.method).toBe('DELETE');
      req.flush(testData);
    });
  });

  describe('Error handling', () => {
    const testUrl = 'https://api.example.com/test';
    const errorResponse = new HttpErrorResponse({
      error: 'test 404 error',
      status: 404,
      statusText: 'Not Found',
    });

    it('should handle errors and log them', () => {
      spyOn(console, 'error');

      service.get(testUrl).subscribe(
        () => fail('should have failed with the 404 error'),
        (error) => expect(error.status).toEqual(404)
      );

      httpMock.expectOne(testUrl).flush('', errorResponse);
      expect(console.error).toHaveBeenCalled();
    });

    it('should return fallback value on error if provided', () => {
      const fallbackValue = { id: 0, name: 'Fallback' };
      const options: RequestOptions = { fallbackValue };

      service.get(testUrl, options).subscribe(
        (data) => expect(data).toEqual(fallbackValue),
        () => fail('should not have thrown an error')
      );

      httpMock.expectOne(testUrl).flush('', errorResponse);
    });
  });

  describe('Option preparation', () => {
    it('should merge custom headers with basic headers', () => {
      const testUrl = 'https://api.example.com/test';
      const customHeaders = new HttpHeaders().set('Custom-Header', 'TestValue');
      const options: RequestOptions = { headers: customHeaders };

      service.get(testUrl, options).subscribe();

      const req = httpMock.expectOne(testUrl);
      expect(req.request.headers.get('Content-Type')).toBe('application/json');
      expect(req.request.headers.get('Custom-Header')).toBe('TestValue');
      req.flush({});
    });
  });
});
