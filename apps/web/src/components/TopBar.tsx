import { Link } from 'react-router-dom'
import rietiLogo from '../assets/rieti-logo.png'
import { currentUser } from '../data/currentUser'

function TopBar() {
  return (
    <header className="topbar">
      <Link to="/" className="topbar-logo">
        <img src={rietiLogo} alt="Logo RIETI" />

        {/* Yo agrego el nombre porque la imagen solo contiene el símbolo. */}
        <span>RIETI</span>
      </Link>

      <nav className="topbar-nav">
        <Link to="/crear-reporte">Crear reporte</Link>
        <Link to="/seguimiento">Seguimiento</Link>
        <Link to="/casos-cerrados">Casos cerrados</Link>
      </nav>

      <Link to="/perfil" className="user-button">
        {currentUser.name}
      </Link>
    </header>
  )
}

export default TopBar
