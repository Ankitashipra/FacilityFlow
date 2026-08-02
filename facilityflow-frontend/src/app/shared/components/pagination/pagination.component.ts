import { Component, EventEmitter, Input, Output, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ff-pagination',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (totalPages > 1) {
      <div class="pagination">
        <span class="pagination__summary">
          Page {{ pageNumber + 1 }} of {{ totalPages }} · {{ totalElements }} total
        </span>
        <div class="pagination__controls">
          <button class="ff-btn ff-btn--ghost" [disabled]="pageNumber === 0" (click)="pageChange.emit(pageNumber - 1)">← Prev</button>
          <button class="ff-btn ff-btn--ghost" [disabled]="last" (click)="pageChange.emit(pageNumber + 1)">Next →</button>
        </div>
      </div>
    }
  `,
  styleUrl: './pagination.component.scss',
})
export class PaginationComponent {
  @Input() pageNumber = 0;
  @Input() totalPages = 0;
  @Input() totalElements = 0;
  @Input() last = true;
  @Output() pageChange = new EventEmitter<number>();
}
