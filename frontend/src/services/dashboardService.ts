import api, { wrap } from "./api";

export interface DashboardStats {
  totalStudents: number;
  feesCollectedThisMonth: number;
  feesCollectedThisYear: number;
  presentToday: number;
  absentToday: number;
  attendancePercent: number;
  upcomingExams: number;
  totalClasses: number;
}

export const dashboardService = {
  stats: (academicYearId?: string) =>
    api
      .get<{ data: DashboardStats }>("/v1/dashboard/stats", {
        params: academicYearId ? { academicYearId } : {},
      })
      .then(wrap),
};
