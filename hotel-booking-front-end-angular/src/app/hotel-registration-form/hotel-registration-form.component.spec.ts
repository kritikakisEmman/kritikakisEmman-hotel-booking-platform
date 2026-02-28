import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HotelRegistrationFormComponent } from './hotel-registration-form.component';

describe('HotelRegistrationFormComponent', () => {
  let component: HotelRegistrationFormComponent;
  let fixture: ComponentFixture<HotelRegistrationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HotelRegistrationFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HotelRegistrationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
