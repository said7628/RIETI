import AdminHero from '../components/Home/AdminHero'
import Waves from '../components/Home/Waves'
import StatusCards from '../components/Home/StatusCards'
import RecentReports from '../components/Home/RecentReports'
import HeatMapSection from '../components/Home/HeatMapSection'
import StatisticsSection from '../components/Home/StatisticsSection'
import ReportDetail from '../components/shared/ReportDetail'
import { currentUser } from '../data/shared/currentUser'
import { reportsData } from '../data/shared/reportsData'
import { sortReports } from '../data/shared/reportUtils'

function HomePage() {
  // El análisis regional es exclusivo del administrador (RN-008).
  const isAdmin = currentUser.role === 'ADMINISTRADOR'
  const latestReport = sortReports(reportsData, 'recientes')[0]

  return (
    <>
      <AdminHero />

      <Waves />

      <StatusCards />

      <section className="reports-section">
        <div className="reports-container">
          <RecentReports />
          <ReportDetail report={latestReport} />
        </div>
      </section>

      {isAdmin && <HeatMapSection />}

      {isAdmin && <StatisticsSection />}
    </>
  )
}

export default HomePage
