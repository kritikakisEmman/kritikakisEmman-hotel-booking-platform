import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckAvailabilityFormComponent } from './check-availability-form.component';

describe('CheckAvailabilityFormComponent', () => {
  let component: CheckAvailabilityFormComponent;
  let fixture: ComponentFixture<CheckAvailabilityFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CheckAvailabilityFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckAvailabilityFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
