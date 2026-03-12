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
    this.loadUsers();
  }

  loadUsers(): void {
    this.adminService.getUsers().subscribe(res => {
      this.users = res;
    }, err => {
      console.log(err);
    });
  }

  deleteUser(userId: number): void {
    this.adminService.deleteUser(userId).subscribe(() => {
      this.loadUsers();
    }, err => {
      console.log(err);
    });
  }
}
