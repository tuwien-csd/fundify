import { Component } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-footer',
  template: `
    <mat-toolbar color="primary" class="footer-toolbar">
      <div class="footer-left">
        <span class="copyright">© {{ currentYear }} FUNDify</span>
      </div>
      <div class="footer-right">
        <a mat-button href="/contact">Contact</a>
        <a mat-button href="/impressum">Impressum</a>
        <a
          mat-button
          href="https://www.tuwien.at/index.php?eID=dms&s=4&path=Documents/Data%20Protection%20Declaration%20Other/Data%20Protection%20Declaration%20Websites.pdf"
          target="_blank"
          rel="noopener noreferrer"
          >Privacy Policy</a
        >
      </div>
    </mat-toolbar>
  `,
  styles: [
    `
      .footer-toolbar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 0 1rem;
        height: 3rem;
        font-size: 0.9em;
      }

      .footer-left,
      .footer-right {
        display: flex;
        align-items: center;
      }

      .copyright {
        opacity: 0.8;
      }

      .footer-right a {
        margin-left: 1rem;
        opacity: 0.8;
        transition: opacity 0.2s ease-in-out;
      }

      .footer-right a:hover {
        opacity: 1;
      }

      @media (max-width: 600px) {
        .footer-toolbar {
          flex-direction: column;
          height: auto;
          padding: 1rem;
        }

        .footer-left,
        .footer-right {
          width: 100%;
          justify-content: center;
        }

        .footer-left {
          margin-bottom: 1rem;
        }

        .footer-right a {
          margin: 0 0.5rem;
        }
      }
    `,
  ],
  imports: [MatToolbar, MatButton],
})
export class FooterComponent {
  currentYear = new Date().getFullYear();
}
