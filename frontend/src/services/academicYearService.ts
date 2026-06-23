import api, { wrap } from "./api"

export interface AcademicYear {
  id:         string
  name:       string
  startDate:  string
  endDate:    string
  isCurrent:  boolean
  isArchived: boolean
  createdAt:  string
}

export interface AcademicYearPayload {
  name:         string
  startDate:    string
  endDate:      string
  makeCurrent?: boolean
}

export const academicYearService = {
  list:   ()                                          =>
    api.get<{ data: AcademicYear[] }>("/v1/academic-years").then(wrap),

  getById: (id: string)                               =>
    api.get<{ data: AcademicYear }>(`/v1/academic-years/${id}`).then(wrap),

  create: (payload: AcademicYearPayload)              =>
    api.post<{ data: AcademicYear }>("/v1/academic-years", payload).then(wrap),

  update: (id: string, payload: AcademicYearPayload)  =>
    api.put<{ data: AcademicYear }>(`/v1/academic-years/${id}`, payload).then(wrap),

  delete: (id: string)                                =>
    api.delete(`/v1/academic-years/${id}`),
}
