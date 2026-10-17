import type { Report } from '../../data/shared/reportsData'
import { ReportLocationMap } from '../shared/ReportDetail'
import ReportAgeBreakdown, { descriptionWithoutAgeHeader } from '../shared/ReportAgeBreakdown'
import ReportEvidenceGallery from '../shared/ReportEvidenceGallery'
import { formatDate, workTypeLabel } from '../../data/shared/reportUtils'
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
        <div className="report-map-container report-modal-map">
          <ReportLocationMap latitude={report.latitude} longitude={report.longitude} />
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
            {workTypeLabel(report.workType)}
          </p>

          <p>
            <strong>Horario de trabajo:</strong>
            <br />
            {report.schedule}
          </p>

          <p>
            <strong>Municipio:</strong>
            <br />
            {report.municipality || 'No especificado'}
          </p>

          <p>
            <strong>Colonia:</strong>
            <br />
            {report.colonia || "No especificada"}
          </p>

          <p>
            <strong>Titular del caso:</strong>
            <br />
            {report.assignedUserInactive ? 'Cuenta desactivada — requiere reasignación' : report.caseHolder ?? 'Sin asignar'}
          </p>

          <p>
            <strong>NNA estimados:</strong>
            <br />
            {report.childrenCount ?? report.nnaEstimate}
          </p>

          <p>
            <strong>Edades aproximadas:</strong>
            <br />
            <ReportAgeBreakdown report={report} />
          </p>
        </div>

        <p>
          <strong>Dirección:</strong> {report.address}
        </p>

        <div className="report-description">
          <strong>Detalles del reportante:</strong>
          <p>{descriptionWithoutAgeHeader(report.description) || "Sin descripción adicional"}</p>
        </div>

        {report.solution && (
          <div className="report-solution">
            <strong>Solución:</strong>
            <p>{report.solution}</p>
          </div>
        )}

        <h3>Evidencia fotográfica</h3>

        <ReportEvidenceGallery key={report.folio} folio={report.folio} />
      </div>
    </Modal>
  )
}

export default ReportDetailsModal
