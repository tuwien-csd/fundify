import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/services/auth.service';
import { HttpRequestService, RequestOptions } from './http-request.service';

/**
 * @deprecated Use {@link BackendServiceV2}  for new code.
 */
@Injectable({
  providedIn: 'root',
})
export class BackendService {
  private authService = inject(AuthService);
  private httpRequestService = inject(HttpRequestService);

  private getBackendBaseUrl(): string {
    return this.httpRequestService.getBackendBaseUrl();
  }

  private getBasicOptions(): RequestOptions {
    return this.httpRequestService.getBasicOptions(this.authService.token());
  }

  get<T>(path: string, options: RequestOptions = {}): Observable<T> {
    const url = `${this.getBackendBaseUrl()}${path}`;
    const finalOptions = { ...this.getBasicOptions(), ...options };
    return this.httpRequestService.get<T>(url, finalOptions);
  }

  post<T>(
    path: string,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
    body: any,
    options: RequestOptions = {}
  ): Observable<T> {
    const url = `${this.getBackendBaseUrl()}${path}`;
    const finalOptions = { ...this.getBasicOptions(), ...options };
    return this.httpRequestService.post<T>(url, body, finalOptions);
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  put<T>(path: string, body: any, options: RequestOptions = {}): Observable<T> {
    const url = `${this.getBackendBaseUrl()}${path}`;
    const finalOptions = { ...this.getBasicOptions(), ...options };
    return this.httpRequestService.put<T>(url, body, finalOptions);
  }

  delete<T>(path: string, options: RequestOptions = {}): Observable<T> {
    const url = `${this.getBackendBaseUrl()}${path}`;
    const finalOptions = { ...this.getBasicOptions(), ...options };
    return this.httpRequestService.delete<T>(url, finalOptions);
  }
}
