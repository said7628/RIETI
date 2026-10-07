import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import {
  createAccount,
  deleteAccount,
  updateAccountRole,
  useAccounts,
} from '../data/shared/accounts'
import { roleLabels, semaforoByStatus, userRoles, type UserRole } from '../data/shared/catalogs'
import type { Report } from '../data/shared/reportsData'
import { syncSessionFromAccount, useReports, useSessionUser } from '../data/shared/session'
import './AccountsPage.css'

type AccountsView = 'crear' | 'administrar'

function AccountsPage() {
  const user = useSessionUser()
  const [searchParams, setSearchParams] = useSearchParams()
  const view: AccountsView =
    searchParams.get('vista') === 'administrar' ? 'administrar' : 'crear'

  if (user?.role !== 'ADMINISTRADOR') {
    return (
      <main className="accounts-page">
        <div className="accounts-denied">
          <h1>Cuentas</h1>
          <p>Solo un administrador puede crear y administrar cuentas.</p>
          <Link to="/">Volver al inicio</Link>
        </div>
      </main>
    )
  }

  function openView(nextView: AccountsView) {
    setSearchParams(nextView === 'administrar' ? { vista: 'administrar' } : {})
  }

  return (
    <main className="accounts-page">
      <div className="accounts-layout">
        <nav className="accounts-side" aria-label="Secciones de cuentas">
          <button
            type="button"
            className={view === 'crear' ? 'accounts-side-active' : ''}
            onClick={() => openView('crear')}
          >
            Crear cuenta
          </button>
          <button
            type="button"
            className={view === 'administrar' ? 'accounts-side-active' : ''}
            onClick={() => openView('administrar')}
          >
            Administrar
          </button>
        </nav>

        {view === 'crear' ? (
          <CreateAccount onCreated={() => openView('administrar')} />
        ) : (
          <ManageAccounts currentEmail={user.email} />
        )}
      </div>
    </main>
  )
}

function CreateAccount({ onCreated }: { onCreated: () => void }) {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [role, setRole] = useState<UserRole>('EMPLEADO')
  const [error, setError] = useState('')

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!name.trim() || !email.trim() || !password || !confirmPassword) {
      setError('Completa nombre, correo y las dos contraseñas.')
      return
    }

    if (!email.includes('@')) {
      setError('Escribe un correo válido.')
      return
    }

    if (password.length < 4) {
      setError('La contraseña debe tener al menos 4 caracteres.')
      return
    }

    if (password !== confirmPassword) {
      setError('Las contraseñas no coinciden.')
      return
    }

    const createError = createAccount({ name, email, password, role })

    if (createError) {
      setError(createError)
      return
    }

    onCreated()
  }

  return (
    <section className="accounts-panel">
      <div className="accounts-title">
        <h1>Crear una nueva cuenta</h1>
        <div className="accounts-title-line"></div>
      </div>

      <form className="accounts-form" onSubmit={handleSubmit}>
        <label className="accounts-field accounts-field-wide">
          Nombre
          <input value={name} onChange={(event) => setName(event.target.value)} />
        </label>

        <label className="accounts-field">
          Correo electrónico
          <input
            type="email"
            autoComplete="off"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
          />
        </label>

        <div className="accounts-password-row">
          <label className="accounts-field">
            Contraseña
            <input
              type="password"
              autoComplete="new-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </label>

          <label className="accounts-field">
            Confirmar contraseña
            <input
              type="password"
              autoComplete="new-password"
              value={confirmPassword}
              onChange={(event) => setConfirmPassword(event.target.value)}
            />
          </label>
        </div>

        <label className="accounts-field">
          Tipo de cuenta
          <select
            value={role}
            onChange={(event) => setRole(event.target.value as UserRole)}
          >
            {userRoles.map((option) => (
              <option key={option} value={option}>
                {roleLabels[option]}
              </option>
            ))}
          </select>
        </label>

        <p className="accounts-hint">
          El empleado puede iniciar sesión y recibir reportes en seguimiento.
        </p>

        {error && <p className="accounts-error">{error}</p>}

        <button type="submit" className="accounts-submit">
          Crear cuenta
        </button>
      </form>
    </section>
  )
}

type AccountSort = 'nombre-az' | 'nombre-za' | 'reportes'

function countActiveReports(reports: Report[], name: string) {
  return reports.filter((report) => {
    const semaforo = semaforoByStatus[report.status]
    return (
      report.caseHolder === name &&
      (semaforo === 'SIN_ATENDER' || semaforo === 'ATENDIENDO')
    )
  }).length
}

function ManageAccounts({ currentEmail }: { currentEmail: string }) {
  const accounts = useAccounts()
  const reports = useReports()
  const [sort, setSort] = useState<AccountSort>('nombre-az')
  const [selectedEmail, setSelectedEmail] = useState<string | null>(null)
  const [confirmDelete, setConfirmDelete] = useState(false)

  const selected = accounts.find((account) => account.email === selectedEmail) ?? null

  const rows = accounts
    .map((account) => ({
      account,
      activeReports: countActiveReports(reports, account.name),
    }))
    .sort((a, b) => {
      if (sort === 'reportes') {
        return b.activeReports - a.activeReports || a.account.name.localeCompare(b.account.name, 'es')
      }

      const byName = a.account.name.localeCompare(b.account.name, 'es')
      return sort === 'nombre-za' ? -byName : byName
    })

  function changeRole(email: string, role: UserRole) {
    const account = updateAccountRole(email, role)
    if (account) syncSessionFromAccount(account)
  }

  function removeAccount(email: string) {
    deleteAccount(email)
    setSelectedEmail(null)
    setConfirmDelete(false)
  }

  return (
    <section className="accounts-panel">
      <div className="accounts-toolbar">
        <label>
          Ordenar por:
          <select
            value={sort}
            onChange={(event) => setSort(event.target.value as AccountSort)}
          >
            <option value="nombre-az">Nombre (a-z)</option>
            <option value="nombre-za">Nombre (z-a)</option>
            <option value="reportes">Cantidad de reportes</option>
          </select>
        </label>
      </div>

      <div className="accounts-table-wrap">
        <table className="accounts-table">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Correo electrónico</th>
              <th>Tipo de cuenta</th>
              <th>Cantidad de reportes activos</th>
            </tr>
          </thead>
          <tbody>
            {rows.map(({ account, activeReports }) => (
              <tr
                key={account.email}
                className="accounts-row"
                tabIndex={0}
                onClick={() => {
                  setSelectedEmail(account.email)
                  setConfirmDelete(false)
                }}
                onKeyDown={(event) => {
                  if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault()
                    setSelectedEmail(account.email)
                    setConfirmDelete(false)
                  }
                }}
              >
                <td>{account.name}</td>
                <td>{account.email}</td>
                <td>{roleLabels[account.role]}</td>
                <td>{activeReports}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {selected && !confirmDelete && (
        <div
          className="accounts-overlay"
          onClick={(event) => {
            if (event.target === event.currentTarget) setSelectedEmail(null)
          }}
        >
          <div className="accounts-dialog" role="dialog" aria-modal="true" aria-labelledby="account-dialog-title">
            <button
              type="button"
              className="accounts-dialog-close"
              onClick={() => setSelectedEmail(null)}
              aria-label="Cerrar"
            >
              ×
            </button>

            <h2 id="account-dialog-title">{selected.name}</h2>
            <div className="accounts-dialog-line"></div>

            <p>
              <strong>Correo electrónico:</strong> {selected.email}
            </p>
            <p>
              <strong>Contraseña:</strong> {selected.password}
            </p>
            <p>
              <strong>Cantidad de reportes activos:</strong>{' '}
              {countActiveReports(reports, selected.name)}
            </p>
            <p>
              <strong>Tipo de cuenta:</strong> {roleLabels[selected.role]}
            </p>

            <div className="accounts-dialog-actions">
              <button
                type="button"
                className="accounts-dialog-primary"
                onClick={() =>
                  changeRole(
                    selected.email,
                    selected.role === 'ADMINISTRADOR' ? 'EMPLEADO' : 'ADMINISTRADOR',
                  )
                }
              >
                {selected.role === 'ADMINISTRADOR'
                  ? 'Convertir en empleado'
                  : 'Convertir en administrador'}
              </button>

              <button
                type="button"
                className="accounts-dialog-secondary"
                disabled={selected.email === currentEmail}
                onClick={() => setConfirmDelete(true)}
              >
                {selected.email === currentEmail ? 'Sesión actual' : 'Eliminar cuenta'}
              </button>
            </div>
          </div>
        </div>
      )}

      {selected && confirmDelete && (
        <div className="accounts-overlay">
          <div className="accounts-dialog" role="dialog" aria-modal="true" aria-labelledby="delete-dialog-title">
            <button
              type="button"
              className="accounts-dialog-close"
              onClick={() => setConfirmDelete(false)}
              aria-label="Cerrar"
            >
              ×
            </button>

            <h2 id="delete-dialog-title" className="accounts-delete-title">
              La cuenta de <span>{selected.name}</span> se eliminará completamente del sistema
            </h2>

            <div className="accounts-confirm-actions">
              <button type="button" className="accounts-dialog-primary" onClick={() => setConfirmDelete(false)}>
                Cancelar
              </button>
              <button
                type="button"
                className="accounts-dialog-secondary"
                onClick={() => removeAccount(selected.email)}
              >
                Confirmar
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  )
}

export default AccountsPage
