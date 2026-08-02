import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReservationService } from '../../core/services/reservation.service';
import { FacilityService } from '../../core/services/facility.service';
import { Reservation } from '../../core/models/reservation.model';
import { Room } from '../../core/models/facility.model';
import { PageResponse } from '../../core/models/api-response.model';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { BadgeComponent } from '../../shared/components/badge/badge.component';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-reservations',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PageHeaderComponent, EmptyStateComponent, ModalComponent, PaginationComponent, BadgeComponent],
  templateUrl: './reservations.component.html',
  styleUrl: './reservations.component.scss',
})
export class ReservationsComponent implements OnInit {
  private reservationService = inject(ReservationService);
  private facilityService = inject(FacilityService);
  private fb = inject(FormBuilder);
  private toast = inject(ToastService);
  auth = inject(AuthService);

  page = signal<PageResponse<Reservation> | null>(null);
  loading = signal(true);
  showModal = signal(false);
  saving = signal(false);
  showAll = signal(false);
  rejectingId = signal<number | null>(null);

  rooms = signal<Room[]>([]);

  form = this.fb.nonNullable.group({
    roomId: [0, Validators.required],
    purpose: ['', Validators.required],
    startTime: ['', Validators.required],
    endTime: ['', Validators.required],
    attendeeCount: [2, [Validators.min(1)]],
  });

  rejectForm = this.fb.nonNullable.group({
    reason: ['', Validators.required],
  });

  ngOnInit(): void {
    this.load(0);
    this.facilityService.listRooms(0, 200).subscribe(res => this.rooms.set(res.data.content));
  }

  load(pageNumber: number): void {
    this.loading.set(true);
    const source = this.showAll() ? this.reservationService.all(pageNumber, 15) : this.reservationService.my(pageNumber, 15);
    source.subscribe({
      next: res => {
        this.page.set(res.data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  toggleView(): void {
    this.showAll.update(v => !v);
    this.load(0);
  }

  openCreate(): void {
    this.form.reset({ roomId: 0, attendeeCount: 2 });
    this.showModal.set(true);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.reservationService.create(this.form.getRawValue()).subscribe({
      next: () => {
        this.toast.success('Reservation requested');
        this.showModal.set(false);
        this.saving.set(false);
        this.load(0);
      },
      error: () => this.saving.set(false),
    });
  }

  approve(r: Reservation): void {
    this.reservationService.approve(r.id).subscribe({
      next: () => {
        this.toast.success('Reservation approved');
        this.load(this.page()?.pageNumber ?? 0);
      },
    });
  }

  openReject(r: Reservation): void {
    this.rejectForm.reset();
    this.rejectingId.set(r.id);
  }

  confirmReject(): void {
    const id = this.rejectingId();
    if (!id || this.rejectForm.invalid) return;
    this.reservationService.reject(id, this.rejectForm.getRawValue().reason).subscribe({
      next: () => {
        this.toast.success('Reservation rejected');
        this.rejectingId.set(null);
        this.load(this.page()?.pageNumber ?? 0);
      },
    });
  }

  cancel(r: Reservation): void {
    if (!confirm('Cancel this reservation?')) return;
    this.reservationService.cancel(r.id).subscribe({
      next: () => {
        this.toast.success('Reservation cancelled');
        this.load(this.page()?.pageNumber ?? 0);
      },
    });
  }
}
