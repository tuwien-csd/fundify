import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ViewEnum } from 'src/app/shared/models/enums/view.enum';
import { Program } from '../../models/program.interface';
import { ROUTER_LINKS } from 'src/app/core/router-links.constants';
import { Observable, of } from 'rxjs';
import { select, Store } from '@ngrx/store';
import * as fromRoute from '../../../store/selectors/router.selectors';
import { switchMap } from 'rxjs/operators';
import * as fromPrograms from '../../store';
import {
  LOCAL_STORAGE_KEYS,
  LocalStorageService,
} from '../../../core/services/local-storage.service';
import { AppState } from '../../../store';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';
import { FundingEntityRef } from '../../../shared/models/interfaces/funding-entity-ref.interface';
import { AuthService } from '../../../core/auth/services/auth.service';
import { ProgramDetailEditComponent } from '../program-detail-edit/program-detail-edit.component';
import { ProgramDetailPreviewComponent } from '../program-detail-preview/program-detail-preview.component';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-program',
  templateUrl: './program-detail.component.html',
  imports: [
    ProgramDetailEditComponent,
    ProgramDetailPreviewComponent,
    MatProgressSpinner,
    AsyncPipe,
  ],
})
export class ProgramDetailComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private localStorageService = inject(LocalStorageService);
  private router = inject(Router);
  private store = inject<Store<AppState>>(Store);

  authService = inject(AuthService);

  programId!: string;
  isProgramInStore$!: Observable<boolean>;
  program$!: Observable<Program | undefined>;
  activeView$!: Observable<string | null>;

  ngOnInit(): void {
    this.programId = this.route.snapshot.params['id'];
    this.activeView$ = this.store.pipe(select(fromRoute.selectPageMode));
    this.isProgramInStore$ = this.store.pipe(
      select(fromPrograms.entityExists(this.programId))
    );
    this.program$ = this.fetchOrCreateProgram();
  }

  ngOnDestroy(): void {
    this.localStorageService.remove(LOCAL_STORAGE_KEYS.STAGED_PROGRAM_CHANGES);
  }

  async goBack(): Promise<void> {
    await this.router.navigate([ROUTER_LINKS.FUNDINGS, ROUTER_LINKS.PROGRAMS]);
  }

  async saveAsDraft(program: Program): Promise<void> {
    program.status = PublicationStatusEnum.DRAFT;
    if (!program.id) {
      this.store.dispatch(fromPrograms.addProgram({ program }));
    } else {
      this.store.dispatch(fromPrograms.upsertProgram({ program }));
    }
    await this.goBack();
  }

  async publish(program: Program): Promise<void> {
    program.status = PublicationStatusEnum.PUBLISHED;
    if (!program.id) {
      this.store.dispatch(fromPrograms.addProgram({ program }));
    } else {
      this.store.dispatch(fromPrograms.upsertProgram({ program }));
    }
    await this.goBack();
  }

  async goTo(view: ViewEnum): Promise<void> {
    await this.router.navigate([view], { relativeTo: this.route });
  }

  private fetchOrCreateProgram(): Observable<Program | undefined> {
    if (!this.programId) {
      return of({
        funder: {
          id: this.authService.userAffiliationId(),
        } as FundingEntityRef,
      } as Program);
    } else {
      return this.isProgramInStore$.pipe(
        switchMap((isInStore) => {
          if (!isInStore) {
            this.store.dispatch(
              fromPrograms.loadProgram({ id: this.programId })
            );
          }
          return this.store.pipe(
            select(fromPrograms.selectProgramById(this.programId))
          );
        })
      );
    }
  }
}
