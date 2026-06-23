import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { AlertCircle, ExternalLink, Loader2, Printer, Users } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Label } from "@/components/ui/label"
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select"
import { useAcademicYears, useCurrentAcademicYear } from "@/hooks/useAcademicYears"
import { useFeeDefaulters } from "@/hooks/useReports"
import { useAuth } from "@/context/AuthContext"

export default function FeeDefaulters() {
  const navigate    = useNavigate()
  const { user }    = useAuth()
  const currentYear = useCurrentAcademicYear()
  const [selectedYearId, setSelectedYearId] = useState<string>("")
  const yearId = selectedYearId || currentYear?.id || ""

  const { data: years }            = useAcademicYears()
  const { data: defaulters = [], isLoading } = useFeeDefaulters(yearId || undefined)

  const handlePrint = () => {
    const yearName = years?.find((y) => y.id === yearId)?.name ?? ""
    const win = window.open("", "_blank", "width=820,height=600")
    if (!win) return
    win.document.write(`
      <html><head><title>Fee Defaulters — ${yearName}</title>
      <style>
        * { font-family: -apple-system, Segoe UI, Roboto, sans-serif; box-sizing: border-box; }
        body { margin: 24px; color: #111; }
        h1 { font-size: 18px; margin: 0 0 4px; }
        .sub { color: #666; font-size: 12px; margin: 0 0 16px; }
        table { width: 100%; border-collapse: collapse; font-size: 13px; }
        th { text-align: left; padding: 8px; background: #f5f5f5; border-bottom: 2px solid #ddd; }
        td { padding: 7px 8px; border-bottom: 1px solid #eee; }
        .foot { margin-top: 16px; font-size: 11px; color: #aaa; }
      </style></head>
      <body>
        <h1>${user?.schoolName ?? "School"} — Fee Defaulters</h1>
        <p class="sub">Academic Year: ${yearName} &nbsp;·&nbsp; Total: ${defaulters.length} students &nbsp;·&nbsp; Generated: ${new Date().toLocaleDateString("en-IN")}</p>
        <table>
          <thead><tr><th>#</th><th>Student Name</th><th>Class</th><th>Roll No.</th><th>Admission No.</th></tr></thead>
          <tbody>
            ${defaulters.map((d, i) => `
              <tr>
                <td>${i + 1}</td>
                <td>${d.fullName}</td>
                <td>${d.className ?? "—"}${d.classSection ? " " + d.classSection : ""}</td>
                <td>${d.rollNumber ?? "—"}</td>
                <td>${d.admissionNumber ?? "—"}</td>
              </tr>`).join("")}
          </tbody>
        </table>
        <div class="foot">This is a computer-generated report.</div>
        <script>window.onload = function(){ window.print(); }</script>
      </body></html>
    `)
    win.document.close()
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-foreground">Fee Defaulters</h1>
          <p className="text-sm text-muted-foreground mt-0.5">Students with no fee payments in the selected year</p>
        </div>
        {defaulters.length > 0 && (
          <Button variant="outline" onClick={handlePrint} className="gap-2">
            <Printer className="h-4 w-4" /> Print Report
          </Button>
        )}
      </div>

      {/* Year filter */}
      <div className="flex items-center gap-3">
        <Label className="shrink-0">Academic Year</Label>
        <Select value={yearId} onValueChange={setSelectedYearId}>
          <SelectTrigger className="w-52">
            <SelectValue placeholder="Select year" />
          </SelectTrigger>
          <SelectContent>
            {years?.map((y) => (
              <SelectItem key={y.id} value={y.id}>{y.name}</SelectItem>
            ))}
          </SelectContent>
        </Select>
        {!isLoading && yearId && (
          <Badge variant={defaulters.length > 0 ? "destructive" : "secondary"}>
            {defaulters.length} defaulter{defaulters.length !== 1 ? "s" : ""}
          </Badge>
        )}
      </div>

      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-base flex items-center gap-2">
            <AlertCircle className="h-4 w-4 text-destructive" />
            Students with Zero Payments
          </CardTitle>
        </CardHeader>
        <CardContent>
          {!yearId ? (
            <div className="text-center py-12 text-muted-foreground">
              <Users className="h-10 w-10 mx-auto mb-3 opacity-30" />
              <p className="text-sm">Select an academic year to view defaulters.</p>
            </div>
          ) : isLoading ? (
            <div className="flex justify-center py-12">
              <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
            </div>
          ) : defaulters.length === 0 ? (
            <div className="text-center py-12 text-muted-foreground">
              <p className="text-sm font-medium">No defaulters</p>
              <p className="text-xs mt-1">All active students have at least one payment recorded.</p>
            </div>
          ) : (
            <div>
              <div className="grid grid-cols-[2fr_1fr_80px_110px_40px] gap-2 text-xs text-muted-foreground uppercase tracking-wide pb-2 border-b">
                <span>Student</span><span>Class</span><span>Roll No.</span><span>Adm. No.</span><span />
              </div>
              {defaulters.map((d) => (
                <div
                  key={d.id}
                  className="grid grid-cols-[2fr_1fr_80px_110px_40px] gap-2 items-center py-2.5 border-b last:border-0 text-sm"
                >
                  <span className="font-medium truncate">{d.fullName}</span>
                  <span className="text-muted-foreground">
                    {d.className ?? "—"}{d.classSection ? ` ${d.classSection}` : ""}
                  </span>
                  <span className="text-muted-foreground">{d.rollNumber ?? "—"}</span>
                  <span className="text-muted-foreground">{d.admissionNumber ?? "—"}</span>
                  <Button
                    variant="ghost" size="icon" className="h-7 w-7"
                    title="View student profile"
                    onClick={() => navigate(`/students/${d.id}`)}
                  >
                    <ExternalLink className="h-3.5 w-3.5" />
                  </Button>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
