import { NavLink, useNavigate } from "react-router-dom"
import {
  LayoutDashboard,
  Users,
  GraduationCap,
  CalendarDays,
  BookOpen,
  IndianRupee,
  ClipboardList,
  MessageSquare,
  Settings,
  LogOut,
  School,
  AlertCircle,
  ArrowUpCircle,
  History,
  Sun,
  Moon,
  X,
} from "lucide-react"
import { cn } from "@/lib/utils"
import { useAuth } from "@/context/AuthContext"
import { useTheme } from "@/context/ThemeContext"
import { Button } from "@/components/ui/button"

interface NavItem {
  to:       string
  label:    string
  icon:     React.ElementType
  disabled?: boolean
  roles?:   string[]
}

const navItems: NavItem[] = [
  { to: "/",              label: "Dashboard",       icon: LayoutDashboard },
  { to: "/students",      label: "Students",         icon: Users },
  { to: "/academic-years",label: "Academic Years",   icon: CalendarDays },
  { to: "/classes",       label: "Classes",          icon: School },
  { to: "/fees",          label: "Fees",             icon: IndianRupee   },
  { to: "/attendance",    label: "Attendance",       icon: ClipboardList },
  { to: "/exams",         label: "Exams",            icon: BookOpen      },
  { to: "/messages",      label: "Messages",         icon: MessageSquare,  disabled: true },
  { to: "/staff",         label: "Staff",            icon: GraduationCap },
  { to: "/fee-defaulters",label: "Fee Defaulters",   icon: AlertCircle },
  { to: "/promotions",    label: "Promotions",       icon: ArrowUpCircle },
  { to: "/audit-log",     label: "Audit Log",        icon: History,        roles: ["OWNER"] },
  { to: "/settings",      label: "Settings",         icon: Settings },
]

interface SidebarProps {
  open:    boolean
  onClose: () => void
}

export function Sidebar({ open, onClose }: SidebarProps) {
  const { user, logout } = useAuth()
  const { theme, toggleTheme } = useTheme()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate("/login")
  }

  // Auto-close only makes sense on mobile, where the sidebar is an overlay —
  // on desktop it's a pinned panel the user toggles explicitly.
  const closeOnMobile = () => {
    if (window.matchMedia("(max-width: 767px)").matches) onClose()
  }

  return (
    <>
      {open && (
        <div
          className="fixed inset-0 z-30 bg-black/50 md:hidden"
          onClick={onClose}
        />
      )}
      <aside
        className={cn(
          "flex h-screen w-60 flex-col bg-sidebar border-r border-sidebar-border fixed left-0 top-0 z-40 transition-transform duration-200",
          open ? "translate-x-0" : "-translate-x-full"
        )}
      >
      {/* Brand */}
      <div className="flex items-center gap-2 px-5 py-4 border-b border-sidebar-border">
        <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary">
          <span className="text-sm font-bold text-primary-foreground">VS</span>
        </div>
        <div className="flex-1">
          <p className="text-sm font-semibold text-sidebar-foreground">VidyaSetu</p>
          <p className="text-[10px] text-sidebar-foreground/60 truncate max-w-[130px]">
            {user?.schoolName}
          </p>
        </div>
        <button
          className="text-sidebar-foreground/70 hover:text-sidebar-foreground"
          onClick={onClose}
          aria-label="Close menu"
        >
          <X className="h-5 w-5" />
        </button>
      </div>

      {/* Nav */}
      <nav className="flex-1 overflow-y-auto py-3 px-2 space-y-0.5">
        {navItems
          .filter(({ roles }) => !roles || (user?.role && roles.includes(user.role)))
          .map(({ to, label, icon: Icon, disabled }) => (
          <NavLink
            key={to}
            to={to}
            end={to === "/"}
            className={({ isActive }) =>
              cn(
                "flex items-center gap-3 rounded-md px-3 py-2 text-sm transition-colors",
                disabled
                  ? "cursor-not-allowed opacity-40 text-sidebar-foreground"
                  : isActive
                  ? "bg-primary text-primary-foreground font-medium"
                  : "text-sidebar-foreground hover:bg-sidebar-accent"
              )
            }
            onClick={(e) => {
              if (disabled) { e.preventDefault(); return }
              closeOnMobile()
            }}
          >
            <Icon className="h-4 w-4 shrink-0" />
            <span>{label}</span>
            {disabled && (
              <span className="ml-auto text-[9px] uppercase tracking-wide opacity-50">
                Soon
              </span>
            )}
          </NavLink>
        ))}
      </nav>

      {/* Footer */}
      <div className="border-t border-sidebar-border p-3 space-y-1">
        <div className="px-3 py-1.5">
          <p className="text-xs font-medium text-sidebar-foreground truncate">{user?.name}</p>
          <p className="text-[10px] text-sidebar-foreground/60">{user?.role}</p>
        </div>
        <Button
          variant="ghost"
          size="sm"
          className="w-full justify-start gap-3 text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-foreground"
          onClick={toggleTheme}
        >
          {theme === "dark" ? <Sun className="h-4 w-4" /> : <Moon className="h-4 w-4" />}
          {theme === "dark" ? "Light mode" : "Dark mode"}
        </Button>
        <Button
          variant="ghost"
          size="sm"
          className="w-full justify-start gap-3 text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-foreground"
          onClick={handleLogout}
        >
          <LogOut className="h-4 w-4" />
          Log out
        </Button>
      </div>
      </aside>
    </>
  )
}
