import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react"
import { authService, type AuthUser } from "@/services/authService"

interface AuthContextValue {
  user:            AuthUser | null
  isLoading:       boolean
  isAuthenticated: boolean
  isOwner:         boolean
  isAdmin:         boolean
  login:   (phone: string, password: string) => Promise<void>
  logout:  () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser]         = useState<AuthUser | null>(null)
  const [isLoading, setLoading] = useState(true)

  // On mount — try to load the current session from the server.
  // /v1/auth/me is excluded from the api.ts refresh-retry interceptor (to avoid
  // refresh loops), so a 401 here may just mean the access token expired while
  // the refresh token is still valid — retry once via refresh before giving up.
  useEffect(() => {
    authService
      .me()
      .then(setUser)
      .catch(() =>
        authService
          .refresh()
          .then(() => authService.me())
          .then(setUser)
          .catch(() => setUser(null))
      )
      .finally(() => setLoading(false))
  }, [])

  const login = async (phone: string, password: string) => {
    const data = await authService.login({ phone, password })
    setUser(data)
  }

  const logout = async () => {
    await authService.logout()
    setUser(null)
  }

  const value: AuthContextValue = {
    user,
    isLoading,
    isAuthenticated: !!user,
    isOwner:  user?.role === "OWNER",
    isAdmin:  user?.role === "ADMIN" || user?.role === "OWNER",
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error("useAuth must be used inside <AuthProvider>")
  return ctx
}
