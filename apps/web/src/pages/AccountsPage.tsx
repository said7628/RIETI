import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useSearchParams } from 'react-router-dom'

import {
  createUser,
  deactivateUser,
  getUsers,
  updateUser,
  type ApiUser,
} from '../api/users'

import {
  getRoles,
  type RoleCatalog,
} from '../api/catalogs'

import type { Report } from '../data/shared/reportsData'
import {
  useReports,
  useSessionUser,
} from '../data/shared/session'

import { semaforoByStatus } from '../data/shared/catalogs'

import './AccountsPage.css'

type AccountsView = 'crear' | 'administrar'

type AccountSort =
  | 'nombre-az'
  | 'nombre-za'
  | 'reportes'

function AccountsPage() {
  const user = useSessionUser()

  const [searchParams, setSearchParams] =
    useSearchParams()

  const view: AccountsView =
    searchParams.get('vista') === 'administrar'
      ? 'administrar'
      : 'crear'

  const isAdmin =
    user?.roleCode === 'ADMINISTRADOR_FUNDACION'

  if (!isAdmin) {
    return (
      <main className="accounts-page">
        <div className="accounts-denied">
          <h1>Cuentas</h1>

          <p>
            Solo un administrador puede crear y
            administrar cuentas.
          </p>

          <Link to="/">
            Volver al inicio
          </Link>
        </div>
      </main>
    )
  }

  function openView(nextView: AccountsView) {
    setSearchParams(
      nextView === 'administrar'
        ? { vista: 'administrar' }
        : {},
    )
  }

  return (
    <main className="accounts-page">
      <div className="accounts-layout">
        <nav
          className="accounts-side"
          aria-label="Secciones de cuentas"
        >
          <button
            type="button"
            className={
              view === 'crear'
                ? 'accounts-side-active'
                : ''
            }
            onClick={() => openView('crear')}
          >
            Crear cuenta
          </button>

          <button
            type="button"
            className={
              view === 'administrar'
                ? 'accounts-side-active'
                : ''
            }
            onClick={() =>
              openView('administrar')
            }
          >
            Administrar
          </button>
        </nav>

        {view === 'crear' ? (
          <CreateAccount
            onCreated={() =>
              openView('administrar')
            }
          />
        ) : (
          <ManageAccounts
            currentUserId={user.id}
          />
        )}
      </div>
    </main>
  )
}

function CreateAccount({
  onCreated,
}: {
  onCreated: () => void
}) {
  const [roles, setRoles] =
    useState<RoleCatalog[]>([])

  const [loadingRoles, setLoadingRoles] =
    useState(true)

  const [submitting, setSubmitting] =
    useState(false)

  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] =
    useState('')

  const [
    confirmPassword,
    setConfirmPassword,
  ] = useState('')

  const [roleId, setRoleId] =
    useState<number | null>(null)

  const [error, setError] = useState('')

  useEffect(() => {
    let active = true

    getRoles()
      .then((data) => {
        if (!active) return

        setRoles(data)

        const defaultRole =
          data.find(
            (role) =>
              role.codigo ===
              'GESTOR_MUNICIPAL',
          ) ?? data[0]

        if (defaultRole) {
          setRoleId(defaultRole.id)
        }
      })
      .catch((err) => {
        if (!active) return

        setError(
          err instanceof Error
            ? err.message
            : 'No fue posible cargar los roles.',
        )
      })
      .finally(() => {
        if (active) {
          setLoadingRoles(false)
        }
      })

    return () => {
      active = false
    }
  }, [])

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault()

    setError('')

    if (
      !name.trim() ||
      !email.trim() ||
      !password ||
      !confirmPassword
    ) {
      setError(
        'Completa nombre, correo y las dos contraseÃ±as.',
      )
      return
    }

    if (!email.includes('@')) {
      setError('Escribe un correo vÃ¡lido.')
      return
    }

    if (password.length < 8) {
      setError(
        'La contraseÃ±a debe tener al menos 8 caracteres.',
      )
      return
    }

    if (password !== confirmPassword) {
      setError(
        'Las contraseÃ±as no coinciden.',
      )
      return
    }

    if (!roleId) {
      setError(
        'Selecciona un tipo de cuenta.',
      )
      return
    }

    setSubmitting(true)

    try {
      await createUser({
        name: name.trim(),
        email: email.trim().toLowerCase(),
        password,
        roleId,
      })

      onCreated()
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'No fue posible crear la cuenta.',
      )
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="accounts-panel">
      <div className="accounts-title">
        <h1>Crear una nueva cuenta</h1>
        <div className="accounts-title-line" />
      </div>

      <form
        className="accounts-form"
        onSubmit={handleSubmit}
      >
        <label className="accounts-field accounts-field-wide">
          Nombre

          <input
            value={name}
            disabled={submitting}
            onChange={(event) =>
              setName(event.target.value)
            }
          />
        </label>

        <label className="accounts-field">
          Correo electrÃ³nico

          <input
            type="email"
            autoComplete="off"
            value={email}
            disabled={submitting}
            onChange={(event) =>
              setEmail(event.target.value)
            }
          />
        </label>

        <div className="accounts-password-row">
          <label className="accounts-field">
            ContraseÃ±a

            <input
              type="password"
              autoComplete="new-password"
              value={password}
              disabled={submitting}
              onChange={(event) =>
                setPassword(
                  event.target.value,
                )
              }
            />
          </label>

          <label className="accounts-field">
            Confirmar contraseÃ±a

            <input
              type="password"
              autoComplete="new-password"
              value={confirmPassword}
              disabled={submitting}
              onChange={(event) =>
                setConfirmPassword(
                  event.target.value,
                )
              }
            />
          </label>
        </div>

        <label className="accounts-field">
          Tipo de cuenta

          <select
            value={roleId ?? ''}
            disabled={
              loadingRoles || submitting
            }
            onChange={(event) =>
              setRoleId(
                Number(event.target.value),
              )
            }
          >
            {roles.map((role) => (
              <option
                key={role.id}
                value={role.id}
              >
                {role.nombre}
              </option>
            ))}
          </select>
        </label>

        {error && (
          <p className="accounts-error">
            {error}
          </p>
        )}

        <button
          type="submit"
          className="accounts-submit"
          disabled={
            submitting || loadingRoles
          }
        >
          {submitting
            ? 'Creando...'
            : 'Crear cuenta'}
        </button>
      </form>
    </section>
  )
}

function countActiveReports(
  reports: Report[],
  name: string,
) {
  return reports.filter((report) => {
    const semaforo =
      semaforoByStatus[report.status]

    return (
      report.caseHolder === name &&
      (semaforo === 'SIN_ATENDER' ||
        semaforo === 'ATENDIENDO')
    )
  }).length
}

function ManageAccounts({
  currentUserId,
}: {
  currentUserId: number
}) {
  const reports = useReports()

  const [accounts, setAccounts] =
    useState<ApiUser[]>([])

  const [roles, setRoles] =
    useState<RoleCatalog[]>([])

  const [loading, setLoading] =
    useState(true)

  const [error, setError] =
    useState('')

  const [sort, setSort] =
    useState<AccountSort>('nombre-az')

  const [
    selectedUserId,
    setSelectedUserId,
  ] = useState<number | null>(null)

  const [
    confirmDelete,
    setConfirmDelete,
  ] = useState(false)

  const [saving, setSaving] =
    useState(false)

  async function loadData() {
    setLoading(true)
    setError('')

    try {
      const [usersResult, rolesResult] =
        await Promise.all([
          getUsers(),
          getRoles(),
        ])

      setAccounts(usersResult)
      setRoles(rolesResult)
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'No fue posible cargar las cuentas.',
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void loadData()
  }, [])

  const selected =
    accounts.find(
      (account) =>
        account.id === selectedUserId,
    ) ?? null

  const rows = useMemo(() => {
    return accounts
      .map((account) => ({
        account,
        activeReports:
          countActiveReports(
            reports,
            account.name,
          ),
      }))
      .sort((a, b) => {
        if (sort === 'reportes') {
          return (
            b.activeReports -
              a.activeReports ||
            a.account.name.localeCompare(
              b.account.name,
              'es',
            )
          )
        }

        const byName =
          a.account.name.localeCompare(
            b.account.name,
            'es',
          )

        return sort === 'nombre-za'
          ? -byName
          : byName
      })
  }, [accounts, reports, sort])

  async function changeRole(
    account: ApiUser,
    roleId: number,
  ) {
    setSaving(true)
    setError('')

    try {
      await updateUser(account.id, {
        roleId,
      })

      await loadData()

      setSelectedUserId(null)
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'No fue posible actualizar la cuenta.',
      )
    } finally {
      setSaving(false)
    }
  }

  async function removeAccount(
    account: ApiUser,
  ) {
    setSaving(true)
    setError('')

    try {
      await deactivateUser(account.id)

      await loadData()

      setSelectedUserId(null)
      setConfirmDelete(false)
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'No fue posible desactivar la cuenta.',
      )
    } finally {
      setSaving(false)
    }
  }

  if (loading) {
    return (
      <section className="accounts-panel">
        <p>Cargando cuentas...</p>
      </section>
    )
  }

  return (
    <section className="accounts-panel">
      <div className="accounts-toolbar">
        <label>
          Ordenar por:

          <select
            value={sort}
            onChange={(event) =>
              setSort(
                event.target
                  .value as AccountSort,
              )
            }
          >
            <option value="nombre-az">
              Nombre (a-z)
            </option>

            <option value="nombre-za">
              Nombre (z-a)
            </option>

            <option value="reportes">
              Cantidad de reportes
            </option>
          </select>
        </label>
      </div>

      {error && (
        <p className="accounts-error">
          {error}
        </p>
      )}

      <div className="accounts-table-wrap">
        <table className="accounts-table">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Correo electrÃ³nico</th>
              <th>Tipo de cuenta</th>
              <th>Estado</th>
              <th>
                Cantidad de reportes activos
              </th>
            </tr>
          </thead>

          <tbody>
            {rows.map(
              ({
                account,
                activeReports,
              }) => (
                <tr
                  key={account.id}
                  className="accounts-row"
                  tabIndex={0}
                  onClick={() => {
                    setSelectedUserId(
                      account.id,
                    )
                    setConfirmDelete(false)
                  }}
                  onKeyDown={(event) => {
                    if (
                      event.key === 'Enter' ||
                      event.key === ' '
                    ) {
                      event.preventDefault()

                      setSelectedUserId(
                        account.id,
                      )

                      setConfirmDelete(false)
                    }
                  }}
                >
                  <td>{account.name}</td>

                  <td>{account.email}</td>

                  <td>
                    {account.roleName}
                  </td>

                  <td>
                    {account.active
                      ? 'Activa'
                      : 'Inactiva'}
                  </td>

                  <td>
                    {activeReports}
                  </td>
                </tr>
              ),
            )}
          </tbody>
        </table>
      </div>

      {selected && !confirmDelete && (
        <div
          className="accounts-overlay"
          onClick={(event) => {
            if (
              event.target ===
              event.currentTarget
            ) {
              setSelectedUserId(null)
            }
          }}
        >
          <div
            className="accounts-dialog"
            role="dialog"
            aria-modal="true"
            aria-labelledby="account-dialog-title"
          >
            <button
              type="button"
              className="accounts-dialog-close"
              onClick={() =>
                setSelectedUserId(null)
              }
              aria-label="Cerrar"
            >
              Ã—
            </button>

            <h2 id="account-dialog-title">
              {selected.name}
            </h2>

            <div className="accounts-dialog-line" />

            <p>
              <strong>
                Correo electrÃ³nico:
              </strong>{' '}
              {selected.email}
            </p>

            <p>
              <strong>Tipo de cuenta:</strong>{' '}
              {selected.roleName}
            </p>

            <p>
              <strong>Estado:</strong>{' '}
              {selected.active
                ? 'Activa'
                : 'Inactiva'}
            </p>

            <p>
              <strong>
                Cantidad de reportes activos:
              </strong>{' '}
              {countActiveReports(
                reports,
                selected.name,
              )}
            </p>

            <label className="accounts-field">
              Cambiar tipo de cuenta

              <select
                value={
                  roles.find(
                    (role) =>
                      role.codigo ===
                      selected.roleCode,
                  )?.id ?? ''
                }
                disabled={saving}
                onChange={(event) =>
                  void changeRole(
                    selected,
                    Number(
                      event.target.value,
                    ),
                  )
                }
              >
                {roles.map((role) => (
                  <option
                    key={role.id}
                    value={role.id}
                  >
                    {role.nombre}
                  </option>
                ))}
              </select>
            </label>

            <div className="accounts-dialog-actions">
              <button
                type="button"
                className="accounts-dialog-secondary"
                disabled={
                  saving ||
                  selected.id ===
                    currentUserId ||
                  !selected.active
                }
                onClick={() =>
                  setConfirmDelete(true)
                }
              >
                {selected.id ===
                currentUserId
                  ? 'SesiÃ³n actual'
                  : selected.active
                    ? 'Desactivar cuenta'
                    : 'Cuenta inactiva'}
              </button>
            </div>
          </div>
        </div>
      )}

      {selected && confirmDelete && (
        <div className="accounts-overlay">
          <div
            className="accounts-dialog"
            role="dialog"
            aria-modal="true"
            aria-labelledby="delete-dialog-title"
          >
            <button
              type="button"
              className="accounts-dialog-close"
              onClick={() =>
                setConfirmDelete(false)
              }
              aria-label="Cerrar"
            >
              Ã—
            </button>

            <h2
              id="delete-dialog-title"
              className="accounts-delete-title"
            >
              La cuenta de{' '}
              <span>{selected.name}</span>{' '}
              serÃ¡ desactivada
            </h2>

            <p>
              El historial y las acciones realizadas
              por esta cuenta se conservarÃ¡n.
            </p>

            <div className="accounts-confirm-actions">
              <button
                type="button"
                className="accounts-dialog-primary"
                disabled={saving}
                onClick={() =>
                  setConfirmDelete(false)
                }
              >
                Cancelar
              </button>

              <button
                type="button"
                className="accounts-dialog-secondary"
                disabled={saving}
                onClick={() =>
                  void removeAccount(selected)
                }
              >
                {saving
                  ? 'Desactivando...'
                  : 'Confirmar'}
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  )
}

export default AccountsPage


