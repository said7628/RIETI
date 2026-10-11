import AdminHero from '../components/Home/AdminHero'
import Waves from '../components/Home/Waves'
import StatusCards from '../components/Home/StatusCards'
import RecentReports from '../components/Home/RecentReports'
import HeatMapSection from '../components/Home/HeatMapSection'
import StatisticsSection from '../components/Home/StatisticsSection'
import ReportDetail from '../components/shared/ReportDetail'
import { sortReports } from '../data/shared/reportUtils'
import { useReports, useSessionUser } from '../data/shared/session'

/**
 * Pantalla de inicio.
 * Muestra el resumen y los reportes recientes.
 * Si hay sesión, también muestra el mapa y las estadísticas.
 */
function HomePage() {
  const user = useSessionUser()
  const reports = useReports()
  const latestReport = sortReports(reports, 'recientes')[0]

  return (
    <>
      <AdminHero />

      <Waves />

      <StatusCards />

      <section className="reports-section">
        <div className="reports-container">
          <RecentReports />
          {latestReport && <ReportDetail report={latestReport} />}
        </div>
      </section>

      {user && <HeatMapSection />}

      {user && <StatisticsSection />}
    </>
  )
}

export default HomePage


