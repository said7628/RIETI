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
  getMunicipalities,
  getRoles,
  type MunicipalityCatalog,
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

  const [municipalities, setMunicipalities] =
    useState<MunicipalityCatalog[]>([])

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

  const [municipalityId, setMunicipalityId] =
    useState<number | null>(null)

  const [error, setError] = useState('')

  useEffect(() => {
    let active = true

    Promise.all([
      getRoles(),
      getMunicipalities(),
    ])
      .then(([rolesData, municipalitiesData]) => {
        if (!active) return

        setRoles(rolesData)
        setMunicipalities(municipalitiesData)

        const defaultRole =
          rolesData.find(
            (role) =>
              role.codigo ===
              'GESTOR_MUNICIPAL',
          ) ?? rolesData[0]

        if (defaultRole) {
          setRoleId(defaultRole.id)
        }

        if (municipalitiesData[0]) {
          setMunicipalityId(
            municipalitiesData[0].id,
          )
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
        'Completa nombre, correo y las dos contraseñas.',
      )
      return
    }

    if (!email.includes('@')) {
      setError('Escribe un correo válido.')
      return
    }

    if (password.length < 10) {
      setError(
        'La contraseña debe tener al menos 10 caracteres.',
      )
      return
    }

    if (password !== confirmPassword) {
      setError(
        'Las contraseñas no coinciden.',
      )
      return
    }

    if (!roleId) {
      setError(
        'Selecciona un tipo de cuenta.',
      )
      return
    }

    const selectedRole =
      roles.find(
        (role) => role.id === roleId,
      )

    const requiresMunicipality =
      selectedRole?.codigo ===
        'GESTOR_MUNICIPAL' ||
      selectedRole?.codigo ===
        'PROCURADOR'

    if (
      requiresMunicipality &&
      !municipalityId
    ) {
      setError(
        'Selecciona el municipio asignado a la cuenta.',
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
        municipalityId:
          roles.find(
            (role) =>
              role.id === roleId,
          )?.codigo ===
            'ADMINISTRADOR_FUNDACION'
            ? null
            : municipalityId,
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

  const selectedRole =
    roles.find(
      (role) => role.id === roleId,
    )

  const requiresMunicipality =
    selectedRole?.codigo ===
      'GESTOR_MUNICIPAL' ||
    selectedRole?.codigo ===
      'PROCURADOR'

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
          Correo electrónico

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
            Contraseña

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
            Confirmar contraseña

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

        {requiresMunicipality && (
          <label className="accounts-field">
            Municipio asignado

            <select
              value={municipalityId ?? ''}
              disabled={submitting}
              onChange={(event) =>
                setMunicipalityId(
                  Number(event.target.value),
                )
              }
            >
              <option value="">
                Selecciona un municipio
              </option>

              {municipalities.map(
                (municipality) => (
                  <option
                    key={municipality.id}
                    value={municipality.id}
                  >
                    {municipality.nombre}
                  </option>
                ),
              )}
            </select>
          </label>
        )}

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

  async function reactivateAccount(
    account: ApiUser,
  ) {
    setSaving(true)
    setError('')

    try {
      await updateUser(account.id, {
        active: true,
      })

      await loadData()

      setSelectedUserId(null)
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : 'No fue posible reactivar la cuenta.',
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
              <th>Correo electrónico</th>
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
              ×
            </button>

            <h2 id="account-dialog-title">
              {selected.name}
            </h2>

            <div className="accounts-dialog-line" />

            <p>
              <strong>
                Correo electrónico:
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
              {selected.active ? (
                <button
                  type="button"
                  className="accounts-dialog-secondary"
                  disabled={
                    saving ||
                    selected.id ===
                      currentUserId
                  }
                  onClick={() =>
                    setConfirmDelete(true)
                  }
                >
                  {selected.id ===
                  currentUserId
                    ? 'Sesión actual'
                    : saving
                      ? 'Guardando...'
                      : 'Desactivar cuenta'}
                </button>
              ) : (
                <button
                  type="button"
                  className="accounts-dialog-primary"
                  disabled={saving}
                  onClick={() =>
                    void reactivateAccount(
                      selected,
                    )
                  }
                >
                  {saving
                    ? 'Reactivando...'
                    : 'Reactivar cuenta'}
                </button>
              )}
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
              ×
            </button>

            <h2
              id="delete-dialog-title"
              className="accounts-delete-title"
            >
              La cuenta de{' '}
              <span>{selected.name}</span>{' '}
              será desactivada
            </h2>

            <p>
              El historial y las acciones realizadas
              por esta cuenta se conservarán.
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







