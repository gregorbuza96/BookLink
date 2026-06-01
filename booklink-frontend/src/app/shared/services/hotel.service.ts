import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Hotel, Page } from '../models/models';

@Injectable({ providedIn: 'root' })
export class HotelService {
  private readonly API = environment.apiUrl + '/hotels';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, sort = 'name'): Observable<Page<Hotel>> {
    const params = new HttpParams()
      .set('page', page).set('size', size).set('sort', sort);
    return this.http.get<Page<Hotel>>(this.API, { params });
  }

  getById(id: number): Observable<Hotel> {
    return this.http.get<Hotel>(`${this.API}/${id}`);
  }

  create(hotel: Hotel): Observable<Hotel> {
    return this.http.post<Hotel>(this.API, hotel);
  }

  update(id: number, hotel: Hotel): Observable<Hotel> {
    return this.http.put<Hotel>(`${this.API}/${id}`, hotel);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
