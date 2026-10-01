import { reportsData } from '../data/reportsData'

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
                      report.status === 'SIN ASIGNAR'
                        ? 'status-unassigned'
                        : report.status === 'EN ATENCIÓN'
                          ? 'status-attention'
                          : 'status-completed'
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