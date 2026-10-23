import { inject, Injectable, signal } from '@angular/core';
import { BackendServiceV2 } from '../core/services/backend-service-v2.service';
import { InstitutionKindEnum } from '../shared/models/enums/institutionKind.enum';
import { NotificationService } from '../shared/services/notification-service.service';

export type TicketCreate = {
  firstName: string;
  lastName: string;
  subject: string;
  category: string;
  email: string;
  message: string;
  kindOfInstitution?: InstitutionKindEnum;
};

@Injectable({
  providedIn: 'root',
})
export class ContactFormService {
  private readonly backendService = inject(BackendServiceV2);
  private readonly notificationService = inject(NotificationService);
  public isPending = signal<boolean>(false);

  /**
   * Submits the form data to the backend.
   * @param input - The form data to be submitted.
   * @returns true if the form was submitted successfully, false otherwise.
   */
  async submitForm(input: TicketCreate) {
    try {
      this.isPending.set(true);
      const infoNotification = this.notificationService.info(
        'Your request is being submitted.'
      );
      const { error } = await this.backendService.client.POST('/api/contact', {
        body: input,
      });
      infoNotification.dismiss();
      if (error) {
        this.notificationService.error(
          'An unexpected error occurred. Please try again later.'
        );
        return false;
      }

      this.notificationService.success('Your message has been sent!');
      return true;
    } finally {
      this.isPending.set(false);
    }
  }
}
