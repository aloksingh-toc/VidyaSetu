import {
  Users, IndianRupee, CalendarCheck, BookOpen,
  UserPlus, ClipboardCheck, Wallet, CalendarRange, Layers,
} from "lucide-react"
import { Link } from "react-router-dom"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { useAuth } from "@/context/AuthContext"
import { useDashboardStats } from "@/hooks/useDashboard"
import { useCurrentAcademicYear } from "@/hooks/useAcademicYears"

const QUICK_ACTIONS = [
  { label: "Add Student",      to: "/students/add",   icon: UserPlus },
  { label: "Mark Attendance",  to: "/attendance",      icon: ClipboardCheck },
  { label: "Collect Fee",      to: "/fees",             icon: Wallet },
  { label: "Manage Exams",     to: "/exams",            icon: BookOpen },
  { label: "Academic Years",   to: "/academic-years",   icon: CalendarRange },
  { label: "Manage Classes",   to: "/classes",          icon: Layers },
]

function fmt(n: number, type: "rupee" | "percent" | "count" = "count"): string {
  if (n == null) return "—"
  if (type === "rupee") return "₹" + n.toLocaleString("en-IN")
  if (type === "percent") return n.toFixed(1) + "%"
  return n.toLocaleString("en-IN")
}

export default function Dashboard() {
  const { user }   = useAuth()
  const currentAY  = useCurrentAcademicYear()
  const { data: stats, isLoading } = useDashboardStats(currentAY?.id)

  const cards = [
    {
      label: "Total Students",
      value: isLoading ? "…" : fmt(stats?.totalStudents ?? 0),
      sub:   stats ? `${stats.totalClasses} classes` : "",
      icon:  Users,
      color: "text-blue-500 dark:text-blue-400",
    },
    {
      label: "Fees This Month",
      value: isLoading ? "…" : fmt(stats?.feesCollectedThisMonth ?? 0, "rupee"),
      sub:   stats ? `${fmt(stats.feesCollectedThisYear, "rupee")} this year` : "",
      icon:  IndianRupee,
      color: "text-success",
    },
    {
      label: "Attendance Today",
      value: isLoading ? "…" : fmt(stats?.attendancePercent ?? 0, "percent"),
      sub:   stats ? `${stats.presentToday} present, ${stats.absentToday} absent` : "",
      icon:  CalendarCheck,
      color: "text-warning",
    },
    {
      label: "Upcoming Exams",
      value: isLoading ? "…" : fmt(stats?.upcomingExams ?? 0),
      sub:   currentAY ? currentAY.name : "",
      icon:  BookOpen,
      color: "text-purple-500 dark:text-purple-400",
    },
  ]

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-semibold text-foreground">
          Welcome back, {user?.name?.split(" ")[0]} 👋
        </h1>
        <p className="text-sm text-muted-foreground mt-0.5">
          {user?.schoolName} · {new Date().toLocaleDateString("en-IN", {
            weekday: "long", day: "numeric", month: "long", year: "numeric",
          })}
        </p>
      </div>

      {/* Stat cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {cards.map(({ label, value, sub, icon: Icon, color }) => (
          <Card key={label}>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">{label}</CardTitle>
              <Icon className={`h-4 w-4 ${color}`} />
            </CardHeader>
            <CardContent>
              <p className="text-2xl font-bold text-foreground">{value}</p>
              {sub && <p className="text-xs text-muted-foreground mt-1">{sub}</p>}
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Quick actions */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Quick Actions</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
            {QUICK_ACTIONS.map(({ label, to, icon: Icon }) => (
              <Link
                key={to}
                to={to}
                className="flex flex-col items-center gap-2 rounded-md border border-border px-3 py-4 text-center hover:border-primary hover:bg-accent transition-colors"
              >
                <Icon className="h-5 w-5 text-primary" />
                <span className="text-xs font-medium text-foreground">{label}</span>
              </Link>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
