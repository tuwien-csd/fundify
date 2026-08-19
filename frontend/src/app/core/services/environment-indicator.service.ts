import { inject, Injectable } from '@angular/core';
import { ConfigService } from './config.service';

/** Value of `frontend.env` for which no indicator is shown. */
const PRODUCTION_ENV = 'PROD';

const ENV_COLORS: Record<string, string> = {
  STAGING: '#b25e00',
  DEV: '#6a1b9a',
};

const UNKNOWN_ENV_COLOR: string = '#4f4f4f';

/**
 * Makes the deployment environment visible in the UI so a staging tab cannot be
 * mistaken for production. Driven by `frontend.env` from the backend config.
 */
@Injectable({
  providedIn: 'root',
})
export class EnvironmentIndicatorService {
  private configService = inject(ConfigService);

  /** Environment name, or undefined on production where nothing is marked. */
  readonly label = this.resolveLabel();
  readonly color = ENV_COLORS[this.label ?? ''] ?? UNKNOWN_ENV_COLOR;

  private readonly baseTitle = document.title;

  /** Marks document title and favicon - no-op on production. */
  public applyBranding(): void {
    if (!this.label) {
      return;
    }

    document.title = `[${this.label}] ${this.baseTitle}`;
    this.badgeFavicon();
  }

  private resolveLabel(): string | undefined {
    const env = this.configService.getEnvironment()?.toUpperCase();
    return !env || env === PRODUCTION_ENV ? undefined : env;
  }

  /** Draws a colored dot onto the favicon to make the browser tab distinguishable. */
  private badgeFavicon(): void {
    const link = document.querySelector<HTMLLinkElement>('link[rel="icon"]');
    if (!link) {
      return;
    }

    const original = new Image();
    original.onload = () => {
      const size = 64;
      const canvas = document.createElement('canvas');
      canvas.width = size;
      canvas.height = size;

      const ctx = canvas.getContext('2d');
      if (!ctx) {
        return;
      }

      ctx.drawImage(original, 0, 0, size, size);

      const radius = size * 0.28;
      const center = size - radius - 2;
      ctx.beginPath();
      ctx.arc(center, center, radius, 0, 2 * Math.PI);
      ctx.fillStyle = this.color;
      ctx.fill();
      ctx.lineWidth = size * 0.05;
      ctx.strokeStyle = 'white';
      ctx.stroke();

      link.href = canvas.toDataURL('image/png');
    };
    original.src = link.href;
  }
}
