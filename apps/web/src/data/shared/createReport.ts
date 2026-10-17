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

export function createFolio(now = new Date()) {
  const pad = (value: number) => String(value).padStart(2, '0')
  const stamp = `${String(now.getFullYear()).slice(2)}${pad(now.getMonth() + 1)}${pad(now.getDate())}-${pad(now.getHours())}${pad(now.getMinutes())}`
  const letter = String.fromCharCode(65 + Math.floor(Math.random() * 26))
  return `${stamp}${letter}`
}

function toNnaEstimate(value: string): NnaEstimate {
  const count = Number.parseInt(value, 10)

  if (count >= 5) return '5+'
  if (count === 4) return '4'
  if (count === 3) return '3'
  if (count === 2) return '2'
  return '1'
}

function toAgeRange(ages: string): AgeRange {
  const age = Number.parseInt(ages, 10)

  if (!Number.isFinite(age) || age <= 5) return '0-5'
  if (age <= 11) return '6-11'
  if (age <= 14) return '12-14'
  return '15-17'
}

/** Solo para el formulario local de demostracion. La API decide la prioridad real. */
function draftPriority(workType: string, children: string): 'MEDIA' | 'ALTA' | 'URGENTE' {
  const count = Number(children.trim())
  if (!Number.isInteger(count) || count < 1) throw new Error('Indica la cantidad exacta de menores')
  const name = workType.trim().normalize('NFD').replace(/[\u0300-\u036f]/g, '').toUpperCase()
  const isSexual = /EXPLOTACION SEXUAL|EXPLOTACION_SEXUAL/.test(name)
  const isHigh = /SEMAFOR|TORTILLER|CONSTRUCCION|GASOLIN|LIMPIAPARABRISAS|MENDICIDAD/.test(name)
  const isMedium = /VOLANTES|MERCADO|LAVADO DE AUTOS|EMPACADOR|CERILLO|\bOTRO\b/.test(name)
  if (!isSexual && !isHigh && !isMedium) throw new Error('Tipo de trabajo sin prioridad definida')
  if (isSexual || count >= 10) return 'URGENTE'
  if (isHigh || count >= 5) return 'ALTA'
  return 'MEDIA'
}

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
    colonia: draft.neighborhood.trim(),
    municipality: draft.municipality.trim(),
    address,
    status: 'Registrado',
    priority: draftPriority(draft.workType, draft.children),
    caseHolder: null,
    nnaEstimate: toNnaEstimate(draft.children),
    childrenCount: Number(draft.children.trim()),
    ageRange: toAgeRange(draft.ages),
    schedule: draft.schedule.trim() || '—',
    description: draft.details.trim(),
    solution: null,
  }
}
