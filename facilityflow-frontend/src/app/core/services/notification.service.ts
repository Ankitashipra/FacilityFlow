import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { AppNotification } from '../models/notification.model';
import { signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/notifications`;

  unreadCount = signal(0);

  list(page = 0, size = 20): Observable<ApiResponse<PageResponse<AppNotification>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<AppNotification>>>(this.base, { params });
  }

  refreshUnreadCount(): void {
    this.http.get<ApiResponse<{ unread: number }>>(`${this.base}/unread-count`).subscribe({
      next: res => this.unreadCount.set(res.data.unread),
      error: () => {},
    });
  }

  markAsRead(id: number): Observable<ApiResponse<void>> {
    return this.http.patch<ApiResponse<void>>(`${this.base}/${id}/read`, {}).pipe(
      tap(() => this.unreadCount.update(n => Math.max(0, n - 1))),
    );
  }
}
