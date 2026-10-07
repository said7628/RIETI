import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import {
  createAccount,
  setAccountActive,
  updateAccountRole,
  useAccounts,
} from '../data/shared/accounts'
import { roleLabels, userRoles, type UserRole } from '../data/shared/catalogs'
import { syncSessionFromAccount, useSessionUser } from '../data/shared/session'
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

function ManageAccounts({ currentEmail }: { currentEmail: string }) {
  const accounts = useAccounts()

  function changeRole(email: string, role: UserRole) {
    const account = updateAccountRole(email, role)
    if (account) syncSessionFromAccount(account)
  }

  function changeActive(email: string, active: boolean) {
    const account = setAccountActive(email, active)
    if (account) syncSessionFromAccount(account)
  }

  return (
    <section className="accounts-panel">
      <div className="accounts-title">
        <h1>Administrar cuentas</h1>
        <div className="accounts-title-line"></div>
      </div>

      <div className="accounts-table-wrap">
        <table className="accounts-table">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Correo</th>
              <th>Tipo de cuenta</th>
              <th>Estado</th>
            </tr>
          </thead>
          <tbody>
            {accounts.map((account) => {
              const isCurrent = account.email === currentEmail

              return (
                <tr key={account.email} className={account.active ? '' : 'accounts-row-inactive'}>
                  <td>{account.name}</td>
                  <td>{account.email}</td>
                  <td>
                    <select
                      aria-label={`Tipo de cuenta de ${account.name}`}
                      value={account.role}
                      onChange={(event) =>
                        changeRole(account.email, event.target.value as UserRole)
                      }
                    >
                      {userRoles.map((option) => (
                        <option key={option} value={option}>
                          {roleLabels[option]}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td>
                    <button
                      type="button"
                      disabled={isCurrent}
                      onClick={() => changeActive(account.email, !account.active)}
                    >
                      {isCurrent
                        ? 'Sesión actual'
                        : account.active
                          ? 'Desactivar'
                          : 'Activar'}
                    </button>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>
    </section>
  )
}

export default AccountsPage
