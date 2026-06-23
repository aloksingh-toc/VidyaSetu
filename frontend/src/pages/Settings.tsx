import { useEffect, useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { CheckCircle2, Loader2 } from "lucide-react"
import { useSettings, useUpdateSettings, useChangePassword } from "@/hooks/useSettings"
import { useSubscription, usePlans } from "@/hooks/useBilling"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select"

const STATES = [
  "Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat",
  "Haryana","Himachal Pradesh","Jharkhand","Karnataka","Kerala","Madhya Pradesh",
  "Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Odisha","Punjab","Rajasthan",
  "Sikkim","Tamil Nadu","Telangana","Tripura","Uttar Pradesh","Uttarakhand","West Bengal",
  "Delhi","Jammu & Kashmir","Ladakh",
]

const profileSchema = z.object({
  name:               z.string().min(2, "Name required"),
  institutionType:    z.enum(["SCHOOL", "COACHING", "COLLEGE"]),
  city:               z.string().optional(),
  state:              z.string().optional(),
  address:            z.string().optional(),
  phone:              z.string().optional(),
  email:              z.string().email("Invalid email").or(z.literal("")).optional(),
  gstin:              z.string().optional(),
  feeDueDay:          z.string().optional(),
  lateFeeEnabled:     z.boolean().optional(),
  lateFeeAmount:      z.string().optional(),
  weeklyOffDays:      z.string().optional(),
  languagePreference: z.string().optional(),
})

const passwordSchema = z.object({
  currentPassword: z.string().min(1, "Required"),
  newPassword:     z.string().min(8, "Min 8 characters"),
  confirmPassword: z.string().min(1, "Required"),
}).refine((d) => d.newPassword === d.confirmPassword, {
  message: "Passwords do not match",
  path: ["confirmPassword"],
})

type ProfileForm  = z.infer<typeof profileSchema>
type PasswordForm = z.infer<typeof passwordSchema>

export default function Settings() {
  const { data: settings, isLoading } = useSettings()
  const update         = useUpdateSettings()
  const changePassword = useChangePassword()
  const { data: subscription } = useSubscription()
  const { data: plans }        = usePlans()

  const [profileOk, setProfileOk] = useState(false)
  const [pwdOk,     setPwdOk]     = useState(false)
  const [pwdError,  setPwdError]  = useState("")

  const profileForm = useForm<ProfileForm>({ resolver: zodResolver(profileSchema) })
  const passwordForm = useForm<PasswordForm>({ resolver: zodResolver(passwordSchema) })

  useEffect(() => {
    if (!settings) return
    profileForm.reset({
      name:               settings.name,
      institutionType:    settings.institutionType as "SCHOOL" | "COACHING" | "COLLEGE",
      city:               settings.city    ?? "",
      state:              settings.state   ?? "",
      address:            settings.address ?? "",
      phone:              settings.phone   ?? "",
      email:              settings.email   ?? "",
      gstin:              settings.gstin   ?? "",
      feeDueDay:          String(settings.feeDueDay ?? 10),
      lateFeeEnabled:     settings.lateFeeEnabled,
      lateFeeAmount:      settings.lateFeeAmount != null ? String(settings.lateFeeAmount) : "",
      weeklyOffDays:      settings.weeklyOffDays,
      languagePreference: settings.languagePreference,
    })
  }, [settings]) // eslint-disable-line react-hooks/exhaustive-deps

  const onProfileSubmit = (data: ProfileForm) => {
    setProfileOk(false)
    update.mutate(
      {
        name:               data.name,
        institutionType:    data.institutionType,
        city:               data.city    || null,
        state:              data.state   || null,
        address:            data.address || null,
        phone:              data.phone   || null,
        email:              data.email   || null,
        gstin:              data.gstin   || null,
        feeDueDay:          data.feeDueDay ? parseInt(data.feeDueDay, 10) : 10,
        lateFeeEnabled:     data.lateFeeEnabled     ?? false,
        lateFeeAmount:      data.lateFeeAmount ? parseFloat(data.lateFeeAmount) : null,
        weeklyOffDays:      data.weeklyOffDays      ?? "SUNDAY",
        languagePreference: data.languagePreference ?? "hi",
      },
      { onSuccess: () => setProfileOk(true) }
    )
  }

  const onPasswordSubmit = (data: PasswordForm) => {
    setPwdOk(false)
    setPwdError("")
    changePassword.mutate(
      { currentPassword: data.currentPassword, newPassword: data.newPassword },
      {
        onSuccess: () => { setPwdOk(true); passwordForm.reset() },
        onError:   () => setPwdError("Current password is incorrect."),
      }
    )
  }

  if (isLoading) return (
    <div className="flex justify-center pt-24">
      <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
    </div>
  )

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-foreground">Settings</h1>
        <p className="text-sm text-muted-foreground mt-0.5">Manage school profile and account security</p>
      </div>

      {/* ── School Profile ─────────────────────────────────────────────── */}
      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-base">School Profile</CardTitle>
          <CardDescription>Update your institution's information</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={profileForm.handleSubmit(onProfileSubmit)} className="space-y-4">
            <div className="space-y-1.5">
              <Label>Institution Name *</Label>
              <Input {...profileForm.register("name")} />
              {profileForm.formState.errors.name && (
                <p className="text-xs text-destructive">{profileForm.formState.errors.name.message}</p>
              )}
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>Institution Type</Label>
                <Select
                  value={profileForm.watch("institutionType")}
                  onValueChange={(v) => profileForm.setValue("institutionType", v as "SCHOOL" | "COACHING" | "COLLEGE")}
                >
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="SCHOOL">School</SelectItem>
                    <SelectItem value="COACHING">Coaching Institute</SelectItem>
                    <SelectItem value="COLLEGE">College</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-1.5">
                <Label>Phone</Label>
                <Input type="tel" {...profileForm.register("phone")} />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>City</Label>
                <Input placeholder="e.g. Kanpur" {...profileForm.register("city")} />
              </div>
              <div className="space-y-1.5">
                <Label>State</Label>
                <Select
                  value={profileForm.watch("state") ?? ""}
                  onValueChange={(v) => profileForm.setValue("state", v)}
                >
                  <SelectTrigger><SelectValue placeholder="Select state" /></SelectTrigger>
                  <SelectContent>
                    {STATES.map((s) => <SelectItem key={s} value={s}>{s}</SelectItem>)}
                  </SelectContent>
                </Select>
              </div>
            </div>

            <div className="space-y-1.5">
              <Label>Address</Label>
              <Input placeholder="Full address" {...profileForm.register("address")} />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>Email</Label>
                <Input type="email" {...profileForm.register("email")} />
              </div>
              <div className="space-y-1.5">
                <Label>GSTIN</Label>
                <Input placeholder="22AAAAA0000A1Z5" {...profileForm.register("gstin")} />
              </div>
            </div>

            {/* Fee preferences */}
            <div className="border-t pt-4 space-y-3">
              <p className="text-xs font-medium text-muted-foreground uppercase tracking-wide">Fee Preferences</p>
              <div className="grid grid-cols-3 gap-3">
                <div className="space-y-1.5">
                  <Label>Fee Due Day</Label>
                  <Input type="number" min={1} max={31} {...profileForm.register("feeDueDay")} />
                </div>
                <div className="space-y-1.5">
                  <Label>Weekly Off</Label>
                  <select
                    {...profileForm.register("weeklyOffDays")}
                    className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                  >
                    <option value="SUNDAY">Sunday</option>
                    <option value="SATURDAY">Saturday</option>
                    <option value="SATURDAY,SUNDAY">Sat + Sun</option>
                  </select>
                </div>
                <div className="space-y-1.5">
                  <Label>Language</Label>
                  <select
                    {...profileForm.register("languagePreference")}
                    className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                  >
                    <option value="hi">Hindi</option>
                    <option value="en">English</option>
                  </select>
                </div>
              </div>
            </div>

            <div className="flex items-center justify-between pt-1">
              {profileOk
                ? <span className="flex items-center gap-1.5 text-sm text-success"><CheckCircle2 className="h-4 w-4" /> Saved</span>
                : <span />}
              <Button type="submit" disabled={update.isPending}>
                {update.isPending && <Loader2 className="h-4 w-4 animate-spin" />}
                Save Changes
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      {/* ── Billing ──────────────────────────────────────────────────────── */}
      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-base">Billing</CardTitle>
          <CardDescription>Manage your plan and subscription</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center justify-between rounded-md border border-border px-4 py-3">
            <div>
              <p className="text-sm font-medium text-foreground">
                Current Plan: {plans?.find((p) => p.current)?.displayName ?? subscription?.planType ?? "Free"}
              </p>
              <p className="text-xs text-muted-foreground mt-0.5">
                {subscription?.planExpiresAt
                  ? `Renews ${new Date(subscription.planExpiresAt).toLocaleDateString()}`
                  : "No expiry on the Free plan"}
              </p>
            </div>
            <Badge variant="secondary">Coming Soon</Badge>
          </div>

          {plans && plans.length > 0 && (
            <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
              {plans.map((plan) => (
                <div
                  key={plan.planType}
                  className={`rounded-md border px-3 py-3 ${plan.current ? "border-primary" : "border-border"}`}
                >
                  <p className="text-sm font-medium text-foreground">{plan.displayName}</p>
                  <p className="text-xs text-muted-foreground">
                    {plan.monthlyPrice > 0 ? `₹${plan.monthlyPrice}/mo` : "Free"}
                  </p>
                  <p className="text-xs text-muted-foreground">
                    {plan.maxStudents ? `Up to ${plan.maxStudents} students` : "Unlimited students"}
                  </p>
                </div>
              ))}
            </div>
          )}

          <Button type="button" variant="outline" disabled className="w-full sm:w-auto">
            Upgrade — Coming Soon
          </Button>
          <p className="text-xs text-muted-foreground">
            Online upgrades and renewals will open once our payment gateway integration goes live. Your current plan stays active until then.
          </p>
        </CardContent>
      </Card>

      {/* ── Change Password ─────────────────────────────────────────────── */}
      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-base">Change Password</CardTitle>
          <CardDescription>Update your login password</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={passwordForm.handleSubmit(onPasswordSubmit)} className="space-y-4">
            {pwdError && (
              <p className="text-sm text-destructive bg-destructive/10 rounded-md px-3 py-2">{pwdError}</p>
            )}
            <div className="space-y-1.5">
              <Label>Current Password</Label>
              <Input type="password" {...passwordForm.register("currentPassword")} />
              {passwordForm.formState.errors.currentPassword && (
                <p className="text-xs text-destructive">{passwordForm.formState.errors.currentPassword.message}</p>
              )}
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>New Password</Label>
                <Input type="password" placeholder="Min 8 characters" {...passwordForm.register("newPassword")} />
                {passwordForm.formState.errors.newPassword && (
                  <p className="text-xs text-destructive">{passwordForm.formState.errors.newPassword.message}</p>
                )}
              </div>
              <div className="space-y-1.5">
                <Label>Confirm Password</Label>
                <Input type="password" {...passwordForm.register("confirmPassword")} />
                {passwordForm.formState.errors.confirmPassword && (
                  <p className="text-xs text-destructive">{passwordForm.formState.errors.confirmPassword.message}</p>
                )}
              </div>
            </div>
            <div className="flex items-center justify-between pt-1">
              {pwdOk
                ? <span className="flex items-center gap-1.5 text-sm text-success"><CheckCircle2 className="h-4 w-4" /> Password changed</span>
                : <span />}
              <Button type="submit" disabled={changePassword.isPending}>
                {changePassword.isPending && <Loader2 className="h-4 w-4 animate-spin" />}
                Change Password
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
