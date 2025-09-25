import { inject, Injectable } from '@angular/core';
import createClient, { Client } from 'openapi-fetch';
import { environment } from '../../../environments/environment';
import { paths } from '../../../generated/refop-be';
import { AuthService } from '../auth/services/auth.service';

@Injectable({
  providedIn: 'root',
})
export class BackendServiceV2 {
  private readonly authService = inject(AuthService);

  readonly client: Client<paths, `${string}/${string}`>;

  constructor() {
    const client = createClient<paths>({ baseUrl: environment.backendBaseUrl });
    client.use({
      // Pass the authentication token in the Authorization header for every request if the user is authenticated
      onRequest: async ({ request }) => {
        if (this.authService.isAuthenticated()) {
          request.headers.set(
            'Authorization',
            `Bearer ${this.authService.token()}`
          );
        }
        return request;
      },
    });

    this.client = client;
  }
}
