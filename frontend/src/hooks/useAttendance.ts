import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { attendanceService, type BulkMarkPayload } from "@/services/attendanceService"

export const ATTENDANCE_KEYS = {
  all:        ["attendance"] as const,
  byClass:    (classId: string, date: string) => ["attendance", classId, date] as const,
  byStudent:  (studentId: string, yearId: string) => ["attendance", studentId, yearId] as const,
  summary:    (studentId: string, yearId: string) => ["attendanceSummary", studentId, yearId] as const,
}

export function useAttendanceByClass(classId: string | undefined, date: string | undefined) {
  return useQuery({
    queryKey: ATTENDANCE_KEYS.byClass(classId ?? "", date ?? ""),
    queryFn:  () => attendanceService.getByClassAndDate(classId!, date!),
    enabled:  !!classId && !!date,
    staleTime: 30_000,
  })
}

export function useBulkMarkAttendance() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: BulkMarkPayload) => attendanceService.bulkMark(payload),
    onSuccess: (_d, vars) =>
      qc.invalidateQueries({ queryKey: ATTENDANCE_KEYS.byClass(vars.classId, vars.date) }),
  })
}

export function useStudentAttendance(studentId: string | undefined, academicYearId: string | undefined) {
  return useQuery({
    queryKey: ATTENDANCE_KEYS.byStudent(studentId ?? "", academicYearId ?? ""),
    queryFn:  () => attendanceService.getByStudent(studentId!, academicYearId!),
    enabled:  !!studentId && !!academicYearId,
    staleTime: 60_000,
  })
}

export function useAttendanceSummary(studentId: string | undefined, academicYearId: string | undefined) {
  return useQuery({
    queryKey: ATTENDANCE_KEYS.summary(studentId ?? "", academicYearId ?? ""),
    queryFn:  () => attendanceService.summary(studentId!, academicYearId!),
    enabled:  !!studentId && !!academicYearId,
    staleTime: 60_000,
  })
}
