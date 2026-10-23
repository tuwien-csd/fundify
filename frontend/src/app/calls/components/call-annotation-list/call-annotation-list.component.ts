import {
  AfterViewInit,
  Component,
  computed,
  inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { CALL_ANNOTATION_LIST_CONSTANTS } from '../../calls.constants';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource,
} from '@angular/material/table';
import { Call } from '../../models/call.interface';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { combineLatest, Observable, Subject } from 'rxjs';
import { Store } from '@ngrx/store';
import { takeUntil } from 'rxjs/operators';
import * as fromCalls from '../../store';
import { BUTTON_LABELS } from '../../../shared/shared.constants';
import { ROUTER_LINKS } from '../../../core/router-links.constants';
import { PublicationStatusEnum } from '../../../shared/models/enums/publication-status.enum';
import { TranslatedText } from '../../../shared/models/interfaces/translated-text.interface';
import { AnnotatedCall } from '../../models/annotated-call.interface';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import { AustrianStateEnum } from '../../../shared/models/enums/austrian-state.enum';
import { RegionalScopeEnum } from '../../../shared/models/enums/regional-scope.enum';
import { AuthService } from '../../../core/auth/services/auth.service';
import { TopNavbarComponent } from '../../../shared/ui/top-navbar.component';
import { MatCard, MatCardContent } from '@angular/material/card';
import {
  MatFormField,
  MatLabel,
  MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { AnnotatedCallDisplayPipe } from '../../pipes/annotated-call-display.pipe';
import { FundersStore } from '../../../funders/signal/funders-store';
import { FundifyUser } from '../../models/fundify-user.interface';

export type PublicationStatus = 'Upcoming' | 'Running' | 'Closed' | 'Draft';

export type CallAnnotationListViewElement = {
  callId: string;
  callStatus: PublicationStatusEnum;
  callScope: RegionalScopeEnum;
  callRegions: AustrianStateEnum[];
  callStartDate: string;
  callEndDate: string;
  callFunderId: string;
  annotatedCallId: string;
  registrationDate: string;
  lastSync: string;
  name: TranslatedText[];
  partOf: TranslatedText[];
  funder: TranslatedText[];
  status: PublicationStatusEnum | undefined;
  lastUpdatedAt: string;
  lastUpdatedBy: FundifyUser;
};

@Component({
  selector: 'app-call-annotation-list',
  templateUrl: './call-annotation-list.component.html',
  styleUrls: ['./call-annotation-list.component.scss'],
  imports: [
    TopNavbarComponent,
    MatCard,
    MatCardContent,
    FormsModule,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatIcon,
    MatSuffix,
    MatButton,
    MatSlideToggle,
    MatSelect,
    MatOption,
    MatIconButton,
    MatTable,
    MatSort,
    MatColumnDef,
    MatHeaderCellDef,
    MatHeaderCell,
    MatSortHeader,
    MatCellDef,
    MatCell,
    MatMenuTrigger,
    MatMenu,
    MatMenuItem,
    RouterLink,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatPaginator,
    AnnotatedCallDisplayPipe,
    MatProgressSpinner,
  ],
})
export class CallAnnotationListComponent
  implements OnInit, OnDestroy, AfterViewInit
{
  private store = inject(Store);
  private fundersStore = inject(FundersStore);
  private fb = inject(FormBuilder);

  protected isLoading = this.store.selectSignal(
    fromCalls.selectCallAnnotationListLoading
  );

  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly ROUTER_LINKS = ROUTER_LINKS;
  protected readonly CALL_ANNOTATION_LIST_CONSTANTS =
    CALL_ANNOTATION_LIST_CONSTANTS;
  protected readonly ANNOTATIONS_TABS = [
    { routerLink: '/annotations/overview/calls', label: 'Calls' },
  ];

  authService = inject(AuthService);

  filtersForm: FormGroup;
  filterOptions = [
    { value: 'callStatus', display: 'Call Status' },
    { value: 'callRegions', display: 'Region' },
    { value: 'callScope', display: 'Scope' },
    { value: 'callFunderId', display: 'Funder' },
  ];

  calls$: Observable<Call[]>;
  annotatedCalls$: Observable<AnnotatedCall[]>;

  funderMap = computed<Map<string, string>>(() => {
    const map = new Map<string, string>();
    this.funders().map((funder) => {
      if (funder.acronym && funder.id) {
        map.set(funder.id, funder.acronym);
      }
    });
    return map;
  });
  funders = this.fundersStore.entities;

  dataSource: MatTableDataSource<CallAnnotationListViewElement> =
    new MatTableDataSource<CallAnnotationListViewElement>([]);
  displayedColumns: string[] = CALL_ANNOTATION_LIST_CONSTANTS.TABLE_COLUMNS.map(
    (column) => column.field
  );
  displayedColumnsWithMenu: string[] = [...this.displayedColumns, 'menu'];

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;

  private unsubscribe$ = new Subject<void>();

  constructor() {
    this.filtersForm = this.fb.group({
      searchText: [''],
      filters: this.fb.array([]),
      closedCallsToggle: [false],
    });
    this.calls$ = this.store.select(fromCalls.selectAllCalls);
    this.annotatedCalls$ = this.store.select(fromCalls.selectAllAnnotatedCalls);
  }

  ngOnInit(): void {
    this.fundersStore.fetchAll();
    this.funders = this.fundersStore.entities;
    this.loadData();
    this.setupFilters();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  onDelete(annotatedCallId: string, callId: string): void {
    this.store.dispatch(
      fromCalls.deleteCallAnnotation({ annotatedCallId, callId })
    );
  }

  addFilter(): void {
    const filterGroup = this.fb.group({
      property: [''],
      values: [[]],
    });
    this.filters.push(filterGroup);
  }

  removeFilter(index: number): void {
    this.filters.removeAt(index);
  }

  clearFilters(): void {
    this.filters.clear();
    this.filters.reset();
  }

  applyFilters(): void {
    const filterConditions = this.filtersForm.value;
    this.dataSource.filter = JSON.stringify(filterConditions);
  }

  getValues(index: number): string[] {
    const property = this.filters.at(index).get('property')?.value;
    switch (property) {
      case 'callStatus':
        return ['Upcoming', 'Closed', 'Running'] as PublicationStatus[];
      case 'callRegions':
        return Object.values(AustrianStateEnum);
      case 'callScope':
        return Object.values(RegionalScopeEnum);
      case 'callFunderId':
        return this.funders().map((f) => f.acronym ?? '');
      default:
        return [];
    }
  }

  onPropertyChange(selectedProperty: string, index: number): void {
    const valuesControl = this.filters.at(index).get('values');
    valuesControl?.setValue([]);
  }

  private loadData(): void {
    this.store.dispatch(fromCalls.loadCalls({ skipIfPresent: false }));
    this.loadAnnotatedCalls();
  }

  private loadAnnotatedCalls(): void {
    this.store.dispatch(fromCalls.loadAnnotatedCalls({ skipIfPresent: false }));
  }

  private setupFilters(): void {
    this.initMatDataSource();
    this.filtersForm.valueChanges
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(() => this.applyFilters());
  }

  private initMatDataSource(): void {
    this.dataSource.filterPredicate = this.filterPredicate.bind(this);
    this.dataSource.sortingDataAccessor = (item, property) =>
      this.sortingDataAccessor(item, property);
    this.initDefaultSorting();
    combineLatest([this.calls$, this.annotatedCalls$])
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(([calls, annotatedCalls]) => {
        this.dataSource.data = this.mergeToListViewElements(
          calls,
          annotatedCalls
        );
      });
  }

  // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  private sortingDataAccessor(item: any, property: string): string {
    const accessors: { [key: string]: () => string } = {
      name: () => this.joinPropertyTexts(item.name),
      funder: () => this.joinPropertyTexts(item.funder),
      partOf: () => this.joinPropertyTexts(item.partOf),
      callStatus: () => this.deriveCallStatus(item),
    };

    return (
      accessors[property]?.() ??
      (item[property as keyof typeof item] as string) ??
      ''
    );
  }

  private joinPropertyTexts(prop: Array<{ text: string }> | undefined): string {
    return prop?.map((name) => name.text).join('') ?? '';
  }

  private initDefaultSorting(): void {
    this.sort.active = 'registrationDate';
    this.sort.direction = 'desc';
  }

  private mergeToListViewElements(
    calls: Call[],
    annotatedCalls: AnnotatedCall[]
  ): CallAnnotationListViewElement[] {
    return calls
      .filter((call) => call.status !== PublicationStatusEnum.DRAFT)
      .map((call) => {
        const annotatedCall = annotatedCalls.find(
          (aCall) => aCall.callPreview?.id === call.id
        );
        return {
          callId: call.id,
          callScope: call.eligibleApplicantsScope,
          callRegions: call.eligibleApplicantsRegions ?? [],
          callStatus: call.status,
          callStartDate: call.callStages?.[0].duration?.start,
          callEndDate:
            call.callStages?.[call.callStages.length - 1]?.duration?.end,
          callFunderId: call.funder?.id,
          annotatedCallId: annotatedCall?.id ?? '',
          registrationDate: call.registrationDate,
          lastSync: call.lastSync,
          name: call.name ?? [],
          partOf: call.partOf?.name ?? [],
          funder: call.funder?.name ?? [],
          status: annotatedCall?.status,
          lastUpdatedAt: annotatedCall?.lastUpdatedAt ?? '',
          lastUpdatedBy: annotatedCall?.lastUpdatedBy,
        } as CallAnnotationListViewElement;
      });
  }

  private filterPredicate(
    data: CallAnnotationListViewElement,
    filter: string
  ): boolean {
    const filterConditions = JSON.parse(filter);
    return (
      this.matchesClosedCallToggle(data, filterConditions.closedCallsToggle) &&
      this.matchesSearchText(data, filterConditions.searchText) &&
      this.matchesDropdownFilters(data, filterConditions.filters)
    );
  }

  private matchesClosedCallToggle(
    data: CallAnnotationListViewElement,
    hideClosedCalls: boolean
  ) {
    if (!hideClosedCalls) return true;
    const callStatus = this.deriveCallStatus(data);
    return callStatus !== 'Closed';
  }

  private matchesSearchText(
    data: CallAnnotationListViewElement,
    searchText: string
  ): boolean {
    const combinedFields = this.combineCallFields(data).toLowerCase();
    return combinedFields.includes(searchText.toLowerCase());
  }

  private matchesDropdownFilters(
    data: CallAnnotationListViewElement,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
    filters: any[]
  ): boolean {
    return filters.every((f) => this.evaluateFilterCondition(data, f));
  }

  private evaluateFilterCondition(
    data: CallAnnotationListViewElement,
    // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
    filter: any
  ): boolean {
    if (!filter.values || filter.values.length === 0) return true;
    switch (filter.property) {
      case 'callStatus':
        return this.evaluateCallStatusFilter(data, filter.values);
      case 'callFunderId':
        return filter.values.includes(this.funderMap().get(data.callFunderId));
      case 'callRegions':
        return (
          data.callRegions.length === 0 ||
          filter.values.some((val: AustrianStateEnum) =>
            data.callRegions.includes(val)
          )
        );
      case 'callScope':
        return filter.values.includes(data.callScope);
      default:
        return true;
    }
  }

  private evaluateCallStatusFilter(
    data: CallAnnotationListViewElement,
    statuses: string[]
  ): boolean {
    const publicationStatus = this.deriveCallStatus(data);
    return statuses.some((filterStatus) => filterStatus === publicationStatus);
  }

  private deriveCallStatus(
    data: CallAnnotationListViewElement
  ): PublicationStatus {
    const now = new Date();
    const startDate = data.callStartDate
      ? new Date(data.callStartDate)
      : new Date(0);
    const endDate = data.callEndDate
      ? new Date(data.callEndDate)
      : new Date(2100, 1, 1);

    if (startDate > now) {
      return 'Upcoming';
    }
    return endDate < now ? 'Closed' : 'Running';
  }

  private combineCallFields(
    callListItem: CallAnnotationListViewElement
  ): string {
    return `
      ${callListItem.name.map((name) => name.text).join('')}
      ${callListItem.partOf.map((part) => part.text).join('')}
      ${callListItem.funder.map((funder) => funder.text).join('')}
    `;
  }

  get filters(): FormArray {
    return this.filtersForm.get('filters') as FormArray;
  }
}
