import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Asset } from '../models/asset.model';
import { AssetStatus } from '../models/enums';

@Injectable({ providedIn: 'root' })
export class AssetService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/assets`;

  list(page = 0, size = 20, opts: { roomId?: number; status?: AssetStatus; search?: string } = {}): Observable<ApiResponse<PageResponse<Asset>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (opts.roomId) params = params.set('roomId', opts.roomId);
    if (opts.status) params = params.set('status', opts.status);
    if (opts.search) params = params.set('search', opts.search);
    return this.http.get<ApiResponse<PageResponse<Asset>>>(this.base, { params });
  }

  get(id: number): Observable<ApiResponse<Asset>> {
    return this.http.get<ApiResponse<Asset>>(`${this.base}/${id}`);
  }

  create(payload: any): Observable<ApiResponse<Asset>> {
    return this.http.post<ApiResponse<Asset>>(this.base, payload);
  }

  update(id: number, payload: any): Observable<ApiResponse<Asset>> {
    return this.http.put<ApiResponse<Asset>>(`${this.base}/${id}`, payload);
  }

  delete(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/${id}`);
  }
}
