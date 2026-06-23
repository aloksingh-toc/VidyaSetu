import api, { wrap } from "./api"

export interface SchoolSettings {
  id: string
  name: string
  institutionType: string
  city: string | null
  state: string | null
  address: string | null
  phone: string | null
  email: string | null
  gstin: string | null
  feeDueDay: number
  lateFeeEnabled: boolean
  lateFeeAmount: number | null
  weeklyOffDays: string
  languagePreference: string
  planType: string
  logoUrl: string | null
}

export type SettingsPayload = Omit<SchoolSettings, "id" | "planType" | "logoUrl">

export const settingsService = {
  get: () =>
    api.get<{ data: SchoolSettings }>("/v1/settings").then(wrap),

  update: (data: SettingsPayload) =>
    api.put<{ data: SchoolSettings }>("/v1/settings", data).then(wrap),

  changePassword: (currentPassword: string, newPassword: string) =>
    api.patch("/v1/settings/password", { currentPassword, newPassword }),
}
