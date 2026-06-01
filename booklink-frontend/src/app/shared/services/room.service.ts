import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Room, Page } from '../models/models';

@Injectable({ providedIn: 'root' })
export class RoomService {
  private readonly API = environment.apiUrl + '/rooms';

  constructor(private http: HttpClient) {}

  getAll(page = 0, size = 10, sort = 'roomNumber'): Observable<Page<Room>> {
    const params = new HttpParams()
      .set('page', page).set('size', size).set('sort', sort);
    return this.http.get<Page<Room>>(this.API, { params });
  }

  getById(id: number): Observable<Room> {
    return this.http.get<Room>(`${this.API}/${id}`);
  }

  getByHotel(hotelId: number, page = 0, size = 10): Observable<Page<Room>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Room>>(`${this.API}/hotel/${hotelId}`, { params });
  }

  create(room: Room): Observable<Room> {
    return this.http.post<Room>(this.API, room);
  }

  update(id: number, room: Room): Observable<Room> {
    return this.http.put<Room>(`${this.API}/${id}`, room);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}
