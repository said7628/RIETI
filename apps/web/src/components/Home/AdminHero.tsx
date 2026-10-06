import { useSessionUser } from '../../data/shared/session'

function AdminHero() {
  const user = useSessionUser()

  return (
    <section className="admin-hero">
      <div className="admin-hero-content">
        <h1>
          Bienvenido al panel de
          <br />
          administración de RIETI
        </h1>
        <hr className="linea"></hr>
        {user && <p>“{user.name}”</p>}
      </div>
    </section>
  )
}

export default AdminHero