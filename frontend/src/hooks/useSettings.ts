import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { settingsService, type SettingsPayload } from "@/services/settingsService"

const KEY = ["settings"] as const

export function useSettings() {
  return useQuery({
    queryKey: KEY,
    queryFn:  settingsService.get,
  })
}

export function useUpdateSettings() {
  const qc = useQueryClient()
  return useMutation({
    mutationFn: (data: SettingsPayload) => settingsService.update(data),
    onSuccess:  () => qc.invalidateQueries({ queryKey: KEY }),
  })
}

export function useChangePassword() {
  return useMutation({
    mutationFn: ({ currentPassword, newPassword }: { currentPassword: string; newPassword: string }) =>
      settingsService.changePassword(currentPassword, newPassword),
  })
}
