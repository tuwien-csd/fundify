import {
  AfterViewInit,
  Component,
  inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
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
import { PROGRAMS_CONSTANTS } from './programs.constants';
import { ROUTER_LINKS } from '../core/router-links.constants';
import { Program } from './models/program.interface';
import { takeUntil } from 'rxjs/operators';
import { BUTTON_LABELS } from '../shared/shared.constants';
import { PermissionService } from '../core/auth/services/permission.service';
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { Subject } from 'rxjs';
import { Store } from '@ngrx/store';
import * as fromPrograms from './store';
import { UserRoleEnum } from '../core/auth/models/user-role.enum';
import { TabInfo, TopNavbarComponent } from '../shared/ui/top-navbar.component';
import {
  MatSlideToggle,
  MatSlideToggleChange,
} from '@angular/material/slide-toggle';
import { Call } from '../calls/models/call.interface';
import { PublicationStatusEnum } from '../shared/models/enums/publication-status.enum';
import { PublicationStatus } from '../calls/components/call-annotation-list/call-annotation-list.component';
import { AuthService } from '../core/auth/services/auth.service';
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
import { ProgramDisplayPipe } from './pipes/program-display.pipe';

@Component({
  selector: 'app-programs',
  templateUrl: './programs.component.html',
  styleUrls: ['./programs.component.scss'],
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
    ProgramDisplayPipe,
  ],
})
export class ProgramsComponent implements OnInit, OnDestroy, AfterViewInit {
  private store = inject(Store);
  permissionService = inject(PermissionService);

  protected readonly ROUTER_LINKS = ROUTER_LINKS;
  protected readonly PROGRAMS_CONSTANTS = PROGRAMS_CONSTANTS;
  protected readonly UserRoleEnum = UserRoleEnum;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly FUNDING_TABS: TabInfo[] = [
    {
      routerLink: '/fundings/calls',
      label: 'Calls',
    },
    {
      routerLink: '/fundings/programs',
      label: 'Programs',
    },
  ];

  authService = inject(AuthService);

  dataSource = new MatTableDataSource<Program>();
  hideClosedPrograms = false;
  filterValue = '';

  displayedColumns: string[] = this.PROGRAMS_CONSTANTS.TABLE_COLUMNS.map(
    (column) => column.field
  );
  displayedColumnsWithMenu: string[] = [...this.displayedColumns, 'menu'];

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;

  private unsubscribe$ = new Subject<void>();

  ngOnInit(): void {
    this.store.dispatch(fromPrograms.loadPrograms({ skipIfPresent: false }));
    this.initMatDataSource();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  changeFilterValue(event: Event) {
    this.filterValue = (event.target as HTMLInputElement).value
      .trim()
      .toLowerCase();
    this.applyFilters();
  }

  onDelete(programId: string): void {
    this.store.dispatch(fromPrograms.deleteProgram({ programId }));
  }

  toggleHideClosedPrograms(event: MatSlideToggleChange) {
    this.hideClosedPrograms = event.checked;
    this.applyFilters();
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  private initMatDataSource(): void {
    this.dataSource.filterPredicate = this.createFilterPredicate();
    this.dataSource.sortingDataAccessor = (item, property) => {
      if (property === 'name') {
        return item.name?.map((name) => name.text).join('') ?? 0;
      }
      return (item[property as keyof typeof item] as string) ?? 0;
    };
    this.sort.active = 'status';
    this.sort.direction = 'asc';
    this.store
      .select(fromPrograms.selectAllPrograms)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe((programs) => (this.dataSource.data = programs));
  }

  private applyFilters() {
    this.dataSource.filter = JSON.stringify({
      searchTerm: this.filterValue,
      hideClosedCalls: this.hideClosedPrograms,
    });
  }

  private createFilterPredicate(): (data: Call, filter: string) => boolean {
    return (data, filter) => {
      const filterObject = JSON.parse(filter);
      const searchTerm = filterObject.searchTerm;
      const hideClosedPrograms = filterObject.hideClosedCalls;

      const combinedDataStr = this.combineProgramFields(data);
      const matchesSearchTerm = combinedDataStr
        .trim()
        .toLowerCase()
        .includes(searchTerm);

      if (hideClosedPrograms) {
        const programStatus = this.getProgramStatus(data);
        return matchesSearchTerm && programStatus !== 'Closed';
      }

      return matchesSearchTerm;
    };
  }

  private getProgramStatus(program: Program): PublicationStatus {
    if (program.status === PublicationStatusEnum.DRAFT) {
      return 'Draft';
    }

    const now = new Date();
    const startDate = program.duration?.start ?? new Date(0);
    const endDate = program.duration?.end ?? new Date(2100, 1, 1);

    if (startDate > now) {
      return 'Upcoming';
    } else if (startDate <= now && endDate >= now) {
      return 'Running';
    } else {
      return 'Closed';
    }
  }

  private combineProgramFields(program: Program): string {
    const names: string[] = [];
    const funderNames: string[] = [];
    program.name?.forEach((name) => names.push(name.text));
    program.funder?.name?.forEach((name) => funderNames.push(name.text));
    return names.join('') + funderNames.join('') + program.acronym;
  }
}
