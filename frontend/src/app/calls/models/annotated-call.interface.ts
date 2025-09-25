import { CallPreview } from './call-preview.interface';
import { PublicationStatusEnum } from '../../shared/models/enums/publication-status.enum';
import { CallAnnotation } from './call-annotation.interface';
import { UniversityReference } from './university-reference.interface';
import { FundifyUser } from './fundify-user.interface';

export interface AnnotatedCall {
  id?: string;
  university?: UniversityReference;
  callPreview?: CallPreview;
  status?: PublicationStatusEnum;
  annotation?: CallAnnotation;
  lastUpdatedAt?: string;
  lastUpdatedBy?: FundifyUser;
}
