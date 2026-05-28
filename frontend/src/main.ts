import {
  enableProdMode,
  importProvidersFrom,
  inject,
  provideAppInitializer,
} from '@angular/core';

import { environment } from './environments/environment';
import { ConfigService } from './app/core/services/config.service';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { bootstrapApplication, BrowserModule } from '@angular/platform-browser';
import { provideAnimations } from '@angular/platform-browser/animations';
import { CoreModule } from './app/core/core.module';
import { StoreModule } from '@ngrx/store';
import { routerReducer, StoreRouterConnectingModule } from '@ngrx/router-store';
import { EffectsModule } from '@ngrx/effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { OAuthStorage, provideOAuthClient } from 'angular-oauth2-oidc';
import { AppComponent } from './app/app.component';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { mainRoutes } from './app/app.routes';
import { provideDateFnsAdapter } from '@angular/material-date-fns-adapter';
import { de } from 'date-fns/locale';

if (environment.production) {
  enableProdMode();
}

/**
 * Custom provider for LocalStorage - see https://manfredsteyer.github.io/angular-oauth2-oidc/docs/additional-documentation/configure-custom-oauthstorage.html.
 */
export function storageFactory(): OAuthStorage {
  return localStorage;
}

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(
      BrowserModule,
      CoreModule,
      StoreModule.forRoot({ router: routerReducer }),
      EffectsModule.forRoot([]),
      StoreRouterConnectingModule.forRoot(),
      StoreDevtoolsModule.instrument({
        maxAge: 25,
        logOnly: environment.production,
        connectInZone: true,
      })
    ),
    provideOAuthClient(),
    { provide: OAuthStorage, useFactory: storageFactory },
    provideRouter(mainRoutes, withComponentInputBinding()),
    provideAppInitializer(() => {
      const configService = inject(ConfigService);
      return configService.initializeApp(); // Will delay the app initialization until the config is loaded.
    }),
    provideDateFnsAdapter(),
    { provide: MAT_DATE_LOCALE, useValue: de },
    provideAnimations(),
  ],
}).catch((err) => console.error(err));
