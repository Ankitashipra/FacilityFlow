import { Component, Input, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

export type BadgeTone = 'neutral' | 'success' | 'warning' | 'danger' | 'info' | 'accent';

const STATUS_TONE_MAP: Record<string, BadgeTone> = {
  // Rooms
  AVAILABLE: 'success', OCCUPIED: 'warning', UNDER_MAINTENANCE: 'danger', RESERVED: 'info', DECOMMISSIONED: 'neutral',
  // Assets
  ACTIVE: 'success', IN_MAINTENANCE: 'warning', RETIRED: 'neutral', LOST: 'danger', DAMAGED: 'danger',
  // Tickets
  OPEN: 'info', ASSIGNED: 'accent', IN_PROGRESS: 'warning', ON_HOLD: 'neutral', RESOLVED: 'success', CLOSED: 'neutral', ESCALATED: 'danger',
  // Reservations
  PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', CANCELLED: 'neutral', COMPLETED: 'info', EXPIRED: 'neutral',
  // Priority
  LOW: 'neutral', MEDIUM: 'info', HIGH: 'warning', CRITICAL: 'danger',
  // Roles
  ADMIN: 'accent', FACILITY_MANAGER: 'info', EMPLOYEE: 'neutral',
};

@Component({
  selector: 'ff-badge',
  standalone: true,
  imports: [CommonModule],
  template: `<span class="ff-badge" [class]="'ff-badge--' + tone()">{{ label() }}</span>`,
  styleUrl: './badge.component.scss',
})
export class BadgeComponent {
  @Input({ required: true }) set value(v: string) {
    this.rawValue.set(v);
  }
  @Input() toneOverride?: BadgeTone;

  private rawValue = signal('');

  label = computed(() => this.rawValue().replaceAll('_', ' '));
  tone = computed<BadgeTone>(() => this.toneOverride ?? STATUS_TONE_MAP[this.rawValue()] ?? 'neutral');
}
