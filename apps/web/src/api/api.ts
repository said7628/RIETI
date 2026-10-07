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

  let response: Response

  try {
    response = await fetch(`${API_URL}${path}`, {
      ...options,
      headers,
    })
  } catch {
    throw new Error(
      'No fue posible conectar con el servidor. Verifica tu conexión e intenta nuevamente.',
    )
  }

  const raw = await response.text()

  let data: any = null

  if (raw) {
    try {
      data = JSON.parse(raw)
    } catch {
      data = null
    }
  }

  if (!response.ok) {
    if (response.status === 401) {
      setAccessToken(null)
    }

    if (data?.error?.message) {
      throw new Error(data.error.message)
    }

    if (data?.message) {
      throw new Error(data.message)
    }

    if (response.status >= 500) {
      throw new Error(
        `El servidor presentó un problema temporal (HTTP ${response.status}). Intenta nuevamente.`,
      )
    }

    throw new Error(
      `La solicitud no pudo completarse (HTTP ${response.status}).`,
    )
  }

  if (data === null) {
    throw new Error(
      `El servidor devolvió una respuesta inválida (HTTP ${response.status}).`,
    )
  }

  return data as T
}
