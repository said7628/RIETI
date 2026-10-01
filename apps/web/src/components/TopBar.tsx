import rietiLogo from '../assets/rieti-logo.png'

function TopBar() {
  return (
    <header className="topbar">
      <a href="/" className="topbar-logo">
        <img src={rietiLogo} alt="Logo RIETI" />

        {/* Yo agrego el nombre porque la imagen solo contiene el símbolo. */}
        <span>RIETI</span>
      </a>

      <nav className="topbar-nav">
        <a href="/crear-reporte">Crear reporte</a>
        <a href="/seguimiento">Seguimiento</a>
        <a href="/casos-cerrados">Casos cerrados</a>
      </nav>

      <a href="/perfil" className="user-button">
        Usuario
      </a>
    </header>
  )
}

export default TopBar