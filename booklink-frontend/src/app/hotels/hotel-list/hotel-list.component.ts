import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Hotel } from '../../shared/models/models';
import { HotelService } from '../../shared/services/hotel.service';

@Component({
  selector: 'app-hotel-list',
  templateUrl: './hotel-list.component.html',
  styleUrls: ['./hotel-list.component.scss']
})
export class HotelListComponent implements OnInit {
  hotels: Hotel[] = [];
  totalPages = 0;
  currentPage = 0;
  pageSize = 10;
  sortBy = 'name';
  loading = false;
  error = '';

  constructor(private hotelService: HotelService, private router: Router) {}

  ngOnInit(): void {
    this.loadHotels();
  }

  loadHotels(): void {
    this.loading = true;
    this.error = '';
    this.hotelService.getAll(this.currentPage, this.pageSize, this.sortBy).subscribe({
      next: page => {
        this.hotels = page.content;
        this.totalPages = page.totalPages;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load hotels. Make sure the backend is running.';
        this.loading = false;
      }
    });
  }

  onSortChange(sort: string): void {
    this.sortBy = sort;
    this.currentPage = 0;
    this.loadHotels();
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadHotels();
  }

  edit(id: number): void {
    this.router.navigate(['/hotels', id, 'edit']);
  }

  delete(id: number): void {
    if (!confirm('Are you sure you want to delete this hotel?')) return;
    this.hotelService.delete(id).subscribe({
      next: () => this.loadHotels(),
      error: err => this.error = err.error?.message || 'Delete failed'
    });
  }

  stars(n: number): string {
    return '★'.repeat(n || 0);
  }
}
