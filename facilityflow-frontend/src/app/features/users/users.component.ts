import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';
import { PageResponse } from '../../core/models/api-response.model';
import { Role } from '../../core/models/enums';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { BadgeComponent } from '../../shared/components/badge/badge.component';
import { ToastService } from '../../core/services/toast.service';

const ROLES: Role[] = ['ADMIN', 'FACILITY_MANAGER', 'EMPLOYEE'];

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, PageHeaderComponent, EmptyStateComponent, PaginationComponent, BadgeComponent],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss',
})
export class UsersComponent implements OnInit {
  private userService = inject(UserService);
  private toast = inject(ToastService);

  page = signal<PageResponse<User> | null>(null);
  loading = signal(true);
  roleFilter = signal<Role | ''>('');
  searchTerm = signal('');
  roles = ROLES;

  ngOnInit(): void {
    this.load(0);
  }

  load(pageNumber: number): void {
    this.loading.set(true);
    this.userService.list(pageNumber, 15, {
      role: this.roleFilter() || undefined,
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

  toggleEnabled(user: User): void {
    this.userService.update(user.id, { enabled: !user.enabled }).subscribe({
      next: res => {
        user.enabled = res.data.enabled;
        this.toast.success(user.enabled ? 'User enabled' : 'User disabled');
      },
    });
  }

  changeRole(user: User, role: string): void {
    this.userService.update(user.id, { role: role as Role }).subscribe({
      next: res => {
        user.role = res.data.role;
        this.toast.success('Role updated');
      },
    });
  }

  remove(user: User): void {
    if (!confirm(`Soft-delete "${user.fullName}"?`)) return;
    this.userService.delete(user.id).subscribe({
      next: () => {
        this.toast.success('User deleted');
        this.load(this.page()?.pageNumber ?? 0);
      },
    });
  }
}
