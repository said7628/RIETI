import { useEffect, useState } from 'react'
import { getUsers, type ApiUser } from '../../api/users'
import Modal from '../shared/Modal'

interface AssignSelectProps {
  folio: string
  onConfirm: (user: ApiUser) => Promise<void>
}

function AssignSelect({
  folio,
  onConfirm,
}: AssignSelectProps) {
  const [staff, setStaff] = useState<ApiUser[]>([])
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    let active = true

    getUsers()
      .then((users) => {
        if (!active) return

        setStaff(
          users.filter(
            (user) =>
              user.active &&
              user.roleCode !== 'GESTOR_MUNICIPAL',
          ),
        )
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
