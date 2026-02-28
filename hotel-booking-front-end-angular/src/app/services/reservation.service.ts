import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs';
import { environment } from 'src/environments/environment';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  baseUrl = environment.apiUrl + '/api/reservation';
  constructor(private httpClient: HttpClient) { }

  postReservation(data: any) {
    return this.httpClient.post<any>(this.baseUrl + '/setReservation', data, httpOptions)
      .pipe(map((res: any) => {
        return res;
      }));
  }
  deleteReservation(hotelId: number, reservationId: number) {
    return this.httpClient.delete<any>(this.baseUrl + '/deleteReservation/' + `${hotelId}/` + `${reservationId}/`, httpOptions)
      .pipe(map((res: any) => {
        return res;
      }));
  }
}
