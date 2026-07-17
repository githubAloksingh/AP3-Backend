import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest, LoginResponse, SessionUser, SignupRequest, SignupResponse } from '../models/auth.model';

const STORAGE_KEYS = ['token', 'customerId', 'userId', 'fullName', 'email'] as const;

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly apiUrl = environment.apiUrl;
  private readonly sessionSubject = new BehaviorSubject<SessionUser | null>(this.readSession());

  readonly session$ = this.sessionSubject.asObservable();

  signup(payload: SignupRequest): Observable<SignupResponse> {
    return this.http.post<SignupResponse>(`${this.apiUrl}/auth/signup`, payload);
  }

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, payload).pipe(
      tap((response) => this.storeLoginResponse(response))
    );
  }

  logout(): void {
    this.clearSession();
    this.router.navigate(['/login']);
  }

  expireSession(): void {
    this.clearSession();
    this.router.navigate(['/login'], {
      queryParams: { sessionExpired: 'true' }
    });
  }

  isAuthenticated(): boolean {
    return Boolean(this.getToken());
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getCustomerId(): number | null {
    const value = localStorage.getItem('customerId');
    return value ? Number(value) : null;
  }

  getFullName(): string {
    return localStorage.getItem('fullName') ?? '';
  }

  getEmail(): string {
    return localStorage.getItem('email') ?? '';
  }

  isAdmin(): boolean {
    const email = this.getEmail().toLowerCase();
    return email === 'admin@gmail.com';
  }

  private readSession(): SessionUser | null {
    if (typeof window === 'undefined') {
      return null;
    }
    const token = localStorage.getItem('token');
    const customerId = localStorage.getItem('customerId');
    const userId = localStorage.getItem('userId');
    const fullName = localStorage.getItem('fullName');
    const email = localStorage.getItem('email');

    if (token && customerId && userId && fullName && email) {
      return {
        id: Number(userId),
        customerId: Number(customerId),
        fullName,
        email,
        role: email.toLowerCase() === 'admin@gmail.com' ? 'ADMIN' : 'USER'
      };
    }
    return null;
  }

  private storeLoginResponse(response: LoginResponse): void {
    if (response.token) {
      localStorage.setItem('token', response.token);
    }
    localStorage.setItem('customerId', String(response.customerId));
    localStorage.setItem('userId', String(response.id));
    localStorage.setItem('fullName', response.fullName);
    localStorage.setItem('email', response.email);

    this.sessionSubject.next({
      id: response.id,
      customerId: response.customerId,
      fullName: response.fullName,
      email: response.email,
      role: response.email.toLowerCase() === 'admin@gmail.com' ? 'ADMIN' : 'USER'
    });
  }

  private clearSession(): void {
    STORAGE_KEYS.forEach((key) => localStorage.removeItem(key));
    this.sessionSubject.next(null);
  }
}
