import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ff-stat-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="stat-card">
      <div class="stat-card__icon" [style.background]="iconBg" [style.color]="iconColor">
        <span [innerHTML]="icon"></span>
      </div>
      <div class="stat-card__body">
        <span class="stat-card__value">{{ value }}</span>
        <span class="stat-card__label">{{ label }}</span>
      </div>
    </div>
  `,
  styleUrl: './stat-card.component.scss',
})
export class StatCardComponent {
  @Input({ required: true }) label = '';
  @Input({ required: true }) value: string | number = 0;
  @Input() icon = '';
  @Input() iconColor = 'var(--ff-accent-text)';
  @Input() iconBg = 'var(--ff-accent-dim)';
}
