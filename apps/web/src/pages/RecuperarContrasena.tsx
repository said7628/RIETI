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

function RecuperarContrasena() {
  const navigate = useNavigate()

  const [email, setEmail] =
    useState('')

  const [message, setMessage] =
    useState('')

  const [loading, setLoading] =
    useState(false)
  const [requested, setRequested] = useState(false)
  const [code, setCode] = useState('')

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

      // La API responde lo mismo si el correo no existe o está inactivo.
      // No avanzamos automáticamente a una pantalla de contraseña.
      setRequested(true)
      setMessage('Si existe una cuenta activa con ese correo, recibirás un código. Introdúcelo para continuar.')
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
            onChange={(event) => {
              setEmail(event.target.value)
              setRequested(false)
              setCode('')
              setMessage('')
            }}
          />
        </div>

        {message && (
          <p className="login-message">
            {message}
          </p>
        )}

        {requested && (
          <div className="form-group">
            <label htmlFor="recovery-code-before-reset">Código recibido por correo</label>
            <input id="recovery-code-before-reset" className="recovery-code-input"
              inputMode="numeric" autoComplete="one-time-code" maxLength={6}
              placeholder="000000" value={code}
              onChange={(event) => setCode(event.target.value.replace(/\D/g, ''))} />
            <button type="button" className="login-button" disabled={code.length !== 6}
              onClick={() => navigate('/recuperar/codigo', { state: { email: email.trim().toLowerCase(), code } })}>
              Continuar con el código
            </button>
          </div>
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
        >
          Volver a iniciar sesión
        </Link>
      </form>
    </AuthLayout>
  )
}

export default RecuperarContrasena
