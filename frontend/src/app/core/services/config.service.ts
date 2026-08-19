import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, of, Subject } from 'rxjs';
import { Config } from '../models/config.interface';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  http = inject(HttpClient);

  private config?: Config;
  $config = new Subject<Config>();

  public initializeApp(): Observable<boolean> {
    return this.loadConfig().pipe(
      map((config) => {
        this.config = config;
        this.$config.next(config);
        return true;
      }),
      catchError((error) => {
        console.error(
          `Unexpected error while loading config from backend at ${environment.backendUrl}.`,
          error
        );
        console.error('Make sure your backend is up and running.');
        return of(false);
      })
    );
  }

  public getEnvironment(): string | undefined {
    return this.config?.env;
  }

  private loadConfig(): Observable<Config> {
    const host = environment.backendUrl;
    return this.http.get<Config>(`${host}config`);
  }
}
