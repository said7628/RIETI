import { useState } from 'react'
import {
  statusTransitions,
  semaforoByStatus,
  type ReportStatus,
} from '../../data/shared/catalogs'
import type { Report } from '../../data/shared/reportsData'
import Modal from '../shared/Modal'

interface UpdateStatusModalProps {
  report: Report
  saving?: boolean
  serverError?: string
  isAdmin?: boolean
  onCancel: () => void
  onConfirm: (
    status: ReportStatus,
    solution: string | null,
    recurrenceReason?: string,
  ) => void | Promise<void>
}

function UpdateStatusModal({
  report,
  saving = false,
  serverError = '',
  isAdmin = false,
  onCancel,
  onConfirm,
}: UpdateStatusModalProps) {
  const [status, setStatus] =
    useState<ReportStatus>(report.status)

  const [solution, setSolution] =
    useState(report.solution ?? '')

  const [error, setError] = useState('')
  const [recurrenceReason, setRecurrenceReason] = useState('')

  const terminal = ['Concluido', 'Archivado', 'Cancelado'].includes(report.status)
  const validStatuses = [report.status, ...statusTransitions[report.status].filter(
    (next) => next !== 'Reincidente' || !terminal || isAdmin
  )]

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

    if (['En seguimiento', 'Canalizado'].includes(status) && !report.assignedUserId) {
      setError('Asigna un responsable autorizado antes de pasar a seguimiento o canalizado.')
      return
    }

    if (terminal && status === 'Reincidente' && recurrenceReason.trim().length < 10) {
      setError('Explica el motivo de la reincidencia (mínimo 10 caracteres).')
      return
    }
    void onConfirm(status, solution.trim() || null,
      terminal && status === 'Reincidente' ? recurrenceReason.trim() : undefined)

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
          {validStatuses.map(
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

      <p className="modal-help">
        Solo aparecen cambios permitidos desde el estado actual.
        {validStatuses.length === 1 && ' Este reporte está en un estado final.'}
      </p>
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

      {terminal && status === 'Reincidente' && (
        <label className="modal-field">
          Motivo de la reincidencia
          <textarea value={recurrenceReason} disabled={saving}
            onChange={(event) => { setRecurrenceReason(event.target.value); setError('') }}
            rows={3} minLength={10} placeholder="Describe por qué se reabre el caso..." />
        </label>
      )}
      {requiresSolution && (
        <p className="modal-help">
          Para un estado atendido debes registrar una solución.
        </p>
      )}

      {(error || serverError) && (
        <p className="modal-error" role="alert">
          {error || serverError}
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
