import { useState } from 'react'
import { Route, Routes, useLocation } from 'react-router-dom'
import Footer from './components/shared/Footer'
import TopBar from './components/shared/TopBar'
import Sidebar from './components/tracking/Sidebar'
import AccountsPage from './pages/AccountsPage'
import HomePage from './pages/HomePage'
import PendingPage from './pages/PendingPage'
import TrackingPage from './pages/TrackingPage'
import CrearReporte from './pages/CrearReporte'
import Login from './pages/Login'
import RecuperarContrasena from './pages/RecuperarContrasena'
import ReporteEnviado from './pages/ReporteEnviado'
import RestablecerContrasena from './pages/RestablecerContrasena'

function App() {
  const location = useLocation()
  const isTracking = location.pathname === '/seguimiento'
  const isAuthPage =
    location.pathname === '/login' ||
    location.pathname === '/recuperar' ||
    location.pathname === '/recuperar/codigo'
  const isStandalonePage =
    isAuthPage ||
    location.pathname === '/crear-reporte' ||
    location.pathname === '/reporte-enviado'
  const [isSidebarOpen, setIsSidebarOpen] = useState(true)

  if (isStandalonePage) {
    return (
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/recuperar" element={<RecuperarContrasena />} />
        <Route path="/recuperar/codigo" element={<RestablecerContrasena />} />
        <Route path="/crear-reporte" element={<CrearReporte />} />
        <Route path="/reporte-enviado" element={<ReporteEnviado />} />
      </Routes>
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
          onToggle={() => setIsSidebarOpen(!isSidebarOpen)}
        />
      )}

      <div className="app-column">
        <TopBar showLogo={!isTracking} />

        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/seguimiento" element={<TrackingPage />} />
          <Route path="/cuentas" element={<AccountsPage />} />
          <Route path="/perfil" element={<PendingPage title="Perfil" />} />
          <Route path="*" element={<PendingPage title="Página no encontrada" />} />
        </Routes>
      </div>

      {isTracking && <Footer />}
    </div>
  )
}

export default App
