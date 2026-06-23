import api, { wrap } from "./api"

export interface FeePayment {
  id:               string
  studentId:        string
  studentName:      string
  feeTypeId:        string
  feeTypeName:      string
  academicYearId:   string
  academicYearName: string
  amountPaid:       number
  amountDue:        number
  amountWaived:     number
  paymentMethod:    string   // CASH | UPI | CHEQUE | ONLINE
  paymentDate:      string
  forMonth:         string | null
  receiptNumber:    string
  receiptUrl:       string | null
  status:           string   // ACTIVE | VOIDED
  voidReason:       string | null
  voidedAt:         string | null
  collectedByName:  string | null
  notes:            string | null
  transactionRef:   string | null
  createdAt:        string
}

export interface CollectPaymentPayload {
  feeTypeId:      string
  academicYearId: string
  amountPaid:     number
  amountDue:      number
  amountWaived?:  number
  paymentMethod:  string
  paymentDate?:   string
  forMonth?:      string
  notes?:         string
  transactionRef?: string
}

export const feePaymentService = {
  list: (studentId: string, academicYearId: string) =>
    api.get<{ data: FeePayment[] }>(
      `/v1/students/${studentId}/payments?academicYearId=${academicYearId}`
    ).then(wrap),

  collect: (studentId: string, payload: CollectPaymentPayload) =>
    api.post<{ data: FeePayment }>(
      `/v1/students/${studentId}/payments`, payload
    ).then(wrap),

  void: (studentId: string, paymentId: string, reason: string) =>
    api.patch<{ data: FeePayment }>(
      `/v1/students/${studentId}/payments/${paymentId}/void`, { reason }
    ).then(wrap),
}
