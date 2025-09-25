import { NumbersOnlyDirective } from './numbers-only.directive';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Component, DebugElement } from '@angular/core';
import { ReactiveFormsModule, UntypedFormControl } from '@angular/forms';

@Component({
  template: ` <input [formControl]="numberTest" appNumbersOnly />
    <input [formControl]="integerTest" appNumbersOnly [integer]="true" />
    <input
      [formControl]="nonNegativeTest"
      appNumbersOnly
      [nonNegative]="true"
    />
    <input
      [formControl]="decimalPlacesTest"
      appNumbersOnly
      [decimalPlaces]="2"
    />`,
  imports: [ReactiveFormsModule, NumbersOnlyDirective],
})
class TestComponent {
  numberTest = new UntypedFormControl();
  integerTest = new UntypedFormControl();
  nonNegativeTest = new UntypedFormControl();
  decimalPlacesTest = new UntypedFormControl();
}

describe('NumbersOnlyDirective', () => {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars -- TODO: This was disabled during the proper setup of eslint. If you touch this code, fix it properly.
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let des: DebugElement[];

  describe('NumbersOnlyDirective with', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [ReactiveFormsModule, TestComponent, NumbersOnlyDirective],
      }).compileComponents();
    });

    beforeEach(() => {
      fixture = TestBed.createComponent(TestComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      des = fixture.debugElement.queryAll(By.directive(NumbersOnlyDirective));
    });

    it('should only allow numeric input', () => {
      // Test input of alphanumeric strings, including strings with special
      // characters and spaces. The input should be stripped of all non-numeric
      // characters.
      const inputEl = des[0].nativeElement;
      const expectedValuePairs = {
        a: '',
        '1': '1',
        '1a': '1',
        '1a2': '12',
        '1a2!': '12',
        '01': '1',
        '.1': '0.1',
        '-1': '-1',
        '.1.2.3': '0.1',
        '1.2.3': '1.2',
        '1-2': '12',
        '-1-2-3': '-123',
        '1.1234567800': '1.12345678',
      };
      for (const [key, expectedValue] of Object.entries(expectedValuePairs)) {
        inputEl.value = key;
        inputEl.dispatchEvent(new Event('input'));
        expect(inputEl.value).toBe(expectedValue);
      }
    });

    it('should restrict input to integers only when `integer` is `true`', () => {
      // Set the `integer` input property to `true` and ensure that the input
      // is restricted to integers only, i.e. no decimal points or fractions
      // are allowed.
      const inputEl = des[1].nativeElement;
      const expectedValuePairs = {
        '1': '1',
        '1.2': '1',
        '1.2.3': '1',
        '-1': '-1',
        '-1.2': '-1',
        '-1.2.3': '-1',
      };
      for (const [key, expectedValue] of Object.entries(expectedValuePairs)) {
        inputEl.value = key;
        inputEl.dispatchEvent(new Event('input'));
        expect(inputEl.value).toBe(expectedValue);
      }
    });

    it('should restrict input to non-negative numbers only when `nonNegative` is `true`', () => {
      // Set the `nonNegative` input property to `true` and ensure that the input
      // is restricted to non-negative numbers only, i.e. no negative sign (-)
      // is allowed.
      const inputEl = des[2].nativeElement;
      const expectedValuePairs = {
        '1': '1',
        '1.2': '1.2',
        '1.2.3': '1.2',
        '-1': '1',
        '-1.2': '1.2',
        '-1.2.3': '1.2',
      };
      for (const [key, expectedValue] of Object.entries(expectedValuePairs)) {
        inputEl.value = key;
        inputEl.dispatchEvent(new Event('input'));
        expect(inputEl.value).toBe(expectedValue);
      }
    });

    it('should restrict input to the specified number of decimal places when `decimalPlaces` is set', () => {
      // Set the `decimalPlaces` input property to a number greater than zero and
      // ensure that the input is restricted to the specified number of decimal
      // places.
      const inputEl = des[3].nativeElement;
      const expectedValuePairs = {
        '1': '1',
        '1.2': '1.2',
        '1.234': '1.23',
        '-1.234': '-1.23',
        '1a2.3b4': '12.34',
        '  01.234  ': '1.23',
      };
      for (const [key, expectedValue] of Object.entries(expectedValuePairs)) {
        inputEl.value = key;
        inputEl.dispatchEvent(new Event('input'));
        expect(inputEl.value).toBe(expectedValue);
      }
    });
  });
});
