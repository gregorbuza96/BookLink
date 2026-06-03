import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map, of } from 'rxjs';

export interface AuthUser {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
}

export interface LoginRequest {
  username: string;
  password: string;
}

interface LoginResponse extends AuthUser {
  token: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = environment.apiUrl + '/auth';
  private readonly TOKEN_KEY = 'booklink_token';
  private readonly USER_KEY = 'booklink_user';

  // Restore the session from localStorage so a page refresh keeps the user logged in.
  private currentUserSubject = new BehaviorSubject<AuthUser | null>(this.loadStoredUser());
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  get token(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  get currentUser(): AuthUser | null {
    return this.currentUserSubject.value;
  }

  get isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  get isAdmin(): boolean {
    return this.currentUserSubject.value?.role === 'ADMIN';
  }

  login(credentials: LoginRequest): Observable<AuthUser> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      map(res => {
        const user: AuthUser = { id: res.id, username: res.username, email: res.email, role: res.role };
        localStorage.setItem(this.TOKEN_KEY, res.token);
        localStorage.setItem(this.USER_KEY, JSON.stringify(user));
        this.currentUserSubject.next(user);
        return user;
      })
    );
  }

  // JWT is stateless — logging out just clears local state (no backend call).
  logout(): Observable<void> {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
    return of(void 0);
  }

  private loadStoredUser(): AuthUser | null {
    const raw = localStorage.getItem(this.USER_KEY);
    try {
      return raw ? (JSON.parse(raw) as AuthUser) : null;
    } catch {
      return null;
    }
  }
}
