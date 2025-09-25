import { adjustDateForTimezone } from './date-utils';

describe('adjustDateForTimezone', () => {
  let originalTimezoneOffset: () => number;

  beforeEach(() => {
    originalTimezoneOffset = Date.prototype.getTimezoneOffset;
  });

  afterEach(() => {
    Date.prototype.getTimezoneOffset = originalTimezoneOffset;
  });

  it('should not change date for UTC+0', () => {
    Date.prototype.getTimezoneOffset = () => 0;
    const input = new Date('2023-08-15T00:00:00');

    adjustDateForTimezone(input);

    expect(input.getHours()).toBe(0);
    expect(input.getMinutes()).toBe(0);
    expect(input.getDate()).toBe(15);
  });

  it('should add hours for positive offsets (e.g., UTC+2)', () => {
    Date.prototype.getTimezoneOffset = () => -120;
    const input = new Date('2023-08-15T00:00:00');

    adjustDateForTimezone(input);

    expect(input.getHours()).toBe(2);
    expect(input.getMinutes()).toBe(0);
    expect(input.getDate()).toBe(15);
  });

  it('should subtract hours for negative offsets (e.g., UTC-5)', () => {
    Date.prototype.getTimezoneOffset = () => 300;
    const input = new Date('2023-08-15T00:00:00');

    adjustDateForTimezone(input);

    expect(input.getHours()).toBe(19);
    expect(input.getMinutes()).toBe(0);
    expect(input.getDate()).toBe(14);
  });

  it('should handle date change for far eastern timezones (e.g., UTC+14)', () => {
    Date.prototype.getTimezoneOffset = () => -840;
    const input = new Date('2023-08-15T00:00:00');

    adjustDateForTimezone(input);

    expect(input.getHours()).toBe(14);
    expect(input.getMinutes()).toBe(0);
    expect(input.getDate()).toBe(15);
  });

  it('should handle date change for far western timezones (e.g., UTC-12)', () => {
    Date.prototype.getTimezoneOffset = () => 720;
    const input = new Date('2023-08-15T00:00:00');

    adjustDateForTimezone(input);

    expect(input.getHours()).toBe(12);
    expect(input.getMinutes()).toBe(0);
    expect(input.getDate()).toBe(14);
  });

  it('should handle non-integer hour offsets (e.g., India UTC+5:30)', () => {
    Date.prototype.getTimezoneOffset = () => -330;
    const input = new Date('2023-08-15T00:00:00');

    adjustDateForTimezone(input);

    expect(input.getHours()).toBe(5);
    expect(input.getMinutes()).toBe(30);
    expect(input.getDate()).toBe(15);
  });
});
