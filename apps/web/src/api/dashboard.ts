import { apiRequest } from './api'

export interface DashboardSummary {
  totalReports: number
  attended: number
  attending: number
  unattended: number
  inactive: number

  attentionRate: number

  urgent: number
  urgentAttended: number
  urgentAttentionRate: number

  confirmedDuplicates: number

  minimumEstimatedChildren: number
  minimumChildrenAttended: number
  minimumChildrenAttentionRate: number

  reportsWithEvidence: number
  reportsWithoutEvidence: number
  evidenceCoverageRate: number

  closedReports: number
  averageResolutionHours: number | null
}

export interface MonthlyMetric {
  month: string
  reports: number
  attended: number
  attending: number
  urgent: number
  minimumEstimatedChildren: number
}

export interface RankingMetric {
  name: string
  total: number
}

export interface MunicipalityMetric {
  id: number
  name: string
  total: number
}

export interface DashboardResponse {
  ok: boolean

  summary: DashboardSummary

  monthly: MonthlyMetric[]

  bySex: Record<string, number>
  byAgeRange: Record<string, number>
  byRisk: Record<string, number>
  byPriority: Record<string, number>
  byStatus: Record<string, number>
  byTrafficLight: Record<string, number>
  byWorkType: Record<string, number>
  byOrigin: Record<string, number>
  byLocationSource: Record<string, number>

  estimatedChildrenBuckets: Record<string, number>

  topColonies: RankingMetric[]
  byMunicipality: MunicipalityMetric[]
}

export interface DashboardFilters {
  from?: string
  to?: string
  municipalityId?: number
}

export async function getDashboardSummary(
  filters: DashboardFilters = {},
) {
  const params = new URLSearchParams()

  for (const [key, value] of Object.entries(filters)) {
    if (
      value !== undefined &&
      value !== null &&
      String(value).trim() !== ''
    ) {
      params.set(key, String(value))
    }
  }

  const query = params.toString()

  return apiRequest<DashboardResponse>(
    `/dashboard/resumen${query ? `?${query}` : ''}`,
  )
}
