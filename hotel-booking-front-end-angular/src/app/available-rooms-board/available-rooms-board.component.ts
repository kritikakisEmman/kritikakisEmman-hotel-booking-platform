import { Component, OnChanges, OnInit, SecurityContext } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbDate, NgbDateParserFormatter, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { Availability } from '../classes/availability';
import { Hotel } from '../classes/hotel';
import { HotelService } from '../services/hotel.service';
import { RmsService } from '../services/rms.service';
import { RoomBoardService } from '../services/room-board.service';

@Component({
  selector: 'app-available-rooms-board',
  templateUrl: './available-rooms-board.component.html',
  styleUrls: ['./available-rooms-board.component.css']
})
export class AvailableRoomsBoardComponent implements OnInit {
  //Variable declarations

  //Static image matrix
  images = [1, 2].map((n) => `assets/images/${n}.png`);

  //Dynamicaly changed parameters
  numberOfPersons!: number;
  numberOfRooms!: number;
  fromDate: NgbDateStruct | null = null;
  toDate: NgbDateStruct | null = null;
  tempfromDate!: Date;
  tempToDate!: Date;
  stringFromDate!: string;
  stringToDate!: string;
  testFromDate!: Date;
  location!: string;
  //matrix that holds all hotels
  hotels: Hotel[] = [];
  previews: any[] = [];
  rmsAvailability: Availability[] = [];
  //Paginator variables 
  pageNumber: number = 1;
  sizeNumber: number = 2;
  theTotalElements: number = 0;

  constructor(private rmsService:RmsService,private sanitizer: DomSanitizer, private roomBoardService: RoomBoardService, private router: Router, private route: ActivatedRoute, private ngbDateParserFormatter: NgbDateParserFormatter, private hotelService: HotelService) { }

  ngOnInit(): void {
    //subscribe on values that come from the search bar
    this.route.queryParams.subscribe(queryParams => {
      this.numberOfRooms = queryParams['numberOfRooms'];
      this.numberOfPersons = queryParams['numberOfPersons'];
      this.stringFromDate = queryParams['stringFromDate'];
      this.stringToDate = queryParams['stringToDate'];
      this.location = queryParams['location'];
      console.log("hello");
      if (this.stringFromDate  && this.stringToDate && this.location) {

        console.log(this.location);
        this.pageNumber=1;
        this.getHotelsPaginated();
      }
      

    });
    //end of subscibe on routerparams

    // TODO: RMS integration - currently causes CORS errors
    // Uncomment when backend endpoint is ready
    // this.rmsService.getAvailability().subscribe(res => {
    //   this.rmsAvailability = res;
    //   console.log(this.rmsAvailability)
    //   this.rmsService.postAvailability(this.rmsAvailability).subscribe(res => { console.log(res) })
    // });

    //This function calls the api to get ALL hotels
    this.getHotelsPaginated();


  }
  //end of on init method

  //This method is called from the paginator and calls the api to get ALL hotels
  getHotelsPaginated() {
    if (!this.stringFromDate) {  //getting hotels from the backend
      this.hotelService.getHotelsPaginated(this.pageNumber - 1, this.sizeNumber).subscribe(res => {
        console.log(res);
        this.hotels = res.content;
        this.pageNumber = res.number + 1;
        this.sizeNumber = res.size;
        this.theTotalElements = res.totalElements;

        console.log(this.hotels);
        for (let hotel of this.hotels) {
          if (hotel.hotelImages.length != 0) {
            let objectURL = 'data:image/jpeg;base64,' + hotel.hotelImages[0].data;
            hotel.hotelImages[0].data = this.sanitizer.bypassSecurityTrustResourceUrl(objectURL);
          }
        }
      });
      //end of subscribe call for get hotels


    }
    else {


      //getting hotels from the backend
      this.hotelService.getHotelsPaginatedWithSearchCriteria(this.pageNumber - 1, this.sizeNumber, this.stringFromDate, this.stringToDate, this.location, this.numberOfPersons, this.numberOfRooms).subscribe(res => {
      console.log(res);
      this.hotels = res.content;
      this.pageNumber = res.number + 1;
      this.sizeNumber = res.size;
      this.theTotalElements = res.totalElements;
      console.log
      console.log(this.hotels);
      for (let hotel of this.hotels) {
        if (hotel.hotelImages.length != 0) {
          let objectURL = 'data:image/jpeg;base64,' + hotel.hotelImages[0].data;
          hotel.hotelImages[0].data = this.sanitizer.bypassSecurityTrustResourceUrl(objectURL);
        }
      }
    });
    //end of subscribe call for get hotels

    }
  }
   //end of getHotelsPaginated method
  callHotelAvailability(id: number) {
    console.log(id);
    this.router.navigate(['/hotelAvailability'], { queryParams: { hotelId: id, stringFromDate: this.stringFromDate, stringToDate: this.stringToDate, numberOfPersons:this.numberOfPersons } });


  }
 
 
}
