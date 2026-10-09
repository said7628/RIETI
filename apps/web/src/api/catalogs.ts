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

export interface DependencyCatalog {
  id: number
  nombre: string
  municipio_id?: number | null
}

export interface MunicipalityCatalog {
  id: number
  clave: string
  nombre: string
  activo?: number
}

interface CatalogsResponse {
  ok: boolean
  catalogs: {
    roles: RoleCatalog[]
    municipios: MunicipalityCatalog[]
    dependencias: DependencyCatalog[]
  }
}

async function getCatalogs() {
  return apiRequest<CatalogsResponse>(
    '/catalogos',
  )
}

export async function getRoles() {
  const response = await getCatalogs()

  return response.catalogs.roles.filter(
    (role) =>
      role.activo === 1 &&
      role.acceso_web === 1,
  )
}

export async function getMunicipalities() {
  const response = await getCatalogs()

  return response.catalogs.municipios
    .filter(
      (municipality) =>
        municipality.activo === undefined ||
        municipality.activo === 1,
    )
    .sort((a, b) =>
      a.nombre.localeCompare(
        b.nombre,
        'es',
      ),
    )
}


export async function getDependencies() {
  const response = await getCatalogs()
  return response.catalogs.dependencias ?? []
}
