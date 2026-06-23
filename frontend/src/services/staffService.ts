import api, { wrap } from "./api";

export interface StaffMember {
  id: string;
  name: string;
  phone: string;
  email: string | null;
  role: string;
  isActive: boolean;
  lastLoginAt: string | null;
  createdAt: string;
}

export interface CreateStaffPayload {
  name: string;
  phone: string;
  email?: string;
  role: string;
  password?: string;
}

export const staffService = {
  list: () =>
    api.get<{ data: StaffMember[] }>("/v1/staff").then(wrap),

  create: (payload: CreateStaffPayload) =>
    api.post<{ data: StaffMember }>("/v1/staff", payload).then(wrap),

  update: (id: string, payload: CreateStaffPayload) =>
    api.put<{ data: StaffMember }>(`/v1/staff/${id}`, payload).then(wrap),

  deactivate: (id: string) =>
    api.patch(`/v1/staff/${id}/deactivate`),

  activate: (id: string) =>
    api.patch(`/v1/staff/${id}/activate`),
};
