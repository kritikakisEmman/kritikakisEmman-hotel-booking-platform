import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { Availability } from '../classes/availability';
import { BookingAppReservationRequest } from '../classes/booking-app-reservation-request';
import { environment } from 'src/environments/environment';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class RmsService {
  baseUrl = environment.apiUrl; myBaseUrl = environment.apiUrl;
  constructor(private httpClient: HttpClient) { }
  getAvailability(): Observable<any> {
    return this.httpClient.get<any>(this.baseUrl +'/noJwt/getBookingAppAvailability');
     
  }
  postAvailability(availability: Availability[]): Observable<any> {
    return this.httpClient.post<any>(this.myBaseUrl + '/api/hotel/availability' , availability);

  }
  postReservationToRms(bookingAppReservationRequest: BookingAppReservationRequest): Observable<any> {
    return this.httpClient.post<any>(this.baseUrl + '/api/hotel/availability', bookingAppReservationRequest);

  }
}
