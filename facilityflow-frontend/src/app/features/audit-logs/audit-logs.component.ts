import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuditLogService } from '../../core/services/audit-log.service';
import { AuditLog } from '../../core/models/audit-log.model';
import { PageResponse } from '../../core/models/api-response.model';
import { AuditAction } from '../../core/models/enums';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { BadgeComponent } from '../../shared/components/badge/badge.component';

const ACTIONS: AuditAction[] = ['LOGIN', 'LOGOUT', 'CREATE', 'UPDATE', 'DELETE', 'ROLE_CHANGE', 'PASSWORD_CHANGE', 'APPROVE', 'REJECT'];

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule, PageHeaderComponent, EmptyStateComponent, PaginationComponent, BadgeComponent],
  templateUrl: './audit-logs.component.html',
  styleUrl: './audit-logs.component.scss',
})
export class AuditLogsComponent implements OnInit {
  private auditLogService = inject(AuditLogService);

  page = signal<PageResponse<AuditLog> | null>(null);
  loading = signal(true);
  actionFilter = signal<AuditAction | ''>('');
  actions = ACTIONS;

  ngOnInit(): void {
    this.load(0);
  }

  load(pageNumber: number): void {
    this.loading.set(true);
    this.auditLogService.list(pageNumber, 20, this.actionFilter() || undefined).subscribe({
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

  actionTone(action: AuditAction): 'success' | 'danger' | 'warning' | 'info' | 'neutral' | 'accent' {
    switch (action) {
      case 'CREATE': case 'APPROVE': return 'success';
      case 'DELETE': case 'REJECT': return 'danger';
      case 'UPDATE': case 'ROLE_CHANGE': return 'warning';
      case 'LOGIN': case 'LOGOUT': return 'info';
      default: return 'neutral';
    }
  }
}
