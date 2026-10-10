import { sortReports } from '../../data/shared/reportUtils'
import { useReports } from '../../data/shared/session'
import StatusBadge from '../shared/StatusBadge'

/** Lista de los 10 reportes más recientes. */
function RecentReports() {
  const recentReports = sortReports(useReports(), 'recientes').slice(0, 10)

  return (
    <div className="recent-reports-card">
      <h3>DENUNCIAS RECIENTES</h3>

      <div className="reports-table-wrapper">
        <table className="reports-table">
          <thead>
            <tr>
              <th>Folio</th>
              <th>Tipo de trabajo</th>
              <th>Colonia</th>
              <th>Fase actual</th>
              <th>Titular del caso</th>
            </tr>
          </thead>

          <tbody>
            {recentReports.map((report) => (
              <tr key={report.folio}>
                <td>{report.folio}</td>

                <td>{report.workType}</td>

                <td>{report.colonia}</td>

                <td>
                  <StatusBadge status={report.status} />
                </td>

                <td>{report.caseHolder ?? '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default RecentReports
