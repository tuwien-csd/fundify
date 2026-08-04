import { inject, Injectable } from '@angular/core';
import { from, Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
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

  /**
   * Defers the request until the OAuth flow has initialized. Initialization is
   * asynchronous, so a request sent during app startup would otherwise read a
   * token that is not there yet and fail with a 401.
   */
  private authenticated<T>(
    send: (options: RequestOptions) => Observable<T>,
    options: RequestOptions
  ): Observable<T> {
    return from(this.authService.authInitialized).pipe(
      switchMap(() => send({ ...this.getBasicOptions(), ...options }))
    );
  }

  get<T>(path: string, options: RequestOptions = {}): Observable<T> {
    const url = `${this.getBackendBaseUrl()}${path}`;
    return this.authenticated(
      (finalOptions) => this.httpRequestService.get<T>(url, finalOptions),
      options
    );
  }

  post<T>(
    path: string,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
    body: any,
    options: RequestOptions = {}
  ): Observable<T> {
    const url = `${this.getBackendBaseUrl()}${path}`;
    return this.authenticated(
      (finalOptions) =>
        this.httpRequestService.post<T>(url, body, finalOptions),
      options
    );
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  put<T>(path: string, body: any, options: RequestOptions = {}): Observable<T> {
    const url = `${this.getBackendBaseUrl()}${path}`;
    return this.authenticated(
      (finalOptions) => this.httpRequestService.put<T>(url, body, finalOptions),
      options
    );
  }

  delete<T>(path: string, options: RequestOptions = {}): Observable<T> {
    const url = `${this.getBackendBaseUrl()}${path}`;
    return this.authenticated(
      (finalOptions) => this.httpRequestService.delete<T>(url, finalOptions),
      options
    );
  }
}
