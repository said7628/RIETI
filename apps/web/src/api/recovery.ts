import { apiRequest } from './api'

export async function requestPasswordRecovery(
  email: string,
) {
  return apiRequest<{
    ok: boolean
    message: string
  }>(
    '/auth/recovery/request',
    {
      method: 'POST',
      credentials: 'omit',
      body: JSON.stringify({
        email: email.trim().toLowerCase(),
      }),
    },
  )
}

export async function resetPasswordWithCode(
  input: {
    email: string
    code: string
    password: string
  },
) {
  return apiRequest<{
    ok: boolean
  }>(
    '/auth/recovery/reset',
    {
      method: 'POST',
      credentials: 'omit',
      body: JSON.stringify({
        email: input.email.trim().toLowerCase(),
        code: input.code.trim(),
        password: input.password,
      }),
    },
  )
}
