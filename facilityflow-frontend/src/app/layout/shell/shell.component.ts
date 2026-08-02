import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { NotificationService } from '../../core/services/notification.service';

interface NavItem {
  label: string;
  path: string;
  icon: string;
  roles?: Array<'ADMIN' | 'FACILITY_MANAGER' | 'EMPLOYEE'>;
}

const NAV_ITEMS: NavItem[] = [
  { label: 'Dashboard', path: '/dashboard', icon: '▦' },
  { label: 'Buildings', path: '/buildings', icon: '▤' },
  { label: 'Rooms', path: '/rooms', icon: '⌗' },
  { label: 'Assets', path: '/assets', icon: '◈' },
  { label: 'Tickets', path: '/tickets', icon: '✎' },
  { label: 'Reservations', path: '/reservations', icon: '◷' },
  { label: 'Users', path: '/users', icon: '◔', roles: ['ADMIN'] },
  { label: 'Audit Logs', path: '/audit-logs', icon: '▥', roles: ['ADMIN'] },
];

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent implements OnInit {
  auth = inject(AuthService);
  notifications = inject(NotificationService);

  navItems = NAV_ITEMS;
  userMenuOpen = signal(false);

  visibleNavItems = () => this.navItems.filter(item => !item.roles || item.roles.includes(this.auth.role()!));

  ngOnInit(): void {
    this.notifications.refreshUnreadCount();
  }

  toggleUserMenu(): void {
    this.userMenuOpen.update(v => !v);
  }

  logout(): void {
    this.auth.logout();
  }

  initials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(p => p[0]).slice(0, 2).join('').toUpperCase();
  }
}
