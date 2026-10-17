import { useEffect, useState } from 'react'
import { getUsers, type ApiUser } from '../../api/users'
import Modal from '../shared/Modal'

interface AssignSelectProps {
  folio: string
  reportMunicipalityId?: number | null
  onConfirm: (user: ApiUser) => Promise<void>
}

const ASSIGNABLE_ROLES = new Set([
  'ADMINISTRADOR_FUNDACION',
  'GESTOR_MUNICIPAL',
  'PROCURADOR',
  'PROCURADOR_PROTECCION',
])

function AssignSelect({
  folio,
  reportMunicipalityId,
  onConfirm,
}: AssignSelectProps) {
  const [staff, setStaff] = useState<ApiUser[]>([])
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true

    getUsers()
      .then((users) => {
        if (!active) return

        setStaff(
          users.filter(
            (user) =>
              user.active &&
              ASSIGNABLE_ROLES.has(user.roleCode) &&
              (user.roleCode === 'ADMINISTRADOR_FUNDACION' ||
                (user.municipalityId != null &&
                 (reportMunicipalityId == null || user.municipalityId === reportMunicipalityId))),
          ),
        )
      })
      .catch(() => {
        if (active) setStaff([])
      })
      .finally(() => {
        if (active) setLoading(false)
      })

    return () => {
      active = false
    }
  }, [folio, reportMunicipalityId])

  const selected =
    staff.find((user) => user.id === selectedId) ?? null

  async function confirmAssignment() {
    if (!selected) return
    setError('')
    setSaving(true)
    try {
      await onConfirm(selected)
      setSelectedId(null)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No fue posible asignar el reporte.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <>
      <label className="assign-select">
        Asignar titular:{' '}

        <select
          value=""
          disabled={loading}
          onChange={(event) =>
            setSelectedId(Number(event.target.value))
          }
        >
          <option value="" disabled>
            {loading ? 'Cargando personal...' : staff.length ? '(Asignar)' : 'Sin personal disponible para este municipio'}
          </option>

          {staff.map((user) => (
            <option
              key={user.id}
              value={user.id}
            >
              {user.name}
            </option>
          ))}
        </select>
      </label>

      {selected && (
        <Modal title="Confirmar asignación">
          <p>
            ¿Asignar el reporte{' '}
            <strong>{folio}</strong> a{' '}
            <strong>{selected.name}</strong>?
          </p>

          {error && <p role="alert" className="modal-error">{error}</p>}
          <div className="modal-buttons">
            <button
              type="button"
              disabled={saving}
              onClick={() => setSelectedId(null)}
            >
              Cancelar
            </button>

            <button
              type="button"
              className="button-primary"
              disabled={saving}
              onClick={() => void confirmAssignment()}
            >
              {saving ? 'Asignando...' : 'Confirmar'}
            </button>
          </div>
        </Modal>
      )}
    </>
  )
}

export default AssignSelect
