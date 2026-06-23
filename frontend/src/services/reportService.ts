import api, { wrap } from "./api"

export interface Defaulter {
  id: string
  fullName: string
  rollNumber: string | null
  admissionNumber: string | null
  classId: string | null
  className: string | null
  classSection: string | null
}

export const reportService = {
  getFeeDefaulters: (academicYearId: string) =>
    api.get<{ data: Defaulter[] }>("/v1/reports/fee-defaulters", {
      params: { academicYearId },
    }).then(wrap),
}
