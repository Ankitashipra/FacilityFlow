import { NotificationType } from './enums';

export interface AppNotification {
  id: number;
  type: NotificationType;
  title: string;
  message: string;
  read: boolean;
  referenceUrl?: string;
  createdAt: string;
}
