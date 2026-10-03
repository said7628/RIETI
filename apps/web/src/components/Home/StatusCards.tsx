import { Link } from 'react-router-dom'
import dashboardIcon from '../../assets/dashboard-icon.png'
import { semaforoByStatus, type Semaforo } from '../../data/shared/catalogs'
import { reportsData } from '../../data/shared/reportsData'

function countBySemaforo(semaforo: Semaforo) {
  return reportsData.filter(
    (report) => semaforoByStatus[report.status] === semaforo,
  ).length
}

function StatusCards() {
  // Por ahora uso datos de prueba. Después estos números van a venir del backend.
  const statusData = [
    {
      title: 'Reportes sin asignar',
      value: countBySemaforo('SIN_ATENDER'),
      path: '/seguimiento?vista=sin-asignar',
    },
    {
      title: 'Reportes en atención',
      value: countBySemaforo('ATENDIENDO'),
      path: '/seguimiento?vista=en-atencion',
    },
    {
      title: 'Casos cerrados',
      value: countBySemaforo('ATENDIDO'),
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
