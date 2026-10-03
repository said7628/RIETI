import { useState } from 'react'
import { reportStatuses, semaforoByStatus, type ReportStatus } from '../../data/shared/catalogs'
import type { Report } from '../../data/shared/reportsData'
import Modal from '../shared/Modal'

interface UpdateStatusModalProps {
  report: Report
  onCancel: () => void
  onConfirm: (status: ReportStatus, solution: string | null) => void
}

function UpdateStatusModal({ report, onCancel, onConfirm }: UpdateStatusModalProps) {
  const [status, setStatus] = useState<ReportStatus>(report.status)
  const [solution, setSolution] = useState(report.solution ?? '')
  const [error, setError] = useState('')

  // RN-006: no se puede cerrar un caso sin describir la solución.
  const requiresSolution = semaforoByStatus[status] === 'ATENDIDO'

  function handleConfirm() {
    if (requiresSolution && solution.trim() === '') {
      setError('Describe la solución brindada antes de cerrar el caso.')
      return
    }

    onConfirm(status, requiresSolution ? solution.trim() : null)
  }

  return (
    <Modal title={`Actualizar estatus: ${report.folio}`}>
      <label className="modal-field">
        Nuevo estatus
        <select
          value={status}
          onChange={(event) => setStatus(event.target.value as ReportStatus)}
        >
          {reportStatuses.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
      </label>

      {requiresSolution && (
        <label className="modal-field">
          Descripción de la solución
          <textarea
            value={solution}
            onChange={(event) => setSolution(event.target.value)}
            rows={4}
          />
        </label>
      )}

      {error && <p className="modal-error">{error}</p>}

      <div className="modal-buttons">
        <button type="button" onClick={onCancel}>
          Cancelar
        </button>
        <button type="button" className="button-primary" onClick={handleConfirm}>
          Guardar
        </button>
      </div>
    </Modal>
  )
}

export default UpdateStatusModal
