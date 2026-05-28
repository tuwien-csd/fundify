import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  FormControl,
  NgControl,
  ReactiveFormsModule,
  UntypedFormBuilder,
} from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SearchSelectComponent } from './search-select.component';
import { ValidationService } from '../../services/validation.service';

describe('SearchSelectComponent', () => {
  let component: SearchSelectComponent;
  let fixture: ComponentFixture<SearchSelectComponent>;
  let validationServiceSpy: jasmine.SpyObj<ValidationService>;

  const mockCountryEnum = {
    Austria: 'Austria',
    Belgium: 'Belgium',
    Germany: 'Germany',
  };

  beforeEach(async () => {
    validationServiceSpy = jasmine.createSpyObj('ValidationService', [
      'getValidationErrorMessage',
    ]);

    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        MatAutocompleteModule,
        BrowserAnimationsModule,
        SearchSelectComponent,
      ],
      providers: [
        UntypedFormBuilder,
        { provide: ValidationService, useValue: validationServiceSpy },
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
    fixture = TestBed.createComponent(SearchSelectComponent);
    component = fixture.componentInstance;

    // Set signal inputs
    fixture.componentRef.setInput('type', mockCountryEnum);
    fixture.componentRef.setInput('multiSelect', true);
    fixture.componentRef.setInput('required', false);
    fixture.componentRef.setInput('label', 'Test Label');

    fixture.detectChanges();
  });

  it('should add selected item to selectionsFormArray and clear search', () => {
    component.search.setValue('Austria');
    component.setSelectedItem();

    expect(component.selectionsFormArray.length).toBe(1);
    expect(component.selectionsFormArray.value[0]).toBe('Austria');
    expect(component.search.value).toBe('');
  });

  it('should only allow one selection when multiSelect is false', () => {
    fixture.componentRef.setInput('multiSelect', false);
    fixture.detectChanges();

    component.search.setValue('Austria');
    component.setSelectedItem();
    expect(component.selectionsFormArray.length).toBe(1);

    component.search.setValue('Belgium');
    component.setSelectedItem();

    // Should remain 1 because it early returns when selection length > 0
    expect(component.selectionsFormArray.length).toBe(1);
    expect(component.selectionsFormArray.value[0]).toBe('Austria');
    expect(component.search.value).toBe(''); // Search should have been cleared
  });
});
