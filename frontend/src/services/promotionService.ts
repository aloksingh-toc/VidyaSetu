import api, { wrap } from "./api"

export interface PromotionEntry {
  fromClassId: string
  toClassId: string
}

export const promotionService = {
  promote: (entries: PromotionEntry[]) =>
    api.post<{ data: { promoted: number } }>("/v1/promotions", { entries }).then(wrap),
}
