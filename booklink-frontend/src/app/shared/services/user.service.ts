import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppUser, UserProfile } from '../models/models';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly API = environment.apiUrl + '/users';

  constructor(private http: HttpClient) {}

  getAll(): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(this.API);
  }

  getById(id: number): Observable<AppUser> {
    return this.http.get<AppUser>(`${this.API}/${id}`);
  }

  create(user: AppUser): Observable<AppUser> {
    return this.http.post<AppUser>(this.API, user);
  }

  update(id: number, user: AppUser): Observable<AppUser> {
    return this.http.put<AppUser>(`${this.API}/${id}`, user);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }

  getProfile(userId: number): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.API}/${userId}/profile`);
  }

  saveProfile(userId: number, profile: UserProfile): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.API}/${userId}/profile`, profile);
  }
}
