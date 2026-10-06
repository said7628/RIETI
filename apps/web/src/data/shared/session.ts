import { useSyncExternalStore } from 'react'
import type { UserRole } from './catalogs'
import { currentUser } from './currentUser'
import { reportsData, type Report } from './reportsData'

export interface SessionUser {
  name: string
  role: UserRole
}

const demoAccounts: { email: string; password: string; user: SessionUser }[] = [
  {
    email: 'manolo@rieti.mx',
    password: 'rieti',
    user: { name: 'Manolo Sánchez', role: 'ADMINISTRADOR' },
  },
  {
    email: 'joaquin@rieti.mx',
    password: 'rieti',
    user: { name: 'Joaquín Mondragón', role: 'GESTOR_MUNICIPAL' },
  },
  {
    email: 'maria@rieti.mx',
    password: 'rieti',
    user: { name: 'María Posada', role: 'PROCURADOR' },
  },
]

let user: SessionUser | null = currentUser
let reports: Report[] = [...reportsData]
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

export function login(email: string, password: string) {
  const account = demoAccounts.find(
    (item) =>
      item.email === email.trim().toLowerCase() && item.password === password,
  )

  if (!account) {
    return false
  }

  user = account.user
  emit()
  return true
}

export function addReport(report: Report) {
  reports = [report, ...reports]
  emit()
}
