import { apiRequest } from './api'

export interface ApiUser {
  id: number
  uuid: string
  name: string
  email: string
  phone: string | null
  active: boolean
  municipalityId: number | null
  dependencyId: number | null
  roleCode: string
  roleName: string
  isAdmin: boolean
}

interface UsersResponse {
  ok: boolean
  users: ApiUser[]
}

export async function getUsers() {
  const response = await apiRequest<UsersResponse>('/usuarios')
  return response.users
}

export async function createUser(input: {
  name: string
  email: string
  password: string
  roleId: number
  municipalityId?: number | null
  dependencyId?: number | null
  phone?: string | null
}) {
  return apiRequest<{ ok: boolean }>('/usuarios', {
    method: 'POST',
    body: JSON.stringify(input),
  })
}

export async function updateUser(
  id: number,
  input: {
    name?: string
    roleId?: number
    municipalityId?: number | null
    dependencyId?: number | null
    phone?: string | null
    password?: string
    active?: boolean
  },
) {
  return apiRequest<{ ok: boolean }>(`/usuarios/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(input),
  })
}

export async function deactivateUser(id: number) {
  return apiRequest<{ ok: boolean }>(`/usuarios/${id}`, {
    method: 'DELETE',
  })
}