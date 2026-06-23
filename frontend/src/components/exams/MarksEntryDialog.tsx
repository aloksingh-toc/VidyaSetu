import { useEffect, useMemo, useState } from "react"
import { Loader2, Save } from "lucide-react"
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { useStudents } from "@/hooks/useStudents"
import { useExamMarks, useBulkSaveMarks } from "@/hooks/useExams"
import type { ExamSubject, MarkEntry } from "@/services/examService"

interface Props {
  subject:  ExamSubject | null
  examName: string
  onClose:  () => void
}

interface RowState {
  marks:   string
  absent:  boolean
  remarks: string
}

export function MarksEntryDialog({ subject, examName, onClose }: Props) {
  const open = !!subject

  const { data: studentsPage, isLoading: loadingStudents } = useStudents({
    classId:    subject?.classId,
    activeOnly: true,
    size:       200,
  })
  const students = useMemo(() => studentsPage?.content ?? [], [studentsPage])

  const { data: existingMarks, isLoading: loadingMarks } = useExamMarks(subject?.id ?? "")
  const saveMarks = useBulkSaveMarks(subject?.id ?? "")

  const [rows, setRows] = useState<Record<string, RowState>>({})
  const [error, setError] = useState("")

  // Pre-fill rows from students + existing marks
  useEffect(() => {
    if (!open) return
    const byStudent: Record<string, RowState> = {}
    students.forEach((s) => {
      byStudent[s.id] = { marks: "", absent: false, remarks: "" }
    })
    existingMarks?.forEach((m) => {
      byStudent[m.studentId] = {
        marks:   m.marksObtained != null ? String(m.marksObtained) : "",
        absent:  m.isAbsent,
        remarks: m.remarks ?? "",
      }
    })
    setRows(byStudent)
  }, [open, students, existingMarks])

  if (!subject) return null

  const maxMarks = subject.maxMarks

  const setRow = (id: string, patch: Partial<RowState>) =>
    setRows((prev) => ({ ...prev, [id]: { ...prev[id], ...patch } }))

  const handleSave = async () => {
    setError("")
    // Validate marks within range
    for (const s of students) {
      const r = rows[s.id]
      if (!r || r.absent) continue
      if (r.marks === "") continue
      const val = parseFloat(r.marks)
      if (Number.isNaN(val) || val < 0 || val > maxMarks) {
        setError(`${s.fullName}: marks must be between 0 and ${maxMarks}`)
        return
      }
    }

    const entries: MarkEntry[] = students
      .map((s) => {
        const r = rows[s.id]
        if (!r) return null
        if (!r.absent && r.marks === "" && !r.remarks) return null // skip empty
        return {
          studentId:     s.id,
          isAbsent:      r.absent,
          marksObtained: r.absent || r.marks === "" ? undefined : parseFloat(r.marks),
          remarks:       r.remarks || undefined,
        } as MarkEntry
      })
      .filter((e): e is MarkEntry => e !== null)

    if (entries.length === 0) {
      setError("Enter marks for at least one student.")
      return
    }

    await saveMarks.mutateAsync(entries)
    onClose()
  }

  const isLoading = loadingStudents || loadingMarks

  return (
    <Dialog open={open} onOpenChange={(v) => !v && onClose()}>
      <DialogContent className="max-w-3xl">
        <DialogHeader>
          <DialogTitle>
            Enter Marks — {subject.subject}
            <span className="block text-sm font-normal text-muted-foreground mt-0.5">
              {examName} · {subject.className}
              {subject.classSection ? ` ${subject.classSection}` : ""} · Max marks: {maxMarks}
            </span>
          </DialogTitle>
        </DialogHeader>

        {error && (
          <p className="text-sm text-destructive bg-destructive/10 rounded-md px-3 py-2">{error}</p>
        )}

        <div className="max-h-[55vh] overflow-y-auto rounded-md border">
          {isLoading ? (
            <div className="flex items-center justify-center py-12 text-muted-foreground">
              <Loader2 className="h-5 w-5 animate-spin" />
            </div>
          ) : students.length === 0 ? (
            <p className="text-sm text-muted-foreground text-center py-12">
              No active students in this class.
            </p>
          ) : (
            <table className="w-full text-sm">
              <thead className="border-b bg-muted/30 sticky top-0">
                <tr>
                  <th className="px-3 py-2 text-left text-xs font-medium text-muted-foreground uppercase w-14">Roll</th>
                  <th className="px-3 py-2 text-left text-xs font-medium text-muted-foreground uppercase">Student</th>
                  <th className="px-3 py-2 text-left text-xs font-medium text-muted-foreground uppercase w-28">Marks</th>
                  <th className="px-3 py-2 text-center text-xs font-medium text-muted-foreground uppercase w-20">Absent</th>
                  <th className="px-3 py-2 text-left text-xs font-medium text-muted-foreground uppercase">Remarks</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {students.map((s) => {
                  const r = rows[s.id] ?? { marks: "", absent: false, remarks: "" }
                  return (
                    <tr key={s.id} className="hover:bg-muted/20">
                      <td className="px-3 py-2 text-muted-foreground text-xs">{s.rollNumber ?? "—"}</td>
                      <td className="px-3 py-2 font-medium">{s.fullName}</td>
                      <td className="px-3 py-2">
                        <Input
                          type="number"
                          min={0}
                          max={maxMarks}
                          step="0.01"
                          value={r.marks}
                          disabled={r.absent}
                          placeholder="—"
                          onChange={(e) => setRow(s.id, { marks: e.target.value })}
                          className="h-8"
                        />
                      </td>
                      <td className="px-3 py-2 text-center">
                        <input
                          type="checkbox"
                          checked={r.absent}
                          onChange={(e) => setRow(s.id, { absent: e.target.checked, marks: e.target.checked ? "" : r.marks })}
                          className="h-4 w-4 cursor-pointer"
                        />
                      </td>
                      <td className="px-3 py-2">
                        <Input
                          value={r.remarks}
                          placeholder="Optional"
                          onChange={(e) => setRow(s.id, { remarks: e.target.value })}
                          className="h-8"
                        />
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          )}
        </div>

        <DialogFooter>
          <Button type="button" variant="outline" onClick={onClose}>Cancel</Button>
          <Button onClick={handleSave} disabled={saveMarks.isPending || isLoading || students.length === 0}>
            {saveMarks.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <Save className="h-4 w-4" />}
            Save Marks
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
