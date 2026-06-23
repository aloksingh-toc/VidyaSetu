import { useState } from "react"
import { UserPlus, Pencil, UserX, UserCheck } from "lucide-react"
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
import {
  useStaff, useCreateStaff, useUpdateStaff,
  useDeactivateStaff, useActivateStaff,
} from "@/hooks/useStaff"
import type { StaffMember } from "@/services/staffService"

const staffSchema = z.object({
  name:     z.string().min(1, "Required"),
  phone:    z.string().regex(/^[6-9][0-9]{9}$/, "Enter valid 10-digit mobile number"),
  email:    z.string().email("Invalid email").or(z.literal("")).optional(),
  role:     z.enum(["TEACHER", "ADMIN"]),
  password: z.string().optional(),
})

type StaffForm = z.infer<typeof staffSchema>

function roleBadge(role: string) {
  const colors: Record<string, string> = {
    OWNER:   "bg-purple-100 text-purple-700 dark:bg-purple-500/15 dark:text-purple-400",
    ADMIN:   "bg-blue-100 text-blue-700 dark:bg-blue-500/15 dark:text-blue-400",
    TEACHER: "bg-success/15 text-success",
  }
  return (
    <span className={`inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium ${colors[role] ?? ""}`}>
      {role}
    </span>
  )
}

export default function Staff() {
  const { data: staff = [], isLoading } = useStaff()
  const createStaff   = useCreateStaff()
  const updateStaff   = useUpdateStaff()
  const deactivate    = useDeactivateStaff()
  const activate      = useActivateStaff()

  const [dialog, setDialog]       = useState(false)
  const [editing, setEditing]     = useState<StaffMember | null>(null)

  const form = useForm<StaffForm>({ resolver: zodResolver(staffSchema) })

  const openCreate = () => {
    setEditing(null)
    form.reset({ name: "", phone: "", email: "", role: "TEACHER", password: "" })
    setDialog(true)
  }

  const openEdit = (s: StaffMember) => {
    setEditing(s)
    form.reset({ name: s.name, phone: s.phone, email: s.email ?? "", role: s.role as "TEACHER" | "ADMIN", password: "" })
    setDialog(true)
  }

  const onSubmit = (data: StaffForm) => {
    const payload = {
      name:     data.name,
      phone:    data.phone,
      email:    data.email || undefined,
      role:     data.role,
      password: data.password || undefined,
    }
    if (editing) {
      updateStaff.mutate(
        { id: editing.id, payload },
        { onSuccess: () => setDialog(false) },
      )
    } else {
      createStaff.mutate(payload, { onSuccess: () => setDialog(false) })
    }
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-foreground">Staff</h1>
          <p className="text-sm text-muted-foreground mt-0.5">Manage teachers and admins</p>
        </div>
        <Button onClick={openCreate} className="gap-2">
          <UserPlus className="h-4 w-4" /> Add Staff
        </Button>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="text-base">Staff Members</CardTitle>
        </CardHeader>
        <CardContent className="p-0">
          {isLoading && (
            <p className="text-sm text-muted-foreground p-6">Loading…</p>
          )}
          {!isLoading && staff.length === 0 && (
            <p className="text-sm text-muted-foreground p-6 text-center">
              No staff members yet. Add your first teacher or admin.
            </p>
          )}
          <div className="divide-y">
            {staff.map((s) => (
              <div key={s.id} className="flex items-center justify-between px-6 py-3">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-medium text-sm">{s.name}</span>
                    {roleBadge(s.role)}
                    {!s.isActive && (
                      <Badge variant="secondary" className="text-xs text-muted-foreground">Inactive</Badge>
                    )}
                  </div>
                  <p className="text-xs text-muted-foreground mt-0.5">
                    {s.phone}{s.email ? ` · ${s.email}` : ""}
                  </p>
                </div>
                <div className="flex items-center gap-1">
                  <Button
                    size="sm" variant="ghost"
                    onClick={() => openEdit(s)}
                  >
                    <Pencil className="h-3.5 w-3.5" />
                  </Button>
                  {s.isActive ? (
                    <Button
                      size="sm" variant="ghost"
                      className="text-destructive hover:text-destructive"
                      onClick={() => deactivate.mutate(s.id)}
                    >
                      <UserX className="h-3.5 w-3.5" />
                    </Button>
                  ) : (
                    <Button
                      size="sm" variant="ghost"
                      className="text-success hover:text-success"
                      onClick={() => activate.mutate(s.id)}
                    >
                      <UserCheck className="h-3.5 w-3.5" />
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      <Dialog open={dialog} onOpenChange={setDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editing ? "Edit Staff Member" : "Add Staff Member"}</DialogTitle>
          </DialogHeader>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-1.5">
              <Label>Full Name</Label>
              <Input placeholder="e.g. Ramesh Kumar" {...form.register("name")} />
              {form.formState.errors.name && (
                <p className="text-xs text-destructive">{form.formState.errors.name.message}</p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label>Mobile Number</Label>
              <Input placeholder="10-digit number" {...form.register("phone")} />
              {form.formState.errors.phone && (
                <p className="text-xs text-destructive">{form.formState.errors.phone.message}</p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label>Email (optional)</Label>
              <Input type="email" placeholder="ramesh@school.com" {...form.register("email")} />
            </div>
            <div className="space-y-1.5">
              <Label>Role</Label>
              <Select
                defaultValue={form.getValues("role")}
                onValueChange={(v: string) => form.setValue("role", v as "TEACHER" | "ADMIN")}
              >
                <SelectTrigger><SelectValue placeholder="Select role" /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="TEACHER">Teacher</SelectItem>
                  <SelectItem value="ADMIN">Admin</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-1.5">
              <Label>{editing ? "New Password (leave blank to keep)" : "Password"}</Label>
              <Input type="password" placeholder={editing ? "••••••••" : "Min 8 characters"} {...form.register("password")} />
              {form.formState.errors.password && (
                <p className="text-xs text-destructive">{form.formState.errors.password.message}</p>
              )}
            </div>
            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setDialog(false)}>Cancel</Button>
              <Button type="submit" disabled={createStaff.isPending || updateStaff.isPending}>
                {editing ? "Save Changes" : "Add Staff"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  )
}
