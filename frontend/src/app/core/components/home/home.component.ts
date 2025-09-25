import { Component, inject } from '@angular/core';
import { AuthService } from '../../auth/services/auth.service';
import { MatIcon } from '@angular/material/icon';
import {
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle,
} from '@angular/material/expansion';
import { MatTooltip } from '@angular/material/tooltip';
import { FUNDIFY_FAQ_LIST } from './faq-list';
import { ROUTER_LINKS } from '../../router-links.constants';
import { MatButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  template: `
    <div class="landing-page">
      <!-- Hero Section -->
      <section class="hero">
        <div class="hero-content">
          <h1>Simplify Your Funding Management with FUNDify</h1>
          <p>Aggregate, Manage, and Annotate Funding Data in One Place</p>
          @if (!authService.isAuthenticated()) {
            <p>
              Not registered yet? Fill out the
              <a href="${ROUTER_LINKS.CONTACT}">contact Form</a> and we will get
              back to you!
            </p>
            <button
              mat-raised-button
              color="primary"
              style="font-weight:bold"
              [routerLink]="'/' + ROUTER_LINKS.CONTACT"
            >
              Contact Us
            </button>
          }
        </div>
      </section>

      <!-- Key Features Section -->
      <section class="features">
        <div class="features-grid">
          @for (feature of features; track feature) {
            <div class="feature">
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.description }}</p>
            </div>
          }
        </div>
      </section>
      <div class="section-divider"></div>

      <!-- FAQ Section -->
      <section class="faq">
        <div class="section-header">
          <mat-icon>help_outline</mat-icon>
          <h2>Frequently Asked Questions</h2>
        </div>
        <div class="faq-grid">
          @for (faq of faqs; track faq) {
            <div class="faq-item">
              <mat-expansion-panel>
                <mat-expansion-panel-header>
                  <mat-panel-title>
                    {{ faq.question }}
                  </mat-panel-title>
                </mat-expansion-panel-header>
                <p>{{ faq.answer }}</p>
              </mat-expansion-panel>
            </div>
          }
        </div>
      </section>
      <div class="section-divider"></div>

      <!-- Links Section -->
      <section class="links">
        <div class="links-container">
          @for (link of links; track link) {
            <a
              [href]="link.url"
              target="_blank"
              rel="noopener noreferrer"
              [matTooltip]="link.label"
              class="link-item"
            >
              <mat-icon>{{ link.icon }}</mat-icon>
            </a>
          }
        </div>
      </section>
    </div>
  `,
  styleUrls: ['./home.component.scss'],
  imports: [
    MatIcon,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatTooltip,
    MatButton,
    RouterLink,
  ],
})
export class HomeComponent {
  authService = inject(AuthService);

  features = [
    {
      title: 'Aggregate',
      description:
        'The central access point for funding information within the RIS Synergy Network. FUNDify collects metadata from all our partners, providing a comprehensive overview of funding opportunities.',
    },
    {
      title: 'Manage',
      description: `Enter, update, and track funding information with ease. Our platform allows users to input new funding data, modify existing entries, and monitor the status of various funding initiatives.`,
    },
    {
      title: 'Annotate',
      description:
        'Add custom metadata to enrich funding information. FUNDify provides a platform for university personnel to annotate funding data with additional context.',
    },
  ];

  faqs = FUNDIFY_FAQ_LIST;

  links = [
    {
      icon: 'menu_book',
      url: 'https://documentation.forschungsdaten.at',
      label: 'Ris Synergy Documentation',
    },
    { icon: 'api', url: './api/docs', label: 'FUNDify REST API' },
    {
      icon: 'code',
      url: 'https://github.com/tuwien-csd/fundify',
      label: 'GitHub Repository',
    },
    {
      icon: 'public',
      url: 'https://forschungsdaten.at/ris-synergy',
      label: 'RIS Synergy Home Page',
    },
  ];
  protected readonly ROUTER_LINKS = ROUTER_LINKS;
}
