import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  examService,
  examSubjectService,
  examMarkService,
} from "@/services/examService"
import type {
  CreateExamPayload,
  CreateExamSubjectPayload,
  MarkEntry,
} from "@/services/examService";

const EXAM_KEYS = {
  list: (academicYearId: string) => ["exams", "list", academicYearId] as const,
  detail: (id: string) => ["exams", "detail", id] as const,
  marks: (subjectId: string) => ["examMarks", subjectId] as const,
  studentMarks: (studentId: string, examId: string) =>
    ["examMarks", "student", studentId, examId] as const,
};

export const useExams = (academicYearId: string) =>
  useQuery({
    queryKey: EXAM_KEYS.list(academicYearId),
    queryFn: () => examService.list(academicYearId),
    enabled: !!academicYearId,
  });

export const useExamDetail = (id: string) =>
  useQuery({
    queryKey: EXAM_KEYS.detail(id),
    queryFn: () => examService.get(id),
    enabled: !!id,
  });

export const useCreateExam = (academicYearId: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateExamPayload) => examService.create(payload),
    onSuccess: () => qc.invalidateQueries({ queryKey: EXAM_KEYS.list(academicYearId) }),
  });
};

export const useUpdateExam = (examId: string, academicYearId: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateExamPayload) => examService.update(examId, payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: EXAM_KEYS.list(academicYearId) });
      qc.invalidateQueries({ queryKey: EXAM_KEYS.detail(examId) });
    },
  });
};

export const useDeleteExam = (academicYearId: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => examService.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: EXAM_KEYS.list(academicYearId) }),
  });
};

export const usePublishExam = (academicYearId: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => examService.publish(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: EXAM_KEYS.list(academicYearId) }),
  });
};

export const useAddExamSubject = (examId: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (payload: CreateExamSubjectPayload) =>
      examSubjectService.addSubject(examId, payload),
    onSuccess: () => qc.invalidateQueries({ queryKey: EXAM_KEYS.detail(examId) }),
  });
};

export const useDeleteExamSubject = (examId: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (subjectId: string) => examSubjectService.deleteSubject(subjectId),
    onSuccess: () => qc.invalidateQueries({ queryKey: EXAM_KEYS.detail(examId) }),
  });
};

export const useExamMarks = (examSubjectId: string) =>
  useQuery({
    queryKey: EXAM_KEYS.marks(examSubjectId),
    queryFn: () => examMarkService.getBySubject(examSubjectId),
    enabled: !!examSubjectId,
  });

export const useBulkSaveMarks = (examSubjectId: string) => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (entries: MarkEntry[]) => examMarkService.bulkSave(examSubjectId, entries),
    onSuccess: () => qc.invalidateQueries({ queryKey: EXAM_KEYS.marks(examSubjectId) }),
  });
};

export const useStudentExamMarks = (studentId: string, examId: string | null) =>
  useQuery({
    queryKey: EXAM_KEYS.studentMarks(studentId, examId ?? ""),
    queryFn:  () => examMarkService.getByStudent(studentId, examId!),
    enabled:  !!studentId && !!examId,
  });
