import { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
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
  type RoleCatalog,
} from '../api/catalogs'

import type { Report } from '../data/shared/reportsData'
import {
  useReports,
  useSessionUser,
  refreshReports,
} from '../data/shared/session'

import { semaforoByStatus } from '../data/shared/catalogs'
import { getMunicipalities, getDependencies, type MunicipalityCatalog, type DependencyCatalog } from '../api/catalogs'

import './AccountsPage.css'

type AccountsView = 'crear' | 'administrar'

function accountRoleLabel(account: ApiUser) {
  const labels: Record<string, string> = {
    CIUDADANO: 'Ciudadano',
    CIUDADANO_REPORTANTE: 'Ciudadano',
    REPORTANTE: 'Ciudadano',
    ADMINISTRADOR_FUNDACION: 'Administrador de la fundación',
    GESTOR_MUNICIPAL: 'Gestor municipal',
    PROCURADOR: 'Procurador de protección',
    PROCURADOR_PROTECCION: 'Procurador de protección',
  }
  return labels[account.roleCode] ?? account.roleName ?? 'Rol no identificado'
}

type AccountSort =
  | 'nombre-az'
  | 'nombre-za'
  | 'reportes'

/**
 * Pantalla de Cuentas.
 * Solo la ve el rol administrador de la fundación.
 * Permite crear cuentas y administrarlas.
 */
function AccountsPage() {
  const user = useSessionUser()

  const [searchParams, setSearchParams] = useSearchParams()
  const view: AccountsView = searchParams.get('vista') === 'administrar' ? 'administrar' : 'crear'

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
        <nav className="accounts-side" aria-label="Secciones de cuentas">
          <button type="button" className={view === 'crear' ? 'accounts-side-active' : ''}
            onClick={() => openView('crear')}>Crear cuenta</button>
          <button type="button" className={view === 'administrar' ? 'accounts-side-active' : ''}
            onClick={() => openView('administrar')}>Administrar</button>
        </nav>
        {view === 'crear' ? (
          <CreateAccount onCreated={() => openView('administrar')} />
        ) : (
          <ManageAccounts currentUserId={user.id} />
        )}
      </div>
    </main>
  )
}


/** Cambia el rol, el municipio y la dependencia de una cuenta. */
function RoleEditor({account, roles, municipalities, dependencies, saving, disabled, onSave}: {
  account: ApiUser
  roles: RoleCatalog[]
  municipalities: MunicipalityCatalog[]
  dependencies: DependencyCatalog[]
  saving: boolean
  disabled: boolean
  onSave: (roleId: number, municipalityId: number | null, dependencyId: number | null) => void
}) {
  const [roleId, setRoleId] = useState<number>(roles.find(r => r.codigo === account.roleCode)?.id ?? 0)
  const [municipalityId, setMunicipalityId] = useState<number | null>(account.municipalityId)
  const [dependencyId, setDependencyId] = useState<number | null>(account.dependencyId)
  const [error, setError] = useState('')
  const role = roles.find(r => r.id === roleId)
  const needsMunicipality = Boolean(role && !role.es_administrador && role.acceso_web)
  const changed = roleId !== (roles.find(r => r.codigo === account.roleCode)?.id ?? 0) || municipalityId !== account.municipalityId || dependencyId !== account.dependencyId
  function save() {
    if (!roleId) {setError('Selecciona un rol.');return}
    if (needsMunicipality && !municipalityId) {setError('Este rol requiere un municipio asignado.');return}
    setError('')
    onSave(roleId, needsMunicipality ? municipalityId : null, needsMunicipality ? dependencyId : null)
  }
  return <div className="accounts-role-editor">
    <label className="accounts-field">Cambiar tipo de cuenta
      <select value={roleId} disabled={saving || disabled} onChange={e => {setRoleId(Number(e.target.value));setError('')}}>
        {!roleId && <option value={0} disabled>{accountRoleLabel(account)} (rol actual)</option>}
        {roles.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
      </select>
    </label>
    {needsMunicipality && <label className="accounts-field">Municipio asignado
      <select value={municipalityId ?? ''} disabled={saving || disabled} onChange={e => {setMunicipalityId(e.target.value ? Number(e.target.value) : null);setDependencyId(null)}}>
        <option value="">Selecciona un municipio</option>
        {municipalities.map(m => <option key={m.id} value={m.id}>{m.nombre}</option>)}
      </select>
    </label>}
    {needsMunicipality && <label className="accounts-field">Dependencia (opcional)
      <select value={dependencyId ?? ''} disabled={saving || disabled} onChange={e => setDependencyId(e.target.value ? Number(e.target.value) : null)}>
        <option value="">Sin dependencia</option>
        {dependencies.filter(d => !d.municipio_id || d.municipio_id === municipalityId).map(d => <option key={d.id} value={d.id}>{d.nombre}</option>)}
      </select>
    </label>}
    {error && <p className="accounts-error" role="alert">{error}</p>}
    {disabled && <p>No puedes modificar tus propios permisos desde esta sesión.</p>}
    <button type="button" className="accounts-dialog-primary" disabled={saving || disabled || !changed} onClick={save}>
      {saving ? 'Guardando...' : 'Guardar permisos y municipio'}
    </button>
  </div>
}

/** Formulario para crear una cuenta. */
function CreateAccount({ onCreated }: { onCreated: () => void }) {
  const [roles, setRoles] = useState<RoleCatalog[]>([])
  const [municipalities, setMunicipalities] = useState<MunicipalityCatalog[]>([])
  const [dependencies, setDependencies] = useState<DependencyCatalog[]>([])
  const [roleId, setRoleId] = useState<number | null>(null)
  const [municipalityId, setMunicipalityId] = useState<number | null>(null)
  const [dependencyId, setDependencyId] = useState<number | null>(null)
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirmation, setConfirmation] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    Promise.all([getRoles(), getMunicipalities(), getDependencies()])
      .then(([r, m, d]) => {
        if (!active) return
        setRoles(r)
        setMunicipalities(m)
        setDependencies(d)
        setRoleId(r.find(x => x.codigo === 'GESTOR_MUNICIPAL')?.id ?? r[0]?.id ?? null)
      })
      .catch(() => { if (active) setError('No fue posible cargar los catálogos de cuentas.') })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [])
  const role = roles.find(r => r.id === roleId)
  const requiresMunicipality = Boolean(role && !role.es_administrador && role.acceso_web)
  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    if (!name.trim() || !email.trim() || !password || !confirmation || !roleId) {
      setError('Completa el nombre, correo, rol y ambas contraseñas.')
      return
    }
    if (password.length < 10 || password !== confirmation) {
      setError('La contraseña debe tener al menos 10 caracteres y ambas deben coincidir.')
      return
    }
    if (requiresMunicipality && !municipalityId) {
      setError('Selecciona el municipio asignado a esta cuenta.')
      return
    }
    setSaving(true)
    try {
      await createUser({
        name: name.trim(), email: email.trim().toLowerCase(), password, roleId,
        municipalityId: requiresMunicipality ? municipalityId : null,
        dependencyId: requiresMunicipality ? dependencyId : null,
      })
      onCreated()
    } catch (e) { setError(e instanceof Error ? e.message : 'No fue posible crear la cuenta.') }
    finally { setSaving(false) }
  }
  return (
    <section className="accounts-panel">
      <div className="accounts-title"><h1>Crear una nueva cuenta</h1><div className="accounts-title-line" /></div>
      <form className="accounts-form" onSubmit={event => void submit(event)}>
        <label className="accounts-field accounts-field-wide">Nombre
          <input value={name} disabled={saving} onChange={e => setName(e.target.value)} required />
        </label>
        <label className="accounts-field">Correo electrónico
          <input type="email" value={email} disabled={saving} onChange={e => setEmail(e.target.value)} required />
        </label>
        <div className="accounts-password-row">
          <label className="accounts-field">Contraseña
            <input type="password" autoComplete="new-password" value={password} disabled={saving}
              onChange={e => setPassword(e.target.value)} required minLength={10} />
          </label>
          <label className="accounts-field">Confirmar contraseña
            <input type="password" autoComplete="new-password" value={confirmation} disabled={saving}
              onChange={e => setConfirmation(e.target.value)} required minLength={10} />
          </label>
        </div>
        <label className="accounts-field">Tipo de cuenta
          <select value={roleId ?? ''} disabled={loading || saving} onChange={e => {setRoleId(Number(e.target.value));setMunicipalityId(null);setDependencyId(null)}} required>
            <option value="" disabled>Selecciona un tipo de cuenta</option>
            {roles.map(r => <option value={r.id} key={r.id}>{r.nombre}</option>)}
          </select>
        </label>
        {requiresMunicipality && <label className="accounts-field">Municipio asignado
          <select value={municipalityId ?? ''} disabled={saving} onChange={e => {setMunicipalityId(Number(e.target.value));setDependencyId(null)}} required>
            <option value="">Selecciona un municipio</option>
            {municipalities.map(m => <option key={m.id} value={m.id}>{m.nombre}</option>)}
          </select>
        </label>}
        {requiresMunicipality && <label className="accounts-field">Dependencia (opcional)
          <select value={dependencyId ?? ''} disabled={saving} onChange={e => setDependencyId(e.target.value ? Number(e.target.value) : null)}>
            <option value="">Sin dependencia</option>
            {dependencies.filter(d => !d.municipio_id || d.municipio_id === municipalityId).map(d => <option key={d.id} value={d.id}>{d.nombre}</option>)}
          </select>
        </label>}
        {error && <p className="accounts-error" role="alert">{error}</p>}
        <button type="submit" className="accounts-submit" disabled={loading || saving}>{saving ? 'Creando...' : 'Crear cuenta'}</button>
      </form>
    </section>
  )
}

function countActiveReports(
  reports: Report[],
  name: string,
  userId: number,
) {
  return reports.filter((report) => {
    const semaforo =
      semaforoByStatus[report.status]

    return (
      (report.assignedUserId === userId || (!report.assignedUserId && report.caseHolder === name)) &&
      (semaforo === 'SIN_ATENDER' ||
        semaforo === 'ATENDIENDO')
    )
  }).length
}

/** Lista las cuentas para verlas y cambiarlas. */
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
  const [municipalities, setMunicipalities] = useState<MunicipalityCatalog[]>([])
  const [dependencies, setDependencies] = useState<DependencyCatalog[]>([])

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
      void refreshReports().catch(() => undefined)
      const [usersResult, rolesResult, municipalitiesResult, dependenciesResult] =
        await Promise.all([getUsers(), getRoles(), getMunicipalities(), getDependencies()])

      setAccounts(usersResult)
      setRoles(rolesResult)
      setMunicipalities(municipalitiesResult)
      setDependencies(dependenciesResult)
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
            account.id,
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

  async function changeRole(account: ApiUser, roleId: number, municipalityId: number | null, dependencyId: number | null) {
    if (account.id === currentUserId) {
      setError('Para proteger la sesión no puedes cambiar tus propios permisos.')
      return
    }
    setSaving(true)
    setError('')
    try {
      await updateUser(account.id, {roleId, municipalityId, dependencyId})
      await loadData()
      setSelectedUserId(null)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No fue posible actualizar la cuenta.')
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

      <p className="accounts-metrics-note">
        Reportes visibles en tu sesión: {reports.length}. La columna «Reportes activos asignados» cuenta únicamente los casos con titular asignado a cada persona; no representa todos los reportes de la fundación.
      </p>
      <div className="accounts-table-wrap">
        <table className="accounts-table">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Correo electrónico</th>
              <th>Tipo de cuenta</th>
              <th>Estado</th>
              <th>
                Reportes activos asignados
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
                    {accountRoleLabel(account)}
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
              {accountRoleLabel(selected)}
            </p>

            <p>
              <strong>Municipio:</strong>{' '}
              {selected.municipalityId == null ? 'No asignado' :
                (municipalities.find(m => m.id === selected.municipalityId)?.nombre ?? `ID ${selected.municipalityId}`)}
            </p>
            <p>
              <strong>Dependencia:</strong>{' '}
              {selected.dependencyId == null ? 'No asignada' :
                (dependencies.find(d => d.id === selected.dependencyId)?.nombre ?? `ID ${selected.dependencyId}`)}
            </p>
            <p>
              <strong>Estado:</strong>{' '}
              {selected.active
                ? 'Activa'
                : 'Inactiva'}
            </p>

            <p>
              <strong>
                Reportes activos asignados:
              </strong>{' '}
              {countActiveReports(
                reports,
                selected.name,
                selected.id,
              )}
            </p>

            <RoleEditor
              key={selected.id}
              account={selected}
              roles={roles}
              municipalities={municipalities}
              dependencies={dependencies}
              saving={saving}
              disabled={selected.id === currentUserId}
              onSave={(roleId, municipalityId, dependencyId) => void changeRole(selected, roleId, municipalityId, dependencyId)}
            />

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







