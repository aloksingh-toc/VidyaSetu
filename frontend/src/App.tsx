import { Navigate, Route, Routes } from "react-router-dom"
import { useAuth } from "@/context/AuthContext"
import { InstallBanner } from "@/components/pwa/InstallBanner"
import { OfflineBanner } from "@/components/pwa/OfflineBanner"
import { AppLayout }       from "@/components/layout/AppLayout"
import Login               from "@/pages/Login"
import Register            from "@/pages/Register"
import Dashboard           from "@/pages/Dashboard"
import AcademicYears       from "@/pages/AcademicYears"
import Classes             from "@/pages/Classes"
import StudentList         from "@/pages/students/StudentList"
import AddStudent          from "@/pages/students/AddStudent"
import StudentProfile      from "@/pages/students/StudentProfile"
import Fees                from "@/pages/Fees"
import AttendancePage      from "@/pages/AttendancePage"
import Exams               from "@/pages/Exams"
import Staff               from "@/pages/Staff"
import Settings            from "@/pages/Settings"
import FeeDefaulters       from "@/pages/FeeDefaulters"
import Promotions          from "@/pages/Promotions"
import AuditLog            from "@/pages/AuditLog"

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth()

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-6 w-6 animate-spin rounded-full border-2 border-primary border-t-transparent" />
      </div>
    )
  }
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />
}

function PublicRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth()
  if (isLoading) return null
  return isAuthenticated ? <Navigate to="/" replace /> : <>{children}</>
}

export default function App() {
  return (
    <>
    <OfflineBanner />
    <Routes>
      {/* Public */}
      <Route path="/login"    element={<PublicRoute><Login /></PublicRoute>} />
      <Route path="/register" element={<PublicRoute><Register /></PublicRoute>} />

      {/* Protected — inside sidebar layout */}
      <Route
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route index                       element={<Dashboard />} />

        {/* Students */}
        <Route path="students"             element={<StudentList />} />
        <Route path="students/add"         element={<AddStudent />} />
        <Route path="students/:id"         element={<StudentProfile />} />
        <Route path="students/:id/edit"    element={<AddStudent />} />

        {/* Academic setup */}
        <Route path="academic-years"       element={<AcademicYears />} />
        <Route path="classes"              element={<Classes />} />

        {/* Fee management */}
        <Route path="fees"                 element={<Fees />} />

        {/* Attendance */}
        <Route path="attendance"           element={<AttendancePage />} />
        <Route path="exams"                element={<Exams />} />
        <Route path="messages"             element={<ComingSoon title="Messages" />} />
        <Route path="staff"                element={<Staff />} />
        <Route path="settings"             element={<Settings />} />
        <Route path="fee-defaulters"       element={<FeeDefaulters />} />
        <Route path="promotions"           element={<Promotions />} />
        <Route path="audit-log"            element={<AuditLog />} />
      </Route>

      {/* Catch-all */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
    <InstallBanner />
    </>
  )
}

function ComingSoon({ title }: { title: string }) {
  return (
    <div className="flex flex-col items-center justify-center min-h-[60vh] gap-3">
      <div className="text-5xl">🚧</div>
      <h2 className="text-xl font-semibold text-foreground">{title}</h2>
      <p className="text-sm text-muted-foreground">Coming soon!</p>
    </div>
  )
}
