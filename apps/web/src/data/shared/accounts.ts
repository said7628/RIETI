import { useSyncExternalStore } from 'react'
import { type UserRole } from './catalogs'

export interface Account {
  name: string
  email: string
  password: string
  role: UserRole
  active: boolean
}

let accounts: Account[] = [
  {
    name: 'Manolo Sánchez',
    email: 'manolo@rieti.mx',
    password: 'rieti',
    role: 'ADMINISTRADOR',
    active: true,
  },
  {
    name: 'Joaquín Mondragón',
    email: 'joaquin@rieti.mx',
    password: 'rieti',
    role: 'EMPLEADO',
    active: true,
  },
  {
    name: 'María Posada',
    email: 'maria@rieti.mx',
    password: 'rieti',
    role: 'EMPLEADO',
    active: true,
  },
]

const listeners = new Set<() => void>()

function emit() {
  listeners.forEach((listener) => listener())
}

export function subscribeAccounts(listener: () => void) {
  listeners.add(listener)
  return () => listeners.delete(listener)
}

export function getAccounts() {
  return accounts
}

export function useAccounts() {
  return useSyncExternalStore(subscribeAccounts, getAccounts)
}

export function findActiveAccount(email: string, password: string) {
  const normalizedEmail = email.trim().toLowerCase()

  return accounts.find(
    (account) =>
      account.active &&
      account.email === normalizedEmail &&
      account.password === password,
  )
}

export function createAccount(input: {
  name: string
  email: string
  password: string
  role: UserRole
}) {
  const email = input.email.trim().toLowerCase()

  if (accounts.some((account) => account.email === email)) {
    return 'Ese correo ya tiene una cuenta.'
  }

  accounts = [
    ...accounts,
    {
      name: input.name.trim(),
      email,
      password: input.password,
      role: input.role,
      active: true,
    },
  ]
  emit()
  return null
}

export function updateAccountRole(email: string, role: UserRole) {
  accounts = accounts.map((account) =>
    account.email === email ? { ...account, role } : account,
  )
  emit()
  return accounts.find((account) => account.email === email) ?? null
}

export function setAccountActive(email: string, active: boolean) {
  accounts = accounts.map((account) =>
    account.email === email ? { ...account, active } : account,
  )
  emit()
  return accounts.find((account) => account.email === email) ?? null
}

let recoveryCode: { email: string; code: string } | null = null

export function requestPasswordCode(email: string) {
  const account = accounts.find(
    (item) => item.active && item.email === email.trim().toLowerCase(),
  )

  if (!account) {
    return null
  }

  const code = String(Math.floor(100000 + Math.random() * 900000))
  recoveryCode = { email: account.email, code }
  return code
}

export function resetPassword(email: string, code: string, password: string) {
  const normalizedEmail = email.trim().toLowerCase()

  if (
    !recoveryCode ||
    recoveryCode.email !== normalizedEmail ||
    recoveryCode.code !== code.trim()
  ) {
    return 'El código no coincide.'
  }

  accounts = accounts.map((account) =>
    account.email === normalizedEmail ? { ...account, password } : account,
  )
  recoveryCode = null
  emit()
  return null
}
