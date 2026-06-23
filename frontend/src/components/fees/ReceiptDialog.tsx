import { Printer, CheckCircle2 } from "lucide-react"
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import type { FeePayment } from "@/services/feePaymentService"

interface Props {
  payment:    FeePayment | null
  schoolName: string
  onClose:    () => void
}

const formatINR = (n: number) =>
  "₹" + n.toLocaleString("en-IN", { minimumFractionDigits: 0, maximumFractionDigits: 2 })

const METHOD_LABEL: Record<string, string> = {
  CASH: "Cash", UPI: "UPI", CHEQUE: "Cheque", ONLINE: "Online",
}

export function ReceiptDialog({ payment, schoolName, onClose }: Props) {
  if (!payment) return null

  const handlePrint = () => {
    const win = window.open("", "_blank", "width=420,height=640")
    if (!win) return
    win.document.write(`
      <html>
        <head>
          <title>Receipt ${payment.receiptNumber}</title>
          <style>
            * { font-family: -apple-system, Segoe UI, Roboto, sans-serif; box-sizing: border-box; }
            body { margin: 0; padding: 24px; color: #111; }
            .head { text-align: center; border-bottom: 2px solid #111; padding-bottom: 12px; margin-bottom: 16px; }
            .head h1 { margin: 0; font-size: 20px; }
            .head p { margin: 4px 0 0; font-size: 12px; color: #555; }
            .rno { text-align: center; font-size: 13px; margin-bottom: 16px; font-weight: 600; }
            table { width: 100%; border-collapse: collapse; font-size: 13px; }
            td { padding: 6px 0; }
            td.label { color: #555; }
            td.val { text-align: right; font-weight: 500; }
            .total { border-top: 1px dashed #999; margin-top: 8px; padding-top: 10px; font-size: 16px; font-weight: 700; }
            .foot { margin-top: 28px; text-align: center; font-size: 11px; color: #888; }
          </style>
        </head>
        <body>
          <div class="head">
            <h1>${schoolName}</h1>
            <p>Fee Payment Receipt</p>
          </div>
          <div class="rno">Receipt No: ${payment.receiptNumber}</div>
          <table>
            <tr><td class="label">Student</td><td class="val">${payment.studentName}</td></tr>
            <tr><td class="label">Fee Type</td><td class="val">${payment.feeTypeName}</td></tr>
            <tr><td class="label">Academic Year</td><td class="val">${payment.academicYearName}</td></tr>
            ${payment.forMonth ? `<tr><td class="label">For Month</td><td class="val">${payment.forMonth}</td></tr>` : ""}
            <tr><td class="label">Payment Date</td><td class="val">${payment.paymentDate}</td></tr>
            <tr><td class="label">Method</td><td class="val">${METHOD_LABEL[payment.paymentMethod] ?? payment.paymentMethod}</td></tr>
            ${payment.transactionRef ? `<tr><td class="label">Txn Ref</td><td class="val">${payment.transactionRef}</td></tr>` : ""}
            <tr><td class="label">Amount Due</td><td class="val">${formatINR(payment.amountDue)}</td></tr>
            ${payment.amountWaived > 0 ? `<tr><td class="label">Waived</td><td class="val">${formatINR(payment.amountWaived)}</td></tr>` : ""}
            <tr class="total"><td>Amount Paid</td><td class="val">${formatINR(payment.amountPaid)}</td></tr>
          </table>
          <div class="foot">
            ${payment.collectedByName ? `Collected by ${payment.collectedByName}<br/>` : ""}
            This is a computer-generated receipt.
          </div>
          <script>window.onload = function(){ window.print(); }</script>
        </body>
      </html>
    `)
    win.document.close()
  }

  return (
    <Dialog open={!!payment} onOpenChange={(v) => !v && onClose()}>
      <DialogContent className="max-w-sm">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <CheckCircle2 className="h-5 w-5 text-success" /> Payment Recorded
          </DialogTitle>
        </DialogHeader>

        <div className="rounded-lg border bg-muted/20 p-4 space-y-3">
          <div className="text-center pb-3 border-b">
            <p className="font-semibold">{schoolName}</p>
            <p className="text-xs text-muted-foreground">Fee Payment Receipt</p>
            <p className="text-xs font-medium mt-1">#{payment.receiptNumber}</p>
          </div>
          <Row label="Student" value={payment.studentName} />
          <Row label="Fee Type" value={payment.feeTypeName} />
          {payment.forMonth && <Row label="For Month" value={payment.forMonth} />}
          <Row label="Date" value={payment.paymentDate} />
          <Row label="Method" value={METHOD_LABEL[payment.paymentMethod] ?? payment.paymentMethod} />
          {payment.amountWaived > 0 && <Row label="Waived" value={formatINR(payment.amountWaived)} />}
          <div className="flex justify-between pt-2 border-t text-base font-bold">
            <span>Amount Paid</span>
            <span>{formatINR(payment.amountPaid)}</span>
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Close</Button>
          <Button onClick={handlePrint} className="gap-1.5">
            <Printer className="h-4 w-4" /> Print Receipt
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex justify-between text-sm">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium">{value}</span>
    </div>
  )
}
