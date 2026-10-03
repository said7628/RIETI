import { useState } from 'react'
import { Route, Routes, useLocation } from 'react-router-dom'
import Footer from './components/shared/Footer'
import TopBar from './components/shared/TopBar'
import Sidebar from './components/tracking/Sidebar'
import HomePage from './pages/HomePage'
import PendingPage from './pages/PendingPage'
import TrackingPage from './pages/TrackingPage'

function AppLalo() {
  const location = useLocation()
  const isTracking = location.pathname === '/seguimiento'
  const [isSidebarOpen, setIsSidebarOpen] = useState(true)

  return (
    <div
      className={`app-lalo${isTracking ? ' app-tracking' : ''}${
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
          <Route path="/crear-reporte" element={<PendingPage title="Crear reporte" />} />
          <Route path="/seguimiento" element={<TrackingPage />} />
          <Route path="/perfil" element={<PendingPage title="Perfil" />} />
          <Route path="*" element={<PendingPage title="Página no encontrada" />} />
        </Routes>
      </div>

      {isTracking && <Footer />}
    </div>
  )
}

export default AppLalo
