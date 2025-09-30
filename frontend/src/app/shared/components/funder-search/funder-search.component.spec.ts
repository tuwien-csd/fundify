import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, NgControl, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FunderSearchComponent } from './funder-search.component';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { FundersStore } from '../../../funders/signal/funders-store';

describe('FunderSearchComponent', () => {
  let component: FunderSearchComponent;
  let fixture: ComponentFixture<FunderSearchComponent>;
  let routerSpy: jasmine.SpyObj<Router>;

  // Minimal mock store
  const mockFundersStore = {
    externallyManagedFunders: () => [
      { id: '1', name: [{ text: 'Funder A' }] },
      { id: '2', name: [{ text: 'Funder B' }] },
    ],
    entities: () => [
      { id: '1', name: [{ text: 'Funder A' }] },
      { id: '2', name: [{ text: 'Funder B' }] },
    ],
  };

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        MatAutocompleteModule,
        FunderSearchComponent,
      ],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: FundersStore, useValue: mockFundersStore },
        {
          provide: NgControl,
          useValue: {
            control: new FormControl(),
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FunderSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should add selected funder to fundingEntityRefs array', () => {
    component.search.setValue({ id: '1', name: [{ text: 'Funder A' }] });
    component.setSelectedFunder();
    expect(component.fundingEntityFormArray.length).toBe(1);
  });

  it('should remove funder at specified index', () => {
    component.search.setValue({ id: '1', name: [{ text: 'Funder A' }] });
    component.setSelectedFunder();
    component.removeFunder(0);
    expect(component.fundingEntityFormArray.length).toBe(0);
  });

  it('writeValue should set single value when multiSelect=false', () => {
    component.writeValue({ id: '1', name: [{ text: 'Funder A' }] });
    expect(component.fundingEntityFormArray.length).toBe(1);
  });

  it('only allows one entity to be added when multiSelect=false', () => {
    component.multiSelect = false;
    component.search.setValue({ id: '1', name: [{ text: 'Funder A' }] });
    component.setSelectedFunder();
    component.search.setValue({ id: '2', name: [{ text: 'Funder B' }] });
    component.setSelectedFunder();
    expect(component.fundingEntityFormArray.length).toBe(1);
  });
});
