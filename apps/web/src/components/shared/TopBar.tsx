import { Link, NavLink } from 'react-router-dom'
import rietiLogo from '../../assets/rieti-logo.png'
import { useSessionUser } from '../../data/shared/session'
import UserMenu from './UserMenu'

interface TopBarProps {
  showLogo?: boolean
}

function TopBar({ showLogo = true }: TopBarProps) {
  const user = useSessionUser()

  return (
    <header className={`topbar ${showLogo ? '' : 'topbar-compact'}`}>
      <div className="topbar-brand-slot">
        {showLogo && (
          <Link to="/" className="topbar-logo">
            <img src={rietiLogo} alt="Logo RIETI" />
            <span>RIETI</span>
          </Link>
        )}
      </div>

      <nav className="topbar-nav">
        <NavLink to="/seguimiento">Seguimiento</NavLink>
        {user?.roleCode === 'ADMINISTRADOR_FUNDACION' && (
          <NavLink to="/cuentas">Cuentas</NavLink>
        )}
      </nav>

      <div className="topbar-user-slot">
        <UserMenu />
      </div>
    </header>
  )
}

export default TopBar
