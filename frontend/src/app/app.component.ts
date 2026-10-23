import { Component, OnInit, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import * as fromCore from './core/store';
import { LayoutComponent } from './core/components/layout/layout.component';
import { EnvironmentIndicatorService } from './core/services/environment-indicator.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [LayoutComponent],
})
export class AppComponent implements OnInit {
  private store = inject(Store);
  private environmentIndicator = inject(EnvironmentIndicatorService);

  ngOnInit(): void {
    this.environmentIndicator.applyBranding();
    this.store.dispatch(fromCore.loadOefos());
  }
}
