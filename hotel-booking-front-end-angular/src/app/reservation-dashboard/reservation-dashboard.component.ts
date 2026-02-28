import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Hotel } from '../classes/hotel';
import { ReservationType } from '../classes/reservation-type';
import { HotelService } from '../services/hotel.service';
import { ReservationService } from '../services/reservation.service';
import { TokenStorageService } from '../_services/token-storage.service';

@Component({
  selector: 'app-reservation-dashboard',
  templateUrl: './reservation-dashboard.component.html',
  styleUrls: ['./reservation-dashboard.component.css']
})
export class ReservationDashboardComponent implements OnInit {
  userId!: number;
  hotel: Hotel = new Hotel();
  index: number = 0;
  reservationTypes: ReservationType[] = [];
  reservationsLength = 0;
  constructor(private reservationService: ReservationService, private tokenStorageService: TokenStorageService, private sanitizer: DomSanitizer, private hotelService: HotelService) { }

  ngOnInit(): void {
    const user = this.tokenStorageService.getUser();
    this.userId = user.id
    // get hotel from the back end based on the id
    this.hotelService.getHotelByUserId(this.userId).subscribe(res => {
      this.hotel = res;
      this.reservationsLength = this.hotel.hotelReservations.length;
      console.log(this.hotel);
      
      //we need to make the imageUrls to image format
      for (let i = 0; i < this.hotel.hotelImages.length; i++) {
        let objectURL = 'data:image/jpeg;base64,' + this.hotel.hotelImages[i].data;
        this.hotel.hotelImages[i].data = this.sanitizer.bypassSecurityTrustResourceUrl(objectURL);

      }
      //end of image manipulation
  

      console.log(this.hotel.address.city)

    });
  //end of subscribe hotel
  }
  passIndex(i:number) {
   
    this.index = i;
    console.log(this.index)
    this.reservationTypes = this.hotel.hotelReservations[i].reservationTypes;
  }
  deleteReservation(id: number) {

   
    console.log(id)
    this.reservationService.deleteReservation(this.hotel.id, id).subscribe(res => {

      console.log(res);
      window.location.reload();

    }, err => {

      alert("Server failed to manage your request");

    })
 
  }

}
