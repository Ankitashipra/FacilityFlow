import { RoomStatus, RoomType } from './enums';

export interface Building {
  id: number;
  name: string;
  code: string;
  address: string;
  city?: string;
  totalFloors: number;
  floorCount: number;
}

export interface Floor {
  id: number;
  buildingId: number;
  buildingName: string;
  floorNumber: number;
  name?: string;
  roomCount: number;
}

export interface Room {
  id: number;
  floorId: number;
  floorName?: string;
  buildingId: number;
  buildingName: string;
  name: string;
  code: string;
  type: RoomType;
  status: RoomStatus;
  capacity: number;
  assetCount: number;
}
