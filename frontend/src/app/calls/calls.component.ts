import { Component, computed, inject, ViewChild } from '@angular/core';
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
import { CallWebModel } from './models/call.interface';
import { CALLS_CONSTANTS } from './calls.constants';
import { ROUTER_LINKS } from '../core/router-links.constants';
import { BUTTON_LABELS } from '../shared/shared.constants';
import { PermissionService } from '../core/auth/services/permission.service';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { UserRoleEnum } from '../core/auth/models/user-role.enum';
import {
  MatSlideToggle,
  MatSlideToggleChange,
} from '@angular/material/slide-toggle';
import { PublicationStatusEnum } from '../shared/models/enums/publication-status.enum';
import { DateInfoRange } from '../shared/models/interfaces/date-info-range.interface';
import { PublicationStatus } from './components/call-annotation-list/call-annotation-list.component';
import { AuthService } from '../core/auth/services/auth.service';
import { TopNavbarComponent } from '../shared/ui/top-navbar.component';
import { MatCard, MatCardContent } from '@angular/material/card';
import {
  MatFormField,
  MatLabel,
  MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { CallDisplayPipe } from './pipes/call-display.pipe';
import { CallsStore } from './signal/calls-store';
import { MatDialog } from '@angular/material/dialog';
import { CallVersionHistoryDialogComponent, CallVersionHistoryDialogData } from './components/call-version-history-dialog/call-version-history-dialog.component';

@Component({
  selector: 'app-calls',
  templateUrl: './calls.component.html',
  styleUrls: ['./calls.component.scss'],
  imports: [
    TopNavbarComponent,
    MatCard,
    MatCardContent,
    MatFormField,
    MatLabel,
    MatInput,
    MatIcon,
    MatSuffix,
    MatSlideToggle,
    MatButton,
    RouterLink,
    MatTable,
    MatSort,
    MatColumnDef,
    MatHeaderCellDef,
    MatHeaderCell,
    MatSortHeader,
    MatCellDef,
    MatCell,
    MatIconButton,
    MatMenuTrigger,
    MatMenu,
    MatMenuItem,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatPaginator,
    CallDisplayPipe,
  ],
})
export class CallsComponent {
  private callsStore = inject(CallsStore);
  private dialog = inject(MatDialog);

  protected readonly CALL_CONSTANTS = CALLS_CONSTANTS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly ROUTER_LINKS = ROUTER_LINKS;
  protected readonly UserRoleEnum = UserRoleEnum;
  protected readonly FUNDING_TABS = [
    {
      routerLink: '/' + ROUTER_LINKS.FUNDINGS + '/' + ROUTER_LINKS.CALLS,
      label: 'Calls',
    },
    {
      routerLink: '/' + ROUTER_LINKS.FUNDINGS + '/' + ROUTER_LINKS.PROGRAMS,
      label: 'Programs',
    },
  ];
  authService = inject(AuthService);
  permissionService = inject(PermissionService);

  dataSource = computed(() => {
    const dataSource = new MatTableDataSource<CallWebModel>(
      this.callsStore.entities()
    );
    dataSource.filterPredicate = this.createFilterPredicate();
    dataSource.sort = this.sort;
    dataSource.paginator = this.paginator;
    dataSource.sortingDataAccessor = (item, property) => {
      if (property === 'name') {
        return item.name?.map((name) => name.text).join('') ?? 0;
      }
      if (property === 'status') {
        return this.getCallStatus(item);
      }
      return (item[property as keyof typeof item] as string) ?? 0;
    };
    dataSource.sort.active = 'status';
    dataSource.sort.direction = 'asc';

    return dataSource;
  });

  hideClosedCalls = false;
  filterValue = '';

  displayedColumns: string[] = this.CALL_CONSTANTS.TABLE_COLUMNS.map(
    (column) => column.field
  );
  displayedColumnsWithMenu: string[] = [...this.displayedColumns, 'menu'];

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;

  changeFilterValue(event: Event) {
    this.filterValue = (event.target as HTMLInputElement).value
      .trim()
      .toLowerCase();
    this.applyFilters();
  }

  toggleHideClosedCalls(event: MatSlideToggleChange) {
    this.hideClosedCalls = event.checked;
    this.applyFilters();
  }

  onDelete(callId: string): void {
    this.callsStore.deleteById(callId);
  }

  onSubscribe(callId: string): void {
    this.callsStore.changeSubscriptionById(callId, 'SUBSCRIBED');
  }

  onUnsubscribe(callId: string): void {
    this.callsStore.changeSubscriptionById(callId, 'UNSUBSCRIBED');
  }

  onShowHistory(call: CallWebModel): void {
    const data: CallVersionHistoryDialogData = {
      callId: call.id,
      currentFields: {
        name: call.name,
        description: call.description,
        eligibleApplicants: call.eligibleApplicants,
        callStages: call.callStages,
        callVolumeAmount: call.callVolumeAmount,
        website: call.website,
      },
    };
    this.dialog.open(CallVersionHistoryDialogComponent, { width: '680px', data });
  }

  private applyFilters() {
    this.dataSource().filter = JSON.stringify({
      searchTerm: this.filterValue,
      hideClosedCalls: this.hideClosedCalls,
    });
  }

  private createFilterPredicate(): (
    data: CallWebModel,
    filter: string
  ) => boolean {
    return (data, filter) => {
      const filterObject = JSON.parse(filter);
      const searchTerm = filterObject.searchTerm;
      const hideClosedCalls = filterObject.hideClosedCalls;

      const combinedDataStr = this.combineCallFields(data);
      const matchesSearchTerm = combinedDataStr
        .trim()
        .toLowerCase()
        .includes(searchTerm);

      if (hideClosedCalls) {
        const callStatus = this.getCallStatus(data);
        return matchesSearchTerm && callStatus !== 'Closed';
      }

      return matchesSearchTerm;
    };
  }

  private getCallStatus(call: CallWebModel): PublicationStatus {
    if (call.status === PublicationStatusEnum.DRAFT) {
      return 'Draft';
    }

    const now = new Date();
    const startDate = this.getCallStartDate(call?.callStages ?? []);
    const endDate = this.getCallEndDate(call?.callStages ?? []);

    if (startDate > now) {
      return 'Upcoming';
    } else if (startDate <= now && endDate >= now) {
      return 'Running';
    } else {
      return 'Closed';
    }
  }

  private getCallStartDate(callStages: DateInfoRange[]): Date {
    if (callStages.length === 0) return new Date(0);
    const firstStageStart = callStages[0].duration?.start;
    return firstStageStart ? new Date(firstStageStart) : new Date(0);
  }

  private getCallEndDate(callStages: DateInfoRange[]): Date {
    if (callStages.length === 0) return new Date(2100, 1, 1);
    const lastStageEnd = callStages[callStages.length - 1].duration?.end;
    return lastStageEnd ? new Date(lastStageEnd) : new Date(2100, 1, 1);
  }

  private combineCallFields(call: CallWebModel): string {
    const names: string[] = [];
    const funderNames: string[] = [];
    call.name?.forEach((name) => names.push(name.text));
    call.funder?.name?.forEach((name) => funderNames.push(name.text));
    return names.join('') + funderNames.join('') + call.acronym;
  }
}
