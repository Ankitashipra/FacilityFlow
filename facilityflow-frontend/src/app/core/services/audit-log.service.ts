import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { AuditLog } from '../models/audit-log.model';
import { AuditAction } from '../models/enums';

@Injectable({ providedIn: 'root' })
export class AuditLogService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/audit-logs`;

  list(page = 0, size = 20, action?: AuditAction): Observable<ApiResponse<PageResponse<AuditLog>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (action) params = params.set('action', action);
    return this.http.get<ApiResponse<PageResponse<AuditLog>>>(this.base, { params });
  }
}
