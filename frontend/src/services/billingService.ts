import api, { wrap } from "@/services/api"

export interface Subscription {
  planType: string
  planExpiresAt: string | null
  status: string
  billingEnabled: boolean
}

export interface Plan {
  planType: string
  displayName: string
  monthlyPrice: number
  maxStudents: number | null
  current: boolean
}

export const billingService = {
  getSubscription: () =>
    api.get<{ data: Subscription }>("/v1/billing/subscription").then(wrap<Subscription>),

  getPlans: () =>
    api.get<{ data: Plan[] }>("/v1/billing/plans").then(wrap<Plan[]>),
}
