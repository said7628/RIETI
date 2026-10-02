interface PendingPageProps {
  title: string
}

function PendingPage({ title }: PendingPageProps) {
  return (
    <main className="pending-page">
      <h1>{title}</h1>
      <p>Esta sección está en construcción.</p>
    </main>
  )
}

export default PendingPage
