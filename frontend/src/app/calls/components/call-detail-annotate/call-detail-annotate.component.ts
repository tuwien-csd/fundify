import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { AnnotatedCall } from '../../models/annotated-call.interface';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AppState } from '../../../store';
import { select, Store } from '@ngrx/store';
import * as fromCalls from '../../store';
import { switchMap, take } from 'rxjs/operators';
import {
  Vocabulary,
  VocabularyEntryEvent,
} from '../../models/vocabulary.interface';
import { VocabularyTypeEnum } from '../../models/vocabulary-type.enum';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmCancelModalComponent } from '../confirm-cancel-modal/confirm-cancel-modal.component';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';
import { WebLink } from '../../models/web-link.interface';
import { ROUTER_LINKS } from '../../../core/router-links.constants';
import {
  BUTTON_LABELS,
  TRANSLATED_TEXTS_LABELS,
} from '../../../shared/shared.constants';
import { AuthService } from '../../../core/auth/services/auth.service';
import {
  MatCard,
  MatCardActions,
  MatCardContent,
  MatCardHeader,
  MatCardTitle,
} from '@angular/material/card';
import { MatChipListbox, MatChipOption } from '@angular/material/chips';
import { TranslatedTextFieldComponent } from '../../../shared/components/translated-text-field/translated-text-field.component';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { MatButton } from '@angular/material/button';
import { VocabularyFormFieldComponent } from '../vocabulary-form-field/vocabulary-form-field.component';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { AsyncPipe, DatePipe } from '@angular/common';

@Component({
  selector: 'app-call-detail-annotate',
  templateUrl: './call-detail-annotate.component.html',
  styleUrls: ['./call-detail-annotate.component.scss'],
  imports: [
    MatCard,
    MatCardHeader,
    RouterLink,
    MatCardTitle,
    MatCardContent,
    MatChipListbox,
    MatChipOption,
    FormsModule,
    ReactiveFormsModule,
    TranslatedTextFieldComponent,
    MatFormField,
    MatLabel,
    MatInput,
    CdkTextareaAutosize,
    MatButton,
    VocabularyFormFieldComponent,
    MatCardActions,
    MatIcon,
    MatProgressSpinner,
    AsyncPipe,
    DatePipe,
  ],
})
export class CallDetailAnnotateComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private store = inject<Store<AppState>>(Store);
  dialog = inject(MatDialog);
  private fb = inject(FormBuilder);

  protected readonly PublicationStatusEnum = PublicationStatusEnum;

  private authService = inject(AuthService);

  callId!: string;
  userAffiliationId = this.authService.userAffiliationId;
  isAnnotatedCallInStore$!: Observable<boolean>;
  annotatedCall$!: Observable<AnnotatedCall | undefined>;
  keywordVocabulary$!: Observable<Vocabulary | undefined>;
  targetGroupVocabulary$!: Observable<Vocabulary | undefined>;
  destroy$ = new Subject<void>();

  annotationForm!: FormGroup;

  constructor() {
    this.annotationForm = this.fb.group({
      internalDeadline: [''],
      keywords: [''],
      targetGroups: [''],
      links: this.fb.array([]),
      contact: this.fb.group({
        name: [''],
        email: [''],
        phone: [''],
        website: [''],
        department: [''],
      }),
    });
  }

  ngOnInit(): void {
    this.callId = this.route.snapshot.params['id'];
    this.isAnnotatedCallInStore$ = this.store.select(
      fromCalls.annotatedCallEntityExists(this.callId)
    );
    this.annotatedCall$ = this.fetchOrCreateAnnotatedCall();
    this.loadVocabularies();
    this.keywordVocabulary$ = this.store.select(
      fromCalls.selectVocabulary(VocabularyTypeEnum.KEYWORD)
    );
    this.targetGroupVocabulary$ = this.store.select(
      fromCalls.selectVocabulary(VocabularyTypeEnum.TARGETGROUP)
    );
    this.initForm();
  }

  initForm() {
    this.annotatedCall$.pipe(take(1)).subscribe(
      (annotatedCall) =>
        (this.annotationForm = this.fb.group({
          internalDeadline: [annotatedCall?.annotation?.internalDeadline],
          keywords: [annotatedCall?.annotation?.keywords],
          targetGroups: [annotatedCall?.annotation?.targetGroups],
          links: this.initWebLinksFormArray(
            annotatedCall?.annotation?.links ?? []
          ),
          contact: this.fb.group({
            name: [annotatedCall?.annotation?.contact?.name],
            email: [annotatedCall?.annotation?.contact?.email],
            phone: [annotatedCall?.annotation?.contact?.phone],
            website: [annotatedCall?.annotation?.contact?.website],
            department: [annotatedCall?.annotation?.contact?.department],
          }),
        }))
    );
  }

  addWebLink(): void {
    this.links.push(this.createWebLinkFormGroup());
  }

  removeWebLink(index: number): void {
    this.links.removeAt(index);
  }

  onAddEntry(event: VocabularyEntryEvent): void {
    this.store.dispatch(
      fromCalls.addVocabularyEntry({
        vocabularyId: event.id,
        entry: event.entry,
      })
    );
  }

  onRemoveEntry(event: VocabularyEntryEvent): void {
    this.store.dispatch(
      fromCalls.removeVocabularyEntry({
        vocabularyId: event.id,
        entry: event.entry,
      })
    );
  }

  save(): void {
    this.annotatedCall$.pipe(take(1)).subscribe((annotatedCall) => {
      this.store.dispatch(
        fromCalls.annotateCall({
          annotatedCall: {
            ...annotatedCall,
            annotation: this.annotationForm.value,
          },
        })
      );
    });
    this.router.navigate([
      ROUTER_LINKS.ANNOTATIONS + '/overview/' + ROUTER_LINKS.CALLS,
    ]);
  }

  publish(): void {
    this.annotatedCall$.pipe(take(1)).subscribe((annotatedCall) => {
      this.store.dispatch(
        fromCalls.publishAnnotatedCall({
          annotatedCall: {
            ...annotatedCall,
          },
        })
      );
    });
    this.router.navigate([
      ROUTER_LINKS.ANNOTATIONS + '/overview/' + ROUTER_LINKS.CALLS,
    ]);
  }

  saveAndPublish(): void {
    this.annotatedCall$.pipe(take(1)).subscribe((annotatedCall) => {
      this.store.dispatch(
        fromCalls.saveAndPublishAnnotatedCall({
          annotatedCall: {
            ...annotatedCall,
            annotation: this.annotationForm.value,
          },
        })
      );
    });
    this.router.navigate([
      ROUTER_LINKS.ANNOTATIONS + '/overview/' + ROUTER_LINKS.CALLS,
    ]);
  }

  cancel() {
    if (this.annotationForm.touched) {
      const dialogRef = this.dialog.open(ConfirmCancelModalComponent, {
        width: '400px',
      });

      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.router.navigate([
            ROUTER_LINKS.ANNOTATIONS + '/overview/' + ROUTER_LINKS.CALLS,
          ]);
        }
      });
    } else {
      this.router.navigate([
        ROUTER_LINKS.ANNOTATIONS + '/overview/' + ROUTER_LINKS.CALLS,
      ]);
    }
  }

  initWebLinksFormArray(webLinks: WebLink[]): FormArray {
    if (!webLinks || webLinks.length === 0) {
      return this.fb.array([this.createWebLinkFormGroup()]);
    }
    return this.fb.array(
      webLinks.map((webLink) => this.createWebLinkFormGroup(webLink))
    );
  }

  createWebLinkFormGroup(link = { url: '', description: '' }) {
    return this.fb.group({
      url: [link.url],
      description: [link.description],
    });
  }

  get links(): FormArray {
    return this.annotationForm.get('links') as FormArray;
  }

  ngOnDestroy(): void {
    this.store.dispatch(fromCalls.clearAnnotatedCall());
    this.store.dispatch(fromCalls.clearVocabularies());
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadVocabularies() {
    const universityId = this.userAffiliationId();
    if (universityId) {
      this.store.dispatch(fromCalls.loadVocabularies());
    }
  }

  private fetchOrCreateAnnotatedCall(): Observable<AnnotatedCall | undefined> {
    return this.isAnnotatedCallInStore$.pipe(
      switchMap((isInStore) => {
        if (!isInStore) {
          this.store.dispatch(
            fromCalls.loadAnnotatedCall({
              callId: this.callId,
              universityId: this.userAffiliationId() ?? '',
            })
          );
        }
        return this.store.pipe(
          select(fromCalls.selectAnnotatedCallByCallId(this.callId))
        );
      })
    );
  }

  protected readonly constants = TRANSLATED_TEXTS_LABELS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
}
