import { HttpClient, HttpEvent, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { NgbDateStructAdapter } from '@ng-bootstrap/ng-bootstrap/datepicker/adapters/ngb-date-adapter';
import { map, Observable } from 'rxjs';
import { AvailableRooms } from '../classes/available-rooms';
import { Hotel } from '../classes/hotel';
import { HotelInfoRequest } from '../classes/hotel-info-request';
import { HotelRegister } from '../classes/hotel-register';
import { HotelServiceClass } from '../classes/hotel-service-class';
import { Image } from '../classes/image';
import { environment } from 'src/environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class HotelService {
  baseUrl = environment.apiUrl + '/api/hotel';

  constructor(private httpClient: HttpClient) { }

  postHotel(data: any) {
    return this.httpClient.post<any>(this.baseUrl + '/setHotel', data, httpOptions)
      .pipe(map((res: any) => {
        return res;
      }));
  }
  updateHotelInfo(hotelId: number, hotelInfoRequest: HotelInfoRequest) {
    return this.httpClient.put<any>(this.baseUrl + '/updateHotelInfo/' + `${hotelId}/`, hotelInfoRequest, httpOptions)
      .pipe(map((res: any) => {
        return res;
      }));
  }
  updateAvailableRooms(hotelId: number, availableRooms:AvailableRooms[]) {
    return this.httpClient.put<any>(this.baseUrl + '/updateAvailableRooms/' + `${hotelId}/`, availableRooms, httpOptions)
      .pipe(map((res: any) => {
        return res;
      }));
  }

  updateHotelServices(hotelId: number, hotelServices: HotelServiceClass[]) {
    return this.httpClient.put<any>(this.baseUrl + '/updateHotelServices/' + `${hotelId}/`, hotelServices, httpOptions)
      .pipe(map((res: any) => {
        return res;
      }));
  }
  updateHotelImages(hotelId: number, files: FileList) {
    const formData: FormData = new FormData;

    for (let i = 0; i < files.length; i++) {
      formData.append('files', files[i]);
    }
    const blobOverrides = new Blob([JSON.stringify(hotelId)], {
      type: 'application/json',
    });
    formData.append("hotelId", blobOverrides);
    console.log(formData);
    return this.httpClient.put<any>(this.baseUrl + '/updateHotelImages/', formData )
      .pipe(map((res: any) => {
        return res;
      }));
  }
  deleteHotelImageById(hotelId: number, hotelImageId: number) {
    return this.httpClient.delete<any>(this.baseUrl + '/deleteHotelImageById/' + `${hotelId}/` + `${hotelImageId}/`, httpOptions)
      .pipe(map((res: any) => {
        return res;
      }));
  }
 
  getHotels(): Observable<any> {
    return this.httpClient.get<any>(this.baseUrl + '/getHotels')
      .pipe(map((res: any) => {
        return res;
      }));
  }
  getHotelById(hotelId:number): Observable<any> {
    return this.httpClient.get<any>(this.baseUrl + '/getHotelById/' + `${hotelId}/`,httpOptions )
      .pipe(map((res: any) => {
        return res;
      }));
  }
  getHotelByUserId(userId: number): Observable<any> {
    return this.httpClient.get<any>(this.baseUrl + '/getHotelByUserId/' + `${userId}/`)
      .pipe(map((res: any) => {
        return res;
      }));
  }
  getHotelsPaginated(pageNumber: number, pageSize: number): Observable<any> {
    return this.httpClient.get<any>(this.baseUrl + '/getHotelsPaginated' + `?page=${pageNumber}&size=${pageSize}`)
      .pipe(map((res: any) => {
        return res;
      }));
  }
  getHotelsPaginatedWithSearchCriteria(pageNumber: number, pageSize: number, stringFromDate: string, stringToDate: string, location: string, numberOfPersons: number, numberOfRooms: number): Observable<any> {
    const url = this.baseUrl + '/getHotelsPaginatedWithSearchCriteria/' + `${stringFromDate}/` + `${stringToDate}/` + `${location}/` + `${numberOfPersons}/` + `${numberOfRooms}/` + `?page=${pageNumber}&size=${pageSize}`;
    console.log('🔍 Search API URL:', url);
    console.log('📅 From:', stringFromDate, '| To:', stringToDate);
    console.log('📍 Location:', location);
    console.log('👥 Persons:', numberOfPersons, '| Rooms:', numberOfRooms);

    return this.httpClient.get<any>(url)
      .pipe(map((res: any) => {
        console.log('✅ Search Results:', res);
        return res;
      }));
  }
  hotelRegistration(data: any, files: FileList): Observable<HttpEvent<any>>{
    const formData: FormData = new FormData;
    
    for (let i = 0; i < files.length; i++) {
      formData.append('files', files[i]);
    }
    const blobOverrides = new Blob([JSON.stringify(data)], {
      type: 'application/json',
    });
    formData.append("hotel", blobOverrides);
    console.log(formData);

    return this.httpClient.post<any>(`${this.baseUrl}/setHotel`, formData)
      .pipe(map(res => {
        return res;
      }));


  }
}
