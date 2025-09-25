import { Component, OnInit, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import * as fromCore from './core/store';
import { LayoutComponent } from './core/components/layout/layout.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [LayoutComponent],
})
export class AppComponent implements OnInit {
  private store = inject(Store);

  ngOnInit(): void {
    this.store.dispatch(fromCore.loadOefos());
  }
}
