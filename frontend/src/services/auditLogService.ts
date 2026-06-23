import api, { wrap } from "./api"

export interface AuditLog {
  id: string
  userName: string | null
  action: string
  entityType: string | null
  entityId: string | null
  oldValue: string | null
  newValue: string | null
  ipAddress: string | null
  createdAt: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface AuditLogFilters {
  page?: number
  size?: number
  action?: string
  entityType?: string
}

export const auditLogService = {
  list: (filters: AuditLogFilters = {}) => {
    const params = new URLSearchParams()
    if (filters.page !== undefined) params.set("page", String(filters.page))
    if (filters.size !== undefined) params.set("size", String(filters.size))
    if (filters.action)             params.set("action", filters.action)
    if (filters.entityType)         params.set("entityType", filters.entityType)
    return api.get<{ data: PageResponse<AuditLog> }>(`/v1/audit-logs?${params}`).then(wrap)
  },
}
