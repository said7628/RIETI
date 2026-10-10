import { useEffect, useState } from 'react'
import { getAssignableUsers, type ApiUser } from '../../api/users'
import Modal from '../shared/Modal'

interface AssignSelectProps {
  folio: string
  onConfirm: (user: ApiUser) => Promise<void>
}

/** Elige a quién se le asigna el reporte de ese folio. */
function AssignSelect({
  folio,
  onConfirm,
}: AssignSelectProps) {
  const [staff, setStaff] = useState<ApiUser[]>([])
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)
  const [loadError, setLoadError] = useState('')
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    let active = true

    getAssignableUsers()
      .then((users) => {
        if (!active) return

        setStaff(users)
      })
      .catch(() => {
        if (active) setLoadError('No fue posible cargar los titulares disponibles.')
      })
      .finally(() => {
        if (active) setLoading(false)
      })

    return () => {
      active = false
    }
  }, [])

  const selected =
    staff.find((user) => user.id === selectedId) ?? null

  async function confirmAssignment() {
    if (!selected) return

    setSaving(true)

    try {
      await onConfirm(selected)
      setSelectedId(null)
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
            {loading ? 'Cargando...' : '(Asignar)'}
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

      {loadError && <p role="alert" className="tracking-error">{loadError}</p>}

      {selected && (
        <Modal title="Confirmar asignación">
          <p>
            ¿Asignar el reporte{' '}
            <strong>{folio}</strong> a{' '}
            <strong>{selected.name}</strong>?
          </p>

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
