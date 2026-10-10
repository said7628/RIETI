interface PendingPageProps {
  title: string
}

/** Pantalla de una sección que todavía no está lista. Perfil usa esta página. */
function PendingPage({ title }: PendingPageProps) {
  return (
    <main className="pending-page">
      <h1>{title}</h1>
      <p>Esta sección está en construcción.</p>
    </main>
  )
}

export default PendingPage
