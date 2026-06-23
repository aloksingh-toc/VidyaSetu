import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import {
  academicYearService,
  type AcademicYearPayload,
} from "@/services/academicYearService"

export const AY_KEYS = {
  all:  ["academic-years"] as const,
  list: ["academic-years", "list"] as const,
}

export function useAcademicYears() {
  return useQuery({
    queryKey: AY_KEYS.list,
    queryFn:  academicYearService.list,
    staleTime: 60_000,
  })
}

export function useCurrentAcademicYear() {
  const { data: years } = useAcademicYears()
  return years?.find((y) => y.isCurrent) ?? years?.[0] ?? null
}

export function useCreateAcademicYear() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: academicYearService.create,
    onSuccess:  () => qc.invalidateQueries({ queryKey: AY_KEYS.all }),
  })
}

export function useUpdateAcademicYear(id: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: AcademicYearPayload) =>
      academicYearService.update(id, payload),
    onSuccess: () => qc.invalidateQueries({ queryKey: AY_KEYS.all }),
  })
}

export function useDeleteAcademicYear() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: academicYearService.delete,
    onSuccess:  () => qc.invalidateQueries({ queryKey: AY_KEYS.all }),
  })
}
