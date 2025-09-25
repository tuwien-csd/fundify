import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import {
  HttpClient,
  HttpErrorResponse,
  HttpHeaders,
  HttpParams,
} from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export type RequestOptions = {
  headers?: HttpHeaders;
  params?: HttpParams;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  fallbackValue?: any;
};

@Injectable({
  providedIn: 'root',
})
export class HttpRequestService {
  private http = inject(HttpClient);

  private backendUrl = environment.backendUrl;
  private httpBasicOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  };

  getBackendBaseUrl(): string {
    return this.backendUrl;
  }

  getBasicOptions(token?: string | null): { headers: HttpHeaders } {
    let headers = this.httpBasicOptions.headers;
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return { headers };
  }

  get<T>(url: string, options: RequestOptions = {}): Observable<T> {
    const finalOptions = this.prepareOptions(options);
    return this.http
      .get<T>(url, finalOptions)
      .pipe(
        catchError((error) => this.handleError<T>(error, `GET ${url}`, options))
      );
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  post<T>(url: string, body: any, options: RequestOptions = {}): Observable<T> {
    const finalOptions = this.prepareOptions(options);
    return this.http
      .post<T>(url, body, finalOptions)
      .pipe(
        catchError((error) =>
          this.handleError<T>(error, `POST ${url}`, options)
        )
      );
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  put<T>(url: string, body: any, options: RequestOptions = {}): Observable<T> {
    const finalOptions = this.prepareOptions(options);
    return this.http
      .put<T>(url, body, finalOptions)
      .pipe(
        catchError((error) => this.handleError<T>(error, `PUT ${url}`, options))
      );
  }

  delete<T>(url: string, options: RequestOptions = {}): Observable<T> {
    const finalOptions = this.prepareOptions(options);
    return this.http
      .delete<T>(url, finalOptions)
      .pipe(
        catchError((error) =>
          this.handleError<T>(error, `DELETE ${url}`, options)
        )
      );
  }

  private prepareOptions(options: RequestOptions): RequestOptions {
    let headers = this.getBasicOptions().headers;

    if (options.headers) {
      options.headers.keys().forEach((key) => {
        headers = headers.set(key, options.headers!.get(key)!);
      });
    }
    return { ...options, headers };
  }

  private handleError<T>(
    error: HttpErrorResponse,
    operation = 'operation',
    options: RequestOptions
  ): Observable<T> {
    console.error(`${operation} request:`, error);
    return options.fallbackValue
      ? of(options.fallbackValue)
      : throwError(error);
  }
}
