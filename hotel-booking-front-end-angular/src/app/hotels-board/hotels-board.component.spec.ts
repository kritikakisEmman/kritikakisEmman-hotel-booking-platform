import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HotelsBoardComponent } from './hotels-board.component';

describe('HotelsBoardComponent', () => {
  let component: HotelsBoardComponent;
  let fixture: ComponentFixture<HotelsBoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HotelsBoardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HotelsBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
