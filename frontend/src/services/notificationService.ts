import api, { wrap } from "./api"

export interface AppNotification {
  id: string
  type: string
  title: string
  body: string | null
  actionUrl: string | null
  isRead: boolean
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

export const notificationService = {
  list: (page = 0, size = 10) =>
    api.get<{ data: PageResponse<AppNotification> }>(`/v1/notifications?page=${page}&size=${size}`).then(wrap),

  unreadCount: () =>
    api.get<{ data: { count: number } }>(`/v1/notifications/unread-count`).then(wrap),

  markRead: (id: string) =>
    api.patch<{ data: null }>(`/v1/notifications/${id}/read`).then(wrap),

  markAllRead: () =>
    api.patch<{ data: null }>(`/v1/notifications/read-all`).then(wrap),
}
