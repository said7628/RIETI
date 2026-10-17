import { priorities } from './catalogs'
import type { Report } from './reportsData'

export type SortOption = 'recientes' | 'antiguos' | 'prioridad'

export const sortOptions: { id: SortOption; label: string }[] = [
  { id: 'recientes', label: 'Más recientes' },
  { id: 'antiguos', label: 'Más antiguos' },
  { id: 'prioridad', label: 'Prioridad' },
]

export function sortReports(reports: Report[], sort: SortOption) {
  return [...reports].sort((a, b) => {
    if (sort === 'prioridad') {
      const priorityDifference =
        priorities.indexOf(a.priority) - priorities.indexOf(b.priority)

      if (priorityDifference !== 0) {
        return priorityDifference
      }
    }

    // Las fechas ISO se pueden comparar como texto.
    const dateDifference = a.createdAt.localeCompare(b.createdAt)
    return sort === 'antiguos' ? dateDifference : -dateDifference
  })
}

export function formatDate(isoDate: string) {
  return new Date(isoDate).toLocaleDateString('es-MX', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  })
}

/** Etiqueta comun con el catalogo mostrado en Android. No cambia IDs en la base. */
export function workTypeLabel(value: string) {
  return /tortiller[ií]a/i.test(value) ? 'Despachador/trabajador de tortillería' : value
}
