import AdminHero from '../components/AdminHero'
import Waves from '../components/Waves'
import StatusCards from '../components/StatusCards'
import RecentReports from '../components/RecentReports'
import ReportDetail from '../components/ReportDetail'
import HeatMapSection from '../components/HeatMapSection'
import StatisticsSection from '../components/StatisticsSection'
import { currentUser } from '../data/currentUser'

function DashboardPage() {
  // El análisis regional es exclusivo del administrador (RN-008).
  const isAdmin = currentUser.role === 'ADMINISTRADOR'

  return (
    <>
      <AdminHero />

      <Waves />

      <StatusCards />

      <section className="reports-section">
        <div className="reports-container">
          <RecentReports />
          <ReportDetail />
        </div>
      </section>

      {isAdmin && <HeatMapSection />}

      {isAdmin && <StatisticsSection />}
    </>
  )
}

export default DashboardPage
