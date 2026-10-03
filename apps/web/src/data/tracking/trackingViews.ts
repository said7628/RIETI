import { semaforoByStatus } from '../shared/catalogs'
import { currentUser } from '../shared/currentUser'
import type { Report } from '../shared/reportsData'

export type TrackingView =
  | 'mis-reportes'
  | 'todos'
  | 'sin-asignar'
  | 'en-atencion'
  | 'cerrados'

export const trackingViews: { id: TrackingView; label: string }[] = [
  { id: 'mis-reportes', label: 'Mis reportes' },
  { id: 'todos', label: 'Todos los reportes' },
  { id: 'sin-asignar', label: 'Reportes sin asignar' },
  { id: 'en-atencion', label: 'En atención' },
  { id: 'cerrados', label: 'Casos cerrados' },
]

export function parseTrackingView(value: string | null): TrackingView {
  const view = trackingViews.find((item) => item.id === value)
  return view ? view.id : 'mis-reportes'
}

export function matchesView(report: Report, view: TrackingView) {
  const semaforo = semaforoByStatus[report.status]

  switch (view) {
    case 'mis-reportes':
      return report.caseHolder === currentUser.name
    case 'sin-asignar':
      return semaforo === 'SIN_ATENDER'
    case 'en-atencion':
      return semaforo === 'ATENDIENDO'
    case 'cerrados':
      return semaforo === 'ATENDIDO'
    case 'todos':
      return true
  }
}
