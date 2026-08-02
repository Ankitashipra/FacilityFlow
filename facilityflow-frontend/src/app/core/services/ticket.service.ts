import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { MaintenanceTicket } from '../models/ticket.model';
import { TicketStatus } from '../models/enums';

@Injectable({ providedIn: 'root' })
export class TicketService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/tickets`;

  list(page = 0, size = 20, status?: TicketStatus): Observable<ApiResponse<PageResponse<MaintenanceTicket>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<ApiResponse<PageResponse<MaintenanceTicket>>>(this.base, { params });
  }

  myAssignments(page = 0, size = 20): Observable<ApiResponse<PageResponse<MaintenanceTicket>>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<ApiResponse<PageResponse<MaintenanceTicket>>>(`${this.base}/my-assignments`, { params });
  }

  get(id: number): Observable<ApiResponse<MaintenanceTicket>> {
    return this.http.get<ApiResponse<MaintenanceTicket>>(`${this.base}/${id}`);
  }

  create(payload: any): Observable<ApiResponse<MaintenanceTicket>> {
    return this.http.post<ApiResponse<MaintenanceTicket>>(this.base, payload);
  }

  assign(id: number, technicianId: number): Observable<ApiResponse<MaintenanceTicket>> {
    return this.http.post<ApiResponse<MaintenanceTicket>>(`${this.base}/${id}/assign`, { technicianId });
  }

  updateStatus(id: number, status: TicketStatus): Observable<ApiResponse<MaintenanceTicket>> {
    return this.http.patch<ApiResponse<MaintenanceTicket>>(`${this.base}/${id}/status`, { status });
  }

  addComment(id: number, content: string): Observable<ApiResponse<MaintenanceTicket>> {
    return this.http.post<ApiResponse<MaintenanceTicket>>(`${this.base}/${id}/comments`, { content });
  }
}
