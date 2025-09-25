import { TestBed } from '@angular/core/testing';

import { CallValidationService } from './call-validation.service';

describe('CallValidationService', () => {
  let service: CallValidationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CallValidationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
