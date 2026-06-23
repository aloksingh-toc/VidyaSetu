import { useState, useEffect } from "react"
import { useNavigate, useParams } from "react-router-dom"
import { useForm, useFieldArray } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { Loader2, Plus, Trash2, ArrowLeft } from "lucide-react"
import { useCreateStudent, useUpdateStudent, useStudent } from "@/hooks/useStudents"
import { useAcademicYears, useCurrentAcademicYear }       from "@/hooks/useAcademicYears"
import { useClasses }                                       from "@/hooks/useClasses"
import { Button } from "@/components/ui/button"
import { Input }  from "@/components/ui/input"
import { Label }  from "@/components/ui/label"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"

const parentSchema = z.object({
  name:      z.string().min(1, "Name required"),
  relation:  z.enum(["FATHER", "MOTHER", "GUARDIAN"]),
  phone:     z.string().regex(/^[6-9]\d{9}$/, "Valid 10-digit mobile number"),
  whatsappNumber: z.string().optional(),
  isPrimary: z.boolean().optional(),
})

const schema = z.object({
  classId:         z.string().uuid("Select a class"),
  firstName:       z.string().min(1, "First name required"),
  lastName:        z.string().optional(),
  rollNumber:      z.string().optional(),
  gender:          z.enum(["MALE", "FEMALE", "OTHER"]).optional(),
  dateOfBirth:     z.string().optional(),
  admissionNumber: z.string().optional(),
  bloodGroup:      z.string().optional(),
  address:         z.string().optional(),
  parents:         z.array(parentSchema).optional(),
})
type FormValues = z.infer<typeof schema>

export default function AddStudent() {
  const navigate = useNavigate()
  const { id }   = useParams<{ id?: string }>()   // present on edit
  const isEdit   = !!id

  const { data: existing, isLoading: loadingExisting } = useStudent(id ?? "")
  const create = useCreateStudent()
  const update = useUpdateStudent(id ?? "")

  // Class picker state
  const { data: years }   = useAcademicYears()
  const currentYear       = useCurrentAcademicYear()
  const [yearId, setYearId] = useState<string>("")
  const activeYearId        = yearId || currentYear?.id || ""
  const { data: classes, isLoading: loadingClasses } = useClasses(activeYearId)

  const [apiError, setApiError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    control,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<FormValues>({
    resolver:      zodResolver(schema),
    defaultValues: { parents: [] },
  })

  // Populate form when editing and data has loaded
  useEffect(() => {
    if (isEdit && existing) {
      reset({
        classId:         existing.classId,
        firstName:       existing.firstName,
        lastName:        existing.lastName ?? "",
        rollNumber:      existing.rollNumber ?? "",
        gender:          (existing.gender as "MALE" | "FEMALE" | "OTHER") ?? undefined,
        admissionNumber: existing.admissionNumber ?? "",
        bloodGroup:      existing.bloodGroup ?? "",
        address:         existing.address ?? "",
        parents: [],
      })
    }
  }, [existing?.id]) // eslint-disable-line react-hooks/exhaustive-deps

  const { fields, append, remove } = useFieldArray({ control, name: "parents" })

  const onSubmit = async (values: FormValues) => {
    setApiError(null)
    try {
      if (isEdit) {
        await update.mutateAsync(values as Parameters<typeof update.mutateAsync>[0])
        navigate(`/students/${id}`)
      } else {
        const student = await create.mutateAsync(values as Parameters<typeof create.mutateAsync>[0])
        navigate(`/students/${student.id}`)
      }
    } catch (err: unknown) {
      const message =
        (err as { response?: { data?: { error?: { message?: string } } } })
          ?.response?.data?.error?.message ?? "Failed to save student."
      setApiError(message)
    }
  }

  if (isEdit && loadingExisting) {
    return (
      <div className="p-6 space-y-4">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-64 w-full rounded-xl" />
      </div>
    )
  }

  return (
    <div className="p-6 space-y-4">
      {/* Header */}
      <div className="flex items-center gap-3">
        <Button
          variant="ghost"
          size="icon"
          onClick={() => navigate(isEdit ? `/students/${id}` : "/students")}
        >
          <ArrowLeft className="h-4 w-4" />
        </Button>
        <h1 className="text-xl font-semibold">
          {isEdit ? "Edit Student" : "Add New Student"}
        </h1>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        {apiError && (
          <div className="rounded-md bg-destructive/10 px-3 py-2 text-sm text-destructive">
            {apiError}
          </div>
        )}

        {/* Basic info */}
        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-base">Student Details</CardTitle>
          </CardHeader>
          <CardContent className="grid grid-cols-2 gap-4">

            {/* Academic year (only affects class picker, not stored on student) */}
            <div className="col-span-2 grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>Academic Year</Label>
                <select
                  value={yearId || activeYearId}
                  onChange={(e) => setYearId(e.target.value)}
                  className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                >
                  {years?.map((y) => (
                    <option key={y.id} value={y.id}>
                      {y.name}{y.isCurrent ? " (Current)" : ""}
                    </option>
                  ))}
                </select>
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="classId">Class *</Label>
                <select
                  id="classId"
                  {...register("classId")}
                  disabled={loadingClasses}
                  className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:opacity-50"
                >
                  <option value="">{loadingClasses ? "Loading…" : "Select class…"}</option>
                  {classes?.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name}{c.section ? ` – ${c.section}` : ""}
                    </option>
                  ))}
                </select>
                {errors.classId && (
                  <p className="text-xs text-destructive">{errors.classId.message}</p>
                )}
              </div>
            </div>

            {/* First name */}
            <div className="space-y-1.5">
              <Label htmlFor="firstName">First Name *</Label>
              <Input id="firstName" {...register("firstName")} />
              {errors.firstName && (
                <p className="text-xs text-destructive">{errors.firstName.message}</p>
              )}
            </div>

            {/* Last name */}
            <div className="space-y-1.5">
              <Label htmlFor="lastName">Last Name</Label>
              <Input id="lastName" {...register("lastName")} />
            </div>

            {/* Roll number */}
            <div className="space-y-1.5">
              <Label htmlFor="rollNumber">Roll Number</Label>
              <Input id="rollNumber" {...register("rollNumber")} />
            </div>

            {/* Gender */}
            <div className="space-y-1.5">
              <Label htmlFor="gender">Gender</Label>
              <select
                id="gender"
                {...register("gender")}
                className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
              >
                <option value="">Select…</option>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
            </div>

            {/* Date of birth */}
            <div className="space-y-1.5">
              <Label htmlFor="dateOfBirth">Date of Birth</Label>
              <Input id="dateOfBirth" type="date" {...register("dateOfBirth")} />
            </div>

            {/* Blood group */}
            <div className="space-y-1.5">
              <Label htmlFor="bloodGroup">Blood Group</Label>
              <Input id="bloodGroup" placeholder="A+, B–, O+…" {...register("bloodGroup")} />
            </div>

            {/* Admission number */}
            <div className="space-y-1.5">
              <Label htmlFor="admissionNumber">Admission Number</Label>
              <Input id="admissionNumber" {...register("admissionNumber")} />
            </div>

            {/* Address */}
            <div className="col-span-2 space-y-1.5">
              <Label htmlFor="address">Address</Label>
              <Input id="address" {...register("address")} />
            </div>
          </CardContent>
        </Card>

        {/* Parents (only shown on create) */}
        {!isEdit && (
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-3">
              <CardTitle className="text-base">Parents / Guardians</CardTitle>
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={() =>
                  append({
                    name: "",
                    relation: "FATHER",
                    phone: "",
                    isPrimary: fields.length === 0,
                  })
                }
              >
                <Plus className="h-3.5 w-3.5" /> Add Parent
              </Button>
            </CardHeader>
            <CardContent className="space-y-4">
              {fields.length === 0 && (
                <p className="text-sm text-muted-foreground text-center py-4">
                  No parents added yet. Click "Add Parent" above.
                </p>
              )}
              {fields.map((field, i) => (
                <div
                  key={field.id}
                  className="grid grid-cols-2 gap-3 p-3 rounded-lg border bg-muted/20 relative"
                >
                  <Button
                    type="button"
                    variant="ghost"
                    size="icon"
                    className="absolute top-2 right-2 h-6 w-6 text-muted-foreground hover:text-destructive"
                    onClick={() => remove(i)}
                  >
                    <Trash2 className="h-3 w-3" />
                  </Button>

                  <div className="space-y-1.5">
                    <Label>Name *</Label>
                    <Input {...register(`parents.${i}.name`)} />
                    {errors.parents?.[i]?.name && (
                      <p className="text-xs text-destructive">{errors.parents[i].name?.message}</p>
                    )}
                  </div>

                  <div className="space-y-1.5">
                    <Label>Relation *</Label>
                    <select
                      {...register(`parents.${i}.relation`)}
                      className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                    >
                      <option value="FATHER">Father</option>
                      <option value="MOTHER">Mother</option>
                      <option value="GUARDIAN">Guardian</option>
                    </select>
                  </div>

                  <div className="space-y-1.5">
                    <Label>Phone *</Label>
                    <Input
                      type="tel"
                      inputMode="numeric"
                      maxLength={10}
                      {...register(`parents.${i}.phone`)}
                    />
                    {errors.parents?.[i]?.phone && (
                      <p className="text-xs text-destructive">
                        {errors.parents[i].phone?.message}
                      </p>
                    )}
                  </div>

                  <div className="flex items-center gap-2 pt-5">
                    <input
                      type="checkbox"
                      id={`primary-${i}`}
                      {...register(`parents.${i}.isPrimary`)}
                      className="h-4 w-4"
                    />
                    <Label htmlFor={`primary-${i}`} className="font-normal cursor-pointer">
                      Primary contact
                    </Label>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>
        )}

        {/* Submit */}
        <div className="flex gap-3 justify-end">
          <Button
            type="button"
            variant="outline"
            onClick={() => navigate(isEdit ? `/students/${id}` : "/students")}
          >
            Cancel
          </Button>
          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting && <Loader2 className="h-4 w-4 animate-spin" />}
            {isSubmitting ? "Saving…" : isEdit ? "Save Changes" : "Add Student"}
          </Button>
        </div>
      </form>
    </div>
  )
}
