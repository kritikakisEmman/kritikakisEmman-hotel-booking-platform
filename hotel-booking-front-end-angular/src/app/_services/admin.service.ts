import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const API_URL = environment.apiUrl + '/api/admin/';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http: HttpClient) { }

  getUsers(): Observable<any>{
    return this.http.get(API_URL + 'users');
  }

  deleteUser(userId:number):Observable<any>{
    return this.http.delete(API_URL+`users/${userId}`, {responseType: 'text'})
  }
}
