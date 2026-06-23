import api, { wrap } from "./api";

export interface Exam {
  id: string;
  academicYearId: string;
  academicYearName: string;
  name: string;
  examType: string;
  startDate: string | null;
  endDate: string | null;
  resultPublished: boolean;
  publishedAt: string | null;
  createdAt: string;
  subjects: ExamSubject[];
}

export interface ExamSubject {
  id: string;
  examId: string;
  classId: string;
  className: string;
  classSection: string | null;
  subject: string;
  maxMarks: number;
  passingMarks: number | null;
  examDate: string | null;
  createdAt: string;
}

export interface ExamMark {
  id: string;
  studentId: string;
  studentName: string;
  rollNumber: string | null;
  examSubjectId: string;
  subject: string;
  marksObtained: number | null;
  maxMarks: number;
  isAbsent: boolean;
  remarks: string | null;
  grade: string;
  enteredByName: string | null;
  updatedAt: string | null;
}

export interface CreateExamPayload {
  name: string;
  examType: string;
  academicYearId: string;
  startDate?: string;
  endDate?: string;
}

export interface CreateExamSubjectPayload {
  classId: string;
  subject: string;
  maxMarks: number;
  passingMarks?: number;
  examDate?: string;
}

export interface MarkEntry {
  studentId: string;
  marksObtained?: number;
  isAbsent?: boolean;
  remarks?: string;
}

export const examService = {
  list: (academicYearId: string) =>
    api.get<{ data: Exam[] }>("/v1/exams", { params: { academicYearId } }).then(wrap),

  get: (id: string) =>
    api.get<{ data: Exam }>(`/v1/exams/${id}`).then(wrap),

  create: (payload: CreateExamPayload) =>
    api.post<{ data: Exam }>("/v1/exams", payload).then(wrap),

  update: (id: string, payload: CreateExamPayload) =>
    api.put<{ data: Exam }>(`/v1/exams/${id}`, payload).then(wrap),

  publish: (id: string) =>
    api.patch(`/v1/exams/${id}/publish`),

  delete: (id: string) =>
    api.delete(`/v1/exams/${id}`),
};

export const examSubjectService = {
  addSubject: (examId: string, payload: CreateExamSubjectPayload) =>
    api.post<{ data: ExamSubject }>(`/v1/exams/${examId}/subjects`, payload).then(wrap),

  updateSubject: (subjectId: string, payload: CreateExamSubjectPayload) =>
    api.put<{ data: ExamSubject }>(`/v1/exam-subjects/${subjectId}`, payload).then(wrap),

  deleteSubject: (subjectId: string) =>
    api.delete(`/v1/exam-subjects/${subjectId}`),
};

export const examMarkService = {
  getBySubject: (examSubjectId: string) =>
    api.get<{ data: ExamMark[] }>(`/v1/exam-subjects/${examSubjectId}/marks`).then(wrap),

  bulkSave: (examSubjectId: string, entries: MarkEntry[]) =>
    api.post<{ data: ExamMark[] }>(`/v1/exam-subjects/${examSubjectId}/marks`, { entries }).then(wrap),

  getByStudent: (studentId: string, examId: string) =>
    api.get<{ data: ExamMark[] }>(`/v1/students/${studentId}/exam-marks`, { params: { examId } }).then(wrap),
};
