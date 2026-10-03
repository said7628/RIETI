import type { Report } from '../../data/shared/reportsData'
import { formatDate } from '../../data/shared/reportUtils'
import Modal from '../shared/Modal'
import StatusBadge from '../shared/StatusBadge'

interface ReportDetailsModalProps {
  report: Report
  onClose: () => void
}

function ReportDetailsModal({ report, onClose }: ReportDetailsModalProps) {
  return (
    <Modal
      title={`Detalles del reporte: ${report.folio}`}
      onClose={onClose}
      large
    >
      <div className="report-details-modal">
        <div className="report-map-placeholder">
          <span>Ubicación del reporte</span>
        </div>

        <div className="report-details-grid">
          <p>
            <strong>Folio:</strong>
            <br />
            {report.folio}
          </p>

          <p>
            <strong>Fecha:</strong>
            <br />
            {formatDate(report.createdAt)}
          </p>

          <p>
            <strong>Fase actual:</strong>
            <br />
            <StatusBadge status={report.status} />
          </p>

          <p>
            <strong>Prioridad:</strong>
            <br />
            {report.priority}
          </p>

          <p>
            <strong>Tipo de trabajo:</strong>
            <br />
            {report.workType}
          </p>

          <p>
            <strong>Horario de trabajo:</strong>
            <br />
            {report.schedule}
          </p>

          <p>
            <strong>Colonia:</strong>
            <br />
            {report.colonia}
          </p>

          <p>
            <strong>Titular del caso:</strong>
            <br />
            {report.caseHolder ?? 'Sin asignar'}
          </p>

          <p>
            <strong>NNA estimados:</strong>
            <br />
            {report.nnaEstimate}
          </p>

          <p>
            <strong>Rango de edad:</strong>
            <br />
            {report.ageRange} años
          </p>
        </div>

        <p>
          <strong>Dirección:</strong> {report.address}
        </p>

        <div className="report-description">
          <strong>Detalles del reportante:</strong>
          <p>{report.description}</p>
        </div>

        {report.solution && (
          <p>
            <strong>Solución:</strong> {report.solution}
          </p>
        )}

        <h3>Evidencia fotográfica</h3>

        <div className="evidence-container">
          <div className="evidence-placeholder">Evidencia 1</div>
          <div className="evidence-placeholder">Evidencia 2</div>
        </div>
      </div>
    </Modal>
  )
}

export default ReportDetailsModal
