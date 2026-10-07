import { Link, useSearchParams } from 'react-router-dom'
import rietiLogo from '../../assets/rieti-logo.png'
import { useSessionUser } from '../../data/shared/session'
import {
  parseTrackingView,
  trackingViews,
  type TrackingView,
} from '../../data/tracking/trackingViews'

interface SidebarProps {
  isOpen: boolean
  onToggle: () => void
}

function Sidebar({ isOpen, onToggle }: SidebarProps) {
  const [searchParams, setSearchParams] = useSearchParams()
  const user = useSessionUser()

  const activeView = parseTrackingView(searchParams.get('vista'))

  const isAdmin =
    user?.roleCode === 'ADMINISTRADOR_FUNDACION' ||
    user?.roleCode === 'ADMINISTRADOR_FUNDACION'

  function selectView(view: TrackingView) {
    setSearchParams({ vista: view })
  }

  return (
    <aside className={`sidebar ${isOpen ? 'sidebar-open' : ''}`}>
      <div className="sidebar-brand">
        {isOpen && (
          <Link to="/" className="sidebar-logo">
            <img src={rietiLogo} alt="Logo RIETI" />
            <span>RIETI</span>
          </Link>
        )}

        <button
          type="button"
          className="sidebar-toggle"
          onClick={onToggle}
          aria-expanded={isOpen}
          aria-label={isOpen ? 'Cerrar menú' : 'Abrir menú'}
        >
          {isOpen ? '◄☰' : '☰'}
        </button>
      </div>

      <nav className="sidebar-nav" inert={!isOpen}>
        {trackingViews.map((view) => (
          <button
            key={view.id}
            type="button"
            className={`sidebar-item ${
              view.id === activeView
                ? 'sidebar-item-active'
                : ''
            }`}
            onClick={() => selectView(view.id)}
          >
            {view.label}
          </button>
        ))}

        {isAdmin && (
          <>
            <div className="sidebar-divider" />

            <Link
              to="/cuentas"
              className="sidebar-item sidebar-link"
            >
              Crear cuenta
            </Link>

            <Link
              to="/cuentas?vista=administrar"
              className="sidebar-item sidebar-link"
            >
              Administrar cuentas
            </Link>
          </>
        )}
      </nav>
    </aside>
  )
}

export default Sidebar

