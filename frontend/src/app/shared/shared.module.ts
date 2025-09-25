import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslatedTextFieldComponent } from './components/translated-text-field/translated-text-field.component';
import { ContactFieldComponent } from './components/contact-field/contact-field.component';
import { DateRangeFieldComponent } from './components/date-range-field/date-range-field.component';
import { DateRangeInfoFieldComponent } from './components/date-range-info-field/date-range-info-field.component';
import { DateRangesInfoFieldComponent } from './components/date-ranges-info-field/date-ranges-info-field.component';
import { DurationFieldComponent } from './components/duration-field/duration-field.component';
import { FundingSearchComponent } from './components/funding-search/funding-search.component';
import { MonetaryFieldComponent } from './components/monetary-field/monetary-field.component';
import { MultipleChoiceFieldComponent } from './components/multiple-choice-field/multiple-choice-field.component';
import { NumberFieldComponent } from './components/number-field/number-field.component';
import { SingleChoiceFieldComponent } from './components/single-choice-field/single-choice-field.component';
import { SubjectFieldComponent } from './components/subject-field/subject-field.component';
import { TextFieldComponent } from './components/text-field/text-field.component';
import { TextsFieldComponent } from './components/texts-field/texts-field.component';
import { TranslatedTextsFieldComponent } from './components/translated-texts-field/translated-texts-field.component';
import { UrlFieldComponent } from './components/url-field/url-field.component';
import { ValidationErrorsComponent } from './components/validation-errors/validation-errors.component';
import { NumbersOnlyDirective } from './directives/numbers-only.directive';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { TopNavbarComponent } from './ui/top-navbar.component';
import { RouterLink, RouterLinkActive } from '@angular/router';

const SHARED_COMPONENTS = [
  TranslatedTextFieldComponent,
  UrlFieldComponent,
  DateRangeFieldComponent,
  DateRangeInfoFieldComponent,
  DateRangesInfoFieldComponent,
  ContactFieldComponent,
  TextFieldComponent,
  SingleChoiceFieldComponent,
  MultipleChoiceFieldComponent,
  TranslatedTextsFieldComponent,
  DurationFieldComponent,
  MonetaryFieldComponent,
  NumberFieldComponent,
  SubjectFieldComponent,
  FundingSearchComponent,
  TextsFieldComponent,
  ValidationErrorsComponent,
];

const SHARED_DIRECTIVES = [NumbersOnlyDirective];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterLink,
    RouterLinkActive,
    ...SHARED_COMPONENTS,
    ...SHARED_DIRECTIVES,
    TopNavbarComponent,
  ],
  exports: [
    FormsModule,
    ReactiveFormsModule,
    ...SHARED_COMPONENTS,
    ...SHARED_DIRECTIVES,
    TopNavbarComponent,
  ],
})
export class SharedModule {}
