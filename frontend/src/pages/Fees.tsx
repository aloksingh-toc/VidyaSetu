import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { Plus, Pencil, Trash2, Loader2, XCircle } from "lucide-react"
import {
  useFeeTypes, useCreateFeeType, useUpdateFeeType, useDeactivateFeeType,
  useFeeStructuresByYear, useCreateFeeStructure, useUpdateFeeStructure, useDeleteFeeStructure,
} from "@/hooks/useFees"
import { useAcademicYears, useCurrentAcademicYear } from "@/hooks/useAcademicYears"
import { useClasses } from "@/hooks/useClasses"
import type { FeeType, FeeStructure } from "@/services/feeService"
import { Button }        from "@/components/ui/button"
import { Input }         from "@/components/ui/input"
import { Label }         from "@/components/ui/label"
import { Badge }         from "@/components/ui/badge"
import { SkeletonTable } from "@/components/shared/SkeletonTable"
import { EmptyState }    from "@/components/shared/EmptyState"
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from "@/components/ui/dialog"
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs"

const FREQUENCIES: Record<string, string> = {
  MONTHLY: "Monthly", QUARTERLY: "Quarterly", ANNUAL: "Annual", ONE_TIME: "One Time",
}

// ── Fee Type schemas ──────────────────────────────────────────────────────────
const ftSchema = z.object({
  name:        z.string().min(1, "Name required").max(100),
  description: z.string().max(500).optional(),
})
type FTForm = z.infer<typeof ftSchema>

// ── Fee Structure schemas ─────────────────────────────────────────────────────
const fsSchema = z.object({
  classId:        z.string().min(1, "Class required"),
  feeTypeId:      z.string().min(1, "Fee type required"),
  academicYearId: z.string().min(1, "Academic year required"),
  amount:         z.string().min(1, "Amount required"),
  frequency:      z.string().min(1, "Frequency required"),
  dueDay:         z.string().optional(),
})
type FSForm = z.infer<typeof fsSchema>

// ── Main page ─────────────────────────────────────────────────────────────────
export default function Fees() {
  return (
    <div className="p-6 space-y-4">
      <div>
        <h1 className="text-xl font-semibold">Fees</h1>
        <p className="text-sm text-muted-foreground mt-0.5">
          Manage fee types and structures for your school
        </p>
      </div>
      <Tabs defaultValue="types">
        <TabsList>
          <TabsTrigger value="types">Fee Types</TabsTrigger>
          <TabsTrigger value="structures">Fee Structures</TabsTrigger>
        </TabsList>
        <TabsContent value="types" className="mt-4">
          <FeeTypesTab />
        </TabsContent>
        <TabsContent value="structures" className="mt-4">
          <FeeStructuresTab />
        </TabsContent>
      </Tabs>
    </div>
  )
}

// ── Fee Types tab ─────────────────────────────────────────────────────────────
function FeeTypesTab() {
  const { data: types, isLoading, isError } = useFeeTypes(false)
  const [editing, setEditing] = useState<FeeType | null>(null)
  const [open, setOpen]       = useState(false)

  const openCreate = () => { setEditing(null); setOpen(true) }
  const openEdit   = (t: FeeType) => { setEditing(t); setOpen(true) }
  const close      = () => { setEditing(null); setOpen(false) }

  return (
    <div className="space-y-3">
      <div className="flex justify-end">
        <Button size="sm" onClick={openCreate}>
          <Plus className="h-4 w-4" /> Add Fee Type
        </Button>
      </div>

      <div className="rounded-lg border bg-card overflow-hidden">
        {isLoading ? <SkeletonTable rows={3} cols={3} /> :
         isError   ? <p className="text-center text-sm text-destructive py-10">Failed to load fee types.</p> :
         !types?.length ? (
           <EmptyState
             title="No fee types"
             description="Add fee types like Tuition Fee, Transport Fee, Exam Fee."
             action={<Button size="sm" onClick={openCreate}><Plus className="h-4 w-4" />Add Fee Type</Button>}
           />
         ) : (
           <table className="w-full text-sm">
             <thead className="border-b bg-muted/30">
               <tr>
                 {["Name", "Description", "Status", ""].map(h => (
                   <th key={h} className="px-4 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wide">{h}</th>
                 ))}
               </tr>
             </thead>
             <tbody className="divide-y divide-border">
               {types.map(t => (
                 <FeeTypeRow key={t.id} type={t} onEdit={openEdit} />
               ))}
             </tbody>
           </table>
         )}
      </div>

      <Dialog open={open} onOpenChange={v => !v && close()}>
        <DialogContent>
          <FeeTypeForm editing={editing} onClose={close} />
        </DialogContent>
      </Dialog>
    </div>
  )
}

function FeeTypeRow({ type, onEdit }: { type: FeeType; onEdit: (t: FeeType) => void }) {
  const deactivate = useDeactivateFeeType()
  return (
    <tr className="hover:bg-muted/20 transition-colors">
      <td className="px-4 py-3 font-medium">{type.name}</td>
      <td className="px-4 py-3 text-muted-foreground">{type.description ?? "—"}</td>
      <td className="px-4 py-3">
        {type.isActive
          ? <Badge variant="success">Active</Badge>
          : <Badge variant="secondary">Inactive</Badge>}
      </td>
      <td className="px-4 py-3">
        <div className="flex items-center justify-end gap-1">
          <Button variant="ghost" size="icon" className="h-7 w-7" onClick={() => onEdit(type)}>
            <Pencil className="h-3.5 w-3.5" />
          </Button>
          {type.isActive && (
            <Button
              variant="ghost" size="icon"
              className="h-7 w-7 text-muted-foreground hover:text-destructive"
              disabled={deactivate.isPending}
              title="Deactivate fee type"
              onClick={() => {
                if (confirm(`Deactivate fee type "${type.name}"?`)) deactivate.mutate(type.id)
              }}
            >
              <XCircle className="h-3.5 w-3.5" />
            </Button>
          )}
        </div>
      </td>
    </tr>
  )
}

function FeeTypeForm({ editing, onClose }: { editing: FeeType | null; onClose: () => void }) {
  const create = useCreateFeeType()
  const update = useUpdateFeeType(editing?.id ?? "")
  const isPending = create.isPending || update.isPending

  const { register, handleSubmit, formState: { errors } } = useForm<FTForm>({
    resolver: zodResolver(ftSchema),
    defaultValues: editing ? { name: editing.name, description: editing.description ?? "" } : {},
  })

  const onSubmit = async (values: FTForm) => {
    if (editing) await update.mutateAsync(values)
    else await create.mutateAsync(values)
    onClose()
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogHeader><DialogTitle>{editing ? "Edit Fee Type" : "New Fee Type"}</DialogTitle></DialogHeader>
      <div className="space-y-4">
        <div className="space-y-1.5">
          <Label>Name *</Label>
          <Input placeholder="Tuition Fee, Transport Fee…" {...register("name")} />
          {errors.name && <p className="text-xs text-destructive">{errors.name.message}</p>}
        </div>
        <div className="space-y-1.5">
          <Label>Description</Label>
          <Input placeholder="Optional description" {...register("description")} />
        </div>
      </div>
      <DialogFooter>
        <Button type="button" variant="outline" onClick={onClose}>Cancel</Button>
        <Button type="submit" disabled={isPending}>
          {isPending && <Loader2 className="h-4 w-4 animate-spin" />}
          {editing ? "Save Changes" : "Create"}
        </Button>
      </DialogFooter>
    </form>
  )
}

// ── Fee Structures tab ────────────────────────────────────────────────────────
function FeeStructuresTab() {
  const { data: years }    = useAcademicYears()
  const currentYear        = useCurrentAcademicYear()
  const [yearId, setYearId] = useState<string>("")
  const activeYearId       = yearId || currentYear?.id || ""

  const { data: structures, isLoading, isError } = useFeeStructuresByYear(activeYearId)
  const { data: feeTypes }  = useFeeTypes()
  const { data: classes }   = useClasses(activeYearId)

  const [editing, setEditing] = useState<FeeStructure | null>(null)
  const [open, setOpen]       = useState(false)

  const openCreate = () => { setEditing(null); setOpen(true) }
  const openEdit   = (s: FeeStructure) => { setEditing(s); setOpen(true) }
  const close      = () => { setEditing(null); setOpen(false) }

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <Label className="shrink-0 text-sm">Academic Year</Label>
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
        <Button size="sm" onClick={openCreate} disabled={!activeYearId}>
          <Plus className="h-4 w-4" /> Add Structure
        </Button>
      </div>

      <div className="rounded-lg border bg-card overflow-hidden">
        {!activeYearId ? (
          <EmptyState title="Select academic year" description="Choose an academic year to view fee structures." />
        ) : isLoading ? <SkeletonTable rows={4} cols={5} /> :
          isError ? <p className="text-center text-sm text-destructive py-10">Failed to load fee structures.</p> :
          !structures?.length ? (
            <EmptyState
              title="No fee structures"
              description="Define the fee amounts for each class."
              action={<Button size="sm" onClick={openCreate}><Plus className="h-4 w-4" />Add Structure</Button>}
            />
          ) : (
            <table className="w-full text-sm">
              <thead className="border-b bg-muted/30">
                <tr>
                  {["Class", "Fee Type", "Amount", "Frequency", "Due Day", ""].map(h => (
                    <th key={h} className="px-4 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wide">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-border">
                {structures.map(s => (
                  <FeeStructureRow key={s.id} structure={s} yearId={activeYearId} onEdit={openEdit} />
                ))}
              </tbody>
            </table>
          )
        }
      </div>

      <Dialog open={open} onOpenChange={v => !v && close()}>
        <DialogContent>
          <FeeStructureForm
            editing={editing}
            academicYearId={activeYearId}
            feeTypes={feeTypes ?? []}
            classes={classes ?? []}
            onClose={close}
          />
        </DialogContent>
      </Dialog>
    </div>
  )
}

function FeeStructureRow({
  structure, yearId, onEdit,
}: { structure: FeeStructure; yearId: string; onEdit: (s: FeeStructure) => void }) {
  const del = useDeleteFeeStructure()
  return (
    <tr className="hover:bg-muted/20 transition-colors">
      <td className="px-4 py-3 font-medium">
        {structure.className}{structure.classSection ? ` (${structure.classSection})` : ""}
      </td>
      <td className="px-4 py-3 text-muted-foreground">{structure.feeTypeName}</td>
      <td className="px-4 py-3 font-medium">₹{structure.amount.toLocaleString("en-IN")}</td>
      <td className="px-4 py-3 text-muted-foreground">{FREQUENCIES[structure.frequency] ?? structure.frequency}</td>
      <td className="px-4 py-3 text-muted-foreground">{structure.dueDay ?? "—"}</td>
      <td className="px-4 py-3">
        <div className="flex items-center justify-end gap-1">
          <Button variant="ghost" size="icon" className="h-7 w-7" onClick={() => onEdit(structure)}>
            <Pencil className="h-3.5 w-3.5" />
          </Button>
          <Button
            variant="ghost" size="icon"
            className="h-7 w-7 text-muted-foreground hover:text-destructive"
            disabled={del.isPending}
            onClick={() => {
              if (confirm(`Delete fee structure for "${structure.className} — ${structure.feeTypeName}"?`))
                del.mutate({ id: structure.id, yearId })
            }}
          >
            <Trash2 className="h-3.5 w-3.5" />
          </Button>
        </div>
      </td>
    </tr>
  )
}

function FeeStructureForm({
  editing, academicYearId, feeTypes, classes, onClose,
}: {
  editing: FeeStructure | null
  academicYearId: string
  feeTypes: FeeType[]
  classes:  { id: string; name: string; section: string | null }[]
  onClose: () => void
}) {
  const create = useCreateFeeStructure()
  const update = useUpdateFeeStructure(editing?.id ?? "")
  const isPending = create.isPending || update.isPending

  const { register, handleSubmit, formState: { errors } } = useForm<FSForm>({
    resolver: zodResolver(fsSchema),
    defaultValues: editing ? {
      classId:        editing.classId,
      feeTypeId:      editing.feeTypeId,
      academicYearId: editing.academicYearId,
      amount:         String(editing.amount),
      frequency:      editing.frequency,
      dueDay:         editing.dueDay != null ? String(editing.dueDay) : "",
    } : { academicYearId, frequency: "MONTHLY" },
  })

  const onSubmit = async (values: FSForm) => {
    const payload = {
      classId:        values.classId,
      feeTypeId:      values.feeTypeId,
      academicYearId: values.academicYearId,
      amount:         parseFloat(values.amount),
      frequency:      values.frequency,
      dueDay:         values.dueDay ? parseInt(values.dueDay, 10) : undefined,
    }
    if (editing) await update.mutateAsync(payload)
    else await create.mutateAsync(payload)
    onClose()
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogHeader><DialogTitle>{editing ? "Edit Fee Structure" : "New Fee Structure"}</DialogTitle></DialogHeader>
      <div className="space-y-4">
        <div className="space-y-1.5">
          <Label>Class *</Label>
          <select {...register("classId")}
            className="w-full flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
            <option value="">Select class</option>
            {classes.map(c => (
              <option key={c.id} value={c.id}>{c.name}{c.section ? ` (${c.section})` : ""}</option>
            ))}
          </select>
          {errors.classId && <p className="text-xs text-destructive">{errors.classId.message}</p>}
        </div>

        <div className="space-y-1.5">
          <Label>Fee Type *</Label>
          <select {...register("feeTypeId")}
            className="w-full flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
            <option value="">Select fee type</option>
            {feeTypes.map(ft => (
              <option key={ft.id} value={ft.id}>{ft.name}</option>
            ))}
          </select>
          {errors.feeTypeId && <p className="text-xs text-destructive">{errors.feeTypeId.message}</p>}
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div className="space-y-1.5">
            <Label>Amount (₹) *</Label>
            <Input type="number" min="0" step="0.01" placeholder="500" {...register("amount")} />
            {errors.amount && <p className="text-xs text-destructive">{errors.amount.message}</p>}
          </div>
          <div className="space-y-1.5">
            <Label>Frequency *</Label>
            <select {...register("frequency")}
              className="w-full flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
              <option value="MONTHLY">Monthly</option>
              <option value="QUARTERLY">Quarterly</option>
              <option value="ANNUAL">Annual</option>
              <option value="ONE_TIME">One Time</option>
            </select>
          </div>
        </div>

        <div className="space-y-1.5">
          <Label>Due Day (1–28)</Label>
          <Input type="number" min="1" max="28" placeholder="10" {...register("dueDay")} />
          <p className="text-xs text-muted-foreground">Day of month fees are due</p>
        </div>
      </div>
      <DialogFooter>
        <Button type="button" variant="outline" onClick={onClose}>Cancel</Button>
        <Button type="submit" disabled={isPending}>
          {isPending && <Loader2 className="h-4 w-4 animate-spin" />}
          {editing ? "Save Changes" : "Create"}
        </Button>
      </DialogFooter>
    </form>
  )
}
