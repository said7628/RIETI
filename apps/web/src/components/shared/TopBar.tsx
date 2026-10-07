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
      {showLogo && (
        <Link to="/" className="topbar-logo">
          <img src={rietiLogo} alt="Logo RIETI" />

          {/* Yo agrego el nombre porque la imagen solo contiene el sÃ­mbolo. */}
          <span>RIETI</span>
        </Link>
      )}

      <nav className="topbar-nav">
        <NavLink to="/crear-reporte">Crear reporte</NavLink>
        <NavLink to="/seguimiento">Seguimiento</NavLink>
        {user?.roleCode === 'ADMINISTRADOR_FUNDACION' && (
          <NavLink to="/cuentas">Cuentas</NavLink>
        )}
      </nav>

      <UserMenu />
    </header>
  )
}

export default TopBar



