import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { logout, useSessionUser } from '../../data/shared/session'

/**
 * Menú de la persona con sesión.
 * Si no hay sesión, manda al login. Si hay sesión, permite cerrarla.
 */
function UserMenu() {
  const user = useSessionUser()
  const navigate = useNavigate()
  const [isOpen, setIsOpen] = useState(false)
  const menuRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    if (!isOpen) {
      return
    }

    function closeOnOutsideClick(event: PointerEvent) {
      if (!menuRef.current?.contains(event.target as Node)) {
        setIsOpen(false)
      }
    }

    function closeOnEscape(event: KeyboardEvent) {
      if (event.key === 'Escape') {
        setIsOpen(false)
      }
    }

    document.addEventListener('pointerdown', closeOnOutsideClick)
    document.addEventListener('keydown', closeOnEscape)

    return () => {
      document.removeEventListener('pointerdown', closeOnOutsideClick)
      document.removeEventListener('keydown', closeOnEscape)
    }
  }, [isOpen])

  if (!user) {
    return (
      <Link to="/login" className="user-button">
        Iniciar sesión
      </Link>
    )
  }

  /** Cierra la sesión y manda al login. */
  function handleLogout() {
    logout()
    setIsOpen(false)
    navigate('/login')
  }

  return (
    <div className="user-menu" ref={menuRef}>
      <button
        type="button"
        className="user-button"
        aria-expanded={isOpen}
        aria-haspopup="menu"
        onClick={() => setIsOpen((current) => !current)}
      >
        {user.name}
      </button>

      {isOpen && (
        <div className="user-menu-panel" role="menu">
          <button
            type="button"
            className="user-menu-item"
            role="menuitem"
            onClick={handleLogout}
          >
            Cerrar sesión
          </button>
        </div>
      )}
    </div>
  )
}

export default UserMenu
