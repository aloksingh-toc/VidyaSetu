import { useState, useEffect, useRef } from "react"
import { useNavigate } from "react-router-dom"
import { Search, Plus, UserX, Users, RefreshCw, Download, Upload, Loader2 } from "lucide-react"
import {
  useReactTable,
  getCoreRowModel,
  flexRender,
  type ColumnDef,
} from "@tanstack/react-table"
import { useStudents, useDeactivateStudent, STUDENT_KEYS } from "@/hooks/useStudents"
import { studentService, type StudentSummary, type ImportResult } from "@/services/studentService"
import { useAcademicYears, useCurrentAcademicYear } from "@/hooks/useAcademicYears"
import { useClasses }                               from "@/hooks/useClasses"
import { useQueryClient } from "@tanstack/react-query"
import { Button }       from "@/components/ui/button"
import { Input }        from "@/components/ui/input"
import { Badge }        from "@/components/ui/badge"
import { Label }        from "@/components/ui/label"
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter,
} from "@/components/ui/dialog"
import { SkeletonTable } from "@/components/shared/SkeletonTable"
import { EmptyState }    from "@/components/shared/EmptyState"

export default function StudentList() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  // ── Search + filters ────────────────────────────────────────────────────────
  const [search, setSearch]   = useState("")
  const [page, setPage]       = useState(0)
  const [classId, setClassId] = useState<string>("")

  // ── CSV import/export ───────────────────────────────────────────────────────
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [isExporting, setIsExporting] = useState(false)
  const [isImporting, setIsImporting] = useState(false)
  const [importResult, setImportResult] = useState<ImportResult | null>(null)
  const [importErrorMsg, setImportErrorMsg] = useState<string | null>(null)

  const handleExport = async () => {
    setIsExporting(true)
    try {
      await studentService.exportCsv({ classId: classId || undefined, activeOnly: true })
    } finally {
      setIsExporting(false)
    }
  }

  const handleImportFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    e.target.value = ""
    if (!file) return
    setIsImporting(true)
    setImportErrorMsg(null)
    try {
      const result = await studentService.importCsv(file)
      setImportResult(result)
      queryClient.invalidateQueries({ queryKey: STUDENT_KEYS.all })
    } catch {
      setImportErrorMsg("Import failed. Please check the file and try again.")
    } finally {
      setIsImporting(false)
    }
  }

  const debouncedSearch = useDebounce(search, 300)

  // Class picker data
  const { data: years }   = useAcademicYears()
  const currentYear       = useCurrentAcademicYear()
  const [yearId, setYearId] = useState<string>("")
  const activeYearId        = yearId || currentYear?.id || ""
  const { data: classes }   = useClasses(activeYearId)

  // Reset classId when year changes
  useEffect(() => { setClassId(""); setPage(0) }, [yearId])

  // ── Data ────────────────────────────────────────────────────────────────────
  const { data, isLoading, isError, refetch } = useStudents({
    page,
    size:       20,
    search:     debouncedSearch || undefined,
    classId:    classId || undefined,
    activeOnly: true,
  })

  const deactivate = useDeactivateStudent()

  // ── Table columns ────────────────────────────────────────────────────────────
  const columns: ColumnDef<StudentSummary>[] = [
    {
      accessorKey: "rollNumber",
      header: "Roll",
      cell: ({ getValue }) => (
        <span className="text-muted-foreground text-xs">{(getValue() as string) || "—"}</span>
      ),
      size: 60,
    },
    {
      accessorKey: "fullName",
      header: "Name",
      cell: ({ row }) => (
        <button
          className="text-left font-medium text-foreground hover:text-primary hover:underline"
          onClick={() => navigate(`/students/${row.original.id}`)}
        >
          {row.original.fullName}
        </button>
      ),
    },
    {
      id: "class",
      header: "Class",
      cell: ({ row }) => (
        <span className="text-sm">
          {row.original.className}
          {row.original.section ? ` – ${row.original.section}` : ""}
        </span>
      ),
    },
    {
      accessorKey: "gender",
      header: "Gender",
      cell: ({ getValue }) => {
        const g = getValue() as string | null
        if (!g) return <span className="text-muted-foreground">—</span>
        return (
          <Badge variant={g === "MALE" ? "secondary" : "outline"}>
            {g === "MALE" ? "M" : g === "FEMALE" ? "F" : "O"}
          </Badge>
        )
      },
      size: 80,
    },
    {
      accessorKey: "admissionNumber",
      header: "Adm. No.",
      cell: ({ getValue }) => (
        <span className="text-xs text-muted-foreground">{(getValue() as string) || "—"}</span>
      ),
    },
    {
      id: "actions",
      header: "",
      cell: ({ row }) => (
        <Button
          variant="ghost"
          size="icon"
          className="h-7 w-7 text-muted-foreground hover:text-destructive"
          title="Deactivate student"
          onClick={() => {
            if (confirm(`Deactivate ${row.original.fullName}?`)) {
              deactivate.mutate(row.original.id)
            }
          }}
        >
          <UserX className="h-3.5 w-3.5" />
        </Button>
      ),
      size: 48,
    },
  ]

  const table = useReactTable({
    data:             data?.content ?? [],
    columns,
    getCoreRowModel:  getCoreRowModel(),
    manualPagination: true,
    pageCount:        data?.totalPages ?? -1,
  })

  return (
    <div className="p-6 space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-foreground">Students</h1>
          {data && (
            <p className="text-sm text-muted-foreground mt-0.5">
              {data.totalElements} student{data.totalElements !== 1 ? "s" : ""}
            </p>
          )}
        </div>
        <div className="flex items-center gap-2">
          <input
            ref={fileInputRef}
            type="file"
            accept=".csv"
            className="hidden"
            onChange={handleImportFileChange}
          />
          <Button
            variant="outline"
            size="sm"
            disabled={isImporting}
            onClick={() => fileInputRef.current?.click()}
          >
            {isImporting ? <Loader2 className="h-4 w-4 animate-spin" /> : <Upload className="h-4 w-4" />}
            Import
          </Button>
          <Button variant="outline" size="sm" disabled={isExporting} onClick={handleExport}>
            {isExporting ? <Loader2 className="h-4 w-4 animate-spin" /> : <Download className="h-4 w-4" />}
            Export
          </Button>
          <Button onClick={() => navigate("/students/add")} size="sm">
            <Plus className="h-4 w-4" />
            Add Student
          </Button>
        </div>
      </div>

      {/* Filters row */}
      <div className="flex flex-wrap items-center gap-3">
        {/* Search */}
        <div className="relative flex-1 min-w-[200px] max-w-sm">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Name, roll no., admission no."
            value={search}
            onChange={(e) => { setSearch(e.target.value); setPage(0) }}
            className="pl-9"
          />
        </div>

        {/* Year picker */}
        <div className="flex items-center gap-1.5">
          <Label className="text-sm shrink-0 text-muted-foreground">Year</Label>
          <select
            value={yearId || activeYearId}
            onChange={(e) => { setYearId(e.target.value); setPage(0) }}
            className="flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
          >
            {years?.map((y) => (
              <option key={y.id} value={y.id}>
                {y.name}{y.isCurrent ? " ✓" : ""}
              </option>
            ))}
          </select>
        </div>

        {/* Class picker */}
        <div className="flex items-center gap-1.5">
          <Label className="text-sm shrink-0 text-muted-foreground">Class</Label>
          <select
            value={classId}
            onChange={(e) => { setClassId(e.target.value); setPage(0) }}
            className="flex h-9 rounded-md border border-input bg-transparent px-3 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
          >
            <option value="">All classes</option>
            {classes?.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}{c.section ? ` – ${c.section}` : ""}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Table */}
      <div className="rounded-lg border bg-card overflow-hidden">
        {isLoading ? (
          <SkeletonTable rows={8} cols={6} />
        ) : isError ? (
          <div className="flex flex-col items-center gap-3 py-16">
            <p className="text-sm text-destructive">Failed to load students.</p>
            <Button variant="outline" size="sm" onClick={() => refetch()}>
              <RefreshCw className="h-4 w-4" />
              Retry
            </Button>
          </div>
        ) : data?.content.length === 0 ? (
          <EmptyState
            icon={<Users />}
            title="No students found"
            description={
              search || classId
                ? "No results for this filter. Try broadening your search."
                : "Add your first student to get started."
            }
            action={
              !search && !classId ? (
                <Button size="sm" onClick={() => navigate("/students/add")}>
                  <Plus className="h-4 w-4" />
                  Add Student
                </Button>
              ) : undefined
            }
          />
        ) : (
          <>
            {/* TanStack table */}
            <table className="w-full text-sm">
              <thead className="border-b bg-muted/30">
                {table.getHeaderGroups().map((hg) => (
                  <tr key={hg.id}>
                    {hg.headers.map((h) => (
                      <th
                        key={h.id}
                        className="px-4 py-3 text-left text-xs font-medium text-muted-foreground uppercase tracking-wide"
                        style={{ width: h.column.getSize() !== 150 ? h.column.getSize() : undefined }}
                      >
                        {h.isPlaceholder ? null : flexRender(h.column.columnDef.header, h.getContext())}
                      </th>
                    ))}
                  </tr>
                ))}
              </thead>
              <tbody className="divide-y divide-border">
                {table.getRowModel().rows.map((row) => (
                  <tr key={row.id} className="hover:bg-muted/20 transition-colors">
                    {row.getVisibleCells().map((cell) => (
                      <td key={cell.id} className="px-4 py-3">
                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>

            {/* Pagination */}
            {data && data.totalPages > 1 && (
              <div className="flex items-center justify-between px-4 py-3 border-t text-sm text-muted-foreground">
                <span>
                  Page {page + 1} of {data.totalPages} · {data.totalElements} total
                </span>
                <div className="flex gap-2">
                  <Button
                    variant="outline" size="sm"
                    disabled={data.first}
                    onClick={() => setPage((p) => p - 1)}
                  >
                    Previous
                  </Button>
                  <Button
                    variant="outline" size="sm"
                    disabled={data.last}
                    onClick={() => setPage((p) => p + 1)}
                  >
                    Next
                  </Button>
                </div>
              </div>
            )}
          </>
        )}
      </div>

      {importErrorMsg && (
        <div className="rounded-md border border-destructive/30 bg-destructive/10 px-4 py-2 text-sm text-destructive">
          {importErrorMsg}
        </div>
      )}

      <Dialog open={!!importResult} onOpenChange={(open) => !open && setImportResult(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Import results</DialogTitle>
            <DialogDescription>
              {importResult?.successCount} of {importResult?.totalRows} row(s) imported successfully.
            </DialogDescription>
          </DialogHeader>

          {importResult && importResult.errorCount > 0 && (
            <div className="max-h-64 overflow-y-auto rounded-md border divide-y">
              {importResult.errors.map((err, i) => (
                <div key={i} className="px-3 py-2 text-sm">
                  <span className="font-medium text-foreground">Row {err.row}:</span>{" "}
                  <span className="text-muted-foreground">{err.message}</span>
                </div>
              ))}
            </div>
          )}

          <DialogFooter>
            <Button onClick={() => setImportResult(null)}>Close</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  )
}

// ── Tiny debounce hook ────────────────────────────────────────────────────────
function useDebounce<T>(value: T, delay: number): T {
  const [debounced, setDebounced] = useState(value)
  useEffect(() => {
    const id = setTimeout(() => setDebounced(value), delay)
    return () => clearTimeout(id)
  }, [value, delay])
  return debounced
}
