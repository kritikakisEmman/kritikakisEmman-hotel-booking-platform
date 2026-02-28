import { DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, Input, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormControl, NgForm, ValidatorFn, Validators } from '@angular/forms';
import { isDefined } from '@ng-bootstrap/ng-bootstrap/util/util';
import { Observable } from 'rxjs';
import { AvailableRooms } from '../classes/available-rooms';
import { Hotel } from '../classes/hotel';
import { HotelRegister } from '../classes/hotel-register';
import { HotelServiceClass } from '../classes/hotel-service-class';
import { NoEmptySpacesAllowedValidator } from '../classes/no-empty-spaces-allowed-validator';
import { User } from '../classes/user';
import { HotelService } from '../services/hotel.service';
import { ImageService } from '../services/image.service';
import { AuthService } from '../_services/auth.service';


declare var $: any;
@Component({
  selector: 'app-hotel-registration-form',
  templateUrl: './hotel-registration-form.component.html',
  styleUrls: ['./hotel-registration-form.component.css']
 
})
export class HotelRegistrationFormComponent implements OnInit {
  minForFromDate: any = new Date();
  minForToDate: any = new Date();
  emptyRoomsVariable: boolean = false;
  addRoomClicked: boolean= false;
  steps: string[] = ["Hotel Info", "Rooms", "Services", "Pictures","Sign Up"];
  currentStep: number = 1;
  hotel!: Hotel;
  availableRooms: AvailableRooms[] = [];
  roomTypes: String[] = ['Si', 'Db', 'Tr'];
  selectedType = 'Si';
  hotelServices: String[] = ['Wifi', 'Parking', 'Gym', 'Pool', 'Spa', 'Bar', 'Breakfast', 'Restorant', 'Pool Bar', 'Garden','Play Room','ATM'];
  selectedFiles!: FileList;
  progressInfos: any[] = [];
  message: string[] = [];
  hotelRegisterWrapper!: HotelRegister;
  previews: string[] = [];
  imageInfos?: Observable<any>;

  isSignupSuccessful!: boolean;

  msgToChild: boolean = false;
  form: any = {
    username: null,
    email: null,
    password: null
  };
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';
  roles: string[] = ['user'];

  //This form takes the values from the 1st step
  hotelInfoForm = this.fb.group({
    hotelName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), this.noWhitespaceValidator]],
    hotelDescription: ['', [Validators.required, Validators.minLength(100), this.noWhitespaceValidator]],
    address: this.fb.group({
      street: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), this.noWhitespaceValidator]],
      city: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), this.noWhitespaceValidator]],
      state: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), this.noWhitespaceValidator]],
      zipCode: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(5), Validators.pattern('^[0-9]*$'),this.noWhitespaceValidator]]
    })

  });
  //This form takes the values from the 2nd step
  availableRoomsDateForm = this.fb.group({
    fromDate: ['',[ Validators.required, this.fromDateValidator]],
    toDate: ['', [Validators.required, this.toDateValidator]],


  });

  
  //This form takes the values from the 2nd step
  availableRoomsForm = this.fb.group({
   
    roomName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(20), NoEmptySpacesAllowedValidator.notWhitespaceAtAll]],
    roomType: ['', Validators.required],
    quantity: ['', [Validators.required, Validators.min(1)]],
    roomPrice: ['', [Validators.required, Validators.min(1)]],

  });

  servicesForm = this.fb.group({
    selectedServices: new FormArray([])
  });
  constructor(private datePipe: DatePipe, private fb: FormBuilder, private hotelService: HotelService, private imageService: ImageService, private authService: AuthService) {


  }

  ngOnInit(): void {

    this.minForToDate.setDate(this.minForToDate.getDate() + 1);

  }
  selectFiles(event: any): void {
    this.message = [];
    this.progressInfos = [];
    this.selectedFiles = event.target.files;

    this.previews = [];
    if (this.selectedFiles && this.selectedFiles[0]) {
      const numberOfFiles = this.selectedFiles.length;
      for (let i = 0; i < numberOfFiles; i++) {
        const reader = new FileReader();

        reader.onload = (e: any) => {
          this.previews.push(e.target.result);
        };

        reader.readAsDataURL(this.selectedFiles[i]);
      }
    }
  }
  uploadFiles(): void {
    if (this.selectedFiles) {
      
        this.imageService.upload(this.selectedFiles).subscribe(
          res => {
            console.log(res);
          
        });
      
    }

  }
  
  onCheckboxChange(event: any) {

    const selectedServices = (this.servicesForm.controls['selectedServices'] as FormArray);
    if (event.target.checked) {
      selectedServices.push(new FormControl(event.target.value));
    } else {
      const index = selectedServices.controls
        .findIndex(x => x.value === event.target.value);
      selectedServices.removeAt(index);
    }
   
  }

  
  addRoom() {
    if (this.availableRoomsDateForm.invalid && this.currentStep == 2) {
      this.availableRoomsDateForm.markAllAsTouched();
      console.log("entered here 1");
      return;
    }

    $('#exampleModal').modal('show');

    this.addRoomClicked = true;
    this.selectedType = 'Si';
  }
  next() {
    //Triger validation for first step 
    if (this.hotelInfoForm.invalid && this.currentStep==1) {
      this.hotelInfoForm.markAllAsTouched();
      console.log("entered here 1");
      return;
    }
    if (this.availableRoomsDateForm.invalid && this.currentStep == 2) {
      this.availableRoomsDateForm.markAllAsTouched();
      console.log("entered here 1");
      return;
    }
    
    //Triger validation for second step
    if (this.availableRooms.length == 0 && this.currentStep == 2) {
      console.log("entered here 2");
      return;
    }
    
    //Triger validation for forth step
    if (this.selectedFiles == undefined && this.currentStep == 4) {
      console.log("no files ");
      return;
    }

    console.log(this.hotelInfoForm.value);

    
   /* if (this.currentStep == 1) {
      this.hotel = this.hotelInfoForm.value;
      this.hotelService.postHotel(JSON.stringify(this.hotelInfoForm.value)).subscribe(res => {
        console.log(res);
      });
    }
   */
    if (this.currentStep < this.steps.length + 1) {
      console.log("entered here 3");
      this.currentStep++;
      console.log("currentStep== " + this.currentStep)
    }
    
  }

  previous() {
    if (this.currentStep > 1)
      this.currentStep--;
    console.log("currentStep== " + this.currentStep)
  }
  addAvailableRooms() {
    //Triger validation for second step modal values
    if (this.availableRoomsForm.invalid && this.currentStep == 2) {
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
    this.selectedType = 'Si';
  }
  deleteAvailableRooms(id: number) {
    for (let i = 0; i < this.availableRooms.length; i++) {
      if (id = this.availableRooms[i].number) {
        this.availableRooms.splice(i, 1);
        break;
      }
    }

  }
   complete() {
     const { username, email, password } = this.form;
     this.authService.register(username, email, password, this.roles).subscribe(
       data => {
         console.log(data);
         this.isSuccessful = true;
         this.isSignUpFailed = false;
         // Gather all  the required values for hotel registration

         //gather values from 1st step PS hotel info 
         this.hotel = this.hotelInfoForm.value;

         //gather values from 2nd step PS available rooms
         this.hotel.availableRooms = [];
         this.availableRooms.forEach(val => this.hotel.availableRooms?.push(Object.assign({}, val)));

         //gather values from 3rd step PS hotel services
         //retrieving the values from the form
         this.hotel.hotelServices = [];
         var arrayControl = this.servicesForm.controls['selectedServices'] as FormArray;
         //copy the values into the hotel object
         for (let i = 0; i < arrayControl.length; i++) {
           var hotelService = new HotelServiceClass();
           hotelService.serviceName = arrayControl.at(i).value;
           this.hotel.hotelServices?.push(hotelService);
           // console.log(this.hotel.hotelServices);
         }

          //gather values from 5rd step PS register
         //retrieving the values from the form
         let tempUser: User = new User;;
         tempUser.username = username;
         tempUser.email = email;
         tempUser.password = password;

         this.hotel.user = tempUser;
         //Give the hotel images and hotel  as argument and register the hotel in to the system with Post
         console.log(JSON.stringify(this.hotel))
         this.hotelService.hotelRegistration(this.hotel, this.selectedFiles).subscribe(data => {
           this.currentStep++;
           console.log("currentStep== " + this.currentStep)

         }, err => { console.log("perror") }         );
       },
       err => {
         this.errorMessage = err.error.message;
         this.isSignUpFailed = true;

       }
     );
   


     
      
    }
  
     
      
    
     
   
  
  
  selectChangeHandler(event: any) {
    //update the ui

  }

  //getters to make validetion work in html
  //for step 1
  get hotelName() { return this.hotelInfoForm.get('hotelName'); }
  get hotelDescription() { return this.hotelInfoForm.get('hotelDescription'); }
  get street() { return this.hotelInfoForm.get('address.street'); }
  get city() { return this.hotelInfoForm.get('address.city'); }
  get state() { return this.hotelInfoForm.get('address.state'); }
  get zipCode() { return this.hotelInfoForm.get('address.zipCode'); }

  //getters for step 2 dates
  get fromDate() { return this.availableRoomsDateForm.get('fromDate'); }
  get toDate() { return this.availableRoomsDateForm.get('toDate'); }
   //getters for step 2 values
  get roomName() { return this.availableRoomsForm.get('roomName'); }
  get roomType() { return this.availableRoomsForm.get('roomType'); }
  get quantity() { return this.availableRoomsForm.get('quantity'); }
  get roomPrice() { return this.availableRoomsForm.get('roomPrice'); }

  get selectedServices() {
    return this.servicesForm.controls["selectedServices"] as FormArray;
  }
  takeSignupValue(isSuccessfull: boolean) {
    this.isSignupSuccessful = isSuccessfull;
  }
  onSubmit(): void {
    const { username, email, password } = this.form;
    this.authService.register(username, email, password, this.roles).subscribe(
      data => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        
      },
      err => {
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
       
      }
    );
  }
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

  // Helper function to check if a step is completed
  isStepCompleted(stepNumber: number): boolean {
    return this.currentStep > stepNumber;
  }
}
