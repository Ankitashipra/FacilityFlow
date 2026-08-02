import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FacilityService } from '../../../core/services/facility.service';
import { Building, Floor, Room } from '../../../core/models/facility.model';
import { PageResponse } from '../../../core/models/api-response.model';
import { RoomStatus, RoomType } from '../../../core/models/enums';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ModalComponent } from '../../../shared/components/modal/modal.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { BadgeComponent } from '../../../shared/components/badge/badge.component';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

const ROOM_TYPES: RoomType[] = ['MEETING_ROOM', 'WORKSTATION_AREA', 'CONFERENCE_HALL', 'CABIN', 'SERVER_ROOM', 'UTILITY'];
const ROOM_STATUSES: RoomStatus[] = ['AVAILABLE', 'OCCUPIED', 'UNDER_MAINTENANCE', 'RESERVED', 'DECOMMISSIONED'];

@Component({
  selector: 'app-rooms',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PageHeaderComponent, EmptyStateComponent, ModalComponent, PaginationComponent, BadgeComponent],
  templateUrl: './rooms.component.html',
  styleUrl: './rooms.component.scss',
})
export class RoomsComponent implements OnInit {
  private facilityService = inject(FacilityService);
  private fb = inject(FormBuilder);
  private toast = inject(ToastService);
  auth = inject(AuthService);

  page = signal<PageResponse<Room> | null>(null);
  loading = signal(true);
  showModal = signal(false);
  saving = signal(false);
  statusFilter = signal<RoomStatus | ''>('');
  searchTerm = signal('');

  buildings = signal<Building[]>([]);
  floorsForSelectedBuilding = signal<Floor[]>([]);

  roomTypes = ROOM_TYPES;
  roomStatuses = ROOM_STATUSES;

  form = this.fb.nonNullable.group({
    buildingId: [0, Validators.required],
    floorId: [0, Validators.required],
    name: ['', Validators.required],
    code: ['', Validators.required],
    type: ['MEETING_ROOM' as RoomType, Validators.required],
    capacity: [4, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    this.load(0);
    this.facilityService.listBuildings(0, 100).subscribe(res => this.buildings.set(res.data.content));
  }

  load(pageNumber: number): void {
    this.loading.set(true);
    this.facilityService.listRooms(pageNumber, 12, {
      status: this.statusFilter() || undefined,
      search: this.searchTerm() || undefined,
    }).subscribe({
      next: res => {
        this.page.set(res.data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  onFilterChange(): void {
    this.load(0);
  }

  onBuildingChange(buildingId: number): void {
    this.form.patchValue({ floorId: 0 });
    if (!buildingId) {
      this.floorsForSelectedBuilding.set([]);
      return;
    }
    this.facilityService.listFloors(buildingId).subscribe(res => this.floorsForSelectedBuilding.set(res.data));
  }

  openCreate(): void {
    this.form.reset({ buildingId: 0, floorId: 0, type: 'MEETING_ROOM', capacity: 4 });
    this.floorsForSelectedBuilding.set([]);
    this.showModal.set(true);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    const { floorId, name, code, type, capacity } = this.form.getRawValue();
    this.facilityService.createRoom({ floorId, name, code, type, capacity }).subscribe({
      next: () => {
        this.toast.success('Room created');
        this.showModal.set(false);
        this.saving.set(false);
        this.load(0);
      },
      error: () => this.saving.set(false),
    });
  }

  remove(room: Room): void {
    if (!confirm(`Delete room "${room.name}"?`)) return;
    this.facilityService.deleteRoom(room.id).subscribe({
      next: () => {
        this.toast.success('Room deleted');
        this.load(this.page()?.pageNumber ?? 0);
      },
    });
  }
}
