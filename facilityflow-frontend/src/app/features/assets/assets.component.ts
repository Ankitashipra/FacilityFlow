import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AssetService } from '../../core/services/asset.service';
import { FacilityService } from '../../core/services/facility.service';
import { Asset } from '../../core/models/asset.model';
import { Room } from '../../core/models/facility.model';
import { PageResponse } from '../../core/models/api-response.model';
import { AssetStatus, AssetType } from '../../core/models/enums';
import { PageHeaderComponent } from '../../shared/components/page-header/page-header.component';
import { EmptyStateComponent } from '../../shared/components/empty-state/empty-state.component';
import { ModalComponent } from '../../shared/components/modal/modal.component';
import { PaginationComponent } from '../../shared/components/pagination/pagination.component';
import { BadgeComponent } from '../../shared/components/badge/badge.component';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';

const ASSET_TYPES: AssetType[] = ['COMPUTER', 'CHAIR', 'DESK', 'AC', 'PROJECTOR', 'PRINTER', 'MONITOR', 'OTHER'];
const ASSET_STATUSES: AssetStatus[] = ['ACTIVE', 'IN_MAINTENANCE', 'RETIRED', 'LOST', 'DAMAGED'];

@Component({
  selector: 'app-assets',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PageHeaderComponent, EmptyStateComponent, ModalComponent, PaginationComponent, BadgeComponent],
  templateUrl: './assets.component.html',
  styleUrl: './assets.component.scss',
})
export class AssetsComponent implements OnInit {
  private assetService = inject(AssetService);
  private facilityService = inject(FacilityService);
  private fb = inject(FormBuilder);
  private toast = inject(ToastService);
  auth = inject(AuthService);

  page = signal<PageResponse<Asset> | null>(null);
  loading = signal(true);
  showModal = signal(false);
  saving = signal(false);
  statusFilter = signal<AssetStatus | ''>('');
  searchTerm = signal('');
  qrPreview = signal<Asset | null>(null);

  rooms = signal<Room[]>([]);
  assetTypes = ASSET_TYPES;
  assetStatuses = ASSET_STATUSES;

  form = this.fb.nonNullable.group({
    assetTag: ['', Validators.required],
    name: ['', Validators.required],
    type: ['COMPUTER' as AssetType, Validators.required],
    roomId: [0],
    purchaseDate: ['', Validators.required],
    warrantyExpiryDate: [''],
    vendor: [''],
    purchaseCost: [0],
  });

  ngOnInit(): void {
    this.load(0);
    this.facilityService.listRooms(0, 200).subscribe(res => this.rooms.set(res.data.content));
  }

  load(pageNumber: number): void {
    this.loading.set(true);
    this.assetService.list(pageNumber, 12, {
      status: this.statusFilter() || undefined,
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

  openCreate(): void {
    this.form.reset({ type: 'COMPUTER', roomId: 0, purchaseCost: 0 });
    this.showModal.set(true);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    const raw = this.form.getRawValue();
    const payload = { ...raw, roomId: raw.roomId || undefined, warrantyExpiryDate: raw.warrantyExpiryDate || undefined };
    this.assetService.create(payload).subscribe({
      next: () => {
        this.toast.success('Asset registered');
        this.showModal.set(false);
        this.saving.set(false);
        this.load(0);
      },
      error: () => this.saving.set(false),
    });
  }

  remove(asset: Asset): void {
    if (!confirm(`Delete asset "${asset.name}" (${asset.assetTag})?`)) return;
    this.assetService.delete(asset.id).subscribe({
      next: () => {
        this.toast.success('Asset deleted');
        this.load(this.page()?.pageNumber ?? 0);
      },
    });
  }

  isWarrantyExpiringSoon(asset: Asset): boolean {
    if (!asset.warrantyExpiryDate) return false;
    const days = (new Date(asset.warrantyExpiryDate).getTime() - Date.now()) / (1000 * 60 * 60 * 24);
    return days > 0 && days < 90;
  }
}
