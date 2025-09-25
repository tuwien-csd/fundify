import { TestBed } from '@angular/core/testing';

import { ProgramValidationService } from './program-validation.service';

describe('ValidationService', () => {
  let service: ProgramValidationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgramValidationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
