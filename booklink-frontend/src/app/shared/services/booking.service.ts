import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Booking, Page } from '../models/models';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private readonly API = environment.apiUrl + '/bookings';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, sort = 'checkInDate'): Observable<Page<Booking>> {
    const params = new HttpParams()
      .set('page', page).set('size', size).set('sort', sort);
    return this.http.get<Page<Booking>>(this.API, { params });
  }

  getById(id: number): Observable<Booking> {
    return this.http.get<Booking>(`${this.API}/${id}`);
  }

  getByUser(userId: number, page = 0, size = 10): Observable<Page<Booking>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Booking>>(`${this.API}/user/${userId}`, { params });
  }

  create(booking: Booking): Observable<Booking> {
    return this.http.post<Booking>(this.API, booking);
  }

  update(id: number, booking: Booking): Observable<Booking> {
    return this.http.put<Booking>(`${this.API}/${id}`, booking);
  }

  cancel(id: number): Observable<void> {
    return this.http.patch<void>(`${this.API}/${id}/cancel`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
