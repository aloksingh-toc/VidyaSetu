import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { Loader2 } from "lucide-react"
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { useFeeTypes } from "@/hooks/useFees"
import { useCollectPayment } from "@/hooks/useFeePayments"
import type { FeePayment } from "@/services/feePaymentService"

const schema = z.object({
  feeTypeId:      z.string().min(1, "Fee type required"),
  amountDue:      z.string().min(1, "Amount due required"),
  amountPaid:     z.string().min(1, "Amount paid required"),
  amountWaived:   z.string().optional(),
  paymentMethod:  z.enum(["CASH", "UPI", "CHEQUE", "ONLINE"]),
  paymentDate:    z.string().optional(),
  forMonth:       z.string().optional(),
  transactionRef: z.string().optional(),
  notes:          z.string().optional(),
})
type Form = z.infer<typeof schema>

interface Props {
  studentId:      string
  academicYearId: string
  open:           boolean
  onClose:        () => void
  onCollected:    (payment: FeePayment) => void
}

const today = new Date().toISOString().slice(0, 10)

export function CollectPaymentDialog({ studentId, academicYearId, open, onClose, onCollected }: Props) {
  const { data: feeTypes } = useFeeTypes(true)
  const collect = useCollectPayment(studentId)

  const { register, handleSubmit, reset, formState: { errors } } = useForm<Form>({
    resolver: zodResolver(schema),
    defaultValues: { paymentMethod: "CASH", paymentDate: today },
  })

  const submit = async (v: Form) => {
    const payment = await collect.mutateAsync({
      feeTypeId:      v.feeTypeId,
      academicYearId,
      amountPaid:     parseFloat(v.amountPaid),
      amountDue:      parseFloat(v.amountDue),
      amountWaived:   v.amountWaived ? parseFloat(v.amountWaived) : undefined,
      paymentMethod:  v.paymentMethod,
      paymentDate:    v.paymentDate || undefined,
      forMonth:       v.forMonth || undefined,
      transactionRef: v.transactionRef || undefined,
      notes:          v.notes || undefined,
    })
    reset({ paymentMethod: "CASH", paymentDate: today })
    onCollected(payment)
  }

  return (
    <Dialog open={open} onOpenChange={(o) => !o && onClose()}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Collect Fee Payment</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(submit)} className="space-y-4">
          <div className="space-y-1.5">
            <Label>Fee Type *</Label>
            <select {...register("feeTypeId")}
              className="w-full flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
              <option value="">Select fee type</option>
              {feeTypes?.map((ft) => (
                <option key={ft.id} value={ft.id}>{ft.name}</option>
              ))}
            </select>
            {errors.feeTypeId && <p className="text-xs text-destructive">{errors.feeTypeId.message}</p>}
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label>Amount Due (₹) *</Label>
              <Input type="number" min="0" step="0.01" placeholder="5000" {...register("amountDue")} />
              {errors.amountDue && <p className="text-xs text-destructive">{errors.amountDue.message}</p>}
            </div>
            <div className="space-y-1.5">
              <Label>Amount Paid (₹) *</Label>
              <Input type="number" min="0.01" step="0.01" placeholder="5000" {...register("amountPaid")} />
              {errors.amountPaid && <p className="text-xs text-destructive">{errors.amountPaid.message}</p>}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label>Amount Waived (₹)</Label>
              <Input type="number" min="0" step="0.01" placeholder="0" {...register("amountWaived")} />
            </div>
            <div className="space-y-1.5">
              <Label>Method *</Label>
              <select {...register("paymentMethod")}
                className="w-full flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
                <option value="CASH">Cash</option>
                <option value="UPI">UPI</option>
                <option value="CHEQUE">Cheque</option>
                <option value="ONLINE">Online</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1.5">
              <Label>Payment Date</Label>
              <Input type="date" max={today} {...register("paymentDate")} />
            </div>
            <div className="space-y-1.5">
              <Label>For Month</Label>
              <Input type="month" {...register("forMonth")} />
            </div>
          </div>

          <div className="space-y-1.5">
            <Label>Transaction Ref (optional)</Label>
            <Input placeholder="UPI / cheque number" {...register("transactionRef")} />
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={onClose}>Cancel</Button>
            <Button type="submit" disabled={collect.isPending}>
              {collect.isPending && <Loader2 className="h-4 w-4 animate-spin" />}
              Collect Payment
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
