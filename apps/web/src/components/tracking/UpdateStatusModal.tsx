import { useMemo, useState } from 'react'
import {
  semaforoByStatus,
  type ReportStatus,
} from '../../data/shared/catalogs'
import type { Report } from '../../data/shared/reportsData'
import Modal from '../shared/Modal'

interface UpdateStatusModalProps {
  report: Report
  onCancel: () => void
  onConfirm: (
    status: ReportStatus,
    solution: string | null,
  ) => void
}

const allowedTransitions: Record<
  ReportStatus,
  ReportStatus[]
> = {
  Registrado: [
    'En revisión',
    'Reincidente',
    'Cancelado',
  ],

  Reincidente: [
    'En revisión',
    'Cancelado',
  ],

  'En revisión': [
    'En seguimiento',
    'Canalizado',
    'Cancelado',
  ],

  'En seguimiento': [
    'Canalizado',
    'Concluido',
    'Cancelado',
  ],

  Canalizado: [
    'En seguimiento',
    'Concluido',
    'Cancelado',
  ],

  Concluido: [
    'Archivado',
  ],

  Archivado: [],

  Cancelado: [],
}

function UpdateStatusModal({
  report,
  onCancel,
  onConfirm,
}: UpdateStatusModalProps) {
  const availableStatuses = useMemo(
    () => allowedTransitions[report.status] ?? [],
    [report.status],
  )

  const [status, setStatus] =
    useState<ReportStatus>(
      availableStatuses[0] ?? report.status,
    )

  const [solution, setSolution] =
    useState(report.solution ?? '')

  const [error, setError] = useState('')

  const requiresSolution =
    semaforoByStatus[status] === 'ATENDIDO'

  function handleConfirm() {
    setError('')

    if (availableStatuses.length === 0) {
      setError(
        'Este caso ya no permite cambios de estatus.',
      )
      return
    }

    if (
      requiresSolution &&
      solution.trim() === ''
    ) {
      setError(
        'Describe la solución brindada antes de cerrar el caso.',
      )
      return
    }

    onConfirm(
      status,
      requiresSolution
        ? solution.trim()
        : null,
    )
  }

  return (
    <Modal
      title={`Actualizar estatus: ${report.folio}`}
    >
      <label className="modal-field">
        Estatus actual

        <input
          value={report.status}
          disabled
        />
      </label>

      {availableStatuses.length > 0 ? (
        <label className="modal-field">
          Nuevo estatus

          <select
            value={status}
            onChange={(event) =>
              setStatus(
                event.target.value as ReportStatus,
              )
            }
          >
            {availableStatuses.map(
              (option) => (
                <option
                  key={option}
                  value={option}
                >
                  {option}
                </option>
              ),
            )}
          </select>
        </label>
      ) : (
        <p className="modal-error">
          Este caso se encuentra en un estatus final y
          ya no permite cambios.
        </p>
      )}

      {requiresSolution &&
        availableStatuses.length > 0 && (
          <label className="modal-field">
            Descripción de la solución

            <textarea
              value={solution}
              onChange={(event) =>
                setSolution(
                  event.target.value,
                )
              }
              rows={4}
            />
          </label>
        )}

      {error && (
        <p className="modal-error">
          {error}
        </p>
      )}

      <div className="modal-buttons">
        <button
          type="button"
          onClick={onCancel}
        >
          Cancelar
        </button>

        {availableStatuses.length > 0 && (
          <button
            type="button"
            className="button-primary"
            onClick={handleConfirm}
          >
            Guardar
          </button>
        )}
      </div>
    </Modal>
  )
}

export default UpdateStatusModal


