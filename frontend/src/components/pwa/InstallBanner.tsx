import { useState } from 'react'
import { Download, X } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { usePWAInstall } from '@/hooks/usePWAInstall'

export function InstallBanner() {
  const { canInstall, install } = usePWAInstall()
  const [dismissed, setDismissed] = useState(false)

  if (!canInstall || dismissed) return null

  return (
    <div className="fixed bottom-0 left-0 right-0 z-50 bg-primary text-primary-foreground px-4 py-3 flex items-center justify-between gap-3 shadow-lg safe-area-pb">
      <div className="flex items-center gap-3 min-w-0">
        <Download className="h-5 w-5 shrink-0" />
        <div className="min-w-0">
          <p className="text-sm font-semibold leading-tight">Install VidyaSetu</p>
          <p className="text-xs opacity-75 leading-tight">Add to home screen — works offline too</p>
        </div>
      </div>
      <div className="flex items-center gap-2 shrink-0">
        <Button
          size="sm"
          variant="secondary"
          onClick={install}
          className="text-xs font-semibold"
        >
          Install
        </Button>
        <button
          onClick={() => setDismissed(true)}
          className="p-1 rounded opacity-70 hover:opacity-100 transition-opacity"
          aria-label="Dismiss"
        >
          <X className="h-4 w-4" />
        </button>
      </div>
    </div>
  )
}
