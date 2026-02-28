import { Component, ElementRef, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { Hotel } from '../classes/hotel';
import { Reservation } from '../classes/reservation';
import { ReservationType } from '../classes/reservation-type';
import { HotelService } from '../services/hotel.service';
import { ReservationService } from '../services/reservation.service';
import { render } from 'creditcardpayments/creditCardPayments';
import { ViewChild } from '@angular/core';
import { FacebookService } from '../services/facebook.service';
import { AvailableRooms } from '../classes/available-rooms';
import { BookingAppReservationRequest } from '../classes/booking-app-reservation-request';
import { ReservationRequest } from '../classes/reservation-request';
import { ReservationTypeRequest } from '../classes/reservation-type-request';

declare var paypal: any;

@Component({
  selector: 'app-reservation',
  templateUrl: './reservation.component.html',
  styleUrls: ['./reservation.component.css']
})
export class ReservationComponent implements OnInit {
  //hotel id variable comes from previous component via router in order to retrieve  hotel from back end
  hotelId!: number
  //hotel object comes from the backend and populate the values in this variable
  hotel!: Hotel;
  //this variables come from hotel-availability component via router because i need them here for presentation and reservation process
  stringFromDate!: string;
  stringToDate!: string;
  numberOfNights!: number;
  selectedRoomTypes: string[] = [];
  selectedRoomNames: string[] = [];
  selectedRooms: number[] = [];
  selectedRoomPrices: number[] = [];
  totalPrice!: number;

  //reservation obj
  reservation!: Reservation;
  reservationTypes!: ReservationType[];
  reservationType!: ReservationType;

  //variables for radio button payment options
  paymentOptions: string[] = [ 'Pay in the hotel'];
  selectedPaymentOption: string = ' ';
  paymentMethodChosed = false;

  paid = false;
  isFormOk = false;

  reservationCompleted = false;
  product = {
    price: 777.77,
    description: 'used couch, decent condition',
    img: 'assets/couch.jpg'
  };

  //variables for kuriakos
  bookingAppReservationRequest!: BookingAppReservationRequest;
  reservationRequest!: ReservationRequest;
  reservationTypeRequest!: ReservationTypeRequest;


  //this form is made in order to create the reservation  and send it to the back end
  reservationForm = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    phoneNumber: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]]
  
   
    

  });
  //constractor with all the requiered variables
  constructor(private fb: FormBuilder, private route: ActivatedRoute, private hotelService: HotelService, private sanitizer: DomSanitizer, private reservationService: ReservationService, private facebookService: FacebookService)
  {
    //render the render object for paypal. it takes a jason for parameter
    console.log(paypal)
   
    
  }
  //end of constructor






  //when the component is initialized we populate the values of the router and ask for the backend to give us the hotel with this id
  
  ngOnInit(): void {
    //with this  line we subscribe changes from router variables
    this.route.queryParams.subscribe(queryParams => {
      this.hotelId = queryParams['hotelId'];
      this.stringFromDate = queryParams['stringFromDate'];
      this.stringToDate = queryParams['stringToDate'];
      this.numberOfNights = queryParams['nights'];
      this.selectedRoomTypes = queryParams['selectedRoomTypes'];
      this.selectedRoomNames = queryParams['selectedRoomNames'];
      this.selectedRooms = queryParams['selectedRooms'];
      this.selectedRoomPrices = queryParams['selectedRoomPrices'];
      this.totalPrice = queryParams['totalPrice'];
      console.log(this.selectedRoomTypes);
     
      //Based on the hotel that user chose we call the back end api to give us this hotel variable;
      this.hotelService.getHotelById(this.hotelId).subscribe(res => {
        this.hotel = res;
        //we need to make the imageUrls to image format
        for (let i = 0; i < this.hotel.hotelImages.length; i++) {
          let objectURL = 'data:image/jpeg;base64,' + this.hotel.hotelImages[i].data;
          this.hotel.hotelImages[i].data = this.sanitizer.bypassSecurityTrustResourceUrl(objectURL);
        }
      });
      //end of get hotel subscribe

    });
    //end of subscribe of router required parameters
  
  }
  //end of onInit method

  @ViewChild('paypal', { static: true }) paypalElement!: ElementRef;
  ngAfterViewInit() {
    paypal
      .Buttons({
        createOrder: (data: any, actions: { order: { create: (arg0: { purchase_units: { description: string; amount: { currency_code: string; value: number; }; }[]; }) => any; }; }) => {
          return actions.order.create({
            purchase_units: [
              {
                description: this.product.description,
                amount: {
                  currency_code: 'EUR',
                  value: this.totalPrice
                }
              }
            ]
          });
        },
        onApprove: async (data: any, actions: { order: { capture: () => any; }; }) => {
          const order = await actions.order.capture();
          this.paid = true;
          this.paymentMethodChosed = true;
          this.selectedPaymentOption = 'Paypal';
          console.log(order);
          this.completeReservation();
        },
        onError: (err: any) => {
          console.log(err);
        }
      })
      .render(this.paypalElement.nativeElement);
   
  }





  //we call this method when the user is ready to submite reservation
  completeReservation() {
    

    // If form values are ok we need to send the reservation in the backend
    this.reservation = this.reservationForm.value;
    this.reservation.fromDate = new Date(this.stringFromDate);
    this.reservation.toDate = new Date(this.stringToDate);
    console.log(this.reservation);

    this.reservationTypes = [];
    
    // Populate reservationTypes array
    for (let i = 0; i < this.selectedRooms.length; i++) {
      if (this.selectedRooms[i] > 0) {

     
      console.log(this.selectedRooms[i])
      this.reservationType = new ReservationType();
        this.reservationType.quantity = this.selectedRooms[i];
        console.log(this.selectedRoomNames);
        this.reservationType.roomName = this.selectedRoomNames[i];
      //this part is because i dont use default room types on database 
        this.reservationType.roomType = this.selectedRoomTypes[i];
      
      console.log(this.reservationType);
        this.reservationTypes.push(this.reservationType);
      }
    }
    //now populate reservation.reservatioTypes
    this.reservation.reservationTypes = this.reservationTypes;
    
    this.reservation.hotelId = this.hotelId;
    console.log(this.reservation);
    if (this.hotelId == 92) {
      this.reservationRequest = new ReservationRequest();
      //populate reservation request
      this.reservationRequest.tourOperatorName="bookingApp"
      this.reservationRequest.checkInDate = this.reservation.fromDate;
      this.reservationRequest.checkOutDate = this.reservation.toDate;
      this.reservationRequest.reservationName = this.reservation.firstName + " " + this.reservation.lastName;
      this.reservationRequest.status = "Pending";
      this.reservationRequest.contactPhone = this.reservation.phoneNumber;
      this.reservationRequest.paymentStatus = this.paid;
      this.reservationRequest.totalPrice = this.totalPrice;
      this.bookingAppReservationRequest = new BookingAppReservationRequest;
      this.bookingAppReservationRequest.reservationTypes = [];
      //add reservation request to booking app reservation
      this.bookingAppReservationRequest.resesvation = this.reservationRequest;
      //populate reservation types for request

     
      
      for (let i = 0; i < this.reservation.reservationTypes.length; i++) {
        this.reservationTypeRequest = new ReservationTypeRequest();
        this.reservationTypeRequest.roomType = this.reservation.reservationTypes[i].roomType;
        this.reservationTypeRequest.terms = "FullBoard";
        this.reservationTypeRequest.numberOfChildren = 0;
        this.reservationTypeRequest.numberOfRooms = this.reservation.reservationTypes[i].quantity;
        if (this.reservationTypeRequest.roomType == "Si")
          this.reservationTypeRequest.numberOfAdults = 1;
        else if (this.reservationTypeRequest.roomType == "Db")
          this.reservationTypeRequest.numberOfAdults = 2;
        else
          this.reservationTypeRequest.numberOfAdults = 3;
        this.bookingAppReservationRequest.reservationTypes.push(this.reservationTypeRequest);
      }
     
      







      console.log(this.bookingAppReservationRequest)
    }
    //end of if for hotel id


    //now that we populate all the values we are ready for Post resrvation
    this.reservationService.postReservation(this.reservation).subscribe(res => {
      console.log(res);
      this.reservationCompleted = true;
    });

  }
    //end of complete reservation method




  //method that checs form and proced to payment step
  checkReservationForm() {
    //we validate the content of the reservation form 
    if (this.reservationForm.invalid) {
      //if validation form is invalid we mark elements with red color and we DONT execute the below code
      this.reservationForm.markAllAsTouched();
      console.log("form is invalid");
      return;
    }
    //end of form validation
    this.isFormOk = true;
  
    //and disable form
    this.reservationForm.disable();
  }

  goBackToForm() {
    this.isFormOk = false;
    this.reservationForm.enable();
  }


  public PayInTheHotel() {
   
    if (this.selectedPaymentOption == ' ') {
      console.log('mpika sto if');
      this.selectedPaymentOption = 'Pay in the hotel';
      this.paymentMethodChosed = !this.paymentMethodChosed;
      
    }
    else {
      console.log('mpika sto else')
      this.selectedPaymentOption = ' ';
      this.paymentMethodChosed = !this.paymentMethodChosed;
    }
  }




  //custom validator for white space check
  public noWhitespaceValidator(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { 'whitespace': true };
  }//end of validator for white spaces









  //getters for step 2 dates
  get firstName() { return this.reservationForm.get('firstName'); }
  get lastName() { return this.reservationForm.get('lastName'); }
  get phoneNumber() { return this.reservationForm.get('phoneNumber'); }
  get email() { return this.reservationForm.get('email'); }
  get street() { return this.reservationForm.get('address.street'); }
  get city() { return this.reservationForm.get('address.city'); }
  get state() { return this.reservationForm.get('address.state'); }
  get zipCode() { return this.reservationForm.get('address.zipCode'); }
  //end of getters
}
