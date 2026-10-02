import { semaforoByStatus, type Semaforo } from '../data/catalogs'
import { reportsData } from '../data/reportsData'

const semaforoClassNames: Record<Semaforo, string> = {
  SIN_ATENDER: 'status-sin-atender',
  ATENDIENDO: 'status-atendiendo',
  ATENDIDO: 'status-atendido',
  INACTIVO: 'status-inactivo',
}

function RecentReports() {
  return (
    <div className="recent-reports-card">
      <h3>DENUNCIAS RECIENTES</h3>

      <div className="reports-table-wrapper">
        <table className="reports-table">
          <thead>
            <tr>
              <th>Folio</th>
              <th>Tipo de trabajo</th>
              <th>Ubicación</th>
              <th>Fase actual</th>
              <th>Titular del caso</th>
            </tr>
          </thead>

          <tbody>
            {reportsData.map((report) => (
              <tr key={report.folio}>
                <td>{report.folio}</td>

                <td>{report.workType}</td>

                <td>{report.location}</td>

                <td>
                  <span
                    className={`report-status ${
                      semaforoClassNames[semaforoByStatus[report.status]]
                    }`}
                  >
                    {report.status}
                  </span>
                </td>

                <td>{report.caseHolder}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default RecentReports