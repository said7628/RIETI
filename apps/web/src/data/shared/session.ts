import { useSyncExternalStore } from 'react'
import {
  ApiError,
  apiRequest,
  setAccessToken,
} from '../../api/api'
import type { UserRole } from './catalogs'
import type { Report } from './reportsData'

export interface SessionUser {
  id: number
  name: string
  email: string
  role: UserRole
  roleCode: string
  roleName: string
  municipalityId: number | null
  dependencyId: number | null
}

interface LoginResponse {
  ok: boolean
  user: SessionUser
  accessToken: string
  refreshToken: string
}

interface RefreshResponse {
  ok: boolean
  user: SessionUser
  accessToken: string
  refreshToken: string
}

interface ReportsResponse {
  ok: boolean
  reports: Report[]
}

function readSavedUser(): SessionUser | null {
  const raw = sessionStorage.getItem('rieti_user')

  if (!raw) {
    return null
  }

  try {
    return JSON.parse(raw) as SessionUser
  } catch {
    sessionStorage.removeItem('rieti_user')
    return null
  }
}

let user: SessionUser | null = readSavedUser()
let reports: Report[] = []

const listeners = new Set<() => void>()
let refreshSessionPromise: Promise<boolean> | null = null

function emit() {
  listeners.forEach((listener) => listener())
}

function saveSession(
  nextUser: SessionUser,
  accessToken: string,
  refreshToken?: string,
) {
  user = nextUser
  setAccessToken(accessToken)

  sessionStorage.setItem(
    'rieti_user',
    JSON.stringify(nextUser),
  )

  if (refreshToken) {
    sessionStorage.setItem(
      'rieti_refresh_token',
      refreshToken,
    )
  }

  emit()
}

export function subscribeSession(listener: () => void) {
  listeners.add(listener)
  return () => listeners.delete(listener)
}

export function getSessionUser() {
  return user
}

export function getReports() {
  return reports
}

export function useSessionUser() {
  return useSyncExternalStore(subscribeSession, getSessionUser)
}

export function useReports() {
  return useSyncExternalStore(subscribeSession, getReports)
}

export async function login(email: string, password: string) {
  try {
    const response = await apiRequest<LoginResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({
        email: email.trim().toLowerCase(),
        password,
      }),
    })

    saveSession(
      response.user,
      response.accessToken,
      response.refreshToken,
    )

    try {
      await refreshReports()
    } catch {
      // El login ya fue válido. Un fallo temporal cargando reportes
      // no debe invalidar la sesión recién creada.
    }

    return null
  } catch (error) {
    return error instanceof Error
      ? error.message
      : 'No fue posible iniciar sesión.'
  }
}

async function renewSessionWithRefreshToken() {
  const refreshToken =
    sessionStorage.getItem('rieti_refresh_token')

  if (!refreshToken) {
    return false
  }

  try {
    const response = await apiRequest<RefreshResponse>(
      '/auth/refresh',
      {
        method: 'POST',
        body: JSON.stringify({
          refreshToken,
        }),
      },
    )

    saveSession(
      response.user,
      response.accessToken,
      response.refreshToken,
    )

    return true
  } catch (error) {
    if (
      error instanceof ApiError &&
      (error.status === 401 ||
        error.status === 403)
    ) {
      logout()
    }

    return false
  }
}

async function refreshSessionInternal() {
  try {
    const response = await apiRequest<{
      ok: boolean
      user: SessionUser
    }>('/auth/me')

    user = response.user

    sessionStorage.setItem(
      'rieti_user',
      JSON.stringify(user),
    )

    emit()

    try {
      await refreshReports()
    } catch {
      // Mantener sesión aunque falle temporalmente /reportes.
    }

    return true
  } catch (error) {
    if (
      error instanceof ApiError &&
      error.status === 401
    ) {
      const renewed =
        await renewSessionWithRefreshToken()

      if (!renewed) {
        return false
      }

      try {
        await refreshReports()
      } catch {
        // Sesión renovada correctamente; no expulsar al usuario.
      }

      return true
    }

    if (
      error instanceof ApiError &&
      error.status === 403
    ) {
      logout()
      return false
    }

    // Network error / 5xx / abort temporal:
    // conservar la sesión local en lugar de expulsar al usuario.
    return Boolean(user)
  }
}

export function refreshSession() {
  if (!refreshSessionPromise) {
    refreshSessionPromise =
      refreshSessionInternal().finally(() => {
        refreshSessionPromise = null
      })
  }

  return refreshSessionPromise
}

export async function refreshReports() {
  if (!user) {
    reports = []
    emit()
    return
  }

  const response =
    await apiRequest<ReportsResponse>('/reportes')

  reports = response.reports
  emit()
}

export function logout() {
  user = null
  reports = []

  setAccessToken(null)

  sessionStorage.removeItem('rieti_user')
  sessionStorage.removeItem('rieti_refresh_token')

  emit()
}

export function addReport(report: Report) {
  reports = [report, ...reports]
  emit()
}
