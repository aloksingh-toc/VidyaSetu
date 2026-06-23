import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { Link, useNavigate } from "react-router-dom"
import { authService } from "@/services/authService"
import { useAuth } from "@/context/AuthContext"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select"
import { useState } from "react"

const schema = z.object({
  schoolName:      z.string().min(2, "School name required"),
  institutionType: z.enum(["SCHOOL", "COACHING", "COLLEGE"], { message: "Select type" }),
  city:            z.string().min(1, "City required"),
  state:           z.string().min(1, "State required"),
  ownerName:       z.string().min(2, "Your name required"),
  phone:           z.string().regex(/^[6-9][0-9]{9}$/, "Enter valid 10-digit mobile"),
  email:           z.string().email("Invalid email").or(z.literal("")).optional(),
  password:        z.string().min(8, "Min 8 characters"),
})

type Form = z.infer<typeof schema>

const STATES = [
  "Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat",
  "Haryana","Himachal Pradesh","Jharkhand","Karnataka","Kerala","Madhya Pradesh",
  "Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Punjab","Rajasthan",
  "Sikkim","Tamil Nadu","Telangana","Tripura","Uttar Pradesh","Uttarakhand","West Bengal",
  "Delhi","Jammu & Kashmir","Ladakh",
]

export default function Register() {
  const { login } = useAuth()
  const navigate   = useNavigate()
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  const form = useForm<Form>({ resolver: zodResolver(schema) })

  const onSubmit = async (data: Form) => {
    setError("")
    setLoading(true)
    try {
      await authService.register({
        schoolName:      data.schoolName,
        institutionType: data.institutionType,
        city:            data.city,
        state:           data.state,
        ownerName:       data.ownerName,
        phone:           data.phone,
        email:           data.email || "",
        password:        data.password,
      })
      await login(data.phone, data.password)
      navigate("/", { replace: true })
    } catch {
      setError("Registration failed. Phone may already be registered.")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4 py-8">
      <div className="w-full max-w-md space-y-6">
        {/* Brand */}
        <div className="text-center space-y-2">
          <div className="inline-flex h-12 w-12 items-center justify-center rounded-xl bg-primary mx-auto">
            <span className="text-lg font-bold text-primary-foreground">VS</span>
          </div>
          <h1 className="text-2xl font-bold text-foreground">Create your school</h1>
          <p className="text-sm text-muted-foreground">Set up VidyaSetu for your institution</p>
        </div>

        <div className="rounded-lg border bg-card p-6 shadow-sm space-y-4">
          {error && (
            <p className="text-sm text-destructive bg-destructive/10 rounded-md px-3 py-2">{error}</p>
          )}

          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            {/* Institution */}
            <div className="space-y-1.5">
              <Label>School / Institution Name</Label>
              <Input placeholder="e.g. Sunrise Public School" {...form.register("schoolName")} />
              {form.formState.errors.schoolName && (
                <p className="text-xs text-destructive">{form.formState.errors.schoolName.message}</p>
              )}
            </div>

            <div className="space-y-1.5">
              <Label>Institution Type</Label>
              <Select onValueChange={(v: string) => form.setValue("institutionType", v as "SCHOOL" | "COACHING" | "COLLEGE")}>
                <SelectTrigger><SelectValue placeholder="Select type" /></SelectTrigger>
                <SelectContent>
                  <SelectItem value="SCHOOL">School</SelectItem>
                  <SelectItem value="COACHING">Coaching Institute</SelectItem>
                  <SelectItem value="COLLEGE">College</SelectItem>
                </SelectContent>
              </Select>
              {form.formState.errors.institutionType && (
                <p className="text-xs text-destructive">{form.formState.errors.institutionType.message}</p>
              )}
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>City</Label>
                <Input placeholder="e.g. Kanpur" {...form.register("city")} />
                {form.formState.errors.city && (
                  <p className="text-xs text-destructive">{form.formState.errors.city.message}</p>
                )}
              </div>
              <div className="space-y-1.5">
                <Label>State</Label>
                <Select onValueChange={(v: string) => form.setValue("state", v)}>
                  <SelectTrigger><SelectValue placeholder="Select state" /></SelectTrigger>
                  <SelectContent>
                    {STATES.map(s => <SelectItem key={s} value={s}>{s}</SelectItem>)}
                  </SelectContent>
                </Select>
                {form.formState.errors.state && (
                  <p className="text-xs text-destructive">{form.formState.errors.state.message}</p>
                )}
              </div>
            </div>

            {/* Owner */}
            <div className="border-t pt-4 space-y-4">
              <p className="text-xs font-medium text-muted-foreground uppercase tracking-wide">Your Details</p>
              <div className="space-y-1.5">
                <Label>Your Name</Label>
                <Input placeholder="e.g. Alok Kumar" {...form.register("ownerName")} />
                {form.formState.errors.ownerName && (
                  <p className="text-xs text-destructive">{form.formState.errors.ownerName.message}</p>
                )}
              </div>
              <div className="space-y-1.5">
                <Label>Mobile Number</Label>
                <Input type="tel" placeholder="10-digit number" {...form.register("phone")} />
                {form.formState.errors.phone && (
                  <p className="text-xs text-destructive">{form.formState.errors.phone.message}</p>
                )}
              </div>
              <div className="space-y-1.5">
                <Label>Email (optional)</Label>
                <Input type="email" placeholder="you@school.com" {...form.register("email")} />
              </div>
              <div className="space-y-1.5">
                <Label>Password</Label>
                <Input type="password" placeholder="Min 8 characters" {...form.register("password")} />
                {form.formState.errors.password && (
                  <p className="text-xs text-destructive">{form.formState.errors.password.message}</p>
                )}
              </div>
            </div>

            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? "Creating account…" : "Create School Account"}
            </Button>
          </form>
        </div>

        <p className="text-center text-sm text-muted-foreground">
          Already have an account?{" "}
          <Link to="/login" className="text-primary hover:underline font-medium">Sign in</Link>
        </p>
      </div>
    </div>
  )
}
