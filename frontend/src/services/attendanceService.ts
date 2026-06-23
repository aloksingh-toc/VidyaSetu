import api, { wrap } from "./api"

export interface AttendanceRecord {
  id:            string
  studentId:     string
  studentName:   string
  classId:       string | null
  className:     string | null
  academicYearId: string | null
  date:          string
  status:        string   // PRESENT | ABSENT | LATE | LEAVE
  markedByName:  string | null
  createdAt:     string
}

export interface AttendanceSummary {
  studentId:          string
  studentName:        string
  academicYearId:     string
  academicYearName:   string
  totalPresent:       number
  totalAbsent:        number
  totalLate:          number
  totalLeave:         number
  totalMarked:        number
  attendancePercent:  number
}

export interface BulkMarkPayload {
  classId:        string
  academicYearId: string
  date:           string   // ISO date "YYYY-MM-DD"
  entries: {
    studentId: string
    status:    string
  }[]
}

export const attendanceService = {
  getByClassAndDate: (classId: string, date: string) =>
    api.get<{ data: AttendanceRecord[] }>(
      `/v1/attendance?classId=${classId}&date=${date}`
    ).then(wrap),

  bulkMark: (payload: BulkMarkPayload) =>
    api.post<{ data: AttendanceRecord[] }>("/v1/attendance/bulk", payload).then(wrap),

  getByStudent: (studentId: string, academicYearId: string) =>
    api.get<{ data: AttendanceRecord[] }>(
      `/v1/students/${studentId}/attendance?academicYearId=${academicYearId}`
    ).then(wrap),

  summary: (studentId: string, academicYearId: string) =>
    api.get<{ data: AttendanceSummary }>(
      `/v1/students/${studentId}/attendance/summary?academicYearId=${academicYearId}`
    ).then(wrap),
}
