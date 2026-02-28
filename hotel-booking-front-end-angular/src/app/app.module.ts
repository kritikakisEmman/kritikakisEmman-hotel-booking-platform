import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { BoardModeratorComponent } from './board-moderator/board-moderator.component';
import { BoardUserComponent } from './board-user/board-user.component';

import { authInterceptorProviders } from './_helpers/auth.interceptor';
import { FooterComponent } from './footer/footer.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { MatStepperModule } from '@angular/material/stepper';
import { HeaderComponent } from './header/header.component';
import { CheckAvailabilityFormComponent } from './check-availability-form/check-availability-form.component';
import { RoomAvailabilityBoardComponent } from './room-availability-board/room-availability-board.component';
import { NavbarComponent } from './navbar/navbar.component';
import { AvailableRoomsBoardComponent } from './available-rooms-board/available-rooms-board.component';
import { HotelsBoardComponent } from './hotels-board/hotels-board.component';
import { HotelRegistrationFormComponent } from './hotel-registration-form/hotel-registration-form.component';
import { DatePipe } from '@angular/common';
import { HotelAvailabilityComponent } from './hotel-availability/hotel-availability.component';
import { ReservationComponent } from './reservation/reservation.component';
import { SideBarComponent } from './side-bar/side-bar.component';
import { HotelInfoComponent } from './hotel-info/hotel-info.component';
import { AvailableRoomsComponent } from './available-rooms/available-rooms.component';
import { HotelServicesComponent } from './hotel-services/hotel-services.component';
import { ReservationDashboardComponent } from './reservation-dashboard/reservation-dashboard.component';
import { HotelImagesComponent } from './hotel-images/hotel-images.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    ProfileComponent,
    BoardAdminComponent,
    BoardModeratorComponent,
    BoardUserComponent,
    FooterComponent,
    HeaderComponent,
    CheckAvailabilityFormComponent,
    RoomAvailabilityBoardComponent,
    NavbarComponent,
    AvailableRoomsBoardComponent,
    HotelsBoardComponent,
    HotelRegistrationFormComponent,
    HotelAvailabilityComponent,
    ReservationComponent,
    SideBarComponent,
    HotelInfoComponent,
    AvailableRoomsComponent,
    HotelServicesComponent,
    ReservationDashboardComponent,
    HotelImagesComponent,
 

  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    NgbModule,
    ReactiveFormsModule,
    MatStepperModule
  ],
  providers: [authInterceptorProviders, DatePipe],
  bootstrap: [AppComponent]
})
export class AppModule { }
