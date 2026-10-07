import { useSyncExternalStore } from 'react'
import { apiRequest, setAccessToken } from '../../api/api'
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
  user: {
    id: number
    name: string
    email: string
    role: UserRole
    roleCode: string
    roleName: string
    municipalityId: number | null
    dependencyId: number | null
  }
  accessToken: string
  refreshToken: string
}

interface ReportsResponse {
  ok: boolean
  reports: Report[]
}

const savedUser = sessionStorage.getItem('rieti_user')

let user: SessionUser | null = savedUser
  ? JSON.parse(savedUser)
  : null

let reports: Report[] = []

const listeners = new Set<() => void>()

function emit() {
  listeners.forEach((listener) => listener())
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

    setAccessToken(response.accessToken)

    sessionStorage.setItem(
      'rieti_refresh_token',
      response.refreshToken,
    )

    user = response.user

    sessionStorage.setItem(
      'rieti_user',
      JSON.stringify(user),
    )

    await refreshReports()

    emit()

    return null
  } catch (error) {
    return error instanceof Error
      ? error.message
      : 'No fue posible iniciar sesión.'
  }
}

export async function refreshSession() {
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

    await refreshReports()

    emit()

    return true
  } catch {
    logout()
    return false
  }
}

export async function refreshReports() {
  if (!user) {
    reports = []
    emit()
    return
  }

  const response = await apiRequest<ReportsResponse>('/reportes')

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