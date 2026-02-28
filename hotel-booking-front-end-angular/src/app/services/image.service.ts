import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  private baseUrl = environment.apiUrl;
  constructor(private http: HttpClient) { }
  upload(files: FileList): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData; 

    for (let i = 0; i < files.length; i++) {
      formData.append('files', files[i]);
    }


    
    return this.http.post<any>(`${this.baseUrl}/api/hotel/setImage`, formData)
      .pipe(map(res => {
        return res;
      }));
  
  }
}
