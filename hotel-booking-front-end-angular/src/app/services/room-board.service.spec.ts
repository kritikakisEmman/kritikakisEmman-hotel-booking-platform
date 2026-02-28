import { TestBed } from '@angular/core/testing';

import { RoomBoardService } from './room-board.service';

describe('RoomBoardService', () => {
  let service: RoomBoardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RoomBoardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
