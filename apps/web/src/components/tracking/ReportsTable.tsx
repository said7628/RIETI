import type { Report } from '../../data/shared/reportsData'
import { formatDate, workTypeLabel } from '../../data/shared/reportUtils'
import StatusBadge from '../shared/StatusBadge'

interface ReportsTableProps {
  reports: Report[]
  selectedFolio: string | undefined
  onSelect: (folio: string) => void
}

function ReportsTable({ reports, selectedFolio, onSelect }: ReportsTableProps) {
  if (reports.length === 0) {
    return (
      <p className="tracking-empty">
        No se encontraron reportes con los criterios seleccionados.
      </p>
    )
  }

  return (
    <table className="tracking-table">
      <thead>
        <tr>
          <th>Folio</th>
          <th>Fecha</th>
          <th>Tipo de trabajo</th>
          <th>Municipio</th>
          <th>Colonia</th>
          <th>Fase actual</th>
          <th>Prioridad</th>
          <th>Titular del caso</th>
        </tr>
      </thead>

      <tbody>
        {reports.map((report) => (
          <tr
            key={report.folio}
            className={report.folio === selectedFolio ? 'tracking-row-selected' : ''}
            onClick={() => onSelect(report.folio)}
            onKeyDown={(event) => {
              if (event.key === 'Enter') onSelect(report.folio)
            }}
            tabIndex={0}
          >
            <td>{report.folio}</td>
            <td>{formatDate(report.createdAt)}</td>
            <td>{workTypeLabel(report.workType)}</td>
            <td>{report.municipality || '—'}</td>
            <td>{report.colonia || 'No especificada'}</td>
            <td>
              <StatusBadge status={report.status} />
            </td>
            <td className={report.priority === 'URGENTE' ? 'priority-urgent' : ''}>
              {report.priority}
            </td>
            <td>{report.assignedUserInactive ? 'Cuenta desactivada — reasignar' : report.caseHolder ?? '—'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  )
}

export default ReportsTable
