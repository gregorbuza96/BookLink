import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Room } from '../../shared/models/models';
import { RoomService } from '../../shared/services/room.service';

@Component({
  selector: 'app-room-list',
  templateUrl: './room-list.component.html',
  styleUrls: ['./room-list.component.scss']
})
export class RoomListComponent implements OnInit {
  rooms: Room[] = [];
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;
  sortBy = 'roomNumber';
  loading = false;
  error = '';

  constructor(private roomService: RoomService, private router: Router) {}

  ngOnInit(): void { this.loadRooms(); }

  loadRooms(): void {
    this.loading = true;
    this.error = '';
    this.roomService.getAll(this.currentPage, this.pageSize, this.sortBy).subscribe({
      next: page => {
        this.rooms = page.content;
        this.totalPages = page.totalPages;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load rooms.';
        this.loading = false;
      }
    });
  }

  onSortChange(sort: string): void {
    this.sortBy = sort;
    this.currentPage = 0;
    this.loadRooms();
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadRooms();
  }

  edit(id: number): void { this.router.navigate(['/rooms', id, 'edit']); }

  delete(id: number): void {
    if (!confirm('Delete this room?')) return;
    this.roomService.delete(id).subscribe({
      next: () => this.loadRooms(),
      error: err => this.error = err.error?.message || 'Delete failed'
    });
  }

  statusBadge(status: string): string {
    return status === 'AVAILABLE' ? 'badge-success' : 'badge-danger';
  }
}
