import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CallsComponent } from './calls.component';
import { CallDetailComponent } from './components/call-detail/call-detail.component';
import { CallDetailEditComponent } from './components/call-detail-edit/call-detail-edit.component';
import { CallDetailPreviewComponent } from './components/call-detail-preview/call-detail-preview.component';
import { SharedModule } from '../shared/shared.module';
import { CallsRoutingModule } from './calls-routing.module';
import { CallDisplayPipe } from './pipes/call-display.pipe';
import { StoreModule } from '@ngrx/store';
import { annotatedCallsReducer, CallsEffects, callsReducer } from './store';
import { EffectsModule } from '@ngrx/effects';
import { CallAnnotationListComponent } from './components/call-annotation-list/call-annotation-list.component';
import { CallDetailAnnotateComponent } from './components/call-detail-annotate/call-detail-annotate.component';
import { VocabularyModalComponent } from './components/vocabulary-modal/vocabulary-modal.component';
import { ConfirmCancelModalComponent } from './components/confirm-cancel-modal/confirm-cancel-modal.component';
import { VocabularyFormFieldComponent } from './components/vocabulary-form-field/vocabulary-form-field.component';
import { AnnotatedCallDisplayPipe } from './pipes/annotated-call-display.pipe';

@NgModule({
  imports: [
    CommonModule,
    CallsRoutingModule,
    SharedModule,
    StoreModule.forFeature('callsModule', {
      calls: callsReducer,
      annotatedCalls: annotatedCallsReducer,
    }),
    EffectsModule.forFeature([CallsEffects]),
    CallsComponent,
    CallDetailComponent,
    CallDetailEditComponent,
    CallDetailPreviewComponent,
    CallDisplayPipe,
    CallDetailAnnotateComponent,
    VocabularyModalComponent,
    ConfirmCancelModalComponent,
    VocabularyFormFieldComponent,
    CallAnnotationListComponent,
    AnnotatedCallDisplayPipe,
  ],
})
export class CallsModule {}
