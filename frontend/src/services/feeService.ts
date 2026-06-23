import api, { wrap } from "./api"

export interface FeeType {
  id:          string
  name:        string
  description: string | null
  isActive:    boolean
  createdAt:   string
}

export interface FeeStructure {
  id:               string
  classId:          string
  className:        string
  classSection:     string | null
  feeTypeId:        string
  feeTypeName:      string
  academicYearId:   string
  academicYearName: string
  amount:           number
  frequency:        string   // MONTHLY | QUARTERLY | ANNUAL | ONE_TIME
  dueDay:           number | null
  createdAt:        string
}

export interface FeeConcession {
  id:               string
  studentId:        string
  studentName:      string
  feeTypeId:        string
  feeTypeName:      string
  academicYearId:   string
  academicYearName: string
  concessionType:   string   // PERCENTAGE | FIXED_AMOUNT | FULL_WAIVER
  concessionValue:  number | null
  reason:           string | null
  approvedById:     string | null
  approvedByName:   string | null
  isActive:         boolean
  createdAt:        string
}

export interface FeeTypePayload {
  name:        string
  description?: string
}

export interface FeeStructurePayload {
  classId:        string
  feeTypeId:      string
  academicYearId: string
  amount:         number
  frequency:      string
  dueDay?:        number
}

export interface FeeConcessionPayload {
  feeTypeId:        string
  academicYearId:   string
  concessionType:   string
  concessionValue?: number
  reason?:          string
}

export const feeTypeService = {
  list: (activeOnly = true) =>
    api.get<{ data: FeeType[] }>(`/v1/fee-types?activeOnly=${activeOnly}`).then(wrap),

  create: (payload: FeeTypePayload) =>
    api.post<{ data: FeeType }>("/v1/fee-types", payload).then(wrap),

  update: (id: string, payload: FeeTypePayload) =>
    api.put<{ data: FeeType }>(`/v1/fee-types/${id}`, payload).then(wrap),

  deactivate: (id: string) =>
    api.delete(`/v1/fee-types/${id}`),
}

export const feeStructureService = {
  listByYear: (academicYearId: string) =>
    api.get<{ data: FeeStructure[] }>(`/v1/fee-structures?academicYearId=${academicYearId}`).then(wrap),

  listByClass: (classId: string) =>
    api.get<{ data: FeeStructure[] }>(`/v1/fee-structures/by-class/${classId}`).then(wrap),

  create: (payload: FeeStructurePayload) =>
    api.post<{ data: FeeStructure }>("/v1/fee-structures", payload).then(wrap),

  update: (id: string, payload: FeeStructurePayload) =>
    api.put<{ data: FeeStructure }>(`/v1/fee-structures/${id}`, payload).then(wrap),

  delete: (id: string) =>
    api.delete(`/v1/fee-structures/${id}`),
}

export const feeConcessionService = {
  list: (studentId: string, academicYearId: string) =>
    api.get<{ data: FeeConcession[] }>(
      `/v1/students/${studentId}/concessions?academicYearId=${academicYearId}`
    ).then(wrap),

  create: (studentId: string, payload: FeeConcessionPayload) =>
    api.post<{ data: FeeConcession }>(
      `/v1/students/${studentId}/concessions`, payload
    ).then(wrap),

  deactivate: (studentId: string, concessionId: string) =>
    api.delete(`/v1/students/${studentId}/concessions/${concessionId}`),
}
