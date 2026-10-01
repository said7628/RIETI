import dashboardIcon from '../assets/dashboard-icon.png'

function StatusCards() {
  // Por ahora uso datos de prueba. Después estos números van a venir del backend.
  const statusData = [
    {
      title: 'Reportes sin asignar',
      value: 15,
      path: '/seguimiento?estado=sin-asignar',
    },
    {
      title: 'Reportes en fase de atención',
      value: 121,
      path: '/seguimiento?estado=en-atencion',
    },
    {
      title: 'Casos cerrados',
      value: 115,
      path: '/casos-cerrados',
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
            <a
              key={status.title}
              href={status.path}
              className="status-card"
            >
              <span className="status-card-title">
                {status.title}
              </span>

              <strong className="status-card-value">
                {status.value}
              </strong>
            </a>
          ))}
        </div>
      </div>
    </section>
  )
}

export default StatusCards