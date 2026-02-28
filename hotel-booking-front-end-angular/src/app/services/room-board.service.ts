import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
const API_URL = 'https://hotel-management-system-app.herokuapp.com/api/rooms';
@Injectable({
  providedIn: 'root'
})
export class RoomBoardService {

  constructor(private http: HttpClient) { }

  public do()
  {
    console.log("this day is going to be awsome!");

  }
  getHotelSpecificsInfo(): Observable<any> {
    return this.http.get(API_URL);
  }
}
