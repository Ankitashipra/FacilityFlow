import { AssetStatus, AssetType } from './enums';

export interface Asset {
  id: number;
  assetTag: string;
  name: string;
  type: AssetType;
  status: AssetStatus;
  roomId?: number;
  roomName?: string;
  purchaseDate: string;
  warrantyExpiryDate?: string;
  qrCodeUrl?: string;
  serialNumber?: string;
  vendor?: string;
  purchaseCost?: number;
}
