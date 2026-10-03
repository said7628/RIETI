import { currentUser } from '../../data/shared/currentUser'

function AdminHero() {
  return (
    <section className="admin-hero">
      <div className="admin-hero-content">
        <h1>
          Bienvenido al panel de
          <br />
          administración de RIETI
        </h1>
        <hr className="linea"></hr>
        <p>“{currentUser.name}”</p>
      </div>
    </section>
  )
}

export default AdminHero