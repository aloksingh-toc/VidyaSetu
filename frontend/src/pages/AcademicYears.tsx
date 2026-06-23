import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { Plus, Pencil, Trash2, Loader2, Star } from "lucide-react"
import {
  useAcademicYears,
  useCreateAcademicYear,
  useUpdateAcademicYear,
  useDeleteAcademicYear,
} from "@/hooks/useAcademicYears"
import type { AcademicYear } from "@/services/academicYearService"
import { Button }     from "@/components/ui/button"
import { Input }      from "@/components/ui/input"
import { Label }      from "@/components/ui/label"
import { Badge }      from "@/components/ui/badge"
import { SkeletonTable } from "@/components/shared/SkeletonTable"
import { EmptyState }    from "@/components/shared/EmptyState"
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from "@/components/ui/dialog"
import { formatDate } from "@/lib/utils"

// ── Form schema ───────────────────────────────────────────────────────────────
const schema = z
  .object({
    name:        z.string().regex(/^\d{4}-\d{4}$/, "Format must be YYYY-YYYY, e.g. 2025-2026"),
    startDate:   z.string().min(1, "Start date required"),
    endDate:     z.string().min(1, "End date required"),
    makeCurrent: z.boolean().optional(),
  })
  .refine((d) => d.endDate > d.startDate, {
    message: "End date must be after start date",
    path: ["endDate"],
  })
type FormValues = z.infer<typeof schema>

// ── Main page ─────────────────────────────────────────────────────────────────
export default function AcademicYears() {
  const { data: years, isLoading, isError } = useAcademicYears()
  const [editing, setEditing] = useState<AcademicYear | null>(null)
  const [open, setOpen]       = useState(false)

  const openCreate = () => { setEditing(null); setOpen(true) }
  const openEdit   = (y: AcademicYear) => { setEditing(y); setOpen(true) }
  const close      = () => { setEditing(null); setOpen(false) }

  return (
    <div className="p-6 space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold">Academic Years</h1>
          <p className="text-sm text-muted-foreground mt-0.5">
            Manage the school's academic years
          </p>
        </div>
        <Button size="sm" onClick={openCreate}>
          <Plus className="h-4 w-4" /> Add Year
        </Button>
      </div>

      {/* Table */}
      <div className="rounded-lg border bg-card overflow-hidden">
        {isLoading ? (
          <SkeletonTable rows={4} cols={4} />
        ) : isError ? (
          <p className="text-center text-sm text-destructive py-10">
            Failed to load academic years.
          </p>
        ) : !years?.length ? (
          <EmptyState
            title="No academic years"
            description="Create your first academic year to get started."
            action={<Button size="sm" onClick={openCreate}><Plus className="h-4 w-4" />Add Year</Button>}
          />
        ) : (
          <table className="w-full text-sm">
            <thead className="border-b bg-muted/30">
              <tr>
                {["Name", "Start Date", "End Date", "Status", ""].map((h) => (
                  <th key={h} className="px-4 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wide">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-border">
              {years.map((y) => (
                <YearRow key={y.id} year={y} onEdit={openEdit} />
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Create / Edit dialog */}
      <Dialog open={open} onOpenChange={(v) => !v && close()}>
        <DialogContent>
          <YearForm editing={editing} onClose={close} />
        </DialogContent>
      </Dialog>
    </div>
  )
}

// ── Table row ─────────────────────────────────────────────────────────────────
function YearRow({
  year,
  onEdit,
}: {
  year: AcademicYear
  onEdit: (y: AcademicYear) => void
}) {
  const del = useDeleteAcademicYear()

  return (
    <tr className="hover:bg-muted/20 transition-colors">
      <td className="px-4 py-3 font-medium">
        {year.name}
        {year.isCurrent && <Star className="inline ml-1.5 h-3.5 w-3.5 text-warning fill-warning" />}
      </td>
      <td className="px-4 py-3 text-muted-foreground">{formatDate(year.startDate)}</td>
      <td className="px-4 py-3 text-muted-foreground">{formatDate(year.endDate)}</td>
      <td className="px-4 py-3">
        {year.isCurrent ? (
          <Badge variant="success">Current</Badge>
        ) : year.isArchived ? (
          <Badge variant="secondary">Archived</Badge>
        ) : (
          <Badge variant="outline">Inactive</Badge>
        )}
      </td>
      <td className="px-4 py-3">
        <div className="flex items-center justify-end gap-1">
          <Button variant="ghost" size="icon" className="h-7 w-7" onClick={() => onEdit(year)}>
            <Pencil className="h-3.5 w-3.5" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            className="h-7 w-7 text-muted-foreground hover:text-destructive"
            disabled={year.isCurrent || del.isPending}
            title={year.isCurrent ? "Cannot delete the current year" : "Delete year"}
            onClick={() => {
              if (confirm(`Delete academic year "${year.name}"? This cannot be undone.`)) {
                del.mutate(year.id)
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

// ── Create / Edit form ────────────────────────────────────────────────────────
function YearForm({
  editing,
  onClose,
}: {
  editing: AcademicYear | null
  onClose: () => void
}) {
  const create = useCreateAcademicYear()
  const update = useUpdateAcademicYear(editing?.id ?? "")
  const isPending = create.isPending || update.isPending

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: editing
      ? {
          name:        editing.name,
          startDate:   editing.startDate,
          endDate:     editing.endDate,
          makeCurrent: editing.isCurrent,
        }
      : { makeCurrent: false },
  })

  const onSubmit = async (values: FormValues) => {
    if (editing) {
      await update.mutateAsync(values)
    } else {
      await create.mutateAsync(values)
    }
    onClose()
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogHeader>
        <DialogTitle>{editing ? "Edit Academic Year" : "New Academic Year"}</DialogTitle>
      </DialogHeader>

      <div className="space-y-4">
        <div className="space-y-1.5">
          <Label>Name *</Label>
          <Input placeholder="2025-2026" {...register("name")} />
          {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div className="space-y-1.5">
            <Label>Start Date *</Label>
            <Input type="date" {...register("startDate")} />
            {errors.startDate && <p className="text-xs text-destructive">{errors.startDate.message}</p>}
          </div>
          <div className="space-y-1.5">
            <Label>End Date *</Label>
            <Input type="date" {...register("endDate")} />
            {errors.endDate && <p className="text-xs text-destructive">{errors.endDate.message}</p>}
          </div>
        </div>

        <div className="flex items-center gap-2">
          <input type="checkbox" id="makeCurrent" {...register("makeCurrent")} className="h-4 w-4" />
          <Label htmlFor="makeCurrent" className="font-normal cursor-pointer">
            Set as current year
          </Label>
        </div>
      </div>

      <DialogFooter>
        <Button type="button" variant="outline" onClick={onClose}>Cancel</Button>
        <Button type="submit" disabled={isPending}>
          {isPending && <Loader2 className="h-4 w-4 animate-spin" />}
          {editing ? "Save Changes" : "Create Year"}
        </Button>
      </DialogFooter>
    </form>
  )
}
