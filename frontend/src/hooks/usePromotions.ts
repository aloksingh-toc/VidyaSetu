import { useMutation } from "@tanstack/react-query"
import { promotionService, type PromotionEntry } from "@/services/promotionService"

export function usePromote() {
  return useMutation({
    mutationFn: (entries: PromotionEntry[]) => promotionService.promote(entries),
  })
}
