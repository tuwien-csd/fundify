import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProgramDetailComponent } from './components/program-detail/program-detail.component';
import { ProgramDetailEditComponent } from './components/program-detail-edit/program-detail-edit.component';
import { ProgramDetailPreviewComponent } from './components/program-detail-preview/program-detail-preview.component';
import { SharedModule } from '../shared/shared.module';
import { ProgramsComponent } from './programs.component';
import { ProgramsRoutingModule } from './programs-routing.module';
import { ProgramDisplayPipe } from './pipes/program-display.pipe';
import { StoreModule } from '@ngrx/store';
import { ProgramsEffects, programsReducer } from './store';
import { EffectsModule } from '@ngrx/effects';

@NgModule({
  imports: [
    CommonModule,
    ProgramsRoutingModule,
    SharedModule,
    StoreModule.forFeature('programs', programsReducer),
    EffectsModule.forFeature([ProgramsEffects]),
    ProgramsComponent,
    ProgramDetailComponent,
    ProgramDetailEditComponent,
    ProgramDetailPreviewComponent,
    ProgramDisplayPipe,
  ],
})
export class ProgramsModule {}
