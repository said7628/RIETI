import { useEffect, useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'

import {
  deactivateUser,
  createUser,
  getUsers,
  updateUser,
  type ApiUser,
} from '../api/users'

import {
  getRoles,
  getMunicipalities,
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

type AccountsView = 'crear' | 'administrar' | 'ciudadanos'

type AccountRoleFilter = 'todos' | 'administrador' | 'gestor' | 'procurador' | 'ciudadano'

function accountCategory(code: string): Exclude<AccountRoleFilter, 'todos'> {
  if (code === 'ADMINISTRADOR_FUNDACION') return 'administrador'
  if (code === 'GESTOR_MUNICIPAL') return 'gestor'
  if (code === 'PROCURADOR' || code === 'PROCURADOR_PROTECCION') return 'procurador'
  return 'ciudadano'
}

type AccountSort =
  | 'nombre-az'
  | 'nombre-za'
  | 'reportes'

function AccountsPage() {
  const user = useSessionUser()

  const [searchParams, setSearchParams] = useSearchParams()
  const view: AccountsView = searchParams.get('vista') === 'crear' ? 'crear' : searchParams.get('vista') === 'ciudadanos' ? 'ciudadanos' : 'administrar'

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
      { vista: nextView },
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
            className={view === 'crear' ? 'accounts-side-active' : ''}
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
            Personal autorizado
          </button>
          <button
            type="button"
            className={view === 'ciudadanos' ? 'accounts-side-active' : ''}
            onClick={() => openView('ciudadanos')}
          >Ciudadanos</button>
        </nav>

        {view === 'crear'
          ? <CreateAccount onCreated={() => openView('administrar')} />
          : <ManageAccounts key={view} currentUserId={user.id} section={view === 'ciudadanos' ? 'ciudadanos' : 'personal'} />}


      </div>
    </main>
  )
}



function CreateAccount({ onCreated }: { onCreated: () => void }) {
  const [roles, setRoles] = useState<RoleCatalog[]>([])
  const [municipalities, setMunicipalities] = useState<MunicipalityCatalog[]>([])
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [roleId, setRoleId] = useState('')
  const [municipalityId, setMunicipalityId] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    Promise.all([getRoles(), getMunicipalities()])
      .then(([rolesResult, municipalityResult]) => {
        if (!active) return
        setRoles(rolesResult)
        setMunicipalities(municipalityResult)
      })
      .catch((err) => {
        if (active) setError(err instanceof Error ? err.message : 'No se pudieron cargar los catálogos.')
      })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [])

  const selectedRole = roles.find((r) => String(r.id) === roleId)
  const needsMunicipality = Boolean(selectedRole && !selectedRole.es_administrador && selectedRole.codigo !== 'CIUDADANO')

  async function submit(event: import('react').FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    if (!name.trim() || !email.trim() || !roleId) {
      setError('Completa el nombre, el correo y el tipo de cuenta.'); return
    }
    if (password.length < 10) {
      setError('La contraseña debe tener al menos 10 caracteres.'); return
    }
    if (password !== confirmPassword) {
      setError('Las contraseñas no coinciden.'); return
    }
    if (needsMunicipality && !municipalityId) {
      setError('Selecciona el municipio del personal antes de crear la cuenta.'); return
    }
    setSaving(true)
    try {
      await createUser({
        name: name.trim(), email: email.trim().toLowerCase(), password,
        roleId: Number(roleId),
        municipalityId: needsMunicipality ? Number(municipalityId) : null,
        phone: phone.trim() || null,
      })
      onCreated()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo crear la cuenta.')
    } finally { setSaving(false) }
  }

  return <section className="accounts-panel">
    <div className="accounts-title"><h1>Crear cuenta</h1><div className="accounts-title-line" /></div>
    <p className="accounts-create-intro">Crea cuentas para el personal autorizado. Los ciudadanos se registran desde Android.</p>
    {loading ? <p role="status">Cargando tipos de cuenta y municipios...</p> :
      <form className="accounts-form" onSubmit={(e) => void submit(e)}>
        <label className="accounts-field accounts-field-wide">Nombre completo
          <input value={name} maxLength={150} required autoComplete="name" onChange={e => setName(e.target.value)} disabled={saving} />
        </label>
        <label className="accounts-field accounts-field-wide">Correo electrónico
          <input type="email" value={email} maxLength={254} required autoComplete="email" onChange={e => setEmail(e.target.value)} disabled={saving} />
        </label>
        <label className="accounts-field accounts-field-wide">Teléfono (opcional)
          <input type="tel" value={phone} maxLength={30} autoComplete="tel" onChange={e => setPhone(e.target.value)} disabled={saving} />
        </label>
        <label className="accounts-field accounts-field-wide">Tipo de cuenta
          <select value={roleId} required onChange={e => {setRoleId(e.target.value); setMunicipalityId('')}} disabled={saving}>
            <option value="">Selecciona un rol</option>
            {roles.filter(role => role.codigo !== 'CIUDADANO').map(role => <option key={role.id} value={role.id}>{role.nombre}</option>)}
          </select>
        </label>
        {needsMunicipality && <label className="accounts-field accounts-field-wide">Municipio asignado
          <select value={municipalityId} required onChange={e => setMunicipalityId(e.target.value)} disabled={saving}>
            <option value="">Selecciona un municipio</option>
            {municipalities.map(m => <option key={m.id} value={m.id}>{m.nombre} ({m.clave})</option>)}
          </select>
        </label>}
        <div className="accounts-password-row">
          <label className="accounts-field">Contraseña temporal (mínimo 10 caracteres)
            <input type="password" value={password} required minLength={10} maxLength={128} autoComplete="new-password" onChange={e => setPassword(e.target.value)} disabled={saving} />
          </label>
          <label className="accounts-field">Confirmar contraseña
            <input type="password" value={confirmPassword} required minLength={10} maxLength={128} autoComplete="new-password" onChange={e => setConfirmPassword(e.target.value)} disabled={saving} />
          </label>
        </div>
        <p className="accounts-hint">Comparte las credenciales mediante un canal privado y pide al usuario cambiar su contraseña al entrar.</p>
        {error && <p role="alert" className="accounts-error">{error}</p>}
        <button type="submit" className="accounts-submit" disabled={saving || roles.length === 0}>
          {saving ? 'Creando cuenta...' : 'Crear cuenta'}
        </button>
      </form>}
      {!loading && error && roles.length === 0 && <p role="alert" className="accounts-error">{error}</p>}
  </section>
}

function countActiveReports(
  reports: Report[],
  account: Pick<ApiUser, 'id' | 'name'>,
) {
  return reports.filter((report) => {
    const semaforo =
      semaforoByStatus[report.status]

    return (
      (report.assignedUserId != null
        ? report.assignedUserId === account.id
        : report.caseHolder === account.name) &&
      (semaforo === 'SIN_ATENDER' ||
        semaforo === 'ATENDIENDO')
    )
  }).length
}

function ManageAccounts({
  currentUserId,
  section,
}: {
  currentUserId: number
  section: 'ciudadanos' | 'personal'
}) {
  const reports = useReports()

  const [accounts, setAccounts] =
    useState<ApiUser[]>([])

  const [roles, setRoles] =
    useState<RoleCatalog[]>([])
  const [municipalities, setMunicipalities] = useState<MunicipalityCatalog[]>([])

  const [loading, setLoading] =
    useState(true)

  const [error, setError] =
    useState('')

  const [sort, setSort] =
    useState<AccountSort>('nombre-az')

  const [roleFilter, setRoleFilter] = useState<AccountRoleFilter>('todos')

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
      const [usersResult, rolesResult, municipalityResult] =
        await Promise.all([
          getUsers(),
          getRoles(),
          getMunicipalities(),
        ])

      setAccounts(usersResult)
      setRoles(rolesResult)
      setMunicipalities(municipalityResult)
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

  const assignedCount = selected
    ? reports.filter((report) => report.assignedUserId === selected.id).length
    : 0

  const rows = useMemo(() => {
    return accounts
      .filter((account) => section === 'ciudadanos' ? account.roleCode === 'CIUDADANO' : account.roleCode !== 'CIUDADANO')
      .filter((account) => roleFilter === 'todos' || accountCategory(account.roleCode) === roleFilter)
      .map((account) => ({
        account,
        activeReports:
          countActiveReports(
            reports,
            account,
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
  }, [accounts, reports, sort, roleFilter, section])

  async function changeRole(
    account: ApiUser,
    roleId: number,
  ) {
    if (account.roleCode === 'CIUDADANO') {
      setError('Una cuenta ciudadana no puede convertirse en personal. Crea una cuenta independiente de personal.'); return
    }
    const targetRole = roles.find((r) => r.id === roleId)
    if (targetRole?.codigo === 'CIUDADANO' || (targetRole?.codigo === 'ADMINISTRADOR_FUNDACION' && account.roleCode !== 'ADMINISTRADOR_FUNDACION')) {
      setError('Este cambio de rol no está permitido. Crea una cuenta independiente con permisos autorizados.'); return
    }
    if (targetRole && !targetRole.es_administrador && !account.municipalityId) {
      setError('Primero asigna un municipio a esta cuenta y después cambia su tipo.')
      return
    }
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

  async function changeMunicipality(account: ApiUser, municipalityId: number | null) {
    if (account.roleCode === 'CIUDADANO' || (account.id === currentUserId && account.isAdmin)) return
    setSaving(true)
    setError('')
    try {
      await updateUser(account.id, { municipalityId })
      await loadData()
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No fue posible actualizar el municipio.')
    } finally { setSaving(false) }
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
      <section className="accounts-panel accounts-loading" role="status" aria-live="polite">
        <h1>Cuentas</h1>
        <p>Cargando cuentas...</p>
        <div className="accounts-skeleton" aria-hidden="true" />
        <div className="accounts-skeleton accounts-skeleton-short" aria-hidden="true" />
        <div className="accounts-skeleton" aria-hidden="true" />
      </section>
    )
  }

  return (
    <section className="accounts-panel">
      <div className="accounts-toolbar">
        <h1 className="accounts-section-name">{section === 'ciudadanos' ? 'Cuentas ciudadanas' : 'Cuentas del personal'}</h1>
        <label>
          Filtrar tipo de cuenta:
          <select aria-label="Filtrar por tipo de cuenta" value={roleFilter}
            onChange={(event) => setRoleFilter(event.target.value as AccountRoleFilter)}>
            <option value="todos">Todos los tipos</option>
            {section === 'personal' && <>
              <option value="administrador">Administrador</option>
              <option value="gestor">Gestor</option>
              <option value="procurador">Procurador</option>
            </>}
            {section === 'ciudadanos' && <option value="ciudadano">Ciudadano</option>}
          </select>
        </label>
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
                selected,
              )}
            </p>

            {selected.roleCode !== 'CIUDADANO' && (
              <label className="accounts-field accounts-municipality-field">
                Municipio asignado
                <select
                  aria-label="Municipio asignado"
                  value={selected.municipalityId ?? ''}
                  disabled={saving}
                  onChange={(event) => {
                    const value = event.target.value
                    void changeMunicipality(selected, value ? Number(value) : null)
                  }}
                >
                  <option value="">Sin municipio asignado</option>
                  {municipalities.map((municipality) => (
                    <option key={municipality.id} value={municipality.id}>
                      {municipality.nombre} ({municipality.clave})
                    </option>
                  ))}
                </select>
                <span className="accounts-hint">Antes de asignarle casos debe tener un municipio. Cambiarlo requiere revisar sus asignaciones existentes.</span>
              </label>
            )}
            {selected.roleCode !== 'CIUDADANO' && <label className="accounts-field">
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
                {roles.filter(role => role.codigo !== 'CIUDADANO' && (role.codigo !== 'ADMINISTRADOR_FUNDACION' || selected.roleCode === 'ADMINISTRADOR_FUNDACION')).map((role) => (
                  <option
                    key={role.id}
                    value={role.id}
                  >
                    {role.nombre}
                  </option>
                ))}
              </select>
            </label>}

            {assignedCount > 0 && selected.active && (
              <p className="accounts-assignment-warning" role="status">
                Tiene {assignedCount} reporte(s) asignado(s). Reasigna esos casos antes de desactivar la cuenta.
              </p>
            )}
            {error && <p role="alert" className="accounts-error">{error}</p>}
            <div className="accounts-dialog-actions">
              {selected.active ? (
                <button
                  type="button"
                  className="accounts-dialog-secondary"
                  disabled={
                    saving ||
                    assignedCount > 0 ||
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
              El historial y las acciones realizadas por esta cuenta se conservarán.
              Solo es posible desactivarla cuando no tenga reportes asignados.
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
