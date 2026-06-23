import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { Plus, Pencil, Trash2, Loader2 } from "lucide-react"
import {
  useAcademicYears,
  useCurrentAcademicYear,
} from "@/hooks/useAcademicYears"
import {
  useClasses,
  useCreateClass,
  useUpdateClass,
  useDeleteClass,
} from "@/hooks/useClasses"
import type { SchoolClass } from "@/services/classService"
import { Button }   from "@/components/ui/button"
import { Input }    from "@/components/ui/input"
import { Label }    from "@/components/ui/label"
import { SkeletonTable } from "@/components/shared/SkeletonTable"
import { EmptyState }    from "@/components/shared/EmptyState"
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from "@/components/ui/dialog"

// ── Form schema ───────────────────────────────────────────────────────────────
// Keep all fields as strings — convert displayOrder to number in onSubmit.
// This avoids z.coerce / z.preprocess TypeScript inference issues with zodResolver.
const schema = z.object({
  name:         z.string().min(1, "Class name required").max(100),
  section:      z.string().max(10).optional(),
  displayOrder: z.string().optional(),
})
type FormValues = z.infer<typeof schema>

// ── Main page ─────────────────────────────────────────────────────────────────
export default function Classes() {
  const { data: years }      = useAcademicYears()
  const currentYear          = useCurrentAcademicYear()
  const [yearId, setYearId]  = useState<string>("")

  // Resolve active year ID
  const activeYearId = yearId || currentYear?.id || ""

  const { data: classes, isLoading, isError } = useClasses(activeYearId)

  const [editing, setEditing] = useState<SchoolClass | null>(null)
  const [open, setOpen]       = useState(false)

  const openCreate = () => { setEditing(null); setOpen(true) }
  const openEdit   = (c: SchoolClass) => { setEditing(c); setOpen(true) }
  const close      = () => { setEditing(null); setOpen(false) }

  return (
    <div className="p-6 space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Classes</h1>
          <p className="text-sm text-muted-foreground mt-0.5">
            Manage classes for each academic year
          </p>
        </div>
        <Button size="sm" onClick={openCreate} disabled={!activeYearId}>
          <Plus className="h-4 w-4" /> Add Class
        </Button>
      </div>

      {/* Year picker */}
      <div className="flex items-center gap-3">
        <Label className="shrink-0 text-sm">Academic Year</Label>
        <select
          value={yearId || activeYearId}
          onChange={(e) => setYearId(e.target.value)}
          className="flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
        >
          {years?.map((y) => (
            <option key={y.id} value={y.id}>
              {y.name}{y.isCurrent ? " (Current)" : ""}
            </option>
          ))}
        </select>
      </div>

      {/* Table */}
      <div className="rounded-lg border bg-card overflow-hidden">
        {!activeYearId ? (
          <EmptyState title="No academic year selected" description="Create an academic year first." />
        ) : isLoading ? (
          <SkeletonTable rows={4} cols={4} />
        ) : isError ? (
          <p className="text-center text-sm text-destructive py-10">Failed to load classes.</p>
        ) : !classes?.length ? (
          <EmptyState
            title="No classes yet"
            description="Add a class to this academic year."
            action={
              <Button size="sm" onClick={openCreate}>
                <Plus className="h-4 w-4" /> Add Class
              </Button>
            }
          />
        ) : (
          <table className="w-full text-sm">
            <thead className="border-b bg-muted/30">
              <tr>
                {["#", "Class Name", "Section", "Teacher", ""].map((h) => (
                  <th key={h} className="px-4 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wide">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {classes.map((c) => (
                <ClassRow
                  key={c.id}
                  cls={c}
                  onEdit={openEdit}
                  yearId={activeYearId}
                />
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Dialog */}
      <Dialog open={open} onOpenChange={(v) => !v && close()}>
        <DialogContent>
          <ClassForm
            editing={editing}
            academicYearId={activeYearId}
            onClose={close}
          />
        </DialogContent>
      </Dialog>
    </div>
  )
}

// ── Table row ─────────────────────────────────────────────────────────────────
function ClassRow({
  cls,
  onEdit,
  yearId,
}: {
  cls:    SchoolClass
  onEdit: (c: SchoolClass) => void
  yearId: string
}) {
  const del = useDeleteClass()

  return (
    <tr className="hover:bg-muted/20 transition-colors">
      <td className="px-4 py-3 text-muted-foreground text-xs w-10">{cls.displayOrder}</td>
      <td className="px-4 py-3 font-medium">{cls.name}</td>
      <td className="px-4 py-3 text-muted-foreground">{cls.section ?? "—"}</td>
      <td className="px-4 py-3 text-muted-foreground">{cls.classTeacherName ?? "—"}</td>
      <td className="px-4 py-3">
        <div className="flex items-center justify-end gap-1">
          <Button variant="ghost" size="icon" className="h-7 w-7" onClick={() => onEdit(cls)}>
            <Pencil className="h-3.5 w-3.5" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            className="h-7 w-7 text-muted-foreground hover:text-destructive"
            disabled={del.isPending}
            onClick={() => {
              if (confirm(`Delete class "${cls.name}${cls.section ? " " + cls.section : ""}"?`)) {
                del.mutate({ id: cls.id, academicYearId: yearId })
              }
            }}
          >
            <Trash2 className="h-3.5 w-3.5" />
          </Button>
        </div>
      </td>
    </tr>
  )
}

// ── Form ──────────────────────────────────────────────────────────────────────
function ClassForm({
  editing,
  academicYearId,
  onClose,
}: {
  editing:        SchoolClass | null
  academicYearId: string
  onClose:        () => void
}) {
  const create = useCreateClass()
  const update = useUpdateClass(editing?.id ?? "")
  const isPending = create.isPending || update.isPending

  const { register, handleSubmit, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: editing
      ? { name: editing.name, section: editing.section ?? "", displayOrder: String(editing.displayOrder ?? 0) }
      : { displayOrder: "0" },
  })

  const onSubmit = async (values: FormValues) => {
    const payload = {
      academicYearId,
      name:         values.name,
      section:      values.section,
      displayOrder: values.displayOrder ? parseInt(values.displayOrder, 10) : undefined,
    }
    if (editing) {
      await update.mutateAsync(payload)
    } else {
      await create.mutateAsync(payload)
    }
    onClose()
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogHeader>
        <DialogTitle>{editing ? "Edit Class" : "New Class"}</DialogTitle>
      </DialogHeader>

      <div className="space-y-4">
        <div className="space-y-1.5">
          <Label>Class Name *</Label>
          <Input placeholder="Class 5, Nursery, Batch A…" {...register("name")} />
          {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div className="space-y-1.5">
            <Label>Section</Label>
            <Input placeholder="A, B…" {...register("section")} />
          </div>
          <div className="space-y-1.5">
            <Label>Display Order</Label>
            <Input type="number" min={0} {...register("displayOrder")} />
          </div>
        </div>
      </div>

      <DialogFooter>
        <Button type="button" variant="outline" onClick={onClose}>Cancel</Button>
        <Button type="submit" disabled={isPending}>
          {isPending && <Loader2 className="h-4 w-4 animate-spin" />}
          {editing ? "Save Changes" : "Create Class"}
        </Button>
      </DialogFooter>
    </form>
  )
}
