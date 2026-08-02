import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TicketService } from '../../core/services/ticket.service';
import { UserService } from '../../core/services/user.service';
import { MaintenanceTicket } from '../../core/models/ticket.model';
import { User } from '../../core/models/user.model';
import { TicketStatus } from '../../core/models/enums';
import { BadgeComponent } from '../../shared/components/badge/badge.component';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

const STATUS_FLOW: TicketStatus[] = ['OPEN', 'ASSIGNED', 'IN_PROGRESS', 'ON_HOLD', 'RESOLVED', 'CLOSED'];

@Component({
  selector: 'app-ticket-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, BadgeComponent],
  templateUrl: './ticket-detail.component.html',
  styleUrl: './ticket-detail.component.scss',
})
export class TicketDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ticketService = inject(TicketService);
  private userService = inject(UserService);
  private fb = inject(FormBuilder);
  private toast = inject(ToastService);
  auth = inject(AuthService);

  ticket = signal<MaintenanceTicket | null>(null);
  loading = signal(true);
  technicians = signal<User[]>([]);
  statusOptions = STATUS_FLOW;
  submittingComment = signal(false);
  assigning = signal(false);

  commentForm = this.fb.nonNullable.group({
    content: ['', Validators.required],
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.load(id);
    this.userService.list(0, 100, { role: 'EMPLOYEE' }).subscribe(res => this.technicians.set(res.data.content));
  }

  load(id: number): void {
    this.loading.set(true);
    this.ticketService.get(id).subscribe({
      next: res => {
        this.ticket.set(res.data);
        this.loading.set(false);
      },
      error: () => this.router.navigate(['/tickets']),
    });
  }

  assign(technicianId: string): void {
    const t = this.ticket();
    if (!t || !technicianId) return;
    this.assigning.set(true);
    this.ticketService.assign(t.id, Number(technicianId)).subscribe({
      next: res => {
        this.ticket.set(res.data);
        this.toast.success('Technician assigned');
        this.assigning.set(false);
      },
      error: () => this.assigning.set(false),
    });
  }

  updateStatus(status: string): void {
    const t = this.ticket();
    if (!t) return;
    this.ticketService.updateStatus(t.id, status as TicketStatus).subscribe({
      next: res => {
        this.ticket.set(res.data);
        this.toast.success(`Status updated to ${status.replaceAll('_', ' ')}`);
      },
    });
  }

  submitComment(): void {
    const t = this.ticket();
    if (!t || this.commentForm.invalid) return;
    this.submittingComment.set(true);
    this.ticketService.addComment(t.id, this.commentForm.getRawValue().content).subscribe({
      next: res => {
        this.ticket.set(res.data);
        this.commentForm.reset();
        this.submittingComment.set(false);
      },
      error: () => this.submittingComment.set(false),
    });
  }

  initials(name: string): string {
    return name.split(' ').map(p => p[0]).slice(0, 2).join('').toUpperCase();
  }
}
