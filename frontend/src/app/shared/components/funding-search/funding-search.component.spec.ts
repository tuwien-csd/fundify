import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  FormControl,
  ReactiveFormsModule,
  UntypedFormBuilder,
} from '@angular/forms';
import { Router } from '@angular/router';
import {
  AllowedEntityStores,
  FundingSearchComponent,
} from './funding-search.component';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { RouterTestingModule } from '@angular/router/testing';
import { InputSignal, signal } from '@angular/core';

describe('FundingSearchComponent', () => {
  let component: FundingSearchComponent;
  let fixture: ComponentFixture<FundingSearchComponent>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        MatAutocompleteModule,
        FundingSearchComponent,
      ],
      providers: [UntypedFormBuilder, { provide: Router, useValue: routerSpy }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FundingSearchComponent);
    component = fixture.componentInstance;
    component.entityStore = signal({
      entities: () => [],
    }) as unknown as InputSignal<AllowedEntityStores>;
    fixture.detectChanges();
  });

  it('should add selected program to fundingEntityRefsArray', () => {
    fixture.detectChanges();
    component.search.setValue('test');
    component.addToList();
    expect(component.fundingEntityFormArray.length).toBe(1);
  });

  it('should remove fundingEntityRef at specified index from fundingEntityRefsArray', () => {
    component.ngOnInit();
    component.search.setValue('test');
    component.addToList();
    component.removeProgram(0);
    expect(component.fundingEntityFormArray.length).toBe(0);
  });

  it('should navigate to create new program page in the same tab when newTab is false', () => {
    component.createNewProgram(false);
    expect(routerSpy.navigate).toHaveBeenCalled();
  });

  it('should navigate to create new program page in a new tab when newTab is true', () => {
    spyOn(window, 'open');
    component.createNewProgram(true);
    expect(window.open).toHaveBeenCalledWith(
      component.pathCreateNew + '?of=' + component.searchRestrictRef,
      '_blank'
    );
  });

  it('should set fundingEntityRefsArray to the provided value', () => {
    component.ngOnInit();
    const programs = [{ id: '1', name: [{ text: 'Test Program' }] }];
    component.writeValue(programs);
    expect(component.fundingEntityFormArray.length).toBe(1);
  });

  it('should return null if form is valid', () => {
    component.ngOnInit();
    const validProgram = { id: '1', name: [{ text: 'Test Program' }] };
    component.fundingEntityFormArray.push(new FormControl(validProgram));
    const result = component.validate(component.form);
    expect(result).toBeNull();
  });

  it('only allows one entity to be added when multiSelect=false', () => {
    component.multiSelect = false;
    component.ngOnInit();
    component.search.setValue('test');
    component.addToList();
    component.search.setValue('test2');
    component.addToList();
    expect(component.fundingEntityFormArray.length).toBe(1);
  });

  it('allows multiple entities to be added when multiSelect=true', () => {
    component.multiSelect = true;
    component.ngOnInit();
    component.search.setValue('test');
    component.addToList();
    component.search.setValue('test2');
    component.addToList();
    expect(component.fundingEntityFormArray.length).toBe(2);
  });
});
