import { useQuery } from "@tanstack/react-query";
import { dashboardService } from "@/services/dashboardService";

export const useDashboardStats = (academicYearId?: string) =>
  useQuery({
    queryKey: ["dashboard", "stats", academicYearId],
    queryFn: () => dashboardService.stats(academicYearId),
    refetchInterval: 60_000,   // refresh every 60s while tab is open
  });
