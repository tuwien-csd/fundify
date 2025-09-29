import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FundersComponent } from './funders.component';
import { SharedModule } from '../shared/shared.module';
import { FunderDisplayPipe } from './pipes/funder-display.pipe';
import { FunderDetailEditComponent } from './components/funder-detail-edit/funder-detail-edit.component';
import { FunderDetailComponent } from './components/funder-detail/funder-detail.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    FundersComponent,
    FunderDisplayPipe,
    FunderDetailEditComponent,
    FunderDetailComponent,
  ],
})
export class FundersModule {}
