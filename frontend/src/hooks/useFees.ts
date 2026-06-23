import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import {
  feeTypeService,
  feeStructureService,
  type FeeTypePayload,
  type FeeStructurePayload,
} from "@/services/feeService"

// ── Fee Type keys ─────────────────────────────────────────────────────────────
export const FEE_TYPE_KEYS = {
  all:    ["feeTypes"] as const,
  list:   (activeOnly: boolean) => ["feeTypes", { activeOnly }] as const,
}

export function useFeeTypes(activeOnly = true) {
  return useQuery({
    queryKey: FEE_TYPE_KEYS.list(activeOnly),
    queryFn:  () => feeTypeService.list(activeOnly),
    staleTime: 60_000,
  })
}

export function useCreateFeeType() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: feeTypeService.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: FEE_TYPE_KEYS.all }),
  })
}

export function useUpdateFeeType(id: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: FeeTypePayload) => feeTypeService.update(id, payload),
    onSuccess: () => qc.invalidateQueries({ queryKey: FEE_TYPE_KEYS.all }),
  })
}

export function useDeactivateFeeType() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: feeTypeService.deactivate,
    onSuccess: () => qc.invalidateQueries({ queryKey: FEE_TYPE_KEYS.all }),
  })
}

// ── Fee Structure keys ────────────────────────────────────────────────────────
export const FEE_STRUCTURE_KEYS = {
  all:     ["feeStructures"] as const,
  byYear:  (yearId: string)  => ["feeStructures", "year", yearId] as const,
  byClass: (classId: string) => ["feeStructures", "class", classId] as const,
}

export function useFeeStructuresByYear(academicYearId: string | undefined) {
  return useQuery({
    queryKey: FEE_STRUCTURE_KEYS.byYear(academicYearId ?? ""),
    queryFn:  () => feeStructureService.listByYear(academicYearId!),
    enabled:  !!academicYearId,
    staleTime: 60_000,
  })
}

export function useCreateFeeStructure() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: feeStructureService.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: FEE_STRUCTURE_KEYS.all }),
  })
}

export function useUpdateFeeStructure(id: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: FeeStructurePayload) => feeStructureService.update(id, payload),
    onSuccess: () => qc.invalidateQueries({ queryKey: FEE_STRUCTURE_KEYS.all }),
  })
}

export function useDeleteFeeStructure() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ id }: { id: string; yearId: string }) => feeStructureService.delete(id),
    onSuccess: (_d, vars) =>
      qc.invalidateQueries({ queryKey: FEE_STRUCTURE_KEYS.byYear(vars.yearId) }),
  })
}
