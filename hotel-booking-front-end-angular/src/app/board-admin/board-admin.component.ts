import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AdminService } from '../_services/admin.service';
import { User } from '../classes/user';

@Component({
  selector: 'app-board-admin',
  templateUrl: './board-admin.component.html',
  styleUrls: ['./board-admin.component.css'],
})
export class BoardAdminComponent implements OnInit {
  users?: User[];

  constructor(private adminService: AdminService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    console.log('loadUsers called');
    this.adminService.getUsers().subscribe(res => {
      console.log('users received:', res);
      this.users = res;
      this.cdr.detectChanges();
    }, err => {
      console.log('loadUsers error:', err);
    });
  }

  deleteUser(userId: number): void {
    this.adminService.deleteUser(userId).subscribe(res => {
      console.log('delete success:', res);
      this.loadUsers();
    }, err => {
      console.log('delete error:', err);
    });
  }
}
