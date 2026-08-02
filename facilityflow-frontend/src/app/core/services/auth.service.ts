import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../models/user.model';
import { TokenStorageService } from './token-storage.service';

/**
 * Holds the current auth state as signals so the whole app (sidebar,
 * guards, role-gated buttons) reacts to login/logout without manual
 * subscriptions.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private tokenStorage = inject(TokenStorageService);

  private readonly baseUrl = `${environment.apiUrl}/auth`;

  currentUser = signal<User | null>(this.tokenStorage.getUser());
  isAuthenticated = computed(() => this.currentUser() !== null);
  role = computed(() => this.currentUser()?.role ?? null);
  isAdmin = computed(() => this.role() === 'ADMIN');
  isManagerOrAdmin = computed(() => this.role() === 'ADMIN' || this.role() === 'FACILITY_MANAGER');

  login(request: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/login`, request).pipe(
      tap(res => this.persistSession(res.data)),
    );
  }

  register(request: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    return this.http.post<ApiResponse<AuthResponse>>(`${this.baseUrl}/register`, request).pipe(
      tap(res => this.persistSession(res.data)),
    );
  }

  logout(): void {
    const refreshToken = this.tokenStorage.getRefreshToken();
    if (refreshToken) {
      this.http.post(`${this.baseUrl}/logout`, { refreshToken }).subscribe({ complete: () => this.finishLogout() });
    } else {
      this.finishLogout();
    }
  }

  private finishLogout(): void {
    this.tokenStorage.clear();
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  private persistSession(auth: AuthResponse): void {
    this.tokenStorage.setSession(auth.accessToken, auth.refreshToken, auth.user);
    this.currentUser.set(auth.user);
  }
}
