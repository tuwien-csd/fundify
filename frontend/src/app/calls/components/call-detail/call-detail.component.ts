import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ViewEnum } from 'src/app/shared/models/enums/view.enum';
import { Call } from '../../models/call.interface';
import { ROUTER_LINKS } from 'src/app/core/router-links.constants';
import { Observable, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { select, Store } from '@ngrx/store';
import {
  LOCAL_STORAGE_KEYS,
  LocalStorageService,
} from '../../../core/services/local-storage.service';
import * as fromCalls from '../../store';
import * as fromRoute from '../../../store/selectors/router.selectors';
import { AppState } from '../../../store';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';
import { AuthService } from '../../../core/auth/services/auth.service';
import { CallDetailEditComponent } from '../call-detail-edit/call-detail-edit.component';
import { CallDetailPreviewComponent } from '../call-detail-preview/call-detail-preview.component';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-call-detail',
  templateUrl: './call-detail.component.html',
  imports: [
    CallDetailEditComponent,
    CallDetailPreviewComponent,
    MatProgressSpinner,
    AsyncPipe,
  ],
})
export class CallDetailComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private localStorageService = inject(LocalStorageService);
  private router = inject(Router);
  private store = inject<Store<AppState>>(Store);

  authService = inject(AuthService);

  callId!: string;
  isCallInStore$!: Observable<boolean>;
  call$!: Observable<Call | undefined>;
  activeView$!: Observable<string | null>;

  ngOnInit(): void {
    this.callId = this.route.snapshot.params['id'];
    this.activeView$ = this.store.pipe(select(fromRoute.selectPageMode));
    this.isCallInStore$ = this.store.pipe(
      select(fromCalls.callEntityExists(this.callId))
    );
    this.call$ = this.fetchOrCreateCall();
  }

  ngOnDestroy(): void {
    this.localStorageService.remove(LOCAL_STORAGE_KEYS.STAGED_CALL_CHANGES);
  }

  async goBack(): Promise<void> {
    await this.router.navigate([ROUTER_LINKS.FUNDINGS]);
  }

  async saveAsDraft(call: Call): Promise<void> {
    call.status = PublicationStatusEnum.DRAFT;
    if (!call.id) {
      this.store.dispatch(fromCalls.addCall({ call }));
    } else {
      this.store.dispatch(fromCalls.upsertCall({ call }));
    }
    await this.goBack();
  }

  async publish(call: Call): Promise<void> {
    const updatedCall = {
      ...call,
      status: PublicationStatusEnum.PUBLISHED,
    };
    if (!call.id) {
      this.store.dispatch(fromCalls.addCall({ call: updatedCall }));
    } else {
      this.store.dispatch(fromCalls.upsertCall({ call: updatedCall }));
    }
    await this.goBack();
  }

  async goTo(view: ViewEnum): Promise<void> {
    await this.router.navigate([view], { relativeTo: this.route });
  }

  private fetchOrCreateCall(): Observable<Call | undefined> {
    if (!this.callId) return of({} as Call);

    return this.isCallInStore$.pipe(
      switchMap((isInStore) => {
        if (!isInStore) {
          this.store.dispatch(fromCalls.loadCall({ id: this.callId }));
        }
        return this.store.pipe(select(fromCalls.selectCallById(this.callId)));
      })
    );
  }
}
