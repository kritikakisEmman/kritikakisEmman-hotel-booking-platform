import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';
import { BoardUserComponent } from './board-user/board-user.component';
import { BoardModeratorComponent } from './board-moderator/board-moderator.component';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { RoomAvailabilityBoardComponent } from './room-availability-board/room-availability-board.component';
import { AvailableRoomsBoardComponent } from './available-rooms-board/available-rooms-board.component';
import { HotelsBoardComponent } from './hotels-board/hotels-board.component';
import { HotelRegistrationFormComponent } from './hotel-registration-form/hotel-registration-form.component';
import { HotelAvailabilityComponent } from './hotel-availability/hotel-availability.component';
import { ReservationComponent } from './reservation/reservation.component';
import { SideBarComponent } from './side-bar/side-bar.component';
import { HotelInfoComponent } from './hotel-info/hotel-info.component';
import { AvailableRoomsComponent } from './available-rooms/available-rooms.component';
import { HotelServicesComponent } from './hotel-services/hotel-services.component';
import { ReservationDashboardComponent } from './reservation-dashboard/reservation-dashboard.component';
import { HotelImagesComponent } from './hotel-images/hotel-images.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'hotelRegistration', component: HotelRegistrationFormComponent },
  {path: 'availability', component: RoomAvailabilityBoardComponent, children: [
    
      { path: 'availableRooms', component: AvailableRoomsBoardComponent },
      
     
    ]
  },
  { path: 'hotelAvailability', component: HotelAvailabilityComponent },
  {
    path: 'sideBar', component: SideBarComponent, children: [

      { path: 'hotelInfo', component: HotelInfoComponent },
      { path: 'availableRooms', component: AvailableRoomsComponent },
      { path: 'hotelServices', component: HotelServicesComponent },
      { path: 'hotelImages', component: HotelImagesComponent },
      { path: 'reservationDashboard', component: ReservationDashboardComponent }
    ]
  },
  { path: 'reservation', component: ReservationComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'user', component: BoardUserComponent },
  { path: 'mod', component: BoardModeratorComponent },
  { path: 'admin', component: BoardAdminComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

