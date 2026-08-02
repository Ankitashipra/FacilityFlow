import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FacilityService } from '../../../core/services/facility.service';
import { Building } from '../../../core/models/facility.model';
import { PageResponse } from '../../../core/models/api-response.model';
import { PageHeaderComponent } from '../../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ModalComponent } from '../../../shared/components/modal/modal.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-buildings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PageHeaderComponent, EmptyStateComponent, ModalComponent, PaginationComponent],
  templateUrl: './buildings.component.html',
  styleUrl: './buildings.component.scss',
})
export class BuildingsComponent implements OnInit {
  private facilityService = inject(FacilityService);
  private fb = inject(FormBuilder);
  private toast = inject(ToastService);
  auth = inject(AuthService);

  page = signal<PageResponse<Building> | null>(null);
  loading = signal(true);
  showModal = signal(false);
  saving = signal(false);

  form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    code: ['', Validators.required],
    address: ['', Validators.required],
    city: [''],
    totalFloors: [1, [Validators.required, Validators.min(1)]],
  });

  ngOnInit(): void {
    this.load(0);
  }

  load(pageNumber: number): void {
    this.loading.set(true);
    this.facilityService.listBuildings(pageNumber, 12).subscribe({
      next: res => {
        this.page.set(res.data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  openCreate(): void {
    this.form.reset({ totalFloors: 1 });
    this.showModal.set(true);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.facilityService.createBuilding(this.form.getRawValue()).subscribe({
      next: () => {
        this.toast.success('Building created');
        this.showModal.set(false);
        this.saving.set(false);
        this.load(0);
      },
      error: () => this.saving.set(false),
    });
  }

  remove(building: Building): void {
    if (!confirm(`Delete "${building.name}"? This cannot be undone.`)) return;
    this.facilityService.deleteBuilding(building.id).subscribe({
      next: () => {
        this.toast.success('Building deleted');
        this.load(this.page()?.pageNumber ?? 0);
      },
    });
  }
}
