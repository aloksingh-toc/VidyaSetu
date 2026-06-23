import api, { wrap } from "./api"

export interface AuthUser {
  userId:          string
  schoolId:        string
  name:            string
  role:            "OWNER" | "ADMIN" | "TEACHER"
  schoolName:      string
  institutionType: string
  planType:        string
  languagePreference: string
}

export interface LoginPayload {
  phone:    string
  password: string
}

export interface RegisterPayload {
  schoolName:      string
  institutionType: string
  city:            string
  state:           string
  ownerName:       string
  phone:           string
  email:           string
  password:        string
}

export const authService = {
  login:    (payload: LoginPayload)    => api.post<{ data: AuthUser }>("/v1/auth/login", payload).then(wrap),
  register: (payload: RegisterPayload) => api.post<{ data: AuthUser }>("/v1/auth/register", payload).then(wrap),
  me:       ()                         => api.get<{ data: AuthUser }>("/v1/auth/me").then(wrap),
  refresh:  ()                         => api.post("/v1/auth/refresh"),
  logout:   ()                         => api.post("/v1/auth/logout"),
}
