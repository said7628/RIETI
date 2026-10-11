import {
  useState,
  type FormEvent,
} from 'react'

import {
  Link,
  useNavigate,
} from 'react-router-dom'

import AuthLayout from '../components/auth/AuthLayout'
import { requestPasswordRecovery } from '../api/recovery'

/**
 * Pantalla para pedir el código de recuperar contraseña.
 * Si el correo es válido, pasa a /recuperar/codigo.
 */
function RecuperarContrasena() {
  const navigate = useNavigate()

  const [email, setEmail] =
    useState('')

  const [message, setMessage] =
    useState('')

  const [loading, setLoading] =
    useState(false)

  /** Pide el código y, si sale bien, pasa a /recuperar/codigo con el correo. */
  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault()

    const normalized =
      email.trim().toLowerCase()

    if (!normalized) {
      setMessage(
        'Escribe el correo de tu cuenta.',
      )
      return
    }

    try {
      setLoading(true)
      setMessage('')

      await requestPasswordRecovery(
        normalized,
      )

      navigate(
        '/recuperar/codigo',
        {
          state: {
            email: normalized,
          },
        },
      )
    } catch (error) {
      setMessage(
        error instanceof Error
          ? error.message
          : 'No fue posible enviar el código.',
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthLayout>
      <form
        className="login-form recovery-form"
        onSubmit={handleSubmit}
      >
        <div className="login-title">
          <h1>
            Recuperar contraseña
          </h1>

          <div className="title-line" />
        </div>

        <p className="recovery-description">
          Escribe el correo asociado a tu
          cuenta. Te enviaremos un código
          temporal para cambiar tu contraseña.
        </p>

        <div className="form-group">
          <label htmlFor="recovery-email">
            Correo electrónico
          </label>

          <input
            id="recovery-email"
            type="email"
            autoComplete="email"
            placeholder="correo@ejemplo.com"
            value={email}
            disabled={loading}
            onChange={(event) =>
              setEmail(event.target.value)
            }
          />
        </div>

        {message && (
          <p className="login-message">
            {message}
          </p>
        )}

        <button
          type="submit"
          className="login-button"
          disabled={loading}
        >
          {loading
            ? 'Enviando código...'
            : 'Enviar código'}
        </button>

        <Link
          to="/login"
          className="login-back-button"
          aria-disabled={false}
        >
          Volver a iniciar sesión
        </Link>
      </form>
    </AuthLayout>
  )
}

export default RecuperarContrasena
