import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule, AbstractControl  } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbDate, NgbCalendar } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder } from '@angular/forms';
import { NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';
import { HotelService } from '../services/hotel.service';
import { Hotel } from '../classes/hotel';
@Component({
  selector: 'app-check-availability-form',
  templateUrl: './check-availability-form.component.html',
  styleUrls: ['./check-availability-form.component.css']
})
export class CheckAvailabilityFormComponent  {
  hoveredDate: NgbDate | null = null;
  minDate = {
    year: new Date().getFullYear(),
    month: new Date().getMonth() + 1,
    day: new Date().getDate()
  };
  maxDate = {
    year: new Date().getFullYear(),
    month: new Date().getMonth() + 3,
    day: new Date().getDate()
  };
  fromDate!: NgbDate;
  toDate: NgbDate | null = null;
  stringFromDate!: String;
  stringToDate!: String;
  numberOfPersons: number = 1;
  numberOfRooms: number = 1;
  public demo: number = 0;
  hotels: Hotel[] = [];

  locationForm = this.fb.group({
   location: ['', [Validators.required,]],
   


  });

  constructor(private fb: FormBuilder ,private hotelService:HotelService,private router: Router, private calendar: NgbCalendar, private formBuilder: FormBuilder, private ngbDateParserFormatter: NgbDateParserFormatter) {
    this.fromDate = calendar.getToday();
    this.toDate = calendar.getNext(calendar.getToday(), 'd', 10);

  }

  ngOnInit(): void {
   
    this.router.navigate(['/availability/availableRooms']);
   
  }
   

  checkAvailableRooms()
  {
    if (this.locationForm.invalid || !this.toDate) {
      this.locationForm.markAllAsTouched();
      return;
    }
    console.log(this.locationForm.get('location')?.value)
    this.stringFromDate = this.ngbDateParserFormatter.format(this.fromDate);
    this.stringToDate = this.ngbDateParserFormatter.format(this.toDate);
    this.router.navigate(['/availability/availableRooms'], { queryParams: { numberOfPersons: this.numberOfPersons, numberOfRooms: this.numberOfRooms, stringFromDate: this.stringFromDate, stringToDate: this.stringToDate, location: this.locationForm.get('location')?.value } });
  }

  onDateSelection(date: NgbDate) {
    if (!this.fromDate && !this.toDate) {
      this.fromDate = date;
    } else if (this.fromDate && !this.toDate && date.after(this.fromDate)) {
      this.toDate = date;
    } else {
      this.toDate =    null;

      this.fromDate = date;
    }
  
  }
  isHovered(date: NgbDate) {
    return (
      this.fromDate && !this.toDate && this.hoveredDate && date.after(this.fromDate) && date.before(this.hoveredDate)
    );
  }
  isInside(date: NgbDate) {
    return this.toDate && date.after(this.fromDate) && date.before(this.toDate);
  }
  isRange(date: NgbDate) {
    return (
      date.equals(this.fromDate) ||
      (this.toDate && date.equals(this.toDate)) ||
      this.isInside(date) ||
      this.isHovered(date)
    );
  }
  incrementNumberOfPersons() {
    if (this.numberOfPersons == 10) {

    }
    else {
      this.numberOfPersons++;
    }
  }
  decrementNumberOfPersons() {
    if (this.numberOfPersons == 1) {

    }
    else {
      this.numberOfPersons--;
    }


  }
  incrementNumberOfRooms() {
    if (this.numberOfRooms == 10) {

    }
    
    else {
      this.numberOfRooms++;
    }
  }
  decrementNumberOfRooms() {
    if (this.numberOfRooms == 1) {

    }
    else {
      this.numberOfRooms--;



    }


    }
  //getters to make validetion work in html
  //for location
  get location() { return this.locationForm.get('location'); }
}
