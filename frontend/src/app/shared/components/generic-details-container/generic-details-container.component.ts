import { Component } from '@angular/core';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
} from '@angular/material/card';

@Component({
  selector: 'app-generic-details-container',
  imports: [MatCard, MatCardHeader, MatCardContent, MatCardActions],
  templateUrl: './generic-details-container.component.html',
  styleUrl: './generic-details-container.component.scss',
})
export class GenericDetailsContainerComponent {}
