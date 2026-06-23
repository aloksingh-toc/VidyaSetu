import { useState } from "react"
import { BookOpen, Plus, Trash2, ChevronRight, CheckCircle2, ClipboardList } from "lucide-react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import {
  Card, CardContent, CardHeader, CardTitle,
} from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle,
} from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select"
import { useAcademicYears, useCurrentAcademicYear } from "@/hooks/useAcademicYears"
import {
  useExams, useCreateExam, useUpdateExam, useDeleteExam,
  usePublishExam, useAddExamSubject, useDeleteExamSubject, useExamDetail,
} from "@/hooks/useExams"
import { useClasses } from "@/hooks/useClasses"
import { MarksEntryDialog } from "@/components/exams/MarksEntryDialog"
import type { ExamSubject } from "@/services/examService"

/* ── Schemas ─────────────────────────────────────────────────────────── */

const examSchema = z.object({
  name:          z.string().min(1, "Required"),
  examType:      z.string().min(1, "Required"),
  academicYearId: z.string().uuid("Required"),
  startDate:     z.string().optional(),
  endDate:       z.string().optional(),
})

const subjectSchema = z.object({
  classId:      z.string().uuid("Required"),
  subject:      z.string().min(1, "Required"),
  maxMarks:     z.string().min(1, "Required"),
  passingMarks: z.string().optional(),
  examDate:     z.string().optional(),
})

type ExamForm    = z.infer<typeof examSchema>
type SubjectForm = z.infer<typeof subjectSchema>

const EXAM_TYPES = ["UNIT_TEST", "HALF_YEARLY", "ANNUAL", "PRACTICAL", "INTERNAL"]

/* ── Component ───────────────────────────────────────────────────────── */

export default function Exams() {
  const currentAY            = useCurrentAcademicYear()
  const [selectedAY, setSelectedAY] = useState<string>("")
  const ayId                 = selectedAY || currentAY?.id || ""

  const { data: years }      = useAcademicYears()
  const { data: exams = [], isLoading } = useExams(ayId)
  const { data: classes = [] } = useClasses(ayId)

  const createExam    = useCreateExam(ayId)
  const deleteExam    = useDeleteExam(ayId)
  const publishExam   = usePublishExam(ayId)

  const [examDialog, setExamDialog]       = useState(false)
  const [editExam, setEditExam]           = useState<{ id: string } | null>(null)
  const [subjectDialog, setSubjectDialog] = useState(false)
  const [activeExamId, setActiveExamId]   = useState<string | null>(null)
  const [marksSubject, setMarksSubject]   = useState<ExamSubject | null>(null)

  /* detail for subject panel */
  const { data: examDetail } = useExamDetail(activeExamId ?? "")
  const addSubject    = useAddExamSubject(activeExamId ?? "")
  const deleteSubject = useDeleteExamSubject(activeExamId ?? "")
  const updateExam    = useUpdateExam(editExam?.id ?? "", ayId)

  /* Exam form */
  const examForm = useForm<ExamForm>({
    resolver: zodResolver(examSchema),
    defaultValues: { academicYearId: ayId },
  })

  const openCreateExam = () => {
    examForm.reset({ academicYearId: ayId })
    setEditExam(null)
    setExamDialog(true)
  }

  const onExamSubmit = (data: ExamForm) => {
    const payload = {
      ...data,
      academicYearId: data.academicYearId || ayId,
    }
    if (editExam) {
      updateExam.mutate(payload, { onSuccess: () => setExamDialog(false) })
    } else {
      createExam.mutate(payload, { onSuccess: () => setExamDialog(false) })
    }
  }

  /* Subject form */
  const subjectForm = useForm<SubjectForm>({ resolver: zodResolver(subjectSchema) })

  const onSubjectSubmit = (data: SubjectForm) => {
    addSubject.mutate(
      {
        classId:      data.classId,
        subject:      data.subject,
        maxMarks:     parseFloat(data.maxMarks),
        passingMarks: data.passingMarks ? parseFloat(data.passingMarks) : undefined,
        examDate:     data.examDate || undefined,
      },
      { onSuccess: () => { setSubjectDialog(false); subjectForm.reset() } },
    )
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-foreground">Exams</h1>
          <p className="text-sm text-muted-foreground mt-0.5">Manage exams, subjects, and marks</p>
        </div>
        <Button onClick={openCreateExam} className="gap-2" disabled={!ayId}>
          <Plus className="h-4 w-4" /> New Exam
        </Button>
      </div>

      {/* Year filter */}
      <div className="flex items-center gap-3">
        <Label className="shrink-0">Academic Year</Label>
        <Select value={ayId} onValueChange={setSelectedAY}>
          <SelectTrigger className="w-52">
            <SelectValue placeholder="Select year" />
          </SelectTrigger>
          <SelectContent>
            {years?.map((y) => (
              <SelectItem key={y.id} value={y.id}>{y.name}</SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        {/* Exam list */}
        <div className="space-y-3">
          {isLoading && <p className="text-sm text-muted-foreground">Loading…</p>}
          {!isLoading && exams.length === 0 && (
            <Card>
              <CardContent className="py-12 text-center text-muted-foreground">
                <BookOpen className="h-10 w-10 mx-auto mb-3 opacity-30" />
                <p>No exams yet. Click "New Exam" to create one.</p>
              </CardContent>
            </Card>
          )}
          {exams.map((exam) => (
            <Card
              key={exam.id}
              className={`cursor-pointer transition-colors ${activeExamId === exam.id ? "ring-2 ring-primary" : ""}`}
              onClick={() => setActiveExamId(exam.id)}
            >
              <CardHeader className="pb-2">
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <CardTitle className="text-base">{exam.name}</CardTitle>
                    <p className="text-xs text-muted-foreground mt-0.5">
                      {exam.examType?.replace("_", " ")}
                      {exam.startDate ? ` · ${exam.startDate}` : ""}
                    </p>
                  </div>
                  <div className="flex items-center gap-1 shrink-0">
                    {exam.resultPublished && (
                      <Badge variant="secondary" className="text-success gap-1">
                        <CheckCircle2 className="h-3 w-3" /> Published
                      </Badge>
                    )}
                    <ChevronRight className="h-4 w-4 text-muted-foreground" />
                  </div>
                </div>
              </CardHeader>
              <CardContent className="pt-0 flex gap-2">
                {!exam.resultPublished && (
                  <Button
                    size="sm" variant="outline"
                    onClick={(e) => { e.stopPropagation(); publishExam.mutate(exam.id) }}
                  >
                    Publish Results
                  </Button>
                )}
                <Button
                  size="sm" variant="ghost"
                  className="text-destructive hover:text-destructive"
                  onClick={(e) => {
                    e.stopPropagation()
                    if (confirm(`Delete "${exam.name}"?`)) deleteExam.mutate(exam.id)
                  }}
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Subject panel */}
        {activeExamId && examDetail && (
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-3">
              <CardTitle className="text-base">{examDetail.name} — Subjects</CardTitle>
              <Button size="sm" onClick={() => setSubjectDialog(true)} className="gap-1">
                <Plus className="h-3 w-3" /> Add Subject
              </Button>
            </CardHeader>
            <CardContent className="space-y-2">
              {examDetail.subjects?.length === 0 && (
                <p className="text-sm text-muted-foreground">No subjects added yet.</p>
              )}
              {examDetail.subjects?.map((s) => (
                <div key={s.id} className="flex items-center justify-between py-1.5 border-b last:border-0">
                  <div>
                    <p className="text-sm font-medium">{s.subject}</p>
                    <p className="text-xs text-muted-foreground">
                      {s.className}{s.classSection ? ` ${s.classSection}` : ""} · Max: {s.maxMarks}
                      {s.passingMarks ? ` · Pass: ${s.passingMarks}` : ""}
                    </p>
                  </div>
                  <div className="flex items-center gap-1">
                    <Button
                      size="sm" variant="outline"
                      className="h-7 gap-1 text-xs"
                      onClick={() => setMarksSubject(s)}
                    >
                      <ClipboardList className="h-3 w-3" /> Marks
                    </Button>
                    <Button
                      size="sm" variant="ghost"
                      className="text-destructive hover:text-destructive h-7 w-7 p-0"
                      onClick={() => deleteSubject.mutate(s.id)}
                    >
                      <Trash2 className="h-3 w-3" />
                    </Button>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>
        )}
      </div>

      {/* Create/Edit Exam Dialog */}
      <Dialog open={examDialog} onOpenChange={setExamDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editExam ? "Edit Exam" : "New Exam"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={examForm.handleSubmit(onExamSubmit)} className="space-y-4">
            <div className="space-y-1.5">
              <Label>Exam Name</Label>
              <Input placeholder="e.g. Unit Test 1" {...examForm.register("name")} />
              {examForm.formState.errors.name && (
                <p className="text-xs text-destructive">{examForm.formState.errors.name.message}</p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label>Type</Label>
              <Select
                onValueChange={(v: string) => examForm.setValue("examType", v)}
                defaultValue={examForm.getValues("examType")}
              >
                <SelectTrigger><SelectValue placeholder="Select type" /></SelectTrigger>
                <SelectContent>
                  {EXAM_TYPES.map((t) => (
                    <SelectItem key={t} value={t}>{t.replace("_", " ")}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>Start Date</Label>
                <Input type="date" {...examForm.register("startDate")} />
              </div>
              <div className="space-y-1.5">
                <Label>End Date</Label>
                <Input type="date" {...examForm.register("endDate")} />
              </div>
            </div>
            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setExamDialog(false)}>Cancel</Button>
              <Button type="submit" disabled={createExam.isPending || updateExam.isPending}>
                {editExam ? "Save" : "Create"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Add Subject Dialog */}
      <Dialog open={subjectDialog} onOpenChange={setSubjectDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Add Subject</DialogTitle>
          </DialogHeader>
          <form onSubmit={subjectForm.handleSubmit(onSubjectSubmit)} className="space-y-4">
            <div className="space-y-1.5">
              <Label>Class</Label>
              <Select onValueChange={(v: string) => subjectForm.setValue("classId", v)}>
                <SelectTrigger><SelectValue placeholder="Select class" /></SelectTrigger>
                <SelectContent>
                  {classes.map((c) => (
                    <SelectItem key={c.id} value={c.id}>
                      {c.name}{c.section ? ` - ${c.section}` : ""}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {subjectForm.formState.errors.classId && (
                <p className="text-xs text-destructive">{subjectForm.formState.errors.classId.message}</p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label>Subject</Label>
              <Input placeholder="e.g. Mathematics" {...subjectForm.register("subject")} />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>Max Marks</Label>
                <Input type="number" placeholder="100" {...subjectForm.register("maxMarks")} />
              </div>
              <div className="space-y-1.5">
                <Label>Passing Marks</Label>
                <Input type="number" placeholder="33" {...subjectForm.register("passingMarks")} />
              </div>
            </div>
            <div className="space-y-1.5">
              <Label>Exam Date (optional)</Label>
              <Input type="date" {...subjectForm.register("examDate")} />
            </div>
            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setSubjectDialog(false)}>Cancel</Button>
              <Button type="submit" disabled={addSubject.isPending}>Add Subject</Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Marks Entry Dialog */}
      <MarksEntryDialog
        subject={marksSubject}
        examName={examDetail?.name ?? ""}
        onClose={() => setMarksSubject(null)}
      />
    </div>
  )
}
