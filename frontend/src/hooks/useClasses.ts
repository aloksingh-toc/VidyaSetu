import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { classService, type ClassPayload } from "@/services/classService"

export const CLASS_KEYS = {
  all:    ["classes"] as const,
  byYear: (yearId: string) => ["classes", yearId] as const,
}

export function useClasses(academicYearId: string | undefined) {
  return useQuery({
    queryKey: CLASS_KEYS.byYear(academicYearId ?? ""),
    queryFn:  () => classService.listByYear(academicYearId!),
    enabled:  !!academicYearId,
    staleTime: 60_000,
  })
}

export function useCreateClass() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: classService.create,
    onSuccess: (_data, variables) =>
      qc.invalidateQueries({ queryKey: CLASS_KEYS.byYear(variables.academicYearId) }),
  })
}

export function useUpdateClass(id: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: ClassPayload) => classService.update(id, payload),
    onSuccess: (_data, variables) =>
      qc.invalidateQueries({ queryKey: CLASS_KEYS.byYear(variables.academicYearId) }),
  })
}

export function useDeleteClass() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id }: { id: string; academicYearId: string }) =>
      classService.delete(id),
    onSuccess: (_data, variables) =>
      qc.invalidateQueries({ queryKey: CLASS_KEYS.byYear(variables.academicYearId) }),
  })
}
