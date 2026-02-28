import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder, FormControl } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { Hotel } from '../classes/hotel';
import { HotelServiceClass } from '../classes/hotel-service-class';
import { HotelService } from '../services/hotel.service';
import { TokenStorageService } from '../_services/token-storage.service';

@Component({
  selector: 'app-hotel-services',
  templateUrl: './hotel-services.component.html',
  styleUrls: ['./hotel-services.component.css']
})
export class HotelServicesComponent implements OnInit {
  userId!: number;
  hotel: Hotel = new Hotel();
  hotelServices: String[] = ['Wifi', 'Parking', 'Gym', 'Pool', 'Spa', 'Bar', 'Breakfast', 'Restorant', 'Pool Bar', 'Garden', 'Play Room', 'ATM'];
  servicesForm = this.fb.group({
    selectedServices: new FormArray([])
  });

  onCheckboxChange(event: any) {
    const selectedServices = (this.servicesForm.controls['selectedServices'] as FormArray);
    if (event.target.checked) {
      selectedServices.push(new FormControl(event.target.value));
    } else {
      const index = selectedServices.controls
        .findIndex(x => x.value === event.target.value);
      selectedServices.removeAt(index);
    }
    console.log(selectedServices)
  }

  constructor(private fb: FormBuilder, private tokenStorageService: TokenStorageService, private hotelService: HotelService, private sanitizer: DomSanitizer,) { }

  ngOnInit(): void {
    const user = this.tokenStorageService.getUser();
    this.userId = user.id;
    this.hotelService.getHotelByUserId(this.userId).subscribe(res => {
      this.hotel = res;
      console.log(this.hotel);

      for (let i = 0; i < this.hotel.hotelImages.length; i++) {
        let objectURL = 'data:image/jpeg;base64,' + this.hotel.hotelImages[i].data;
        this.hotel.hotelImages[i].data = this.sanitizer.bypassSecurityTrustResourceUrl(objectURL);
      }

      const selectedServices = (this.servicesForm.controls['selectedServices'] as FormArray);
      for (let service of this.hotel.hotelServices) {
        selectedServices.push(new FormControl(service.serviceName));
      }
    });
  }

  isServiceSelected(serviceName: string): boolean {
    if (!this.hotel.hotelServices) return false;
    return this.hotel.hotelServices.some(s => s.serviceName === serviceName);
  }

  update() {
    let hotelServices: HotelServiceClass[] = [];
    var arrayControl = this.servicesForm.controls['selectedServices'] as FormArray;
    for (let i = 0; i < arrayControl.length; i++) {
      var hotelService = new HotelServiceClass();
      hotelService.serviceName = arrayControl.at(i).value;
      hotelServices?.push(hotelService);
    }
    this.hotelService.updateHotelServices(this.hotel.id, hotelServices).subscribe(res => {
      console.log(res);
      alert("suceessfuly updated")
    }, err => {
      console.log("failed")
    });
  }
}
