
import { ReservationType } from "./reservation-type";

export class Reservation {
  id!: number;
  firstName!: string;
  lastName!: string;
  phoneNumber!: string;
  passport!: string;
  fromDate!: Date;
  toDate!: Date;
  hotelId!: number;
  reservationTypes!: ReservationType[];

}
