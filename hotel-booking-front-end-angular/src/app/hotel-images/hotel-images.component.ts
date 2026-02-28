import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Hotel } from '../classes/hotel';
import { Image } from '../classes/image';
import { HotelService } from '../services/hotel.service';
import { TokenStorageService } from '../_services/token-storage.service';
import { UserService } from '../_services/user.service';

@Component({
  selector: 'app-hotel-images',
  templateUrl: './hotel-images.component.html',
  styleUrls: ['./hotel-images.component.css']
})
export class HotelImagesComponent implements OnInit {
  userId!: number;
  hotel: Hotel = new Hotel();
  previews: Image[] = [];
  previews2: string[] = [];
  file!: File;
  selectedFiles!: FileList;
  progressInfos: any[] = [];
  message: string[] = [];

  hotelImages: Image[] = [];

  myFiles: string[] = [];
  constructor(private hotelService: HotelService, private tokenStorageService: TokenStorageService, private sanitizer: DomSanitizer) { }

  ngOnInit(): void {
    this.populateHotel();

  }
  //end of on init

  populateHotel() {

    const user = this.tokenStorageService.getUser();
    this.userId = user.id

    // get hotel from the back end based on the id
    this.hotelService.getHotelByUserId(this.userId).subscribe(res => {
      this.hotel = res;
      console.log(this.hotel);

      //we need to make the imageUrls to image format
      for (let i = 0; i < this.hotel.hotelImages.length; i++) {
        console.log(this.hotel.hotelImages[i].data)




        let objectURL = 'data:image/jpeg;base64,' + this.hotel.hotelImages[i].data
        this.hotel.hotelImages[i].data = this.sanitizer.bypassSecurityTrustResourceUrl(objectURL);
        this.previews.push(this.hotel.hotelImages[i])


      }



      console.log(this.hotel.address.city)

    });
    //end of subscribe hotel

  }
  selectFiles(event: any): void {
    this.message = [];
    this.progressInfos = [];
    this.selectedFiles = event.target.files;
    this.myFiles = event.target.files
    this.previews2 = []

    if (this.selectedFiles && this.selectedFiles[0]) {
      const numberOfFiles = this.selectedFiles.length;
      for (let i = 0; i < numberOfFiles; i++) {
        const reader = new FileReader();
        console.log(this.selectedFiles[0].name)
        reader.onload = (e: any) => {
          this.previews2.push(e.target.result);
        };

        reader.readAsDataURL(this.selectedFiles[i]);
        console.log(this.selectedFiles)
      }
    }
    else {
      alert("You didnt add new images")
    }
  }


  updateHotelImages() {
    if (this.previews2.length > 0) {
      this.hotelService.updateHotelImages(this.hotel.id, this.selectedFiles).subscribe(res => {
        // alert("Images Added Succesfully")
        window.location.reload()
      }
        , err => console.log(err))

    } else { alert("You didnt add new images") }



  }

  deleteHotelImage(imageId: number) {

    console.log(imageId)
    this.hotelService.deleteHotelImageById(this.hotel.id,imageId).subscribe(res => {
    
      for (let i = 0; i < this.previews.length; i++) {
        if (this.previews[i].id = imageId) {
          this.previews.splice(i,1)
        }
        
      }
      window.location.reload()
    })

  }
}
