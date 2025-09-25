import { FundingRegexp } from './funding-regexp.enum';

describe('FundingRegexp', () => {
  describe('Email regexp', () => {
    const regexp = new RegExp(FundingRegexp.EMAIL);

    it('valid email should pass regexp test', () => {
      expect(regexp.test('test@test.at')).toBeTrue();
    });

    it('email with missing @ should fail regexp test', () => {
      expect(regexp.test('testtest.at')).toBeFalse();
    });

    it('email with nothing after @ should fail regexp test', () => {
      expect(regexp.test('test@')).toBeFalse();
    });

    it('email with nothing before @ should fail regexp test', () => {
      expect(regexp.test('@test')).toBeFalse();
    });
  });

  describe('Phone regexp', () => {
    const regexp = new RegExp(FundingRegexp.PHONE);

    it('valid phone should pass regexp test', () => {
      expect(regexp.test('+43 220 22 22')).toBeTrue();
    });

    it('phone with characters should fail regexp test', () => {
      expect(regexp.test('test')).toBeFalse();
    });
  });

  describe('URL regexp', () => {
    const regexp = new RegExp(FundingRegexp.URL);

    it('valid url should pass regexp test', () => {
      expect(regexp.test('www.test.at')).toBeTrue();
    });

    it('invalid url example should fail regexp test', () => {
      expect(regexp.test('wwwtest')).toBeFalse();
    });
  });

  describe('Acronym regexp', () => {
    const regexp = new RegExp(FundingRegexp.ACRONYM);

    it('valid acronym should pass regexp test', () => {
      expect(regexp.test('FWF-EINZ-2000')).toBeTrue();
    });

    it('invalid acronym should fail regexp test', () => {
      expect(regexp.test('test@')).toBeFalse();
    });
  });
});
