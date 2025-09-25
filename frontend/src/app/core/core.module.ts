import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutComponent } from './components/layout/layout.component';
import { HomeComponent } from './components/home/home.component';
import {
  provideHttpClient,
  withInterceptorsFromDi,
} from '@angular/common/http';

import { RouterModule } from '@angular/router';
import { HeaderComponent } from './components/layout/ui/header.component';
import { FooterComponent } from './components/layout/ui/footer.component';
import { SidebarComponent } from './components/layout/ui/sidebar.component';
import { UserWidgetComponent } from './components/layout/ui/user-widget.component';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { OefosEffects, coreReducers } from './store';

@NgModule({
  exports: [LayoutComponent, HomeComponent],
  imports: [
    CommonModule,
    RouterModule,
    StoreModule.forFeature('core', coreReducers),
    EffectsModule.forFeature([OefosEffects]),
    LayoutComponent,
    HomeComponent,
    HeaderComponent,
    FooterComponent,
    SidebarComponent,
    UserWidgetComponent,
  ],
  providers: [provideHttpClient(withInterceptorsFromDi())],
})
export class CoreModule {}
