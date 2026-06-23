import { useEffect, useRef, useState } from "react"
import { useNavigate } from "react-router-dom"
import { Bell, CheckCheck } from "lucide-react"
import { cn } from "@/lib/utils"
import {
  useNotifications,
  useUnreadCount,
  useMarkNotificationRead,
  useMarkAllNotificationsRead,
} from "@/hooks/useNotifications"
import type { AppNotification } from "@/services/notificationService"

function timeAgo(value: string): string {
  const diffMs = Date.now() - new Date(value).getTime()
  const mins = Math.floor(diffMs / 60_000)
  if (mins < 1) return "just now"
  if (mins < 60) return `${mins}m ago`
  const hours = Math.floor(mins / 60)
  if (hours < 24) return `${hours}h ago`
  return `${Math.floor(hours / 24)}d ago`
}

export function NotificationBell() {
  const [open, setOpen] = useState(false)
  const containerRef = useRef<HTMLDivElement>(null)
  const navigate = useNavigate()

  const { data: unread } = useUnreadCount()
  const { data: page, isLoading } = useNotifications(open)
  const markRead = useMarkNotificationRead()
  const markAllRead = useMarkAllNotificationsRead()

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setOpen(false)
      }
    }
    document.addEventListener("mousedown", handleClickOutside)
    return () => document.removeEventListener("mousedown", handleClickOutside)
  }, [])

  const count = unread?.count ?? 0

  function handleSelect(n: AppNotification) {
    if (!n.isRead) markRead.mutate(n.id)
    setOpen(false)
    if (n.actionUrl) navigate(n.actionUrl)
  }

  return (
    <div className="relative" ref={containerRef}>
      <button
        className="relative rounded-md p-2 text-foreground/70 hover:bg-muted hover:text-foreground transition-colors"
        onClick={() => setOpen((o) => !o)}
        aria-label="Notifications"
      >
        <Bell className="h-5 w-5" />
        {count > 0 && (
          <span className="absolute top-0.5 right-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-destructive px-1 text-[10px] font-semibold text-destructive-foreground">
            {count > 9 ? "9+" : count}
          </span>
        )}
      </button>

      {open && (
        <div className="absolute right-0 mt-2 w-80 rounded-lg border bg-card shadow-lg z-50">
          <div className="flex items-center justify-between px-4 py-3 border-b">
            <span className="text-sm font-semibold">Notifications</span>
            {count > 0 && (
              <button
                className="flex items-center gap-1 text-xs text-primary hover:underline"
                onClick={() => markAllRead.mutate()}
              >
                <CheckCheck className="h-3.5 w-3.5" />
                Mark all read
              </button>
            )}
          </div>

          <div className="max-h-96 overflow-y-auto">
            {isLoading ? (
              <div className="px-4 py-8 text-center text-sm text-muted-foreground">Loading…</div>
            ) : page?.content.length === 0 ? (
              <div className="px-4 py-8 text-center text-sm text-muted-foreground">
                You're all caught up.
              </div>
            ) : (
              page?.content.map((n) => (
                <button
                  key={n.id}
                  onClick={() => handleSelect(n)}
                  className={cn(
                    "flex w-full flex-col gap-0.5 px-4 py-3 text-left border-b last:border-b-0 hover:bg-muted/40 transition-colors",
                    !n.isRead && "bg-primary/5"
                  )}
                >
                  <div className="flex items-center gap-2">
                    {!n.isRead && <span className="h-1.5 w-1.5 rounded-full bg-primary shrink-0" />}
                    <span className="text-sm font-medium text-foreground truncate">{n.title}</span>
                  </div>
                  {n.body && (
                    <p className="text-xs text-muted-foreground line-clamp-2 ml-3.5">{n.body}</p>
                  )}
                  <span className="text-[10px] text-muted-foreground ml-3.5">{timeAgo(n.createdAt)}</span>
                </button>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  )
}
