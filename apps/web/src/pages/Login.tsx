import { useState } from 'react'
import type { FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import AuthLayout from '../components/auth/AuthLayout'
import { login } from '../data/shared/session'

/**
 * Pantalla de login.
 * Si el correo y la contraseña son correctos, abre la sesión y va al inicio.
 */
function Login() {
  const navigate = useNavigate()
  const location = useLocation()

  /** El aviso llega desde recuperar contraseña, por ejemplo al cambiarla. */
  const notice =
    (location.state as { notice?: string } | null)?.notice ?? ''

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [message, setMessage] = useState(notice)
  const [loading, setLoading] = useState(false)

  /** Envía el correo y la contraseña para entrar. */
  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!email.trim() || !password) {
      setMessage('Escribe tu correo y tu contraseña.')
      return
    }

    setLoading(true)
    setMessage('')

    const error = await login(email, password)

    setLoading(false)

    if (error) {
      setMessage(error)
      return
    }

    navigate('/', { replace: true })
  }

  return (
    <AuthLayout>
      <form className="login-form" onSubmit={handleSubmit}>
        <div className="login-title">
          <h1>Iniciar sesión</h1>
          <div className="title-line"></div>
        </div>

        <div className="form-group">
          <label htmlFor="email">
            Correo electrónico
          </label>

          <input
            id="email"
            type="email"
            autoComplete="username"
            value={email}
            disabled={loading}
            onChange={(event) =>
              setEmail(event.target.value)
            }
          />
        </div>

        <div className="form-group password-group">
          <label htmlFor="password">
            Contraseña
          </label>

          <input
            id="password"
            type="password"
            autoComplete="current-password"
            value={password}
            disabled={loading}
            onChange={(event) =>
              setPassword(event.target.value)
            }
          />
        </div>

        <Link
          to="/recuperar"
          className="forgot-button"
        >
          Olvidé mi contraseña
        </Link>

        {message && (
          <p
            className={
              message.startsWith('Contraseña actualizada')
                ? 'login-message login-message-success'
                : 'login-message'
            }
          >
            {message}
          </p>
        )}

        <button
          type="submit"
          className="login-button"
          disabled={loading}
        >
          {loading
            ? 'Iniciando sesión...'
            : 'Iniciar sesión'}
        </button>
      </form>
    </AuthLayout>
  )
}

export default Login