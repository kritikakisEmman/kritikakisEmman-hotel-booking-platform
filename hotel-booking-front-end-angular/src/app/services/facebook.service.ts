import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class FacebookService {

  constructor(private httpClient: HttpClient) { }
  post() {
    return this.httpClient.post<any>("https://graph.facebook.com/103702075987548/photos?url=https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg&message=gasdasd&access_token=EAAvRY63BlEgBAOuHl4iPUJWbzkClsXKYtCiGHgLSdXSjkFplhXZCbzmvV7X49yGvRiJGv8WXH2ZAihZBHd85zyA6SESJGQ23ZBODKSlZBPolriAZBZAqAgOidUXJztbg48eK4Xo3ZCy4jk26WGZAYQkXmEzpkaNz4I10S2S34ELGAtz4nPKeVrL3w5wh83ALFzxTuwZCHTjpJrTQZDZD" ,  httpOptions)
      .pipe(map((res: any) => {
        return res;
      }));
  }
}
