import AdminHero from '../components/home/AdminHero'
import Waves from '../components/home/Waves'
import StatusCards from '../components/home/StatusCards'
import RecentReports from '../components/home/RecentReports'
import HeatMapSection from '../components/home/HeatMapSection'
import StatisticsSection from '../components/home/StatisticsSection'
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
