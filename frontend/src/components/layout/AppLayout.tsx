import { useState } from "react"
import { Outlet } from "react-router-dom"
import { Menu, X } from "lucide-react"
import { cn } from "@/lib/utils"
import { Sidebar } from "./Sidebar"
import { NotificationBell } from "./NotificationBell"

export function AppLayout() {
  // Drives the sidebar on every breakpoint: an overlay panel on mobile,
  // a pinned/collapsible panel on desktop.
  const [sidebarOpen, setSidebarOpen] = useState(true)

  return (
    <div className="flex min-h-screen bg-background">
      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <main
        className={cn(
          "flex-1 min-h-screen overflow-x-hidden transition-[margin] duration-200",
          sidebarOpen ? "md:ml-60" : "md:ml-0"
        )}
      >
        {/* Top bar */}
        <div className="sticky top-0 z-20 flex items-center gap-3 border-b border-border bg-card px-4 py-3">
          <button
            className="text-foreground"
            onClick={() => setSidebarOpen((o) => !o)}
            aria-label={sidebarOpen ? "Close sidebar" : "Open sidebar"}
          >
            {sidebarOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
          <span className={cn("text-sm font-semibold", sidebarOpen && "md:hidden")}>
            VidyaSetu
          </span>
          <div className="flex-1" />
          <NotificationBell />
        </div>
        <Outlet />
      </main>
    </div>
  )
}
