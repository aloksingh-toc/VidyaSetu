import { useQuery } from "@tanstack/react-query"
import { reportService } from "@/services/reportService"

export function useFeeDefaulters(academicYearId: string | undefined) {
  return useQuery({
    queryKey: ["reports", "fee-defaulters", academicYearId],
    queryFn:  () => reportService.getFeeDefaulters(academicYearId!),
    enabled:  !!academicYearId,
  })
}
