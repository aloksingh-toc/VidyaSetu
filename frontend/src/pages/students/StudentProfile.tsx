import { useState } from "react"
import { useNavigate, useParams } from "react-router-dom"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import {
  ArrowLeft, Pencil, UserX,
  Phone, User, Plus, Trash2, Loader2, Printer, Ban, FileText,
} from "lucide-react"
import { useStudent, useDeactivateStudent } from "@/hooks/useStudents"
import { studentService, type Parent }      from "@/services/studentService"
import { useQueryClient, useMutation }      from "@tanstack/react-query"
import { STUDENT_KEYS }                                        from "@/hooks/useStudents"
import { useFeePayments, useVoidPayment }   from "@/hooks/useFeePayments"
import { useStudentAttendance, useAttendanceSummary } from "@/hooks/useAttendance"
import { useExams, useStudentExamMarks }    from "@/hooks/useExams"
import { useCurrentAcademicYear }           from "@/hooks/useAcademicYears"
import type { ExamMark }                    from "@/services/examService"
import { useAuth }                          from "@/context/AuthContext"
import { CollectPaymentDialog }             from "@/components/fees/CollectPaymentDialog"
import { ReceiptDialog }                    from "@/components/fees/ReceiptDialog"
import type { FeePayment }                  from "@/services/feePaymentService"
import { Button }      from "@/components/ui/button"
import { Input }       from "@/components/ui/input"
import { Label }       from "@/components/ui/label"
import { Badge }       from "@/components/ui/badge"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs"
import { Separator }   from "@/components/ui/separator"
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from "@/components/ui/dialog"
import { Skeleton }    from "@/components/ui/skeleton"
import { formatDate, getInitials } from "@/lib/utils"

// ── Main page ─────────────────────────────────────────────────────────────────
export default function StudentProfile() {
  const { id }          = useParams<{ id: string }>()
  const navigate        = useNavigate()
  const { data: student, isLoading } = useStudent(id!)
  const deactivate      = useDeactivateStudent()

  if (isLoading) return <ProfileSkeleton />
  if (!student) return (
    <div className="p-6">
      <p className="text-muted-foreground">Student not found.</p>
    </div>
  )

  const genderColor = student.gender === "MALE"
    ? "bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400"
    : student.gender === "FEMALE"
    ? "bg-pink-100 text-pink-700 dark:bg-pink-500/15 dark:text-pink-400"
    : "bg-muted text-muted-foreground"

  return (
    <div className="p-6 space-y-4">
      {/* Back + actions */}
      <div className="flex items-center justify-between">
        <Button variant="ghost" size="sm" onClick={() => navigate("/students")} className="gap-1.5">
          <ArrowLeft className="h-4 w-4" /> Students
        </Button>
        <div className="flex gap-2">
          <Button variant="outline" size="sm" onClick={() => navigate(`/students/${id}/edit`)}>
            <Pencil className="h-3.5 w-3.5" /> Edit
          </Button>
          {student.isActive ? (
            <Button
              variant="outline"
              size="sm"
              className="text-destructive hover:text-destructive"
              onClick={() => {
                if (confirm(`Deactivate ${student.fullName}?`)) {
                  deactivate.mutate(id!, { onSuccess: () => navigate("/students") })
                }
              }}
            >
              <UserX className="h-3.5 w-3.5" /> Deactivate
            </Button>
          ) : (
            <Badge variant="destructive">Inactive</Badge>
          )}
        </div>
      </div>

      {/* Header card */}
      <Card>
        <CardContent className="pt-6 flex items-center gap-4">
          {/* Avatar initials */}
          <div className={`flex h-16 w-16 shrink-0 items-center justify-center rounded-full text-xl font-bold ${genderColor}`}>
            {getInitials(student.fullName)}
          </div>
          <div className="flex-1 min-w-0">
            <h1 className="text-xl font-semibold text-foreground truncate">{student.fullName}</h1>
            <div className="flex flex-wrap items-center gap-x-3 gap-y-1 mt-1 text-sm text-muted-foreground">
              <span>{student.className}{student.section ? ` – ${student.section}` : ""}</span>
              {student.rollNumber && (
                <>
                  <span className="text-border">·</span>
                  <span>Roll #{student.rollNumber}</span>
                </>
              )}
              {student.admissionNumber && (
                <>
                  <span className="text-border">·</span>
                  <span>Adm. {student.admissionNumber}</span>
                </>
              )}
              {student.gender && (
                <>
                  <span className="text-border">·</span>
                  <Badge variant="outline" className="text-xs py-0">{student.gender}</Badge>
                </>
              )}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Tabs */}
      <Tabs defaultValue="details">
        <TabsList>
          <TabsTrigger value="details">Details</TabsTrigger>
          <TabsTrigger value="parents">Parents</TabsTrigger>
          <TabsTrigger value="fees">Fees</TabsTrigger>
          <TabsTrigger value="attendance">Attendance</TabsTrigger>
          <TabsTrigger value="marks">Marks</TabsTrigger>
        </TabsList>

        {/* ── Details tab ─────────────────────────────────── */}
        <TabsContent value="details">
          <Card>
            <CardHeader className="pb-3">
              <CardTitle className="text-base">Personal Information</CardTitle>
            </CardHeader>
            <CardContent>
              <dl className="grid grid-cols-2 gap-x-6 gap-y-4 text-sm">
                <InfoRow label="Full Name"        value={student.fullName} />
                <InfoRow label="Date of Birth"    value={formatDate(student.dateOfBirth)} />
                <InfoRow label="Gender"           value={student.gender ?? "—"} />
                <InfoRow label="Blood Group"      value={student.bloodGroup ?? "—"} />
                <InfoRow label="Admission Date"   value={formatDate(student.admissionDate)} />
                <InfoRow label="Admission Number" value={student.admissionNumber ?? "—"} />
                <InfoRow label="Address"          value={student.address ?? "—"} span />
              </dl>
            </CardContent>
          </Card>
        </TabsContent>

        {/* ── Parents tab ─────────────────────────────────── */}
        <TabsContent value="parents">
          <ParentsTab studentId={id!} parents={student.parents ?? []} />
        </TabsContent>

        {/* ── Fees tab ────────────────────────────────────── */}
        <TabsContent value="fees">
          <FeesTab studentId={id!} />
        </TabsContent>

        {/* ── Attendance tab ───────────────────────────────── */}
        <TabsContent value="attendance">
          <AttendanceTab studentId={id!} />
        </TabsContent>

        {/* ── Marks tab ────────────────────────────────────── */}
        <TabsContent value="marks">
          <MarksTab studentId={id!} studentName={student.fullName} />
        </TabsContent>
      </Tabs>
    </div>
  )
}

// ── Parents tab ───────────────────────────────────────────────────────────────
function ParentsTab({ studentId, parents }: { studentId: string; parents: Parent[] }) {
  const [addOpen, setAddOpen] = useState(false)
  const qc = useQueryClient()

  const deleteParent = useMutation({
    mutationFn: ({ parentId }: { parentId: string }) =>
      studentService.deleteParent(studentId, parentId),
    onSuccess: () => qc.invalidateQueries({ queryKey: STUDENT_KEYS.detail(studentId) }),
  })

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between pb-3">
        <CardTitle className="text-base">Parents / Guardians</CardTitle>
        <Button size="sm" variant="outline" onClick={() => setAddOpen(true)}>
          <Plus className="h-3.5 w-3.5" /> Add Parent
        </Button>
      </CardHeader>
      <CardContent className="space-y-3">
        {parents.length === 0 ? (
          <p className="text-sm text-muted-foreground text-center py-6">No parents added yet.</p>
        ) : (
          parents.map((p, i) => (
            <div key={p.id}>
              {i > 0 && <Separator className="mb-3" />}
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-3">
                  <div className="flex h-9 w-9 items-center justify-center rounded-full bg-muted text-muted-foreground text-sm font-medium">
                    <User className="h-4 w-4" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="text-sm font-medium">{p.name}</span>
                      <Badge variant="outline" className="text-xs py-0">{p.relation}</Badge>
                      {p.isPrimary && <Badge variant="success" className="text-xs py-0">Primary</Badge>}
                    </div>
                    <div className="flex items-center gap-1 text-xs text-muted-foreground mt-0.5">
                      <Phone className="h-3 w-3" />
                      <span>{p.phone}</span>
                      {p.whatsappOptOut && (
                        <Badge variant="secondary" className="text-xs py-0 ml-1">WhatsApp off</Badge>
                      )}
                    </div>
                  </div>
                </div>
                <Button
                  variant="ghost"
                  size="icon"
                  className="h-7 w-7 text-muted-foreground hover:text-destructive"
                  onClick={() => {
                    if (confirm(`Remove ${p.name}?`)) {
                      deleteParent.mutate({ parentId: p.id })
                    }
                  }}
                >
                  <Trash2 className="h-3.5 w-3.5" />
                </Button>
              </div>
            </div>
          ))
        )}
      </CardContent>

      {/* Add parent dialog */}
      <Dialog open={addOpen} onOpenChange={setAddOpen}>
        <DialogContent>
          <AddParentForm studentId={studentId} onClose={() => setAddOpen(false)} />
        </DialogContent>
      </Dialog>
    </Card>
  )
}

// ── Add Parent form ───────────────────────────────────────────────────────────
const parentSchema = z.object({
  name:     z.string().min(1, "Name required"),
  relation: z.enum(["FATHER", "MOTHER", "GUARDIAN"]),
  phone:    z.string().regex(/^[6-9]\d{9}$/, "Valid 10-digit mobile"),
  whatsappNumber: z.string().optional(),
  isPrimary: z.boolean().optional(),
})
type ParentFormValues = z.infer<typeof parentSchema>

function AddParentForm({ studentId, onClose }: { studentId: string; onClose: () => void }) {
  const qc = useQueryClient()
  const add = useMutation({
    mutationFn: (payload: ParentFormValues) => studentService.addParent(studentId, payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: STUDENT_KEYS.detail(studentId) })
      onClose()
    },
  })

  const { register, handleSubmit, formState: { errors } } = useForm<ParentFormValues>({
    resolver: zodResolver(parentSchema),
  })

  return (
    <form onSubmit={handleSubmit((v) => add.mutateAsync(v))}>
      <DialogHeader>
        <DialogTitle>Add Parent / Guardian</DialogTitle>
      </DialogHeader>
      <div className="space-y-4">
        <div className="space-y-1.5">
          <Label>Name *</Label>
          <Input {...register("name")} />
          {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
        </div>
        <div className="grid grid-cols-2 gap-3">
          <div className="space-y-1.5">
            <Label>Relation *</Label>
            <select {...register("relation")}
              className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
              <option value="FATHER">Father</option>
              <option value="MOTHER">Mother</option>
              <option value="GUARDIAN">Guardian</option>
            </select>
          </div>
          <div className="space-y-1.5">
            <Label>Phone *</Label>
            <Input type="tel" inputMode="numeric" maxLength={10} {...register("phone")} />
            {errors.phone && <p className="text-xs text-destructive">{errors.phone.message}</p>}
          </div>
        </div>
        <div className="flex items-center gap-2">
          <input type="checkbox" id="isPrimary" {...register("isPrimary")} className="h-4 w-4" />
          <Label htmlFor="isPrimary" className="font-normal cursor-pointer">Set as primary contact</Label>
        </div>
      </div>
      <DialogFooter>
        <Button type="button" variant="outline" onClick={onClose}>Cancel</Button>
        <Button type="submit" disabled={add.isPending}>
          {add.isPending && <Loader2 className="h-4 w-4 animate-spin" />}
          Add Parent
        </Button>
      </DialogFooter>
    </form>
  )
}

// ── Fees tab ──────────────────────────────────────────────────────────────────
const formatINR = (n: number) =>
  "₹" + n.toLocaleString("en-IN", { minimumFractionDigits: 0, maximumFractionDigits: 2 })

function FeesTab({ studentId }: { studentId: string }) {
  const currentYear = useCurrentAcademicYear()
  const yearId      = currentYear?.id ?? ""
  const { user }    = useAuth()

  const { data: payments, isLoading } = useFeePayments(studentId, yearId)
  const voidPayment = useVoidPayment(studentId)

  const [collectOpen, setCollectOpen]       = useState(false)
  const [receipt, setReceipt]               = useState<FeePayment | null>(null)

  if (!yearId) {
    return (
      <Card>
        <CardContent className="py-10 text-center text-sm text-muted-foreground">
          Set a current academic year to manage fees.
        </CardContent>
      </Card>
    )
  }

  const totalPaid = (payments ?? [])
    .filter((p) => p.status === "ACTIVE")
    .reduce((sum, p) => sum + p.amountPaid, 0)

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between pb-3">
        <div>
          <CardTitle className="text-base">Fee Payments</CardTitle>
          <p className="text-xs text-muted-foreground mt-0.5">
            {currentYear?.name} · Collected: {formatINR(totalPaid)}
          </p>
        </div>
        <Button size="sm" onClick={() => setCollectOpen(true)} className="gap-1.5">
          <Plus className="h-3.5 w-3.5" /> Collect Payment
        </Button>
      </CardHeader>
      <CardContent className="space-y-2">
        {isLoading ? (
          <div className="flex justify-center py-8 text-muted-foreground">
            <Loader2 className="h-5 w-5 animate-spin" />
          </div>
        ) : !payments?.length ? (
          <p className="text-sm text-muted-foreground text-center py-8">
            No payments recorded yet.
          </p>
        ) : (
          payments.map((p) => {
            const voided = p.status === "VOIDED"
            return (
              <div
                key={p.id}
                className={`flex items-center justify-between rounded-md border px-3 py-2.5 ${voided ? "opacity-60" : ""}`}
              >
                <div className="min-w-0">
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-medium">{p.feeTypeName}</span>
                    {voided && <Badge variant="destructive" className="text-xs py-0">Voided</Badge>}
                    {p.forMonth && <span className="text-xs text-muted-foreground">{p.forMonth}</span>}
                  </div>
                  <p className="text-xs text-muted-foreground mt-0.5">
                    #{p.receiptNumber} · {p.paymentDate} · {p.paymentMethod}
                  </p>
                </div>
                <div className="flex items-center gap-1 shrink-0">
                  <span className={`text-sm font-semibold ${voided ? "line-through" : ""}`}>
                    {formatINR(p.amountPaid)}
                  </span>
                  {!voided && (
                    <>
                      <Button
                        variant="ghost" size="icon" className="h-7 w-7"
                        title="Print receipt"
                        onClick={() => setReceipt(p)}
                      >
                        <Printer className="h-3.5 w-3.5" />
                      </Button>
                      <Button
                        variant="ghost" size="icon"
                        className="h-7 w-7 text-muted-foreground hover:text-destructive"
                        title="Void payment"
                        disabled={voidPayment.isPending}
                        onClick={() => {
                          const reason = prompt(`Void receipt ${p.receiptNumber}? Enter a reason:`)
                          if (reason) voidPayment.mutate({ paymentId: p.id, reason })
                        }}
                      >
                        <Ban className="h-3.5 w-3.5" />
                      </Button>
                    </>
                  )}
                </div>
              </div>
            )
          })
        )}
      </CardContent>

      <CollectPaymentDialog
        studentId={studentId}
        academicYearId={yearId}
        open={collectOpen}
        onClose={() => setCollectOpen(false)}
        onCollected={(payment) => { setCollectOpen(false); setReceipt(payment) }}
      />

      <ReceiptDialog
        payment={receipt}
        schoolName={user?.schoolName ?? "School"}
        onClose={() => setReceipt(null)}
      />
    </Card>
  )
}

// ── Attendance tab ────────────────────────────────────────────────────────────
const STATUS_META: Record<string, { label: string; cls: string }> = {
  PRESENT: { label: "Present", cls: "text-success bg-success/10 border-success/20" },
  ABSENT:  { label: "Absent",  cls: "text-destructive bg-destructive/10 border-destructive/20" },
  LATE:    { label: "Late",    cls: "text-warning bg-warning/10 border-warning/20" },
  LEAVE:   { label: "Leave",   cls: "text-blue-700 bg-blue-50 border-blue-200 dark:text-blue-400 dark:bg-blue-500/10 dark:border-blue-500/20" },
}

const MONTH_NAMES = ["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"]

function AttendanceTab({ studentId }: { studentId: string }) {
  const currentYear = useCurrentAcademicYear()
  const yearId = currentYear?.id ?? ""
  const [filterMonth, setFilterMonth] = useState<string>("ALL")

  const { data: summary, isLoading: summaryLoading } = useAttendanceSummary(studentId, yearId || undefined)
  const { data: records = [], isLoading: recordsLoading } = useStudentAttendance(studentId, yearId || undefined)

  if (!yearId) {
    return (
      <Card>
        <CardContent className="py-10 text-center text-sm text-muted-foreground">
          Set a current academic year to view attendance.
        </CardContent>
      </Card>
    )
  }

  const months = [...new Set(records.map((r) => r.date.slice(0, 7)))].sort()

  const visible = filterMonth === "ALL"
    ? records
    : records.filter((r) => r.date.slice(0, 7) === filterMonth)

  const pct = summary?.attendancePercent ?? 0
  const pctColor = pct >= 75 ? "text-success" : pct >= 50 ? "text-warning" : "text-destructive"

  return (
    <div className="space-y-3">
      {/* Summary card */}
      <Card>
        <CardContent className="pt-4 pb-4">
          {summaryLoading ? (
            <div className="flex justify-center py-4"><Loader2 className="h-5 w-5 animate-spin text-muted-foreground" /></div>
          ) : summary ? (
            <div className="flex items-center gap-6">
              <div className="text-center min-w-[64px]">
                <p className={`text-3xl font-bold ${pctColor}`}>{pct.toFixed(1)}%</p>
                <p className="text-xs text-muted-foreground mt-0.5">Attendance</p>
              </div>
              <div className="flex gap-4 flex-wrap text-sm">
                <Stat label="Present" value={summary.totalPresent} color="text-success" />
                <Stat label="Absent"  value={summary.totalAbsent}  color="text-destructive" />
                <Stat label="Late"    value={summary.totalLate}    color="text-warning" />
                <Stat label="Leave"   value={summary.totalLeave}   color="text-blue-600 dark:text-blue-400" />
                <Stat label="Total"   value={summary.totalMarked}  color="text-muted-foreground" />
              </div>
            </div>
          ) : (
            <p className="text-sm text-muted-foreground text-center py-2">No attendance recorded yet.</p>
          )}
        </CardContent>
      </Card>

      {/* Month filter */}
      {months.length > 1 && (
        <div className="flex flex-wrap gap-1.5">
          <button
            onClick={() => setFilterMonth("ALL")}
            className={`px-3 py-1 rounded-full text-xs font-medium border transition-colors ${
              filterMonth === "ALL" ? "bg-primary text-primary-foreground border-primary" : "border-border text-muted-foreground hover:bg-muted"
            }`}
          >
            All
          </button>
          {months.map((m) => {
            const [y, mo] = m.split("-")
            return (
              <button
                key={m}
                onClick={() => setFilterMonth(m)}
                className={`px-3 py-1 rounded-full text-xs font-medium border transition-colors ${
                  filterMonth === m ? "bg-primary text-primary-foreground border-primary" : "border-border text-muted-foreground hover:bg-muted"
                }`}
              >
                {MONTH_NAMES[parseInt(mo) - 1]} {y}
              </button>
            )
          })}
        </div>
      )}

      {/* Records */}
      <Card>
        <CardContent className="pt-4 pb-2">
          {recordsLoading ? (
            <div className="flex justify-center py-8"><Loader2 className="h-5 w-5 animate-spin text-muted-foreground" /></div>
          ) : visible.length === 0 ? (
            <p className="text-sm text-muted-foreground text-center py-8">No records for this period.</p>
          ) : (
            <div className="space-y-1">
              {visible.map((r) => {
                const meta = STATUS_META[r.status] ?? { label: r.status, cls: "text-muted-foreground bg-muted border-border" }
                const d = new Date(r.date)
                const dayName = d.toLocaleDateString("en-IN", { weekday: "short" })
                const dateStr = d.toLocaleDateString("en-IN", { day: "2-digit", month: "short" })
                return (
                  <div key={r.id} className="flex items-center justify-between py-1.5 border-b last:border-0 text-sm">
                    <div className="flex items-center gap-3">
                      <span className="font-medium w-20">{dateStr}</span>
                      <span className="text-muted-foreground w-8">{dayName}</span>
                    </div>
                    <span className={`text-xs font-medium px-2 py-0.5 rounded-full border ${meta.cls}`}>
                      {meta.label}
                    </span>
                  </div>
                )
              })}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

function Stat({ label, value, color }: { label: string; value: number; color: string }) {
  return (
    <div className="text-center">
      <p className={`text-xl font-semibold ${color}`}>{value}</p>
      <p className="text-xs text-muted-foreground">{label}</p>
    </div>
  )
}

// ── Marks tab ─────────────────────────────────────────────────────────────────
const GRADE_META: Record<string, string> = {
  "A+": "text-success bg-success/10",
  "A":  "text-success bg-success/10",
  "B":  "text-blue-700 bg-blue-50 dark:text-blue-400 dark:bg-blue-500/10",
  "C":  "text-warning bg-warning/10",
  "D":  "text-orange-700 bg-orange-50 dark:text-orange-400 dark:bg-orange-500/10",
  "F":  "text-destructive bg-destructive/10",
  "AB": "text-muted-foreground bg-muted",
}

function printResultCard(
  marks: ExamMark[], examId: string,
  exams: { id: string; name: string }[],
  studentName: string, schoolName: string
) {
  const examName = exams.find((e) => e.id === examId)?.name ?? ""
  const totalObtained = marks.filter((m) => !m.isAbsent && m.marksObtained != null)
    .reduce((s, m) => s + (m.marksObtained ?? 0), 0)
  const totalMax = marks.filter((m) => !m.isAbsent)
    .reduce((s, m) => s + m.maxMarks, 0)
  const pct = totalMax > 0 ? ((totalObtained / totalMax) * 100).toFixed(1) : null

  const win = window.open("", "_blank", "width=600,height=750")
  if (!win) return
  win.document.write(`
    <html><head><title>Result Card — ${studentName}</title>
    <style>
      * { font-family: -apple-system, Segoe UI, Roboto, sans-serif; box-sizing: border-box; }
      body { margin: 28px; color: #111; }
      .head { text-align: center; border-bottom: 2px solid #111; padding-bottom: 12px; margin-bottom: 16px; }
      .head h1 { margin: 0; font-size: 20px; }
      .head p  { margin: 3px 0 0; font-size: 12px; color: #555; }
      .info { display: flex; justify-content: space-between; font-size: 13px; margin-bottom: 16px; }
      table { width: 100%; border-collapse: collapse; font-size: 13px; }
      th { text-align: left; padding: 8px; background: #f5f5f5; border: 1px solid #ddd; }
      td { padding: 7px 8px; border: 1px solid #eee; }
      .grade { text-align: center; font-weight: 600; }
      .total { font-weight: 700; background: #f9f9f9; }
      .foot { margin-top: 28px; font-size: 11px; color: #aaa; text-align: center; }
    </style></head>
    <body>
      <div class="head">
        <h1>${schoolName}</h1>
        <p>Result Card — ${examName}</p>
      </div>
      <div class="info">
        <span><b>Student:</b> ${studentName}</span>
        <span><b>Date:</b> ${new Date().toLocaleDateString("en-IN")}</span>
      </div>
      <table>
        <thead><tr><th>Subject</th><th style="text-align:right">Marks</th><th style="text-align:right">Max</th><th style="text-align:right">%</th><th style="text-align:center">Grade</th></tr></thead>
        <tbody>
          ${marks.map((m) => {
            const pctVal = !m.isAbsent && m.marksObtained != null && m.maxMarks > 0
              ? ((m.marksObtained / m.maxMarks) * 100).toFixed(1)
              : "—"
            return `<tr>
              <td>${m.subject}</td>
              <td style="text-align:right">${m.isAbsent ? "AB" : (m.marksObtained ?? "—")}</td>
              <td style="text-align:right">${m.maxMarks}</td>
              <td style="text-align:right">${pctVal}</td>
              <td class="grade">${m.grade}</td>
            </tr>`
          }).join("")}
          ${pct ? `<tr class="total">
            <td><b>Total</b></td>
            <td style="text-align:right"><b>${totalObtained}</b></td>
            <td style="text-align:right"><b>${totalMax}</b></td>
            <td style="text-align:right"><b>${pct}%</b></td>
            <td></td>
          </tr>` : ""}
        </tbody>
      </table>
      <div class="foot">This is a computer-generated result card.</div>
      <script>window.onload = function(){ window.print(); }</script>
    </body></html>
  `)
  win.document.close()
}

function MarksTab({ studentId, studentName }: { studentId: string; studentName: string }) {
  const currentYear = useCurrentAcademicYear()
  const { user } = useAuth()
  const yearId = currentYear?.id ?? ""
  const [selectedExamId, setSelectedExamId] = useState<string>("")

  const { data: exams = [], isLoading: examsLoading } = useExams(yearId)
  const { data: marks = [], isLoading: marksLoading } = useStudentExamMarks(studentId, selectedExamId || null)
  const schoolName = user?.schoolName ?? "School"

  if (!yearId) {
    return (
      <Card>
        <CardContent className="py-10 text-center text-sm text-muted-foreground">
          Set a current academic year to view marks.
        </CardContent>
      </Card>
    )
  }

  const totalObtained = marks.filter((m) => !m.isAbsent && m.marksObtained != null)
    .reduce((s, m) => s + (m.marksObtained ?? 0), 0)
  const totalMax = marks.filter((m) => !m.isAbsent)
    .reduce((s, m) => s + m.maxMarks, 0)
  const overallPct = totalMax > 0 ? ((totalObtained / totalMax) * 100).toFixed(1) : null

  return (
    <div className="space-y-3">
      {/* Exam picker */}
      <div className="flex items-center gap-3">
        <Label className="shrink-0">Select Exam</Label>
        {examsLoading ? (
          <Loader2 className="h-4 w-4 animate-spin text-muted-foreground" />
        ) : (
          <select
            value={selectedExamId}
            onChange={(e) => setSelectedExamId(e.target.value)}
            className="flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
          >
            <option value="">Choose an exam…</option>
            {exams.map((ex) => (
              <option key={ex.id} value={ex.id}>{ex.name}</option>
            ))}
          </select>
        )}
      </div>

      {/* Marks table */}
      <Card>
        <CardContent className="pt-4 pb-2">
          {!selectedExamId ? (
            <p className="text-sm text-muted-foreground text-center py-10">
              Select an exam above to view marks.
            </p>
          ) : marksLoading ? (
            <div className="flex justify-center py-8"><Loader2 className="h-5 w-5 animate-spin text-muted-foreground" /></div>
          ) : marks.length === 0 ? (
            <p className="text-sm text-muted-foreground text-center py-8">
              No marks entered for this student in this exam yet.
            </p>
          ) : (
            <>
              <div className="space-y-1">
                <div className="grid grid-cols-[1fr_80px_80px_56px] gap-2 text-xs text-muted-foreground uppercase tracking-wide pb-1 border-b">
                  <span>Subject</span>
                  <span className="text-right">Marks</span>
                  <span className="text-right">Max</span>
                  <span className="text-center">Grade</span>
                </div>
                {marks.map((m: ExamMark) => (
                  <div key={m.id} className="grid grid-cols-[1fr_80px_80px_56px] gap-2 items-center py-1.5 border-b last:border-0 text-sm">
                    <div>
                      <span className="font-medium">{m.subject}</span>
                      {m.remarks && <p className="text-xs text-muted-foreground mt-0.5">{m.remarks}</p>}
                    </div>
                    <span className="text-right">
                      {m.isAbsent ? <span className="text-muted-foreground text-xs">Absent</span> : (m.marksObtained ?? "—")}
                    </span>
                    <span className="text-right text-muted-foreground">{m.maxMarks}</span>
                    <div className="flex justify-center">
                      <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${GRADE_META[m.grade] ?? "text-muted-foreground bg-muted"}`}>
                        {m.grade}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
              {overallPct && (
                <div className="mt-3 pt-2 border-t flex items-center justify-between text-sm font-medium">
                  <span>Total: {totalObtained} / {totalMax} ({overallPct}%)</span>
                  <Button size="sm" variant="outline" className="gap-1.5 h-7 text-xs"
                    onClick={() => printResultCard(marks, selectedExamId, exams, studentName, schoolName)}>
                    <FileText className="h-3 w-3" /> Print Result Card
                  </Button>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

// ── Helpers ───────────────────────────────────────────────────────────────────
function InfoRow({ label, value, span }: { label: string; value: string; span?: boolean }) {
  return (
    <div className={span ? "col-span-2" : ""}>
      <dt className="text-xs text-muted-foreground uppercase tracking-wide mb-0.5">{label}</dt>
      <dd className="text-sm font-medium text-foreground">{value}</dd>
    </div>
  )
}

function ProfileSkeleton() {
  return (
    <div className="p-6 space-y-4">
      <Skeleton className="h-8 w-32" />
      <div className="flex items-center gap-4 p-6 border rounded-xl">
        <Skeleton className="h-16 w-16 rounded-full" />
        <div className="space-y-2 flex-1">
          <Skeleton className="h-5 w-48" />
          <Skeleton className="h-4 w-64" />
        </div>
      </div>
      <Skeleton className="h-64 w-full rounded-xl" />
    </div>
  )
}

