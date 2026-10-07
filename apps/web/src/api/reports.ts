import { apiRequest } from './api'
import type { Report } from '../data/shared/reportsData'

interface ReportsResponse {
  ok: boolean
  reports: Report[]
}

export async function getReports() {
  const response = await apiRequest<ReportsResponse>('/reportes')
  return response.reports
}

export async function getReport(folio: string) {
  return apiRequest<{ ok: boolean; report: Report }>(
    `/reportes/${encodeURIComponent(folio)}`,
  )
}

export async function updateReport(
  folio: string,
  input: {
    status?: string
    priority?: string
    solution?: string | null
  },
) {
  return apiRequest<{ ok: boolean }>(
    `/reportes/${encodeURIComponent(folio)}`,
    {
      method: 'PATCH',
      body: JSON.stringify(input),
    },
  )
}

export async function assignReport(
  folio: string,
  input: {
    userId?: number
    dependencyId?: number
    reason?: string
  },
) {
  return apiRequest<{ ok: boolean }>(
    `/reportes/${encodeURIComponent(folio)}/asignaciones`,
    {
      method: 'POST',
      body: JSON.stringify(input),
    },
  )
}

export async function addReportNote(
  folio: string,
  note: string,
) {
  return apiRequest<{ ok: boolean }>(
    `/reportes/${encodeURIComponent(folio)}/notas`,
    {
      method: 'POST',
      body: JSON.stringify({ note }),
    },
  )
}
export interface ReportEvidence {
  id: number
  uuid: string
  mimeType: string
  sizeBytes: number
  createdAt: string
}

export async function getReportEvidences(
  folio: string,
) {
  return apiRequest<{
    ok: boolean
    evidences: ReportEvidence[]
  }>(
    `/reportes/${encodeURIComponent(folio)}/evidencias`,
  )
}

export async function getEvidenceUrl(
  evidenceId: number,
) {
  return apiRequest<{
    ok: boolean
    url: string
    expiresIn: number
  }>(
    `/evidencias/${evidenceId}/url`,
  )
}
