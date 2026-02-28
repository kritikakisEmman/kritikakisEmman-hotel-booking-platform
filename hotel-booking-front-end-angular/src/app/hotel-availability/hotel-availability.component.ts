import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';

import { Availability } from '../classes/availability';
import { Hotel } from '../classes/hotel';
import { HotelService } from '../services/hotel.service';

@Component({
  selector: 'app-hotel-availability',
  templateUrl: './hotel-availability.component.html',
  styleUrls: ['./hotel-availability.component.css']
})
export class HotelAvailabilityComponent implements OnInit {
  hotelId!: number;
  hotel: Hotel = new Hotel();
  availableRooms: Availability[] = [];
  tempAvailableRoom!: Availability;
  
  minForFromDate: any = new Date();
  minForToDate: any = new Date();
  maxForFromDate: any = new Date();
  maxForToDate: any = new Date();
  numberOfPersons: number=1;  
  doesHotelHasRoomsForRange: boolean = false;
  stringFromDate!: string;
  stringToDate!: string;
  fromDateDateType: Date = new Date(Date.now());
  toDateDateType: Date = new Date(Date.now());
  selectedRooms: number[] = [];
  selectedRoomTypes: string[] = [];
  selectedRoomNames: string[] = [];
  selectedRoomPrices: number[]=[]
  nights!: number;
  totalPrice: number = 0;
  availableRoomsDateForm = this.fb.group({
    fromDate: ['', [Validators.required, this.fromDateValidator]],
    toDate: ['', [Validators.required, this.toDateValidator]],


  });
  selectedRoomNumber: number = 0;
  roomNumbers: number[][]=[];

  constructor(private route: ActivatedRoute, private hotelService: HotelService, private sanitizer: DomSanitizer, private datePipe: DatePipe, private fb: FormBuilder, private ngbDateParserFormatter: NgbDateParserFormatter, private router: Router,) { }

  selectChangeHandler(event: any,avail:Availability,i:number) {
  
    this.totalPrice = 0;
  
    this.selectedRooms[i] = (event.target.value);
    this.selectedRoomPrices[i] = (avail.roomPrice * this.nights * this.selectedRooms[i]);
    console.log(this.selectedRooms);
    console.log(this.selectedRoomPrices);
    console.log(avail);
    for (let i = 0; i < this.selectedRoomPrices.length; i++) {
      this.totalPrice += this.selectedRoomPrices[i];
      
    }
    this.selectedRoomTypes[i] = avail.type;
    this.selectedRoomNames[i] = avail.roomName;
  }
  ngOnInit(): void {
    this.maxForFromDate.setDate(this.maxForFromDate.getDate() + 60);
    this.maxForToDate.setDate(this.maxForToDate.getDate() + 60);
    this.minForToDate.setDate(this.minForToDate.getDate() + 1);
   
    //based on whitch hotel user clicked we take the id from router parameters
    this.route.queryParams.subscribe(queryParams => {
      this.hotelId = queryParams['hotelId'];
     
      //Based on the hotel that user chose we call the back end api to give us this hotel variable;
      this.hotelService.getHotelById(this.hotelId).subscribe(res => {
        this.hotel = res;
       
       
        
          //we need to make the imageUrls to image format
          for (let i = 0; i < this.hotel.hotelImages.length; i++) {
            let objectURL = 'data:image/jpeg;base64,' + this.hotel.hotelImages[i].data;
            this.hotel.hotelImages[i].data = this.sanitizer.bypassSecurityTrustResourceUrl(objectURL);

        }
        //in case that he came without a range
          let tempType: string = '';
          for (let i = 0; i < this.hotel.hotelAvailabilities.length; i++) {

            if (this.hotel.hotelAvailabilities[i].type != tempType) {
              this.availableRooms.push(this.hotel.hotelAvailabilities[i]);


            }
            tempType = this.hotel.hotelAvailabilities[i].type;

        }
        console.log(this.availableRooms)

        this.availableRooms = this.removeDuplicates(this.availableRooms,"roomName")

        this.stringFromDate = queryParams['stringFromDate'];
        this.stringToDate = queryParams['stringToDate'];
     
        this.availableRoomsDateForm?.get('fromDate')?.setValue(queryParams['stringFromDate']);
      
        this.availableRoomsDateForm?.get('toDate')?.setValue(queryParams['stringToDate']);
        //if he came with range
        if (this.stringFromDate && this.stringToDate && this.numberOfPersons) {
          this.numberOfPersons = queryParams['numberOfPersons']
          console.log("i call search")
          this.search();
        }
       
      });
      //end of hotel subscribe


    });
   // end of subscribe on query parameters


  }
  //end of ngOnInit
  //validator function for from date field
  toDateValidator(control: FormControl) {
   
    if (control.value && control.parent?.get('fromDate')?.value) {
      if (control.value <= control.parent?.get('fromDate')?.value) {
        return { toDateValidator: true };
        
      }


    }
    return null;
    
  }
  //end of validator for from date
  //validator function for from to field
  fromDateValidator(control: FormControl) {
   
    if (control.value && control.parent?.get('toDate')?.value) {
      
      if (control.value >= control.parent?.get('toDate')?.value) {
        
        return { fromDateValidator: true };
       
      }


    }
    return null;

  }
   //end of validator for to date



  // this method is called when search button is pressed and checks available rooms for a date range
  search() {
    
    console.log(this.stringFromDate);
    this.stringFromDate = this.availableRoomsDateForm.get('fromDate')?.value;
    this.stringToDate = this.availableRoomsDateForm.get('toDate')?.value;
    this.fromDateDateType = new Date(this.stringFromDate);
    this.toDateDateType = new Date(this.stringToDate);
    this.nights = Math.floor((Date.UTC(this.toDateDateType.getFullYear(), this.toDateDateType.getMonth(), this.toDateDateType.getDate()) - Date.UTC(this.fromDateDateType.getFullYear(), this.fromDateDateType.getMonth(), this.fromDateDateType.getDate())) / (1000 * 60 * 60 * 24));
    console.log("nights-> " + this.nights);

   
    let tempName: string = '';
    let tempDate = new Date;
    let failName: string = '';
    let sum: number = 0;
    this.roomNumbers = [];
    this.availableRooms = [];
  
    console.log(this.availableRooms.length)
    if (this.availableRoomsDateForm.invalid ) {
      this.availableRoomsDateForm.markAllAsTouched();
      console.log("entered here 1");
      return;
    } 

    for (let availableRoom of this.hotel.availableRooms) {
      if (this.hotel.id == 92) { this.doesHotelHasRoomsForRange = true; break; }
      if ((this.availableRoomsDateForm.get('fromDate')?.value > availableRoom.fromDate || this.availableRoomsDateForm.get('fromDate')?.value == availableRoom.fromDate) && (this.availableRoomsDateForm.get('toDate')?.value < (availableRoom.toDate) || this.availableRoomsDateForm.get('toDate')?.value == (availableRoom.toDate))) {

        console.log("this hotel has rooms ");
        this.doesHotelHasRoomsForRange = true;
        break;
      } else {
       console.log("this hotel doesnt have rooms");
        this.doesHotelHasRoomsForRange = false;
        this.roomNumbers=[]
      }
    }


    if (this.doesHotelHasRoomsForRange) {

      let d = new Date(this.availableRoomsDateForm.get('toDate')?.value)
                let temp = new Date(d.setDate(d.getDate() - 1))
    
    
      for (let i = 0; i < this.hotel.hotelAvailabilities.length; i++) {
     
        console.log("-----------------");
        console.log("iteration " + i)
        if ((this.hotel.hotelAvailabilities[i].date > this.availableRoomsDateForm.get('fromDate')?.value || this.hotel.hotelAvailabilities[i].date == this.availableRoomsDateForm.get('fromDate')?.value) && this.hotel.hotelAvailabilities[i].date < this.availableRoomsDateForm.get('toDate')?.value)
        {

        
     
        console.log("availability -> " + this.hotel.hotelAvailabilities[i].available)

        if (this.hotel.hotelAvailabilities[i].available>0) {
         
          if (this.hotel.hotelAvailabilities[i].roomName != failName) {
            if ((this.hotel.hotelAvailabilities[i].roomName != tempName)) {
              
              if (tempName != '') {
            
             
                d = new Date(this.hotel.hotelAvailabilities[i - 1].date);
                let temp1 = new Date(d.setDate(d.getDate()))

                if (tempDate < temp) {
                  console.log("sulamvanese")
                  this.availableRooms.pop();

                }

              }
          
              console.log("to temp date einai"+tempDate)
              console.log("push it")
              if (this.hotel.hotelAvailabilities[i].date == this.availableRoomsDateForm.get('fromDate')?.value)
                this.availableRooms.push(this.hotel.hotelAvailabilities[i]);
              tempName = this.hotel.hotelAvailabilities[i].roomName;
              tempDate = new Date(this.hotel.hotelAvailabilities[i].date)
             
               
            
            } else {
              tempDate = new Date(this.hotel.hotelAvailabilities[i].date)
              //check for min 
              if (this.hotel.hotelAvailabilities[i].available < this.availableRooms[this.availableRooms.length - 1]?.available) {
                console.log('i found a case');
                this.availableRooms.pop();
                this.availableRooms.push(this.hotel.hotelAvailabilities[i]);
              }
            }
          }
        }
        else {
          if (tempName == this.hotel.hotelAvailabilities[i].roomName) {
            console.log("fail Room type")
            this.availableRooms.pop();

          }
          else {
            console.log("fale room name")
            failName = this.hotel.hotelAvailabilities[i].roomName;


          }
           

        }
      }
    
        //end of if (doeshotelrooms has range)
       
      }
      //end of for loops
      if (tempDate < temp) {
        console.log("sulamvanese")
        this.availableRooms.pop();

      }
      
      let numberOfPersonsPerRoom!: number;
      for (let i = 0; i < this.availableRooms.length; i++) {
        if (this.availableRooms[i].type == "Si") {
          numberOfPersonsPerRoom = 1;
        }
        else if (this.availableRooms[i].type == "Db") {
          numberOfPersonsPerRoom = 2;
        }
        else
          numberOfPersonsPerRoom = 3;
        sum += numberOfPersonsPerRoom * this.availableRooms[i].available;
      }
      if (this.numberOfPersons > sum) {
        this.availableRooms=[];
      }
      //end of for that finds available room types
      //now i need to find the number of minimumrooms for rent
      for (let i = 0; i < this.availableRooms.length; i++) {
        console.log(this.availableRooms[i].available)
        this.roomNumbers.push(Array.from(Array(this.availableRooms[i].available+1).keys()));

      }
      this.selectedRooms = new Array(this.availableRooms.length);
      this.selectedRoomPrices = new Array(this.availableRooms.length);
      for (let i = 0; i < this.availableRooms.length; i++) {
        this.selectedRooms[i] = 0;
        this.selectedRoomPrices[i] = 0;
        this.selectedRoomTypes[i] = '';
      }
      console.log(this.selectedRooms);
      console.log(this.selectedRoomPrices);
     
     
    }
    
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

  goToReservationComponent() {


    this.router.navigate(['/reservation'], { queryParams: { hotelId: this.hotel.id, stringFromDate: this.stringFromDate, stringToDate: this.stringToDate, nights: this.nights, selectedRooms: this.selectedRooms, selectedRoomPrices: this.selectedRoomPrices, totalPrice: this.totalPrice, selectedRoomTypes: this.selectedRoomTypes, selectedRoomNames: this.selectedRoomNames } });


  }
  removeDuplicates(myArray:any, Prop:any) {
    return myArray.filter((obj:any, pos:any, arr:any) => {
      return arr.map((mapObj: { [x: string]: any; }) => mapObj[Prop]).indexOf(obj[Prop]) === pos;
    });
  }
  //getters for step 2 dates
  get fromDate() { return this.availableRoomsDateForm.get('fromDate'); }
  get toDate() { return this.availableRoomsDateForm.get('toDate'); }
}
