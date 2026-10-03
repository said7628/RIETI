import type { ReactNode } from 'react'
import type { Report } from '../../data/shared/reportsData'

interface ReportDetailProps {
  report: Report
  actions?: ReactNode
}

function ReportDetail({ report, actions }: ReportDetailProps) {
  return (
    <aside className="report-detail-card">
      <h3>DETALLES DEL REPORTE: {report.folio}</h3>

      <div className="report-map-placeholder">
        <span>Ubicación del reporte</span>
      </div>

      <div className="report-information">
        <p>
          <strong>Dirección:</strong> {report.address}
        </p>

        <div className="report-information-grid">
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
        </div>

        <p className="case-holder">
          <strong>Titular del caso:</strong>{' '}
          {report.caseHolder ?? 'Sin asignar'}
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
      </div>

      {actions && <div className="report-actions">{actions}</div>}

      <h4>EVIDENCIA FOTOGRÁFICA:</h4>

      <div className="evidence-container">
        {/* Yo uso espacios de prueba porque todavía no tenemos las imágenes reales del backend. */}
        <div className="evidence-placeholder">
          Evidencia 1
        </div>

        <div className="evidence-placeholder">
          Evidencia 2
        </div>
      </div>
    </aside>
  )
}

export default ReportDetail
