import { useState } from "react"
import { History, RefreshCw } from "lucide-react"
import { useAuditLogs } from "@/hooks/useAuditLogs"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { SkeletonTable } from "@/components/shared/SkeletonTable"
import { EmptyState } from "@/components/shared/EmptyState"

function formatDateTime(value: string): string {
  return new Intl.DateTimeFormat("en-IN", {
    day: "2-digit", month: "short", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  }).format(new Date(value))
}

function actionLabel(action: string): string {
  return action
    .toLowerCase()
    .split("_")
    .map((w) => w[0].toUpperCase() + w.slice(1))
    .join(" ")
}

function actionVariant(action: string): "default" | "success" | "warning" | "destructive" | "secondary" {
  if (action.includes("CREATED") || action.includes("ADDED") || action.includes("RECORDED")) return "success"
  if (action.includes("VOIDED") || action.includes("DEACTIVATED") || action.includes("DELETED")) return "destructive"
  if (action.includes("UPDATED") || action.includes("CHANGED")) return "warning"
  return "secondary"
}

export default function AuditLog() {
  const [page, setPage] = useState(0)
  const { data, isLoading, isError, refetch } = useAuditLogs({ page, size: 25 })

  return (
    <div className="p-6 space-y-4">
      <div>
        <h1 className="text-xl font-semibold text-foreground">Audit Log</h1>
        <p className="text-sm text-muted-foreground mt-0.5">
          A record of every important action taken in your school's account
        </p>
      </div>

      <div className="rounded-lg border bg-card overflow-hidden">
        {isLoading ? (
          <SkeletonTable rows={10} cols={4} />
        ) : isError ? (
          <div className="flex flex-col items-center gap-3 py-16">
            <p className="text-sm text-destructive">Failed to load audit log.</p>
            <Button variant="outline" size="sm" onClick={() => refetch()}>
              <RefreshCw className="h-4 w-4" />
              Retry
            </Button>
          </div>
        ) : data?.content.length === 0 ? (
          <EmptyState
            icon={<History />}
            title="No activity yet"
            description="Actions like adding students, recording fee payments, and updating settings will show up here."
          />
        ) : (
          <>
            <table className="w-full text-sm">
              <thead className="border-b bg-muted/30">
                <tr>
                  {["Time", "User", "Action", "Entity"].map((h) => (
                    <th key={h} className="px-4 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wide">
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {data?.content.map((entry) => (
                  <tr key={entry.id} className="hover:bg-muted/20 transition-colors">
                    <td className="px-4 py-3 text-muted-foreground whitespace-nowrap">
                      {formatDateTime(entry.createdAt)}
                    </td>
                    <td className="px-4 py-3 font-medium">{entry.userName ?? "System"}</td>
                    <td className="px-4 py-3">
                      <Badge variant={actionVariant(entry.action)}>{actionLabel(entry.action)}</Badge>
                    </td>
                    <td className="px-4 py-3 text-muted-foreground">{entry.entityType ?? "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            {data && data.totalPages > 1 && (
              <div className="flex items-center justify-between px-4 py-3 border-t text-sm text-muted-foreground">
                <span>
                  Page {page + 1} of {data.totalPages} · {data.totalElements} total
                </span>
                <div className="flex gap-2">
                  <Button variant="outline" size="sm" disabled={data.first} onClick={() => setPage((p) => p - 1)}>
                    Previous
                  </Button>
                  <Button variant="outline" size="sm" disabled={data.last} onClick={() => setPage((p) => p + 1)}>
                    Next
                  </Button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}
