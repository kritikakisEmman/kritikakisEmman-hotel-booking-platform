import { TestBed } from '@angular/core/testing';

import { RmsService } from './rms.service';

describe('RmsService', () => {
  let service: RmsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RmsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
