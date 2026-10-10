import type {
  AgeRange,
  NnaEstimate,
  Priority,
  ReportStatus,
} from './catalogs'

/** Datos de un reporte: folio, estatus, prioridad, colonia y titular del caso. */
export interface Report {
  id?: number
  uuid?: string
  folio: string
  createdAt: string
  updatedAt?: string
  workType: string
  colonia: string
  municipality?: string
  address: string
  postalCode?: string
  status: ReportStatus
  statusCode?: string
  priority: Priority
  caseHolder: string | null
  assignedUserId?: number | null
  assignedDependency?: string | null
  nnaEstimate: NnaEstimate
  ageRange: AgeRange
  schedule: string
  description: string
  solution: string | null
  latitude?: number
  longitude?: number
  risk?: string
  modality?: string
  origin?: string
}
