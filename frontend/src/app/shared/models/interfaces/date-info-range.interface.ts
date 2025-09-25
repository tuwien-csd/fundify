import { DateRange } from './date-range.interface';
import { TranslatedText } from './translated-text.interface';

export interface DateInfoRange {
  duration: DateRange;
  description: TranslatedText[];
  number?: number;
}
