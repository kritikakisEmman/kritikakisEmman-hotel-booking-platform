import { Component, OnInit } from '@angular/core';
import { AdminService } from '../_services/admin.service';
import { User } from '../classes/user';

@Component({
  selector: 'app-board-admin',
  templateUrl: './board-admin.component.html',
  styleUrls: ['./board-admin.component.css'],
})
export class BoardAdminComponent implements OnInit {
  users?: User[];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.adminService.getUsers().subscribe({
      next: (data: any) => { this.users = data; },
      error: (err: any) => { console.log(err); }
    });
  }

  deleteUser(userId: number): void {
    this.adminService.deleteUser(userId).subscribe({
      next: () => { this.ngOnInit(); },
      error: (err: any) => { console.log(err); }
    });
  }
}
