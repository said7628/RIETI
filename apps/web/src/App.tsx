import { useEffect, useState } from 'react'
import {
  Navigate,
  Route,
  Routes,
  useLocation,
} from 'react-router-dom'

import { getAccessToken } from './api/api'
import Footer from './components/shared/Footer'
import TopBar from './components/shared/TopBar'
import Sidebar from './components/tracking/Sidebar'
import {
  refreshSession,
  useSessionUser,
} from './data/shared/session'

import AccountsPage from './pages/AccountsPage'
import HomePage from './pages/HomePage'
import Login from './pages/Login'
import PendingPage from './pages/PendingPage'
import RecuperarContrasena from './pages/RecuperarContrasena'
import RestablecerContrasena from './pages/RestablecerContrasena'
import TrackingPage from './pages/TrackingPage'

function App() {
  const location = useLocation()
  const user = useSessionUser()

  const [checkingSession, setCheckingSession] = useState(
    Boolean(getAccessToken()),
  )

  const [isSidebarOpen, setIsSidebarOpen] = useState(true)

  const isAuthPage =
    location.pathname === '/login' ||
    location.pathname === '/recuperar' ||
    location.pathname === '/recuperar/codigo'

  const isTracking = location.pathname === '/seguimiento'

  useEffect(() => {
    if (isAuthPage) {
      setCheckingSession(false)
      return
    }

    if (!getAccessToken()) {
      setCheckingSession(false)
      return
    }

    let active = true

    setCheckingSession(true)

    refreshSession().finally(() => {
      if (active) {
        setCheckingSession(false)
      }
    })

    return () => {
      active = false
    }
  }, [isAuthPage])

  useEffect(() => {
    if (isAuthPage || !user) return
    const update = () => {
      if (document.visibilityState === 'visible') {
        void refreshSession()
      }
    }
    const interval = window.setInterval(update, 4 * 60 * 1000)
    document.addEventListener('visibilitychange', update)
    window.addEventListener('focus', update)
    return () => {
      window.clearInterval(interval)
      document.removeEventListener('visibilitychange', update)
      window.removeEventListener('focus', update)
    }
  }, [isAuthPage, user?.id])

  if (isAuthPage) {
    return (
      <Routes>
        <Route
          path="/login"
          element={<Login />}
        />

        <Route
          path="/recuperar"
          element={<RecuperarContrasena />}
        />

        <Route
          path="/recuperar/codigo"
          element={<RestablecerContrasena />}
        />

        <Route
          path="*"
          element={<Navigate to="/login" replace />}
        />
      </Routes>
    )
  }

  if (checkingSession) {
    return (
      <main
        style={{
          minHeight: '100vh',
          display: 'grid',
          placeItems: 'center',
          background: '#000',
          color: '#fff',
        }}
      >
        Verificando sesión...
      </main>
    )
  }

  if (!user || !getAccessToken()) {
    return (
      <Navigate
        to="/login"
        replace
        state={{ from: location.pathname }}
      />
    )
  }

  return (
    <div
      className={`app${isTracking ? ' app-tracking' : ''}${
        isTracking && isSidebarOpen ? ' sidebar-open' : ''
      }`}
    >
      {isTracking && (
        <Sidebar
          isOpen={isSidebarOpen}
          onToggle={() =>
            setIsSidebarOpen((current) => !current)
          }
        />
      )}

      <div className="app-column">
        <TopBar showLogo={!isTracking} />

        <Routes>
          <Route
            path="/"
            element={<HomePage />}
          />

          <Route
            path="/seguimiento"
            element={<TrackingPage />}
          />

          <Route
            path="/cuentas"
            element={<AccountsPage />}
          />

          <Route
            path="/perfil"
            element={<PendingPage title="Perfil" />}
          />

          <Route
            path="/crear-reporte"
            element={<Navigate to="/" replace />}
          />

          <Route
            path="/reporte-enviado"
            element={<Navigate to="/" replace />}
          />

          <Route
            path="*"
            element={<Navigate to="/" replace />}
          />
        </Routes>
      </div>

      {isTracking && <Footer />}
    </div>
  )
}

export default App