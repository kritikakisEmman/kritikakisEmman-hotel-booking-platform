import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomAvailabilityBoardComponent } from './room-availability-board.component';

describe('RoomAvailabilityBoardComponent', () => {
  let component: RoomAvailabilityBoardComponent;
  let fixture: ComponentFixture<RoomAvailabilityBoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RoomAvailabilityBoardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RoomAvailabilityBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
