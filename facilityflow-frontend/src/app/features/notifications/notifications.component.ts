import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../core/services/notification.service';
import { AppNotification } from '../../core/models/notification.model';
import { PageResponse } from '../../core/models/api-response.model';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';

const ICONS: Record<string, string> = {
  TICKET_CREATED: '✎', TICKET_ASSIGNED: '✎', TICKET_STATUS_CHANGED: '✎', TICKET_ESCALATED: '⚡',
  RESERVATION_REQUESTED: '◷', RESERVATION_APPROVED: '✓', RESERVATION_REJECTED: '✕', RESERVATION_CANCELLED: '✕',
  ASSET_WARRANTY_EXPIRING: '◈', SYSTEM: 'i',
};

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule, PageHeaderComponent, EmptyStateComponent, PaginationComponent],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.scss',
})
export class NotificationsComponent implements OnInit {
  private notificationService = inject(NotificationService);

  page = signal<PageResponse<AppNotification> | null>(null);
  loading = signal(true);

  ngOnInit(): void {
    this.load(0);
  }

  load(pageNumber: number): void {
    this.loading.set(true);
    this.notificationService.list(pageNumber, 20).subscribe({
      next: res => {
        this.page.set(res.data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  markRead(n: AppNotification): void {
    if (n.read) return;
    this.notificationService.markAsRead(n.id).subscribe({
      next: () => {
        n.read = true;
      },
    });
  }

  icon(type: string): string {
    return ICONS[type] ?? 'i';
  }
}
