import TopBar from './components/TopBar'
import AdminHero from './components/AdminHero'
import Waves from './components/Waves'
import StatusCards from './components/StatusCards'
import RecentReports from './components/RecentReports'
import ReportDetail from './components/ReportDetail'
import HeatMapSection from './components/HeatMapSection'
import StatisticsSection from './components/StatisticsSection'

function AppLalo() {
  return (
    <div className="app-lalo">
      <TopBar />

      <AdminHero />

      <Waves />

      <StatusCards />

      <section className="reports-section">
        <div className="reports-container">
          <RecentReports />
          <ReportDetail />
        </div>
      </section>

      <HeatMapSection />

      <StatisticsSection />
    </div>
  )
}

export default AppLalo