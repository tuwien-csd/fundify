import { groupBy } from './list-util';

describe('list-util', () => {
  describe('groupBy', () => {
    const ns = [1, 2, 3, 4, 5, 6];
    const modTwo = (n: number) => n % 2;

    it('returns empty object when input array is empty', () =>
      expect(groupBy([], modTwo)).toEqual({}));

    it('groups numbers into even and odd', () => {
      expect(groupBy(ns, modTwo)).toEqual({ '0': [2, 4, 6], '1': [1, 3, 5] });
    });
  });
});
