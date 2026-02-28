

import { AvailableRooms } from "./available-rooms";
import { Image } from "./image";
import { HotelServiceClass } from "./hotel-service-class";
import { User } from "./user";
import { Availability } from "./availability";
import { Address } from "./address";
import { Reservation } from "./reservation";


export class Hotel {
  id!: number;
  hotelName!: string;
  hotelDescription!: string;
 
  address!: Address;
  availableRooms!: AvailableRooms[];
  hotelServices!: HotelServiceClass[];
  hotelImages!: Image[];
  user!: User;
  hotelAvailabilities !: Availability[];
  hotelReservations !: Reservation[];
}
