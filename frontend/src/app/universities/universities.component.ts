import { Component, computed, inject, OnInit, ViewChild } from '@angular/core';
import { PermissionService } from '../core/auth/services/permission.service';
import { AuthService } from '../core/auth/services/auth.service';
import { TabInfo, TopNavbarComponent } from '../shared/ui/top-navbar.component';
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
import { MatSort, MatSortHeader } from '@angular/material/sort';
import { MatIcon } from '@angular/material/icon';
import { MatPaginator } from '@angular/material/paginator';
import { UNIVERSITIES_CONSTANTS } from './universities.constants';
import { ROUTER_LINKS } from '../core/router-links.constants';
import { BUTTON_LABELS } from '../shared/shared.constants';
import { EntryOriginEnum } from '../shared/models/enums/entry-origin.enum';
import { PublicationStatusEnum } from '../shared/models/enums/publication-status.enum';
import { UniversitiesStore } from './signal/universities-store';
import { UniversityWebModel } from './models/university.interface';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatInput, MatLabel, MatSuffix } from '@angular/material/input';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { UniversityDisplayPipe } from './pipes/university-display.pipe';
import { RouterLink } from '@angular/router';
import { MatFormField } from '@angular/material/form-field';

@Component({
  selector: 'app-universities',
  imports: [
    MatButton,
    MatCard,
    MatCardContent,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatFormField,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatIconButton,
    MatInput,
    MatLabel,
    MatMenu,
    MatMenuItem,
    MatPaginator,
    MatRow,
    MatRowDef,
    MatSort,
    MatSortHeader,
    MatSuffix,
    MatTable,
    TopNavbarComponent,
    UniversityDisplayPipe,
    RouterLink,
    MatHeaderCellDef,
    MatMenuTrigger,
    MatIcon,
  ],
  templateUrl: './universities.component.html',
  styleUrl: './universities.component.scss',
})
export class UniversitiesComponent implements OnInit {
  permissionService = inject(PermissionService);
  authService = inject(AuthService);
  universitiesStore = inject(UniversitiesStore);

  protected readonly UNIVERSITIES_CONSTANTS = UNIVERSITIES_CONSTANTS;
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
  displayedColumns: string[] = this.UNIVERSITIES_CONSTANTS.TABLE_COLUMNS.map(
    (column) => column.field
  );

  displayedColumnsWithMenu: string[] = [...this.displayedColumns, 'menu'];
  @ViewChild(MatSort, { static: true }) sort!: MatSort;

  @ViewChild(MatPaginator, { static: true }) paginator!: MatPaginator;

  dataSource = computed(() => {
    const dataSource = new MatTableDataSource<UniversityWebModel>(
      this.universitiesStore.entities()
    );
    dataSource.filterPredicate = this.createFilterPredicate();
    dataSource.sort = this.sort;
    dataSource.paginator = this.paginator;
    return dataSource;
  });

  ngOnInit(): void {
    this.universitiesStore.fetchAll();
  }

  applyFilter(event: Event): void {
    const filterValue: string = (event.target as HTMLInputElement).value;
    this.dataSource().filter = filterValue.trim().toLowerCase();
  }

  onDelete(funderId: string): void {
    this.universitiesStore.deleteById(funderId);
  }

  private createFilterPredicate(): (
    data: UniversityWebModel,
    filter: string
  ) => boolean {
    return (data, filter) => {
      const combinedDataStr = this.combineUniversityFields(data);
      return combinedDataStr.trim().toLowerCase().includes(filter);
    };
  }

  private combineUniversityFields(university: UniversityWebModel): string {
    const names: string[] = [];
    university.name?.forEach((name) => names.push(name.text));
    return names.join('') + university.website + university.acronym;
  }
}
