import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TokenStorageService } from '../_services/token-storage.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  isLoggedIn = false;

  content?: string;
  constructor(private tokenStorageService: TokenStorageService, private router: Router) { }
    
  ngOnInit(): void {
    console.log(!!this.tokenStorageService.getToken)
    this.isLoggedIn = !!this.tokenStorageService.getToken();
   
  }
  logOut() {
    this.tokenStorageService.signOut();
    this.isLoggedIn = false;
    this.router.navigate(["/home"]);
  }
}
