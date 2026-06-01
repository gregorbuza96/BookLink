import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppUser } from '../../shared/models/models';
import { UserService } from '../../shared/services/user.service';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {
  users: AppUser[] = [];
  loading = false;
  error = '';

  constructor(private userService: UserService, private router: Router) {}

  ngOnInit(): void { this.loadUsers(); }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAll().subscribe({
      next: users => { this.users = users; this.loading = false; },
      error: () => { this.error = 'Failed to load users.'; this.loading = false; }
    });
  }

  edit(id: number): void { this.router.navigate(['/users', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Delete this user?')) return;
    this.userService.delete(id).subscribe({
      next: () => this.loadUsers(),
      error: err => this.error = err.error?.message || 'Delete failed'
    });
  }

  roleBadge(role: string): string {
    return role === 'ADMIN' ? 'badge-danger' : 'badge-secondary';
  }
}
