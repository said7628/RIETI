import { semaforoByStatus } from '../shared/catalogs'
import type { Report } from '../shared/reportsData'

export type TrackingView =
  | 'mis-reportes'
  | 'todos'
  | 'sin-asignar'
  | 'en-atencion'
  | 'cerrados'
  | 'cancelados'

export const trackingViews: { id: TrackingView; label: string }[] = [
  { id: 'mis-reportes', label: 'Mis reportes' },
  { id: 'todos', label: 'Todos los reportes' },
  { id: 'sin-asignar', label: 'Reportes sin asignar' },
  { id: 'en-atencion', label: 'En atención' },
  { id: 'cerrados', label: 'Casos cerrados' },
  { id: 'cancelados', label: 'Cancelados' },
]

export function parseTrackingView(value: string | null): TrackingView {
  const view = trackingViews.find((item) => item.id === value)
  return view ? view.id : 'mis-reportes'
}

export function matchesView(
  report: Report,
  view: TrackingView,
  userName: string | null,
) {
  const semaforo = semaforoByStatus[report.status]

  switch (view) {
    case 'mis-reportes':
      return userName !== null && report.caseHolder === userName
    case 'sin-asignar':
      return semaforo === 'SIN_ATENDER' && report.caseHolder === null
    case 'en-atencion':
      return semaforo === 'ATENDIENDO'
    case 'cerrados':
      return semaforo === 'ATENDIDO'
    case 'cancelados':
      return report.status === 'Cancelado'
    case 'todos':
      return true
  }
}
