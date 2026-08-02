import { TicketPriority, TicketStatus } from './enums';

export interface TicketComment {
  id: number;
  authorId: number;
  authorName: string;
  content: string;
  createdAt: string;
}

export interface MaintenanceTicket {
  id: number;
  title: string;
  description: string;
  assetId?: number;
  assetName?: string;
  roomId?: number;
  roomName?: string;
  reportedById: number;
  reportedByName: string;
  assignedToId?: number;
  assignedToName?: string;
  priority: TicketPriority;
  status: TicketStatus;
  escalated: boolean;
  completionDate?: string;
  createdAt: string;
  comments: TicketComment[];
}
