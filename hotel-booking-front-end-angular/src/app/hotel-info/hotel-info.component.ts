import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, Validators } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { Address } from '../classes/address';
import { Hotel } from '../classes/hotel';
import { HotelInfoRequest } from '../classes/hotel-info-request';
import { HotelService } from '../services/hotel.service';
import { TokenStorageService } from '../_services/token-storage.service';
import { UserService } from '../_services/user.service';


@Component({
  selector: 'app-hotel-info',
  templateUrl: './hotel-info.component.html',
  styleUrls: ['./hotel-info.component.css']
})

export class HotelInfoComponent implements OnInit {
  userId!: number;
  hotel: Hotel = new Hotel();

  constructor(private userService: UserService, private tokenStorageService: TokenStorageService, private router: Router, private hotelService: HotelService, private sanitizer: DomSanitizer,  private fb: FormBuilder) { }
  hotelInfoForm = this.fb.group({
    
    hotelName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), this.noWhitespaceValidator]],
    hotelDescription: ['', [Validators.required, Validators.minLength(100)]],
    address: this.fb.group({
      street: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), this.noWhitespaceValidator]],
      city: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), this.noWhitespaceValidator]],
      state: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), this.noWhitespaceValidator]],
      zipCode: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5), Validators.pattern('^[0-9]*$')]]
    })

  });
  ngOnInit(): void {

    const user = this.tokenStorageService.getUser();
    this.userId=user.id
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
      this.hotelInfoForm.controls['hotelName'].setValue(this.hotel.hotelName);
      this.hotelInfoForm.get('hotelDescription')?.setValue(this.hotel.hotelDescription)
      this.hotelInfoForm.get('address.street')?.setValue(this.hotel.address.street)
      this.hotelInfoForm.get('address.city')?.setValue(this.hotel.address.city)
      this.hotelInfoForm.get('address.state')?.setValue(this.hotel.address.state)
      this.hotelInfoForm.get('address.zipCode')?.setValue(this.hotel.address.zipCode)
      
      console.log(this.hotel.address.city)
  
    });
  //end of subscribe hotel
  }
  //end of ngOninit

  updateHotelInfo() {
    let demo: string;
    demo = this.hotelInfoForm.get('hotelDescription')?.value;
    console.log(demo.length);
    if (this.hotelInfoForm.invalid || demo.length<100) {
      this.hotelInfoForm.markAllAsTouched();
    
      return;
    }
    let hotelInfoRequest: HotelInfoRequest = new HotelInfoRequest();
    hotelInfoRequest.address = new Address();
    console.log(hotelInfoRequest)
    hotelInfoRequest.hotelName = this.hotelInfoForm.get('hotelName')?.value;
    hotelInfoRequest.hotelDescription = this.hotelInfoForm.get('hotelDescription')?.value;
    hotelInfoRequest.address.street = this.hotelInfoForm.get('address.street')?.value;
    hotelInfoRequest.address.city = this.hotelInfoForm.get('address.city')?.value;
    hotelInfoRequest.address.state = this.hotelInfoForm.get('address.state')?.value;
    hotelInfoRequest.address.zipCode = this.hotelInfoForm.get('address.zipCode')?.value;
    this.hotelService.updateHotelInfo(this.hotel.id, hotelInfoRequest).subscribe(res => {
      console.log(res)
      alert("Hotel Info successfully updated");


    }, err => {

      alert("Hotel Info failed to update");


    });

  }

  //validator for form
  public noWhitespaceValidator(control: FormControl) {
    const isWhitespace = (control.value || '').trim().length === 0;
    const isValid = !isWhitespace;
    return isValid ? null : { 'whitespace': true };
  }

  //getters to make validetion work in html
  //for step 1
  get hotelName() { return this.hotelInfoForm.get('hotelName'); }
  get hotelDescription() { return this.hotelInfoForm.get('hotelDescription'); }
 
  get street() { return this.hotelInfoForm.get('address.street'); }
  get city() { return this.hotelInfoForm.get('address.city'); }
  get state() { return this.hotelInfoForm.get('address.state'); }
  get zipCode() { return this.hotelInfoForm.get('address.zipCode'); }
}
