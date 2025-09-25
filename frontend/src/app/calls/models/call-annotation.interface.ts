import { TranslatedText } from '../../shared/models/interfaces/translated-text.interface';
import { WebLink } from './web-link.interface';
import { UniversityContact } from './university-contact.interface';

export interface CallAnnotation {
  internalDeadline: TranslatedText[];
  keywords: string[];
  targetGroups: string[];
  links: WebLink[];
  contact: UniversityContact;
}
