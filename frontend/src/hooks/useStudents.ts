import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import {
  studentService,
  type CreateStudentPayload,
  type StudentFilters,
} from "@/services/studentService"

export const STUDENT_KEYS = {
  all:    ["students"] as const,
  list:   (filters: StudentFilters) => ["students", "list", filters] as const,
  detail: (id: string) => ["students", "detail", id] as const,
}

export function useStudents(filters: StudentFilters = {}) {
  return useQuery({
    queryKey: STUDENT_KEYS.list(filters),
    queryFn:  () => studentService.list(filters),
    staleTime: 30_000,
  })
}

export function useStudent(id: string) {
  return useQuery({
    queryKey: STUDENT_KEYS.detail(id),
    queryFn:  () => studentService.getById(id),
    enabled:  !!id,
  })
}

export function useCreateStudent() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateStudentPayload) => studentService.create(payload),
    onSuccess: () => qc.invalidateQueries({ queryKey: STUDENT_KEYS.all }),
  })
}

export function useUpdateStudent(id: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CreateStudentPayload) => studentService.update(id, payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: STUDENT_KEYS.all })
      qc.invalidateQueries({ queryKey: STUDENT_KEYS.detail(id) })
    },
  })
}

export function useDeactivateStudent() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (id: string) => studentService.deactivate(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: STUDENT_KEYS.all }),
  })
}
