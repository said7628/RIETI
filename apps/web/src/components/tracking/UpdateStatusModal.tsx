import { useState } from 'react'
import {
  reportStatuses,
  semaforoByStatus,
  type ReportStatus,
} from '../../data/shared/catalogs'
import type { Report } from '../../data/shared/reportsData'
import Modal from '../shared/Modal'

interface UpdateStatusModalProps {
  report: Report
  saving?: boolean
  onCancel: () => void
  onConfirm: (
    status: ReportStatus,
    solution: string | null,
  ) => void | Promise<void>
}

function UpdateStatusModal({
  report,
  saving = false,
  onCancel,
  onConfirm,
}: UpdateStatusModalProps) {
  const [status, setStatus] =
    useState<ReportStatus>(report.status)

  const [solution, setSolution] =
    useState(report.solution ?? '')

  const [error, setError] = useState('')

  const requiresSolution =
    semaforoByStatus[status] === 'ATENDIDO'

  function handleConfirm() {
    if (saving) {
      return
    }

    setError('')

    if (
      requiresSolution &&
      solution.trim() === ''
    ) {
      setError(
        'Describe la solución brindada antes de marcar el caso como atendido.',
      )
      return
    }

    void onConfirm(
      status,
      solution.trim() || null,
    )
  }

  return (
    <Modal
      title={`Actualizar estatus: ${report.folio}`}
    >
      <label className="modal-field">
        Nuevo estatus

        <select
          value={status}
          disabled={saving}
          onChange={(event) =>
            setStatus(
              event.target.value as ReportStatus,
            )
          }
        >
          {reportStatuses.map(
            (option) => (
              <option
                key={option}
                value={option}
              >
                {option}
                {option === report.status
                  ? ' (actual)'
                  : ''}
              </option>
            ),
          )}
        </select>
      </label>

      <label className="modal-field">
        Descripción de la solución / seguimiento

        <textarea
          value={solution}
          disabled={saving}
          onChange={(event) =>
            setSolution(
              event.target.value,
            )
          }
          rows={6}
          placeholder="Agrega o amplía la descripción del seguimiento..."
        />
      </label>

      {requiresSolution && (
        <p className="modal-help">
          Para un estado atendido debes registrar una solución.
        </p>
      )}

      {error && (
        <p className="modal-error">
          {error}
        </p>
      )}

      {saving && (
        <div
          className="modal-saving-message"
          role="status"
          aria-live="polite"
        >
          Guardando cambios...
        </div>
      )}

      <div className="modal-buttons">
        <button
          type="button"
          disabled={saving}
          onClick={onCancel}
        >
          Cancelar
        </button>

        <button
          type="button"
          className="button-primary"
          disabled={saving}
          onClick={handleConfirm}
        >
          {saving
            ? 'Guardando...'
            : 'Guardar'}
        </button>
      </div>
    </Modal>
  )
}

export default UpdateStatusModal
