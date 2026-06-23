import axios from "axios"

const api = axios.create({
  baseURL: "/",
  withCredentials: true,          // send HttpOnly cookies automatically
  headers: { "Content-Type": "application/json" },
})

/**
 * Unwraps the standard API envelope `{ success, data, message, timestamp }`
 * down to the `data` payload. Shared by all service modules.
 */
export const wrap = <T>(res: { data: { data: T } }): T => res.data.data

// ── Token refresh interceptor ────────────────────────────────────────────────
let isRefreshing = false
let failedQueue: Array<{
  resolve: (value: unknown) => void
  reject: (reason?: unknown) => void
}> = []

const processQueue = (error: Error | null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) reject(error)
    else resolve(null)
  })
  failedQueue = []
}

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // Never try to refresh on the auth endpoints themselves — avoids retry loops.
    const url: string = originalRequest?.url ?? ""
    const isAuthEndpoint =
      url.includes("/v1/auth/refresh") ||
      url.includes("/v1/auth/login") ||
      url.includes("/v1/auth/me")

    if (error.response?.status === 401 && !originalRequest._retry && !isAuthEndpoint) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        }).then(() => api(originalRequest))
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        await api.post("/v1/auth/refresh")
        processQueue(null)
        return api(originalRequest)
      } catch (refreshError) {
        processQueue(refreshError as Error)
        // Refresh failed — kick to login
        if (window.location.pathname !== "/login") {
          window.location.href = "/login"
        }
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(error)
  }
)

export default api
