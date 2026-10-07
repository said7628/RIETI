import {
  useState,
  type FormEvent,
} from 'react'

import {
  Link,
  Navigate,
  useLocation,
  useNavigate,
} from 'react-router-dom'

import AuthLayout from '../components/auth/AuthLayout'

import {
  requestPasswordRecovery,
  resetPasswordWithCode,
} from '../api/recovery'

interface RecoveryState {
  email?: string
}

function RestablecerContrasena() {
  const navigate = useNavigate()
  const location = useLocation()

  const recovery =
    (location.state as RecoveryState | null) ??
    {}

  const email = recovery.email ?? ''

  const [code, setCode] =
    useState('')

  const [password, setPassword] =
    useState('')

  const [
    confirmPassword,
    setConfirmPassword,
  ] = useState('')

  const [message, setMessage] =
    useState('')

  const [success, setSuccess] =
    useState('')

  const [loading, setLoading] =
    useState(false)

  const [resending, setResending] =
    useState(false)

  if (!email) {
    return (
      <Navigate
        to="/recuperar"
        replace
      />
    )
  }

  async function handleSubmit(
    event: FormEvent<HTMLFormElement>,
  ) {
    event.preventDefault()

    if (
      !code.trim() ||
      !password ||
      !confirmPassword
    ) {
      setMessage(
        'Completa todos los campos.',
      )
      return
    }

    if (!/^\d{6}$/.test(code.trim())) {
      setMessage(
        'El código debe tener 6 dígitos.',
      )
      return
    }

    if (password.length < 10) {
      setMessage(
        'La contraseña debe tener al menos 10 caracteres.',
      )
      return
    }

    if (password !== confirmPassword) {
      setMessage(
        'Las contraseñas no coinciden.',
      )
      return
    }

    try {
      setLoading(true)
      setMessage('')
      setSuccess('')

      await resetPasswordWithCode({
        email,
        code,
        password,
      })

      navigate(
        '/login',
        {
          replace: true,
          state: {
            notice:
              'Contraseña actualizada. Ya puedes iniciar sesión.',
          },
        },
      )
    } catch (error) {
      setMessage(
        error instanceof Error
          ? error.message
          : 'No fue posible cambiar la contraseña.',
      )
    } finally {
      setLoading(false)
    }
  }

  async function resendCode() {
    try {
      setResending(true)
      setMessage('')
      setSuccess('')

      await requestPasswordRecovery(
        email,
      )

      setSuccess(
        'Te enviamos un nuevo código.',
      )
    } catch (error) {
      setMessage(
        error instanceof Error
          ? error.message
          : 'No fue posible reenviar el código.',
      )
    } finally {
      setResending(false)
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
            Nueva contraseña
          </h1>

          <div className="title-line" />
        </div>

        <div className="recovery-sent-box">
          <strong>
            Revisa tu correo
          </strong>

          <span>
            Enviamos un código de 6 dígitos a
          </span>

          <span className="recovery-email">
            {email}
          </span>

          <small>
            El código vence en 15 minutos.
          </small>
        </div>

        <div className="form-group">
          <label htmlFor="recovery-code">
            Código de recuperación
          </label>

          <input
            id="recovery-code"
            className="recovery-code-input"
            inputMode="numeric"
            autoComplete="one-time-code"
            maxLength={6}
            placeholder="000000"
            value={code}
            disabled={loading}
            onChange={(event) =>
              setCode(
                event.target.value.replace(
                  /\D/g,
                  '',
                ),
              )
            }
          />
        </div>

        <div className="form-group">
          <label htmlFor="new-password">
            Nueva contraseña
          </label>

          <input
            id="new-password"
            type="password"
            autoComplete="new-password"
            value={password}
            disabled={loading}
            onChange={(event) =>
              setPassword(event.target.value)
            }
          />

          <span className="password-hint">
            Mínimo 10 caracteres.
          </span>
        </div>

        <div className="form-group">
          <label htmlFor="confirm-password">
            Confirmar contraseña
          </label>

          <input
            id="confirm-password"
            type="password"
            autoComplete="new-password"
            value={confirmPassword}
            disabled={loading}
            onChange={(event) =>
              setConfirmPassword(
                event.target.value,
              )
            }
          />
        </div>

        {message && (
          <p className="login-message">
            {message}
          </p>
        )}

        {success && (
          <p className="login-message login-message-success">
            {success}
          </p>
        )}

        <button
          type="submit"
          className="login-button"
          disabled={loading}
        >
          {loading
            ? 'Actualizando...'
            : 'Cambiar contraseña'}
        </button>

        <button
          type="button"
          className="recovery-resend-button"
          disabled={
            loading ||
            resending
          }
          onClick={resendCode}
        >
          {resending
            ? 'Reenviando...'
            : 'Reenviar código'}
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

export default RestablecerContrasena
