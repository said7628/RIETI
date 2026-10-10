import type { AgeRange, NnaEstimate } from './catalogs'
import type { Report } from './reportsData'

export interface ReportDraft {
  children: string
  ages: string
  workType: string
  schedule: string
  details: string
  address: string
  municipality: string
  neighborhood: string
  postalCode: string
}

export const emptyReportDraft: ReportDraft = {
  children: '',
  ages: '',
  workType: '',
  schedule: '',
  details: '',
  address: '',
  municipality: '',
  neighborhood: '',
  postalCode: '',
}

/** Arma el folio de un reporte nuevo. */
export function createFolio(now = new Date()) {
  const pad = (value: number) => String(value).padStart(2, '0')
  const stamp = `${String(now.getFullYear()).slice(2)}${pad(now.getMonth() + 1)}${pad(now.getDate())}-${pad(now.getHours())}${pad(now.getMinutes())}`
  const letter = String.fromCharCode(65 + Math.floor(Math.random() * 26))
  return `${stamp}${letter}`
}

/** Pasa el número de niños al valor que usa el reporte: 1, 2, 3, 4 o 5+. */
function toNnaEstimate(value: string): NnaEstimate {
  const count = Number.parseInt(value, 10)

  if (count >= 5) return '5+'
  if (count === 4) return '4'
  if (count === 3) return '3'
  if (count === 2) return '2'
  return '1'
}

/** Pasa la edad a un rango: 0-5, 6-11, 12-14 o 15-17. */
function toAgeRange(ages: string): AgeRange {
  const age = Number.parseInt(ages, 10)

  if (!Number.isFinite(age) || age <= 5) return '0-5'
  if (age <= 11) return '6-11'
  if (age <= 14) return '12-14'
  return '15-17'
}

/** Convierte el formulario en un reporte. */
export function draftToReport(draft: ReportDraft, folio: string): Report {
  const address = [
    draft.address.trim(),
    draft.neighborhood.trim(),
    draft.municipality.trim(),
    draft.postalCode.trim(),
  ]
    .filter(Boolean)
    .join(', ')

  return {
    folio,
    createdAt: new Date().toISOString(),
    workType: draft.workType.trim(),
    colonia: draft.neighborhood.trim() || draft.municipality.trim() || 'Sin colonia',
    address,
    status: 'Registrado',
    priority: 'MEDIA',
    caseHolder: null,
    nnaEstimate: toNnaEstimate(draft.children),
    ageRange: toAgeRange(draft.ages),
    schedule: draft.schedule.trim() || '—',
    description: draft.details.trim(),
    solution: null,
  }
}
