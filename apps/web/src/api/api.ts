const API_URL = import.meta.env.VITE_API_URL

if (!API_URL) {
  throw new Error('VITE_API_URL no está configurada')
}

let accessToken = sessionStorage.getItem('rieti_access_token')

export class ApiError extends Error {
  status: number
  code: string | null

  constructor(
    message: string,
    status: number,
    code: string | null = null,
  ) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
  }
}

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
    throw new ApiError(
      'No fue posible conectar con el servidor. Verifica tu conexión e intenta nuevamente.',
      0,
      'NETWORK_ERROR',
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
    const code =
      typeof data?.error?.code === 'string'
        ? data.error.code
        : null

    const serverMessage =
      typeof data?.error?.message === 'string'
        ? data.error.message
        : typeof data?.message === 'string'
          ? data.message
          : null

    if (response.status >= 500) {
      throw new ApiError(
        serverMessage ??
          `El servidor presentó un problema temporal (HTTP ${response.status}). Intenta nuevamente.`,
        response.status,
        code,
      )
    }

    throw new ApiError(
      serverMessage ??
        `La solicitud no pudo completarse (HTTP ${response.status}).`,
      response.status,
      code,
    )
  }

  if (data === null) {
    throw new ApiError(
      `El servidor devolvió una respuesta inválida (HTTP ${response.status}).`,
      response.status,
      'INVALID_RESPONSE',
    )
  }

  return data as T
}
