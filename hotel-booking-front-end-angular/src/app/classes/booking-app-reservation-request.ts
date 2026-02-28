import { ReservationRequest } from "./reservation-request";
import { ReservationTypeRequest } from "./reservation-type-request";

export class BookingAppReservationRequest {
  resesvation!: ReservationRequest;
  reservationTypes!: ReservationTypeRequest[];
}
