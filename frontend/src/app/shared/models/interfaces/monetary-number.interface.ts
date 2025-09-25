import { CurrencyEnum } from '../enums/currency.enum';

export interface MonetaryNumber {
  amount: number;
  currency: CurrencyEnum;
}
