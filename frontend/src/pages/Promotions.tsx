import { useState } from "react"
import { ArrowRight, CheckCircle2, Loader2, Users } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select"
import { useAcademicYears } from "@/hooks/useAcademicYears"
import { useClasses } from "@/hooks/useClasses"
import { usePromote } from "@/hooks/usePromotions"
import type { SchoolClass } from "@/services/classService"

export default function Promotions() {
  const { data: years = [] } = useAcademicYears()

  const [fromYearId, setFromYearId] = useState<string>("")
  const [toYearId,   setToYearId]   = useState<string>("")

  const { data: fromClasses = [], isLoading: fromLoading } = useClasses(fromYearId)
  const { data: toClasses   = [], isLoading: toLoading   } = useClasses(toYearId)

  // mapping: fromClassId → toClassId
  const [mappings, setMappings] = useState<Record<string, string>>({})

  const promote = usePromote()
  const [result, setResult] = useState<number | null>(null)

  const setMapping = (fromClassId: string, toClassId: string) => {
    setMappings((prev) => ({ ...prev, [fromClassId]: toClassId }))
  }

  const readyEntries = fromClasses
    .filter((c) => mappings[c.id])
    .map((c) => ({ fromClassId: c.id, toClassId: mappings[c.id] }))

  const handlePromote = () => {
    if (readyEntries.length === 0) return
    if (!confirm(`Promote students from ${readyEntries.length} class(es)? This will move all active students to their selected target classes.`)) return
    setResult(null)
    promote.mutate(readyEntries, {
      onSuccess: (data) => {
        setResult(data.promoted)
        setMappings({})
      },
    })
  }

  const className = (c: SchoolClass) => `${c.name}${c.section ? " – " + c.section : ""}`

  return (
    <div className="p-6 space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-foreground">Student Promotions</h1>
        <p className="text-sm text-muted-foreground mt-0.5">
          Move students from one year's classes to the next year
        </p>
      </div>

      {/* ── Year selector ─────────────────────────────────── */}
      <Card>
        <CardHeader className="pb-3">
          <CardTitle className="text-base">Step 1 — Choose Academic Years</CardTitle>
          <CardDescription>Select the year students are currently in and the year they're moving to</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center gap-4">
            <div className="flex-1 space-y-1.5">
              <Label>From Year (current)</Label>
              <Select value={fromYearId} onValueChange={(v) => { setFromYearId(v); setMappings({}) }}>
                <SelectTrigger><SelectValue placeholder="Select year" /></SelectTrigger>
                <SelectContent>
                  {years.map((y) => <SelectItem key={y.id} value={y.id}>{y.name}</SelectItem>)}
                </SelectContent>
              </Select>
            </div>
            <ArrowRight className="h-5 w-5 text-muted-foreground mt-5 shrink-0" />
            <div className="flex-1 space-y-1.5">
              <Label>To Year (next)</Label>
              <Select value={toYearId} onValueChange={setToYearId}>
                <SelectTrigger><SelectValue placeholder="Select year" /></SelectTrigger>
                <SelectContent>
                  {years
                    .filter((y) => y.id !== fromYearId)
                    .map((y) => <SelectItem key={y.id} value={y.id}>{y.name}</SelectItem>)}
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* ── Class mapping ─────────────────────────────────── */}
      {fromYearId && toYearId && (
        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-base">Step 2 — Map Classes</CardTitle>
            <CardDescription>
              For each source class, choose where its students should go.
              Leave blank to skip that class.
            </CardDescription>
          </CardHeader>
          <CardContent>
            {fromLoading || toLoading ? (
              <div className="flex justify-center py-8">
                <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
              </div>
            ) : fromClasses.length === 0 ? (
              <p className="text-sm text-muted-foreground text-center py-6">
                No classes found for the selected source year.
              </p>
            ) : (
              <div className="space-y-2">
                <div className="grid grid-cols-[1fr_40px_1fr] gap-3 text-xs text-muted-foreground uppercase tracking-wide pb-1 border-b">
                  <span>From Class</span><span /><span>To Class</span>
                </div>
                {fromClasses.map((fc) => (
                  <div key={fc.id} className="grid grid-cols-[1fr_40px_1fr] gap-3 items-center py-1">
                    <div className="flex items-center gap-2">
                      <Users className="h-3.5 w-3.5 text-muted-foreground shrink-0" />
                      <span className="text-sm font-medium">{className(fc)}</span>
                    </div>
                    <ArrowRight className="h-4 w-4 text-muted-foreground justify-self-center" />
                    <Select
                      value={mappings[fc.id] ?? ""}
                      onValueChange={(v) => setMapping(fc.id, v)}
                    >
                      <SelectTrigger className="h-8 text-sm">
                        <SelectValue placeholder="Skip" />
                      </SelectTrigger>
                      <SelectContent>
                        {toClasses.map((tc) => (
                          <SelectItem key={tc.id} value={tc.id}>{className(tc)}</SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                ))}
              </div>
            )}
          </CardContent>
        </Card>
      )}

      {/* ── Execute ───────────────────────────────────────── */}
      {fromYearId && toYearId && fromClasses.length > 0 && (
        <div className="flex items-center justify-between">
          <div className="text-sm text-muted-foreground">
            {readyEntries.length > 0
              ? <span>{readyEntries.length} class{readyEntries.length !== 1 ? "es" : ""} mapped and ready</span>
              : <span>Map at least one class to continue</span>}
          </div>
          <div className="flex items-center gap-3">
            {result !== null && (
              <span className="flex items-center gap-1.5 text-sm text-success">
                <CheckCircle2 className="h-4 w-4" /> {result} student{result !== 1 ? "s" : ""} promoted
              </span>
            )}
            <Button
              onClick={handlePromote}
              disabled={readyEntries.length === 0 || promote.isPending}
              className="gap-2"
            >
              {promote.isPending
                ? <Loader2 className="h-4 w-4 animate-spin" />
                : <ArrowRight className="h-4 w-4" />}
              Promote Students
            </Button>
          </div>
        </div>
      )}

      {/* Info box */}
      <div className="rounded-lg border border-accent bg-accent/60 p-4 text-sm text-accent-foreground space-y-1">
        <p className="font-medium">How promotions work</p>
        <ul className="list-disc list-inside space-y-0.5 text-xs opacity-80">
          <li>All active students in each mapped class are moved to the target class.</li>
          <li>Classes without a mapping are left unchanged.</li>
          <li>This does not deactivate any students.</li>
          <li>Make sure the next year's classes are created before promoting.</li>
        </ul>
      </div>
    </div>
  )
}
