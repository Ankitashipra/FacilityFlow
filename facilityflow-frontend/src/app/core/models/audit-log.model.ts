import { AuditAction } from './enums';

export interface AuditLog {
  id: number;
  userId?: number;
  userName?: string;
  action: AuditAction;
  entityName: string;
  entityId?: number;
  details?: string;
  ipAddress?: string;
  timestamp: string;
}
