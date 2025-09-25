import { FormControl, FormGroup } from '@angular/forms';
import { CustomValidators } from './custom-validators';

describe('CustomValidators', () => {
  describe('phone', () => {
    it('should return null for a valid phone number', () => {
      const control = new FormControl('1234567890');
      const result = CustomValidators.phone(control);
      expect(result).toBeNull();
    });

    it('should return an error object for an invalid phone number', () => {
      const control = new FormControl('invalid-phone');
      const result = CustomValidators.phone(control);
      expect(result).toEqual({ phone: true });
    });
  });

  describe('url', () => {
    it('should return null for a valid URL', () => {
      const control = new FormControl('https://www.example.com');
      const result = CustomValidators.url(control);
      expect(result).toBeNull();
    });

    it('should return an error object for an invalid URL', () => {
      const control = new FormControl('invalid-url');
      const result = CustomValidators.url(control);
      expect(result).toEqual({ url: true });
    });
  });

  describe('duration', () => {
    it('should return null when control value is null', () => {
      const control = new FormControl(null);

      expect(CustomValidators.duration(control)).toBeNull();
    });

    it('should return null when control value has positive months', () => {
      const control = new FormControl({ months: 5, days: 0 });

      expect(CustomValidators.duration(control)).toBeNull();
    });

    it('should return null when control value has positive days', () => {
      const control = new FormControl({ months: 0, days: 15 });

      expect(CustomValidators.duration(control)).toBeNull();
    });

    it('should return an error when control value has neither positive months nor days', () => {
      const control = new FormControl({ months: 0, days: 0 });

      expect(CustomValidators.duration(control)).toEqual({ duration: true });
    });
  });

  describe('dateRange', () => {
    let group: FormGroup;

    beforeEach(() => {
      group = new FormGroup({
        start: new FormControl(),
        end: new FormControl(),
      });
    });

    it('should not return an error if the start date is before the end date', () => {
      group.setValue({ start: '2023-05-01', end: '2023-05-31' });
      expect(CustomValidators.dateRange(group)).toBeNull();
    });

    it('should return an error if the start date is the same as the end date', () => {
      group.setValue({ start: '2023-05-01', end: '2023-05-01' });
      expect(CustomValidators.dateRange(group)).toEqual({ dateRange: true });
    });

    it('should return an error if the start date is after the end date', () => {
      group.setValue({ start: '2023-05-31', end: '2023-05-01' });
      expect(CustomValidators.dateRange(group)).toEqual({ dateRange: true });
    });

    it('should not return an error if either date is not set', () => {
      group.setValue({ start: '2023-05-31', end: null });
      expect(CustomValidators.dateRange(group)).toBeNull();
      group.setValue({ start: null, end: '2023-05-31' });
      expect(CustomValidators.dateRange(group)).toBeNull();
      group.setValue({ start: null, end: null });
      expect(CustomValidators.dateRange(group)).toBeNull();
    });
  });

  describe('translation', () => {
    let group: FormGroup;

    beforeEach(() => {
      group = new FormGroup({
        text: new FormControl(),
        language: new FormControl(),
      });
    });

    it('should return null if the control value is null', () => {
      group.setValue({ text: null, language: null });
      expect(CustomValidators.translation(group)).toBeNull();
    });

    it('should return null if the text and language both are set', () => {
      group.setValue({ text: 'Hello', language: 'English' });
      expect(CustomValidators.translation(group)).toBeNull();
    });

    it('should return null if the text and language both are not set', () => {
      group.setValue({ text: '', language: '' });
      expect(CustomValidators.translation(group)).toBeNull();
    });

    it('should return error if only the text is set', () => {
      group.setValue({ text: 'Hello', language: null });
      expect(CustomValidators.translation(group)).toEqual({
        translation: true,
      });
    });

    it('should return error if only the language is set', () => {
      group.setValue({ text: null, language: 'English' });
      expect(CustomValidators.translation(group)).toEqual({
        translation: true,
      });
    });
  });
});
