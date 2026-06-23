import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { feePaymentService, type CollectPaymentPayload } from "@/services/feePaymentService"

export const PAYMENT_KEYS = {
  all:        ["feePayments"] as const,
  byStudent:  (studentId: string, yearId: string) =>
                ["feePayments", studentId, yearId] as const,
}

export function useFeePayments(studentId: string | undefined, academicYearId: string | undefined) {
  return useQuery({
    queryKey: PAYMENT_KEYS.byStudent(studentId ?? "", academicYearId ?? ""),
    queryFn:  () => feePaymentService.list(studentId!, academicYearId!),
    enabled:  !!studentId && !!academicYearId,
    staleTime: 30_000,
  })
}

export function useCollectPayment(studentId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (payload: CollectPaymentPayload) =>
      feePaymentService.collect(studentId, payload),
    onSuccess: () =>
      qc.invalidateQueries({ queryKey: ["feePayments", studentId] }),
  })
}

export function useVoidPayment(studentId: string) {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: ({ paymentId, reason }: { paymentId: string; reason: string }) =>
      feePaymentService.void(studentId, paymentId, reason),
    onSuccess: () =>
      qc.invalidateQueries({ queryKey: ["feePayments", studentId] }),
  })
}
