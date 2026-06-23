import api, { wrap } from "./api"

export interface StudentSummary {
  id:              string
  classId:         string
  className:       string
  section:         string | null
  rollNumber:      string | null
  firstName:       string
  lastName:        string | null
  fullName:        string
  gender:          string | null
  admissionNumber: string | null
  isActive:        boolean
}

export interface StudentDetail extends StudentSummary {
  dateOfBirth:  string | null
  photoUrl:     string | null
  admissionDate: string
  bloodGroup:   string | null
  address:      string | null
  parents:      Parent[]
  createdAt:    string
  updatedAt:    string
}

export interface Parent {
  id:             string
  studentId:      string
  name:           string
  relation:       string
  phone:          string
  whatsappNumber: string
  isPrimary:      boolean
  whatsappOptOut: boolean
}

export interface PageResponse<T> {
  content:       T[]
  page:          number
  size:          number
  totalElements: number
  totalPages:    number
  first:         boolean
  last:          boolean
}

export interface StudentFilters {
  page?:       number
  size?:       number
  classId?:    string
  search?:     string
  activeOnly?: boolean
}

export interface CreateStudentPayload {
  classId:         string
  firstName:       string
  lastName?:       string
  rollNumber?:     string
  gender?:         string
  dateOfBirth?:    string
  admissionDate?:  string
  admissionNumber?: string
  bloodGroup?:     string
  address?:        string
  parents?: {
    name:           string
    relation:       string
    phone:          string
    whatsappNumber?: string
    isPrimary?:     boolean
  }[]
}

export interface AddParentPayload {
  name:            string
  relation:        string
  phone:           string
  whatsappNumber?: string
  isPrimary?:      boolean
}

export interface ImportRowError {
  row:     number
  message: string
}

export interface ImportResult {
  totalRows:    number
  successCount: number
  errorCount:   number
  errors:       ImportRowError[]
}

export const studentService = {
  list: (filters: StudentFilters = {}) => {
    const params = new URLSearchParams()
    if (filters.page   !== undefined) params.set("page",       String(filters.page))
    if (filters.size   !== undefined) params.set("size",       String(filters.size))
    if (filters.classId)              params.set("classId",    filters.classId)
    if (filters.search)               params.set("search",     filters.search)
    if (filters.activeOnly !== undefined)
      params.set("activeOnly", String(filters.activeOnly))
    return api.get<{ data: PageResponse<StudentSummary> }>(`/v1/students?${params}`).then(wrap)
  },

  getById: (id: string) =>
    api.get<{ data: StudentDetail }>(`/v1/students/${id}`).then(wrap),

  create: (payload: CreateStudentPayload) =>
    api.post<{ data: StudentDetail }>("/v1/students", payload).then(wrap),

  update: (id: string, payload: CreateStudentPayload) =>
    api.put<{ data: StudentDetail }>(`/v1/students/${id}`, payload).then(wrap),

  deactivate: (id: string) =>
    api.delete(`/v1/students/${id}`),

  reactivate: (id: string) =>
    api.patch<{ data: StudentDetail }>(`/v1/students/${id}/reactivate`).then(wrap),

  addParent: (studentId: string, payload: AddParentPayload) =>
    api.post<{ data: Parent }>(`/v1/students/${studentId}/parents`, payload).then(wrap),

  deleteParent: (studentId: string, parentId: string) =>
    api.delete(`/v1/students/${studentId}/parents/${parentId}`),

  exportCsv: async (filters: { classId?: string; activeOnly?: boolean } = {}) => {
    const params = new URLSearchParams()
    if (filters.classId)              params.set("classId", filters.classId)
    if (filters.activeOnly !== undefined)
      params.set("activeOnly", String(filters.activeOnly))
    const res = await api.get(`/v1/students/export?${params}`, { responseType: "blob" })
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement("a")
    link.href = url
    link.download = "students.csv"
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  },

  importCsv: (file: File) => {
    const formData = new FormData()
    formData.append("file", file)
    return api
      .post<{ data: ImportResult }>("/v1/students/import", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then(wrap)
  },
}
