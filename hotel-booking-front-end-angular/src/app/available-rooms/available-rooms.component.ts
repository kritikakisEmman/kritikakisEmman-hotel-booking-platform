import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { AvailableRooms } from '../classes/available-rooms';
import { NoEmptySpacesAllowedValidator } from '../classes/no-empty-spaces-allowed-validator';

import { Hotel } from '../classes/hotel';
import { HotelService } from '../services/hotel.service';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { TokenStorageService } from '../_services/token-storage.service';
declare var $: any;
@Component({
  selector: 'app-available-rooms',
  templateUrl: './available-rooms.component.html',
  styleUrls: ['./available-rooms.component.css']
})
export class AvailableRoomsComponent implements OnInit {
  minForFromDate: any = new Date();
  minForToDate: any = new Date();
  maxForFromDate: any = new Date();
  maxForToDate: any = new Date();
  addRoomClicked: boolean = false;
  showAddRooms: boolean = false;
  availableRooms: AvailableRooms[] = [];
  roomTypes: string[] = ['Si', 'Db', 'Tr'];
  selectedType: string = 'Si';
  userId!: number;
  hotel: Hotel = new Hotel();


  availableRoomsDateForm = this.fb.group({
    fromDate: ['', [Validators.required, this.fromDateValidator]],
    toDate: ['', [Validators.required, this.toDateValidator]],


  });

  
  availableRoomsForm = this.fb.group({
    
    roomName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), NoEmptySpacesAllowedValidator.notWhitespaceAtAll]],
    roomType: ['', Validators.required],
    quantity: ['', [Validators.required, Validators.min(1)]],
    roomPrice: ['', [Validators.required, Validators.min(1)]],

  });

  constructor(private fb: FormBuilder, private tokenStorageService: TokenStorageService, private router: Router, private hotelService: HotelService, private sanitizer: DomSanitizer) { }
  ngOnInit(): void {
    this.maxForFromDate.setDate(this.maxForFromDate.getDate() + 60);
    this.maxForToDate.setDate(this.maxForToDate.getDate() + 60);
    const user = this.tokenStorageService.getUser();
    this.userId = user.id
    this.populateHotel();

   
  }
  //end of ngOnInit


  populateHotel() {

    // get hotel from the back end based on the id
    this.hotelService.getHotelByUserId(this.userId).subscribe(res => {
      this.hotel = res;
      console.log(this.hotel);

      //we need to make the imageUrls to image format
      for (let i = 0; i < this.hotel.hotelImages.length; i++) {
        let objectURL = 'data:image/jpeg;base64,' + this.hotel.hotelImages[i].data;
        this.hotel.hotelImages[i].data = this.sanitizer.bypassSecurityTrustResourceUrl(objectURL);

      }
      //end of image manipulation




    });
  //end of subscribe hotel
  }






  complete() {
    this.hotelService.updateAvailableRooms(this.hotel.id, this.availableRooms).subscribe(res => {

      console.log(res);
      alert("Rooms Added Successfully");
      window.location.reload();

    })


  }




  changeShowAddRooms() {
    this.showAddRooms = !this.showAddRooms;
  }


  addAvailableRooms() {
   
    //Triger validation for second step modal values
    if (this.availableRoomsForm.invalid) {
      this.availableRoomsForm.markAllAsTouched();
      console.log("entered here 4");
      return;
    }
  

    let availableRoom: AvailableRooms = new AvailableRooms();

    availableRoom.fromDate = this.availableRoomsDateForm.get('fromDate')?.value;
    availableRoom.toDate = this.availableRoomsDateForm.get('toDate')?.value;
    availableRoom.roomName = this.availableRoomsForm.get('roomName')?.value;
    availableRoom.roomType = this.availableRoomsForm.get('roomType')?.value;
    availableRoom.roomPrice = this.availableRoomsForm.get('roomPrice')?.value;
    availableRoom.quantity = this.availableRoomsForm.get('quantity')?.value;
    availableRoom.number = this.availableRooms.length + 1;
    this.availableRooms.push(availableRoom);
    let ref = document.getElementById('cancel')
    ref?.click();
    this.availableRoomsForm.reset(); //reset form modal
    this.selectedType = 'Si'
  }
  deleteAvailableRooms(id: number) {

    for (let i = 0; i < this.availableRooms.length; i++) {
      if (id = this.availableRooms[i].number) {
        this.availableRooms.splice(i, 1);
        break;
      }
    }

  }

  selectChangeHandler(event: any) {
    //update the ui
    

  }





  addRoom() {
  

    if (this.availableRoomsDateForm.invalid) {
      this.availableRoomsDateForm.markAllAsTouched();
      console.log("entered here 1");
      return;
    }

    $('#exampleModal').modal('show');

    this.addRoomClicked = true;
   
  }

  //getters for step 2 dates
  get fromDate() { return this.availableRoomsDateForm.get('fromDate'); }
  get toDate() { return this.availableRoomsDateForm.get('toDate'); }
  //getters for step 2 values
  get roomName() { return this.availableRoomsForm.get('roomName'); }
  get roomType() { return this.availableRoomsForm.get('roomType'); }
  get quantity() { return this.availableRoomsForm.get('quantity'); }
  get roomPrice() { return this.availableRoomsForm.get('roomPrice'); }

  public noWhitespaceValidator(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { 'whitespace': true };
  }
  toDateValidator(control: FormControl, control2: FormControl) {

    if (control.value && control.parent?.get('fromDate')?.value) {
      if (control.value <= control.parent?.get('fromDate')?.value) {
        return { toDateValidator: true };
      }


    }
    return null;

  }
  fromDateValidator(control: FormControl, control2: FormControl) {

    if (control.value && control.parent?.get('toDate')?.value) {
      if (control.value >= control.parent?.get('toDate')?.value) {
        return { fromDateValidator: true };
      }


    }
    return null;

  }
}
