import { useState, useEffect } from "react"
import { CheckCircle2, XCircle, Clock, Plane, Loader2, Save } from "lucide-react"
import { useAcademicYears, useCurrentAcademicYear } from "@/hooks/useAcademicYears"
import { useClasses } from "@/hooks/useClasses"
import { useStudents } from "@/hooks/useStudents"
import { useAttendanceByClass, useBulkMarkAttendance } from "@/hooks/useAttendance"
import type { AttendanceRecord } from "@/services/attendanceService"
import { Button } from "@/components/ui/button"
import { Label }  from "@/components/ui/label"
import { EmptyState } from "@/components/shared/EmptyState"

type AttStatus = "PRESENT" | "ABSENT" | "LATE" | "LEAVE"

const STATUS_CONFIG: Record<AttStatus, { label: string; color: string; icon: React.ReactNode }> = {
  PRESENT: { label: "Present",  color: "text-success",   icon: <CheckCircle2 className="h-4 w-4" /> },
  ABSENT:  { label: "Absent",   color: "text-destructive", icon: <XCircle      className="h-4 w-4" /> },
  LATE:    { label: "Late",     color: "text-warning",   icon: <Clock        className="h-4 w-4" /> },
  LEAVE:   { label: "Leave",    color: "text-blue-600 dark:text-blue-400",   icon: <Plane        className="h-4 w-4" /> },
}

const today = new Date().toISOString().slice(0, 10)

export default function AttendancePage() {
  const { data: years }      = useAcademicYears()
  const currentYear          = useCurrentAcademicYear()
  const [yearId, setYearId]  = useState<string>("")
  const [classId, setClassId] = useState<string>("")
  const [date, setDate]       = useState<string>(today)

  const activeYearId = yearId || currentYear?.id || ""

  // Reset class when year changes
  useEffect(() => { setClassId("") }, [yearId])

  const { data: classes } = useClasses(activeYearId)

  // Load students for the selected class
  const { data: studentsPage } = useStudents({ classId: classId || undefined, activeOnly: true })
  const students = studentsPage?.content ?? []

  // Load existing attendance for class + date
  const { data: existing } = useAttendanceByClass(classId || undefined, date || undefined)

  // Local state: studentId → status
  const [statusMap, setStatusMap] = useState<Record<string, AttStatus>>({})

  // Pre-fill from existing attendance
  useEffect(() => {
    if (!existing) return
    const map: Record<string, AttStatus> = {}
    existing.forEach((a: AttendanceRecord) => { map[a.studentId] = a.status as AttStatus })
    setStatusMap(map)
  }, [existing])

  // Default all students to PRESENT when list changes
  useEffect(() => {
    if (!students.length) return
    setStatusMap(prev => {
      const next = { ...prev }
      students.forEach(s => { if (!next[s.id]) next[s.id] = "PRESENT" })
      return next
    })
  }, [students])

  const markAll = (status: AttStatus) =>
    setStatusMap(students.reduce((acc, s) => ({ ...acc, [s.id]: status }), {}))

  const bulkMark = useBulkMarkAttendance()

  const handleSubmit = async () => {
    if (!classId || !date || !activeYearId || !students.length) return
    await bulkMark.mutateAsync({
      classId,
      academicYearId: activeYearId,
      date,
      entries: students.map(s => ({ studentId: s.id, status: statusMap[s.id] ?? "ABSENT" })),
    })
  }

  const presentCount = Object.values(statusMap).filter(s => s === "PRESENT").length
  const absentCount  = Object.values(statusMap).filter(s => s === "ABSENT").length

  return (
    <div className="p-6 space-y-4">
      {/* Header */}
      <div>
        <h1 className="text-xl font-semibold">Attendance</h1>
        <p className="text-sm text-muted-foreground mt-0.5">Mark daily attendance for each class</p>
      </div>

      {/* Filters */}
      <div className="flex flex-wrap items-end gap-4">
        <div className="space-y-1.5">
          <Label className="text-sm">Academic Year</Label>
          <select
            value={yearId || activeYearId}
            onChange={e => setYearId(e.target.value)}
            className="flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
          >
            {years?.map(y => (
              <option key={y.id} value={y.id}>{y.name}{y.isCurrent ? " (Current)" : ""}</option>
            ))}
          </select>
        </div>

        <div className="space-y-1.5">
          <Label className="text-sm">Class</Label>
          <select
            value={classId}
            onChange={e => setClassId(e.target.value)}
            className="flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
          >
            <option value="">Select class</option>
            {classes?.map(c => (
              <option key={c.id} value={c.id}>{c.name}{c.section ? ` (${c.section})` : ""}</option>
            ))}
          </select>
        </div>

        <div className="space-y-1.5">
          <Label className="text-sm">Date</Label>
          <input
            type="date"
            value={date}
            max={today}
            onChange={e => setDate(e.target.value)}
            className="flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
          />
        </div>
      </div>

      {/* Stats + Mark All */}
      {classId && students.length > 0 && (
        <div className="flex items-center justify-between">
          <div className="flex gap-3 text-sm">
            <span className="text-success font-medium">{presentCount} Present</span>
            <span className="text-destructive font-medium">{absentCount} Absent</span>
            <span className="text-muted-foreground">{students.length} Total</span>
          </div>
          <div className="flex gap-2">
            <Button size="sm" variant="outline" onClick={() => markAll("PRESENT")}>All Present</Button>
            <Button size="sm" variant="outline" onClick={() => markAll("ABSENT")}>All Absent</Button>
          </div>
        </div>
      )}

      {/* Student table */}
      <div className="rounded-lg border bg-card overflow-hidden">
        {!classId ? (
          <EmptyState title="Select a class" description="Choose a class and date to mark attendance." />
        ) : !students.length ? (
          <EmptyState title="No students" description="No active students found in this class." />
        ) : (
          <table className="w-full text-sm">
            <thead className="border-b bg-muted/30">
              <tr>
                {["Roll", "Student Name", "Status"].map(h => (
                  <th key={h} className="px-4 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wide">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {students.map(student => {
                const status = statusMap[student.id] ?? "PRESENT"
                return (
                  <tr key={student.id} className="hover:bg-muted/20">
                    <td className="px-4 py-3 text-muted-foreground text-xs w-16">{student.rollNumber ?? "—"}</td>
                    <td className="px-4 py-3 font-medium">{student.fullName}</td>
                    <td className="px-4 py-3">
                      <div className="flex gap-2">
                        {(["PRESENT", "ABSENT", "LATE", "LEAVE"] as AttStatus[]).map(s => {
                          const cfg = STATUS_CONFIG[s]
                          return (
                            <button
                              key={s}
                              type="button"
                              onClick={() => setStatusMap(prev => ({ ...prev, [student.id]: s }))}
                              className={`flex items-center gap-1 px-2.5 py-1 rounded text-xs font-medium border transition-colors
                                ${status === s
                                  ? `${cfg.color} bg-current/10 border-current/30`
                                  : "text-muted-foreground border-transparent hover:border-muted"}`}
                            >
                              <span className={status === s ? cfg.color : "text-muted-foreground"}>
                                {cfg.icon}
                              </span>
                              {cfg.label}
                            </button>
                          )
                        })}
                      </div>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        )}
      </div>

      {/* Save button */}
      {classId && students.length > 0 && (
        <div className="flex justify-end">
          <Button onClick={handleSubmit} disabled={bulkMark.isPending}>
            {bulkMark.isPending
              ? <><Loader2 className="h-4 w-4 animate-spin" /> Saving…</>
              : <><Save className="h-4 w-4" /> Save Attendance</>
            }
          </Button>
        </div>
      )}
    </div>
  )
}
