import {
  AfterViewInit,
  Component,
  computed,
  inject,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FunderWebModel } from './models/funder.interface';
import { FUNDERS_CONSTANTS } from './funders.constants';
import { ROUTER_LINKS } from '../core/router-links.constants';
import { BUTTON_LABELS } from '../shared/shared.constants';
import { MatSort, MatSortHeader } from '@angular/material/sort';
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
import { MatPaginator } from '@angular/material/paginator';
import { PermissionService } from '../core/auth/services/permission.service';
import { EntryOriginEnum } from '../shared/models/enums/entry-origin.enum';
import { PublicationStatusEnum } from '../shared/models/enums/publication-status.enum';
import { TabInfo, TopNavbarComponent } from '../shared/ui/top-navbar.component';
import { MatCard, MatCardContent } from '@angular/material/card';
import {
  MatFormField,
  MatLabel,
  MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { FunderDisplayPipe } from './pipes/funder-display.pipe';
import { CALLS_CONSTANTS } from '../calls/calls.constants';
import { AuthService } from '../core/auth/services/auth.service';
import { FundersStore } from './signal/funders-store';

@Component({
  selector: 'app-funders',
  templateUrl: './funders.component.html',
  styleUrls: ['./funders.component.scss'],
  imports: [
    TopNavbarComponent,
    MatCard,
    MatCardContent,
    MatFormField,
    MatLabel,
    MatInput,
    MatIcon,
    MatSuffix,
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
    RouterLink,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatPaginator,
    FunderDisplayPipe,
    MatButton,
  ],
})
export class FundersComponent implements OnInit, AfterViewInit {
  permissionService = inject(PermissionService);
  authService = inject(AuthService);
  fundersStore = inject(FundersStore);

  protected readonly FUNDERS_CONSTANTS = FUNDERS_CONSTANTS;
  protected readonly ROUTER_LINKS = ROUTER_LINKS;
  protected readonly BUTTON_LABELS = BUTTON_LABELS;
  protected readonly EntryOriginEnum = EntryOriginEnum;
  protected readonly PublicationStatusEnum = PublicationStatusEnum;
  protected readonly INSTITUTIONS_TABS: TabInfo[] = [
    {
      routerLink: '/' + ROUTER_LINKS.INSTITUTIONS + '/' + ROUTER_LINKS.FUNDERS,
      label: 'Funders',
    },
    {
      routerLink:
        '/' + ROUTER_LINKS.INSTITUTIONS + '/' + ROUTER_LINKS.UNIVERSITIES,
      label: 'Universities',
    },
  ];
  dataSource = computed(() => {
    const dataSource = new MatTableDataSource<FunderWebModel>(
      this.fundersStore.entities()
    );
    dataSource.filterPredicate = this.createFilterPredicate();
    return dataSource;
  });

  displayedColumns: string[] = this.FUNDERS_CONSTANTS.TABLE_COLUMNS.map(
    (column) => column.field
  );
  displayedColumnsWithMenu: string[] = [...this.displayedColumns, 'menu'];

  @ViewChild(MatSort, { static: true }) sort!: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;

  ngOnInit(): void {
    this.fundersStore.fetchAll();
  }

  ngAfterViewInit(): void {
    this.dataSource().sort = this.sort;
    this.dataSource().paginator = this.paginator;
  }

  applyFilter(event: Event): void {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.dataSource().filter = filterValue.trim().toLowerCase();
  }

  onDelete(funderId: string): void {
    this.fundersStore.deleteById(funderId);
  }

  private createFilterPredicate(): (
    data: FunderWebModel,
    filter: string
  ) => boolean {
    return (data, filter) => {
      const combinedDataStr = this.combineFunderFields(data);
      return combinedDataStr.trim().toLowerCase().includes(filter);
    };
  }

  private combineFunderFields(funder: FunderWebModel): string {
    const names: string[] = [];
    funder.name?.forEach((name) => names.push(name.text));
    return names.join('') + funder.website + funder.acronym;
  }

  protected readonly CALL_CONSTANTS = CALLS_CONSTANTS;
}
