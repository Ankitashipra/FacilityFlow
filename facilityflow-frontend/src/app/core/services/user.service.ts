import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { User } from '../models/user.model';
import { Role } from '../models/enums';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/users`;

  list(page = 0, size = 20, opts: { role?: Role; search?: string } = {}): Observable<ApiResponse<PageResponse<User>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (opts.role) params = params.set('role', opts.role);
    if (opts.search) params = params.set('search', opts.search);
    return this.http.get<ApiResponse<PageResponse<User>>>(this.base, { params });
  }

  me(): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>(`${this.base}/me`);
  }

  update(id: number, payload: Partial<User>): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.base}/${id}`, payload);
  }

  changePassword(currentPassword: string, newPassword: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.base}/change-password`, { currentPassword, newPassword });
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/${id}`);
  }
}
