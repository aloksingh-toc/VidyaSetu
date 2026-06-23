import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { staffService } from "@/services/staffService"
import type { CreateStaffPayload } from "@/services/staffService";

const STAFF_KEY = ["staff"] as const;

export const useStaff = () =>
  useQuery({
    queryKey: STAFF_KEY,
    queryFn: () => staffService.list(),
  });

export const useCreateStaff = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateStaffPayload) => staffService.create(payload),
    onSuccess: () => qc.invalidateQueries({ queryKey: STAFF_KEY }),
  });
};

export const useUpdateStaff = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }: { id: string; payload: CreateStaffPayload }) =>
      staffService.update(id, payload),
    onSuccess: () => qc.invalidateQueries({ queryKey: STAFF_KEY }),
  });
};

export const useDeactivateStaff = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => staffService.deactivate(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: STAFF_KEY }),
  });
};

export const useActivateStaff = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => staffService.activate(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: STAFF_KEY }),
  });
};
