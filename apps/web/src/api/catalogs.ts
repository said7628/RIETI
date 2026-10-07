import { apiRequest } from './api'

export interface RoleCatalog {
  id: number
  codigo: string
  nombre: string
  acceso_web: number
  acceso_android: number
  es_administrador: number
  activo: number
}

interface CatalogsResponse {
  ok: boolean
  catalogs: {
    roles: RoleCatalog[]
  }
}

export async function getRoles() {
  const response = await apiRequest<CatalogsResponse>('/catalogos')

  return response.catalogs.roles.filter(
    (role) => role.activo === 1 && role.acceso_web === 1,
  )
}