import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslatedTextFieldComponent } from './translated-text-field.component';
import { RouterTestingModule } from '@angular/router/testing';
import {
  UntypedFormGroup,
  ReactiveFormsModule,
  NgControl,
  FormControl,
} from '@angular/forms';
import { LanguageEnum } from '../../models/enums/language.enum';

describe('TranslatedTextFieldComponent', () => {
  describe('TranslatedTextFieldComponent without @Input', () => {
    let component: TranslatedTextFieldComponent;
    let fixture: ComponentFixture<TranslatedTextFieldComponent>;

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          ReactiveFormsModule,
          TranslatedTextFieldComponent,
        ],
        providers: [
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
      fixture = TestBed.createComponent(TranslatedTextFieldComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('has null control values by default', () => {
      for (const fg of component.translatedTextForm.controls) {
        expect((fg as UntypedFormGroup).controls['text'].value).toBeNull();
        expect((fg as UntypedFormGroup).controls['language'].value).toBeNull();
      }
    });

    it('is valid by default', () => {
      expect(component.translatedTextForm.valid).toEqual(true);
    });
  });

  describe('TranslatedTextFieldComponent with @Input required=true', () => {
    let component: TranslatedTextFieldComponent;
    let fixture: ComponentFixture<TranslatedTextFieldComponent>;

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          ReactiveFormsModule,
          TranslatedTextFieldComponent,
        ],
        providers: [
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
      fixture = TestBed.createComponent(TranslatedTextFieldComponent);
      component = fixture.componentInstance;
      component.required = true;
      fixture.detectChanges();
    });

    it('is invalid when has null text and language', () => {
      component.translatedTextForm.patchValue([{ text: null, language: null }]);
      fixture.detectChanges();
      expect(component.translatedTextForm.valid).toEqual(false);
    });

    it('is valid when has valid text and language values', () => {
      component.translatedTextForm.patchValue([
        { text: 'test', language: LanguageEnum.GERMAN },
      ]);
      fixture.detectChanges();
      expect(component.translatedTextForm.valid).toEqual(true);
    });

    it('is expanded even if empty', () => {
      component.writeValue([]);
      fixture.detectChanges();
      expect(component.expanded).toEqual(true);
    });

    it('is expanded when not empty', () => {
      component.writeValue([{ text: 'test', language: LanguageEnum.GERMAN }]);
      fixture.detectChanges();
      expect(component.expanded).toEqual(true);
    });
  });

  describe('TranslatedTextFieldComponent with @Input required=false and expandEmpty=false', () => {
    let component: TranslatedTextFieldComponent;
    let fixture: ComponentFixture<TranslatedTextFieldComponent>;

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          ReactiveFormsModule,
          TranslatedTextFieldComponent,
        ],
        providers: [
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
      fixture = TestBed.createComponent(TranslatedTextFieldComponent);
      component = fixture.componentInstance;
      component.required = false;
      component.expandEmpty = false;
      fixture.detectChanges();
    });

    it('is not expanded when empty', () => {
      component.writeValue([]);
      fixture.detectChanges();
      expect(component.expanded).toEqual(false);
    });

    it('is expanded when not empty', () => {
      component.writeValue([{ text: 'test', language: LanguageEnum.GERMAN }]);
      fixture.detectChanges();
      expect(component.expanded).toEqual(true);
    });
  });

  describe('TranslatedTextFieldComponent with @Input required=false and expandEmpty=true', () => {
    let component: TranslatedTextFieldComponent;
    let fixture: ComponentFixture<TranslatedTextFieldComponent>;

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          ReactiveFormsModule,
          TranslatedTextFieldComponent,
        ],
        providers: [
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
      fixture = TestBed.createComponent(TranslatedTextFieldComponent);
      component = fixture.componentInstance;
      component.required = false;
      component.expandEmpty = true;
      fixture.detectChanges();
    });

    it('is expanded when empty', () => {
      component.writeValue([]);
      fixture.detectChanges();
      expect(component.expanded).toEqual(true);
    });

    it('is expanded when not empty', () => {
      component.writeValue([{ text: 'test', language: LanguageEnum.GERMAN }]);
      fixture.detectChanges();
      expect(component.expanded).toEqual(true);
    });
  });
});
