import { Directive, HostListener, Input, inject } from '@angular/core';
import { NgControl } from '@angular/forms';

@Directive({ selector: '[appNumbersOnly]' })
export class NumbersOnlyDirective {
  private ngControl = inject(NgControl);

  @Input() integer: boolean = false;
  @Input() nonNegative: boolean = false;
  @Input() decimalPlaces: number = 8;
  @HostListener('input', ['$event']) onInputChange(event: KeyboardEvent) {
    const input = event.target as HTMLInputElement;

    let trimmedValue = input.value
      .replace(/[^\d.-]/g, '') // all but digits, minus sign, and decimal point
      .replace(/^0+(?=\d)/, '') // remove leading zeros
      .replace(/(?<!^)-/g, '') // remove minus if not at start
      .replace(/(^[\d-]*\.\d*).*/, '$1'); // keep everything up to second decimal point

    if (this.nonNegative) {
      trimmedValue = trimmedValue.replace('-', '');
    }
    if (this.integer && trimmedValue.indexOf('.') !== -1) {
      trimmedValue = trimmedValue.substring(0, trimmedValue.indexOf('.'));
    }
    if (this.decimalPlaces > 0 && trimmedValue.indexOf('.') !== -1) {
      trimmedValue = trimmedValue.substring(
        0,
        trimmedValue.indexOf('.') + this.decimalPlaces + 1
      );
    }

    // add zero if there is no digit before decimal point (matches "." and "-.")
    trimmedValue = trimmedValue.replace(/(^-?)(\.)/, '$10$2');

    if (isNaN(Number(trimmedValue)) && trimmedValue !== '-') {
      input.value = '';
      this.ngControl.control?.patchValue('');
    }
    if (input.value !== trimmedValue) {
      input.value = trimmedValue;
      this.ngControl.control?.patchValue(trimmedValue);
    }
  }
}
