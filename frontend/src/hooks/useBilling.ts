import { useQuery } from "@tanstack/react-query"
import { billingService } from "@/services/billingService"

export function useSubscription() {
  return useQuery({
    queryKey: ["billing", "subscription"],
    queryFn:  billingService.getSubscription,
  })
}

export function usePlans() {
  return useQuery({
    queryKey: ["billing", "plans"],
    queryFn:  billingService.getPlans,
  })
}
