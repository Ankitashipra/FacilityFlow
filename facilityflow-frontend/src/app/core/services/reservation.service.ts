import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Reservation } from '../models/reservation.model';

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/reservations`;

  my(page = 0, size = 20): Observable<ApiResponse<PageResponse<Reservation>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<Reservation>>>(`${this.base}/my`, { params });
  }

  all(page = 0, size = 20): Observable<ApiResponse<PageResponse<Reservation>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<Reservation>>>(this.base, { params });
  }

  get(id: number): Observable<ApiResponse<Reservation>> {
    return this.http.get<ApiResponse<Reservation>>(`${this.base}/${id}`);
  }

  create(payload: any): Observable<ApiResponse<Reservation>> {
    return this.http.post<ApiResponse<Reservation>>(this.base, payload);
  }

  approve(id: number): Observable<ApiResponse<Reservation>> {
    return this.http.post<ApiResponse<Reservation>>(`${this.base}/${id}/approve`, {});
  }

  reject(id: number, reason: string): Observable<ApiResponse<Reservation>> {
    return this.http.post<ApiResponse<Reservation>>(`${this.base}/${id}/reject`, { reason });
  }

  cancel(id: number): Observable<ApiResponse<Reservation>> {
    return this.http.post<ApiResponse<Reservation>>(`${this.base}/${id}/cancel`, {});
  }
}
