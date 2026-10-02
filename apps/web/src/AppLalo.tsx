import { Route, Routes } from 'react-router-dom'
import TopBar from './components/TopBar'
import DashboardPage from './pages/DashboardPage'
import PendingPage from './pages/PendingPage'

function AppLalo() {
  return (
    <div className="app-lalo">
      <TopBar />

      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/crear-reporte" element={<PendingPage title="Crear reporte" />} />
        <Route path="/seguimiento" element={<PendingPage title="Seguimiento" />} />
        <Route path="/casos-cerrados" element={<PendingPage title="Casos cerrados" />} />
        <Route path="/perfil" element={<PendingPage title="Perfil" />} />
        <Route path="*" element={<PendingPage title="Página no encontrada" />} />
      </Routes>
    </div>
  )
}

export default AppLalo
