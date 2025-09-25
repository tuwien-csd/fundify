import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProgramDetailEditComponent } from './components/program-detail-edit/program-detail-edit.component';
import { SharedModule } from '../shared/shared.module';
import { ProgramsComponent } from './programs.component';
import { ProgramDisplayPipe } from './pipes/program-display.pipe';
import { StoreModule } from '@ngrx/store';
import { ProgramsEffects, programsReducer } from './store';
import { EffectsModule } from '@ngrx/effects';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    StoreModule.forFeature('programs', programsReducer),
    EffectsModule.forFeature([ProgramsEffects]),
    ProgramsComponent,
    ProgramDetailEditComponent,
    ProgramDisplayPipe,
  ],
})
export class ProgramsModule {}
