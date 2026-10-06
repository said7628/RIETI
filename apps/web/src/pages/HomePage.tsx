import AdminHero from '../components/Home/AdminHero'
import Waves from '../components/Home/Waves'
import StatusCards from '../components/Home/StatusCards'
import RecentReports from '../components/Home/RecentReports'
import HeatMapSection from '../components/Home/HeatMapSection'
import StatisticsSection from '../components/Home/StatisticsSection'
import ReportDetail from '../components/shared/ReportDetail'
import { sortReports } from '../data/shared/reportUtils'
import { useReports, useSessionUser } from '../data/shared/session'

function HomePage() {
  // El análisis regional es exclusivo del administrador (RN-008).
  const user = useSessionUser()
  const reports = useReports()
  const isAdmin = user?.role === 'ADMINISTRADOR'
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

      {isAdmin && <HeatMapSection />}

      {isAdmin && <StatisticsSection />}
    </>
  )
}

export default HomePage
