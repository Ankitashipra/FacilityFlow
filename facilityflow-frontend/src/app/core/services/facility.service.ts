import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Building, Floor, Room } from '../models/facility.model';
import { RoomStatus } from '../models/enums';

@Injectable({ providedIn: 'root' })
export class FacilityService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/facilities`;
  private roomsBase = `${environment.apiUrl}/rooms`;

  // Buildings
  listBuildings(page = 0, size = 20, search?: string): Observable<ApiResponse<PageResponse<Building>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);
    return this.http.get<ApiResponse<PageResponse<Building>>>(`${this.base}/buildings`, { params });
  }

  getBuilding(id: number): Observable<ApiResponse<Building>> {
    return this.http.get<ApiResponse<Building>>(`${this.base}/buildings/${id}`);
  }

  createBuilding(payload: Partial<Building>): Observable<ApiResponse<Building>> {
    return this.http.post<ApiResponse<Building>>(`${this.base}/buildings`, payload);
  }

  updateBuilding(id: number, payload: Partial<Building>): Observable<ApiResponse<Building>> {
    return this.http.put<ApiResponse<Building>>(`${this.base}/buildings/${id}`, payload);
  }

  deleteBuilding(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/buildings/${id}`);
  }

  // Floors
  listFloors(buildingId: number): Observable<ApiResponse<Floor[]>> {
    return this.http.get<ApiResponse<Floor[]>>(`${this.base}/buildings/${buildingId}/floors`);
  }

  createFloor(payload: { buildingId: number; floorNumber: number; name?: string }): Observable<ApiResponse<Floor>> {
    return this.http.post<ApiResponse<Floor>>(`${this.base}/floors`, payload);
  }

  deleteFloor(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.base}/floors/${id}`);
  }

  // Rooms
  listRooms(page = 0, size = 20, opts: { floorId?: number; status?: RoomStatus; search?: string } = {}): Observable<ApiResponse<PageResponse<Room>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (opts.floorId) params = params.set('floorId', opts.floorId);
    if (opts.status) params = params.set('status', opts.status);
    if (opts.search) params = params.set('search', opts.search);
    return this.http.get<ApiResponse<PageResponse<Room>>>(this.roomsBase, { params });
  }

  getRoom(id: number): Observable<ApiResponse<Room>> {
    return this.http.get<ApiResponse<Room>>(`${this.roomsBase}/${id}`);
  }

  createRoom(payload: any): Observable<ApiResponse<Room>> {
    return this.http.post<ApiResponse<Room>>(this.roomsBase, payload);
  }

  updateRoom(id: number, payload: any): Observable<ApiResponse<Room>> {
    return this.http.put<ApiResponse<Room>>(`${this.roomsBase}/${id}`, payload);
  }

  deleteRoom(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.roomsBase}/${id}`);
  }
}
