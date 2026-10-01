import { reportsData } from '../data/reportsData'

function ReportDetail() {
  // Yo tomo el primer elemento porque representa el reporte más reciente.
  // Después este reporte vendrá directamente desde el backend.
  const latestReport = reportsData[0]

  return (
    <aside className="report-detail-card">
      <h3>DETALLES DEL REPORTE: {latestReport.folio}</h3>

      <div className="report-map-placeholder">
        <span>Ubicación del reporte</span>
      </div>

      <div className="report-information">
        <p>
          <strong>Dirección:</strong> {latestReport.address}
        </p>

        <div className="report-information-grid">
          <p>
            <strong>Número de niños:</strong>
            <br />
            {latestReport.children}
          </p>

          <p>
            <strong>Edades de los niños:</strong>
            <br />
            {latestReport.ages}
          </p>

          <p>
            <strong>Tipo de trabajo:</strong>
            <br />
            {latestReport.workType}
          </p>

          <p>
            <strong>Horario de trabajo:</strong>
            <br />
            {latestReport.schedule}
          </p>
        </div>

        <p className="case-holder">
          <strong>Titular del caso:</strong>{' '}
          {latestReport.caseHolder}
        </p>
      </div>

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