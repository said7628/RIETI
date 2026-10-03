import { Link, NavLink } from 'react-router-dom'
import rietiLogo from '../../assets/rieti-logo.png'
import { currentUser } from '../../data/shared/currentUser'

interface TopBarProps {
  showLogo?: boolean
}

function TopBar({ showLogo = true }: TopBarProps) {
  return (
    <header className={`topbar ${showLogo ? '' : 'topbar-compact'}`}>
      {showLogo && (
        <Link to="/" className="topbar-logo">
          <img src={rietiLogo} alt="Logo RIETI" />

          {/* Yo agrego el nombre porque la imagen solo contiene el símbolo. */}
          <span>RIETI</span>
        </Link>
      )}

      <nav className="topbar-nav">
        <NavLink to="/crear-reporte">Crear reporte</NavLink>
        <NavLink to="/seguimiento">Seguimiento</NavLink>
      </nav>

      <NavLink to="/perfil" className="user-button">
        {currentUser.name}
      </NavLink>
    </header>
  )
}

export default TopBar
