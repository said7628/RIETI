import { semaforoByStatus, type ReportStatus, type Semaforo } from '../../data/shared/catalogs'

const semaforoClassNames: Record<Semaforo, string> = {
  SIN_ATENDER: 'status-sin-atender',
  ATENDIENDO: 'status-atendiendo',
  ATENDIDO: 'status-atendido',
  INACTIVO: 'status-inactivo',
}

interface StatusBadgeProps {
  status: ReportStatus
}

/** Muestra el estatus del reporte con su color de semáforo. */
function StatusBadge({ status }: StatusBadgeProps) {
  const semaforoClass = semaforoClassNames[semaforoByStatus[status]]

  return <span className={`report-status ${semaforoClass}`}>{status}</span>
}

export default StatusBadge
