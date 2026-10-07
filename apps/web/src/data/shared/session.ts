import { useSyncExternalStore } from 'react'
import { findActiveAccount, type Account } from './accounts'
import type { UserRole } from './catalogs'
import { currentUser } from './currentUser'
import { reportsData, type Report } from './reportsData'

export interface SessionUser {
  name: string
  email: string
  role: UserRole
}

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

export function logout() {
  user = null
  emit()
}

export function login(email: string, password: string) {
  const account = findActiveAccount(email, password)

  if (!account) {
    return false
  }

  user = toSessionUser(account)
  emit()
  return true
}

export function syncSessionFromAccount(account: Account) {
  if (user?.email !== account.email) {
    return
  }

  user = account.active ? toSessionUser(account) : null
  emit()
}

function toSessionUser(account: Account): SessionUser {
  return {
    name: account.name,
    email: account.email,
    role: account.role,
  }
}

export function addReport(report: Report) {
  reports = [report, ...reports]
  emit()
}
