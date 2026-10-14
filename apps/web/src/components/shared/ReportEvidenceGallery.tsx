import { useEffect, useState } from 'react'
import { getEvidenceUrl, getReportEvidences, type ReportEvidence } from '../../api/reports'

type ImageEvidence = ReportEvidence & { url: string }

/** Imágenes reales del reporte mediante URLs temporales de S3. Nunca incluye datos ficticios. */
export default function ReportEvidenceGallery({ folio }: { folio: string }) {
  const [items, setItems] = useState<ImageEvidence[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [openImage, setOpenImage] = useState<string | null>(null)

  useEffect(() => {
    let active = true
    async function load() {
      try {
        const response = await getReportEvidences(folio)
        const settled = await Promise.allSettled(
          response.evidences.map(async (evidence) => ({
            ...evidence,
            url: (await getEvidenceUrl(evidence.id)).url,
          })),
        )
        if (!active) return
        const available = settled
          .filter((result): result is PromiseFulfilledResult<ImageEvidence> => result.status === 'fulfilled')
          .map((result) => result.value)
        setItems(available)
        if (settled.some((result) => result.status === 'rejected')) {
          setError('Algunas fotografías no pudieron cargarse. Vuelve a abrir los detalles para reintentar.')
        }
      } catch (cause) {
        if (active) setError(cause instanceof Error ? cause.message : 'No fue posible cargar las evidencias.')
      } finally {
        if (active) setLoading(false)
      }
    }
    void load()
    return () => { active = false }
  }, [folio])

  useEffect(() => {
    if (!openImage) return
    const handleKey = (event: KeyboardEvent) => {
      if (event.key === 'Escape') setOpenImage(null)
    }
    window.addEventListener('keydown', handleKey)
    return () => window.removeEventListener('keydown', handleKey)
  }, [openImage])

  if (loading) return <p role="status">Cargando fotografías...</p>
  return (
    <>
      {error && <p role="alert" className="evidence-error">{error}</p>}
      {!items.length && !error && <p>Este reporte no tiene fotografías adjuntas.</p>}
      {items.length > 0 && (
        <div className="evidence-container">
          {items.map((evidence, index) => (
            <button
              className="evidence-image-button"
              key={evidence.id}
              type="button"
              onClick={() => setOpenImage(evidence.url)}
              aria-label={`Ampliar fotografía ${index + 1}`}
            >
              <img loading="lazy" className="evidence-image" src={evidence.url} alt={`Fotografía de evidencia ${index + 1}`} />
            </button>
          ))}
        </div>
      )}
      {openImage && (
        <div className="evidence-modal" role="dialog" aria-modal="true" aria-label="Fotografía ampliada">
          <div className="evidence-modal-content">
            <button className="evidence-modal-close" type="button" onClick={() => setOpenImage(null)} aria-label="Cerrar fotografía">×</button>
            <img src={openImage} className="evidence-modal-image" alt="Evidencia ampliada" />
          </div>
        </div>
      )}
    </>
  )
}
