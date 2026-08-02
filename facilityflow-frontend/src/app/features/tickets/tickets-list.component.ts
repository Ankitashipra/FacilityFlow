import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TicketService } from '../../core/services/ticket.service';
import { FacilityService } from '../../core/services/facility.service';
import { AssetService } from '../../core/services/asset.service';
import { MaintenanceTicket } from '../../core/models/ticket.model';
import { Room } from '../../core/models/facility.model';
import { Asset } from '../../core/models/asset.model';
import { PageResponse } from '../../core/models/api-response.model';
import { TicketPriority, TicketStatus } from '../../core/models/enums';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { BadgeComponent } from '../../shared/components/badge/badge.component';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

const TICKET_STATUSES: TicketStatus[] = ['OPEN', 'ASSIGNED', 'IN_PROGRESS', 'ON_HOLD', 'RESOLVED', 'CLOSED', 'ESCALATED'];
const TICKET_PRIORITIES: TicketPriority[] = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

@Component({
  selector: 'app-tickets-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PageHeaderComponent, EmptyStateComponent, ModalComponent, PaginationComponent, BadgeComponent],
  templateUrl: './tickets-list.component.html',
  styleUrl: './tickets-list.component.scss',
})
export class TicketsListComponent implements OnInit {
  private ticketService = inject(TicketService);
  private facilityService = inject(FacilityService);
  private assetService = inject(AssetService);
  private fb = inject(FormBuilder);
  private toast = inject(ToastService);
  auth = inject(AuthService);

  page = signal<PageResponse<MaintenanceTicket> | null>(null);
  loading = signal(true);
  showModal = signal(false);
  saving = signal(false);
  statusFilter = signal<TicketStatus | ''>('');
  showMineOnly = signal(false);

  rooms = signal<Room[]>([]);
  assets = signal<Asset[]>([]);
  ticketStatuses = TICKET_STATUSES;
  ticketPriorities = TICKET_PRIORITIES;

  form = this.fb.nonNullable.group({
    title: ['', Validators.required],
    description: ['', Validators.required],
    roomId: [0],
    assetId: [0],
    priority: ['MEDIUM' as TicketPriority, Validators.required],
  });

  ngOnInit(): void {
    this.load(0);
    this.facilityService.listRooms(0, 200).subscribe(res => this.rooms.set(res.data.content));
    this.assetService.list(0, 200).subscribe(res => this.assets.set(res.data.content));
  }

  load(pageNumber: number): void {
    this.loading.set(true);
    const source = this.showMineOnly()
      ? this.ticketService.myAssignments(pageNumber, 15)
      : this.ticketService.list(pageNumber, 15, this.statusFilter() || undefined);

    source.subscribe({
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

  toggleMine(): void {
    this.showMineOnly.update(v => !v);
    this.load(0);
  }

  openCreate(): void {
    this.form.reset({ priority: 'MEDIUM', roomId: 0, assetId: 0 });
    this.showModal.set(true);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    const raw = this.form.getRawValue();
    const payload = { ...raw, roomId: raw.roomId || undefined, assetId: raw.assetId || undefined };
    this.ticketService.create(payload).subscribe({
      next: () => {
        this.toast.success('Ticket created');
        this.showModal.set(false);
        this.saving.set(false);
        this.load(0);
      },
      error: () => this.saving.set(false),
    });
  }
}
