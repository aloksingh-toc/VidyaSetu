import { useQuery } from "@tanstack/react-query"
import { auditLogService, type AuditLogFilters } from "@/services/auditLogService"

export function useAuditLogs(filters: AuditLogFilters = {}) {
  return useQuery({
    queryKey: ["audit-logs", filters],
    queryFn:  () => auditLogService.list(filters),
    staleTime: 15_000,
  })
}
