import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../core/services/dashboard.service';
import { DashboardStats } from '../../core/models/dashboard.model';
import { StatCardComponent } from '../../shared/components/stat-card/stat-card.component';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, StatCardComponent, PageHeaderComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private dashboardService = inject(DashboardService);
  auth = inject(AuthService);

  stats = signal<DashboardStats | null>(null);
  loading = signal(true);

  priorityOrder = ['CRITICAL', 'HIGH', 'MEDIUM', 'LOW'];
  statusOrder = ['ACTIVE', 'IN_MAINTENANCE', 'RETIRED', 'DAMAGED', 'LOST'];

  ngOnInit(): void {
    if (!this.auth.isManagerOrAdmin()) {
      this.loading.set(false);
      return;
    }
    this.dashboardService.getStats().subscribe({
      next: res => {
        this.stats.set(res.data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  maxPriorityCount(): number {
    const s = this.stats();
    if (!s) return 1;
    return Math.max(1, ...Object.values(s.ticketsByPriority));
  }

  maxStatusCount(): number {
    const s = this.stats();
    if (!s) return 1;
    return Math.max(1, ...Object.values(s.assetsByStatus));
  }

  greeting(): string {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 17) return 'Good afternoon';
    return 'Good evening';
  }
}
