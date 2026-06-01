import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Booking } from '../../shared/models/models';
import { BookingService } from '../../shared/services/booking.service';

@Component({
  selector: 'app-booking-list',
  templateUrl: './booking-list.component.html',
  styleUrls: ['./booking-list.component.scss']
})
export class BookingListComponent implements OnInit {
  bookings: Booking[] = [];
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;
  sortBy = 'checkInDate';
  loading = false;
  error = '';
  successMsg = '';

  constructor(private bookingService: BookingService, private router: Router) {}

  ngOnInit(): void { this.loadBookings(); }

  loadBookings(): void {
    this.loading = true;
    this.error = '';
    this.bookingService.getAll(this.currentPage, this.pageSize, this.sortBy).subscribe({
      next: page => {
        this.bookings = page.content;
        this.totalPages = page.totalPages;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load bookings.';
        this.loading = false;
      }
    });
  }

  onSortChange(sort: string): void {
    this.sortBy = sort;
    this.currentPage = 0;
    this.loadBookings();
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadBookings();
  }

  cancel(id: number): void {
    if (!confirm('Cancel this booking?')) return;
    this.bookingService.cancel(id).subscribe({
      next: () => {
        this.successMsg = 'Booking cancelled.';
        this.loadBookings();
        setTimeout(() => this.successMsg = '', 3000);
      },
      error: err => this.error = err.error?.message || 'Cancel failed'
    });
  }

  delete(id: number): void {
    if (!confirm('Delete this booking?')) return;
    this.bookingService.delete(id).subscribe({
      next: () => this.loadBookings(),
      error: err => this.error = err.error?.message || 'Delete failed'
    });
  }

  statusBadge(status: string): string {
    const map: Record<string, string> = {
      CONFIRMED: 'badge-success', CANCELLED: 'badge-danger',
      PENDING: 'badge-warning', COMPLETED: 'badge-secondary'
    };
    return map[status] || 'badge-secondary';
  }
}
