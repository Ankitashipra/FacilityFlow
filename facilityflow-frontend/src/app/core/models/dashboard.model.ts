export interface TopBuilding {
  buildingName: string;
  reservationCount: number;
}

export interface DashboardStats {
  totalUsers: number;
  activeUsers: number;
  openTickets: number;
  closedTickets: number;
  totalAssets: number;
  activeReservations: number;
  topBuildings: TopBuilding[];
  ticketsByPriority: Record<string, number>;
  assetsByStatus: Record<string, number>;
}
