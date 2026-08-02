import { ReservationStatus } from './enums';

export interface Reservation {
  id: number;
  roomId: number;
  roomName: string;
  requestedById: number;
  requestedByName: string;
  approvedById?: number;
  approvedByName?: string;
  purpose: string;
  startTime: string;
  endTime: string;
  status: ReservationStatus;
  attendeeCount?: number;
  rejectionReason?: string;
}
