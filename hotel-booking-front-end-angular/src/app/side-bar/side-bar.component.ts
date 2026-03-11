import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TokenStorageService } from '../_services/token-storage.service';

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css'],
})
export class SideBarComponent implements OnInit {
  userRole = '';
  constructor(
    private tokenStorageService: TokenStorageService,
    private router:Router

  ) {}

  ngOnInit(): void {
    const user = this.tokenStorageService.getUser();
    const roles = user?.roles;
    if (roles?.includes('ROLE_ADMIN')) {
      this.userRole = 'ROLE_ADMIN';
      this.router.navigate(['/sideBar/admin']);  // ← redirect αμέσως

    } else if (roles?.includes('ROLE_MODERATOR')) {
      this.userRole = 'ROLE_MODERATOR';
      this.router.navigate(['/sideBar/hotelInfo']);  // ← redirect αμέσως

    } else {
      this.userRole = 'ROLE_USER';
    }
    console.log(this.userRole);
  }
}
