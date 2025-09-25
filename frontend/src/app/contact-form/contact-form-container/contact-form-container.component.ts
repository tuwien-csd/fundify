import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ContactFormComponent } from '../contact-form/contact-form.component';
import { MatCard } from '@angular/material/card';
import { CONTACT_FORM_CONSTANTS } from '../contact-form-constants';

@Component({
  selector: 'app-contact-form-container',
  imports: [ReactiveFormsModule, ContactFormComponent, MatCard],
  template: `
    <div
      class="flex flex-wrap justify-evenly items-center align-center min-h-128 h-[50%] w-full gap-12 p-4"
    >
      <div class="">
        <div class="text-5xl font-bold mb-4 ">
          {{ CONTACT_FORM_CONSTANTS.CONTAINER.HEADER }}
        </div>
        <p class="text-lg text-gray-600 text-center mb-8">
          {{ CONTACT_FORM_CONSTANTS.CONTAINER.BODY }}
        </p>
      </div>
      <mat-card class="w-[50%] p-8">
        <app-contact-form />
      </mat-card>
    </div>
  `,
  styles: ``,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContactFormContainerComponent {
  protected readonly CONTACT_FORM_CONSTANTS = CONTACT_FORM_CONSTANTS;
}
