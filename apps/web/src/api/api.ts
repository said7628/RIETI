const API_URL = import.meta.env.VITE_API_URL

if (!API_URL) {
  throw new Error('VITE_API_URL no está configurada')
}

let accessToken = sessionStorage.getItem('rieti_access_token')

export function setAccessToken(token: string | null) {
  accessToken = token

  if (token) {
    sessionStorage.setItem('rieti_access_token', token)
  } else {
    sessionStorage.removeItem('rieti_access_token')
  }
}

export function getAccessToken() {
  return accessToken
}

export async function apiRequest<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const headers = new Headers(options.headers)

  if (!headers.has('content-type') && options.body) {
    headers.set('content-type', 'application/json')
  }

  if (accessToken) {
    headers.set('authorization', `Bearer ${accessToken}`)
  }

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
  })

  const data = await response.json().catch(() => null)

  if (!response.ok) {
    if (response.status === 401) {
      setAccessToken(null)
    }

    throw new Error(
      data?.error?.message ??
        data?.message ??
        `Error HTTP ${response.status}`,
    )
  }

  return data as T
}