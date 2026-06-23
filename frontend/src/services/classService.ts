import api, { wrap } from "./api"

export interface SchoolClass {
  id:               string
  academicYearId:   string
  academicYearName: string
  name:             string
  section:          string | null
  classTeacherId:   string | null
  classTeacherName: string | null
  displayOrder:     number
  createdAt:        string
}

export interface ClassPayload {
  academicYearId: string
  name:           string
  section?:       string
  displayOrder?:  number
}

export const classService = {
  listByYear: (academicYearId: string) =>
    api.get<{ data: SchoolClass[] }>(`/v1/classes?academicYearId=${academicYearId}`).then(wrap),

  create: (payload: ClassPayload) =>
    api.post<{ data: SchoolClass }>("/v1/classes", payload).then(wrap),

  update: (id: string, payload: ClassPayload) =>
    api.put<{ data: SchoolClass }>(`/v1/classes/${id}`, payload).then(wrap),

  delete: (id: string) =>
    api.delete(`/v1/classes/${id}`),
}
