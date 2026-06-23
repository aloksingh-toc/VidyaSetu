import { useState, useEffect } from 'react'
import { WifiOff, Wifi } from 'lucide-react'

export function OfflineBanner() {
  const [isOffline, setIsOffline]     = useState(!navigator.onLine)
  const [justReconnected, setJustReconnected] = useState(false)

  useEffect(() => {
    const onOnline = () => {
      setIsOffline(false)
      setJustReconnected(true)
      setTimeout(() => setJustReconnected(false), 3000)
    }
    const onOffline = () => {
      setIsOffline(true)
      setJustReconnected(false)
    }

    window.addEventListener('online', onOnline)
    window.addEventListener('offline', onOffline)
    return () => {
      window.removeEventListener('online', onOnline)
      window.removeEventListener('offline', onOffline)
    }
  }, [])

  if (!isOffline && !justReconnected) return null

  return (
    <div
      className={`fixed top-0 left-0 right-0 z-50 px-4 py-2 flex items-center gap-2 justify-center text-sm shadow transition-colors ${
        isOffline
          ? 'bg-destructive text-destructive-foreground'
          : 'bg-success text-success-foreground'
      }`}
    >
      {isOffline ? (
        <>
          <WifiOff className="h-4 w-4 shrink-0" />
          <span>You're offline — viewing cached data</span>
        </>
      ) : (
        <>
          <Wifi className="h-4 w-4 shrink-0" />
          <span>Back online</span>
        </>
      )}
    </div>
  )
}
