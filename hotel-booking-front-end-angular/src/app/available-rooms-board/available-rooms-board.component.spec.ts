import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvailableRoomsBoardComponent } from './available-rooms-board.component';

describe('AvailableRoomsBoardComponent', () => {
  let component: AvailableRoomsBoardComponent;
  let fixture: ComponentFixture<AvailableRoomsBoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AvailableRoomsBoardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AvailableRoomsBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
