import { Link } from 'react-router-dom'
import dashboardIcon from '../../assets/dashboard-icon.png'
import { semaforoByStatus, type Semaforo } from '../../data/shared/catalogs'
import type { Report } from '../../data/shared/reportsData'
import { useReports } from '../../data/shared/session'
import { matchesView } from '../../data/tracking/trackingViews'

function countBySemaforo(reports: Report[], semaforo: Semaforo) {
  return reports.filter(
    (report) => semaforoByStatus[report.status] === semaforo,
  ).length
}

function StatusCards() {
  const reports = useReports()
  const statusData = [
    {
      title: 'Reportes sin asignar',
      value: reports.filter((report) =>
        matchesView(report, 'sin-asignar', null),
      ).length,
      path: '/seguimiento?vista=sin-asignar',
    },
    {
      title: 'Reportes en atención',
      value: countBySemaforo(reports, 'ATENDIENDO'),
      path: '/seguimiento?vista=en-atencion',
    },
    {
      title: 'Casos cerrados',
      value: countBySemaforo(reports, 'ATENDIDO'),
      path: '/seguimiento?vista=cerrados',
    },
  ]

  return (
    <section className="dashboard-section">
      <div className="dashboard-container">
        <div className="dashboard-label">
          <img src={dashboardIcon} alt="" />
          <span>Dashboard</span>
        </div>

        <h2 className="dashboard-title">
          BANDEJA DE ENTRADA OPERATIVA
        </h2>

        <div className="status-cards">
          {statusData.map((status) => (
            <Link
              key={status.title}
              to={status.path}
              className="status-card"
            >
              <span className="status-card-title">
                {status.title}
              </span>

              <strong className="status-card-value">
                {status.value}
              </strong>
            </Link>
          ))}
        </div>
      </div>
    </section>
  )
}

export default StatusCards
