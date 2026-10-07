import { apiRequest } from './api'

export interface MapPoint {
  reportId: number
  municipalityId: number
  colonyId: number | null

  lat: number
  lng: number

  estimatedChildren: string
  ageRange: string
  sex: string | null
  workType: string

  risk: string
  priority: string
  status: string
  trafficLight: string

  confirmedDuplicate: boolean

  observedAt: string | null
  createdAt: string
}

interface MapResponse {
  ok: boolean
  count: number
  points: MapPoint[]
}

export interface MapFilters {
  municipalityId?: number
  colonyId?: number

  risk?: string
  priority?: string
  status?: string
  trafficLight?: string
  workType?: string
  ageRange?: string

  from?: string
  to?: string
}

export async function getMapPoints(
  filters: MapFilters = {},
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

  const response = await apiRequest<MapResponse>(
    `/reportes/mapa${query ? `?${query}` : ''}`,
  )

  return response
}
