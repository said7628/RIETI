import {
  useEffect,
  useRef,
  useState,
  type ReactNode,
} from 'react'

import L from 'leaflet'

import type { Report } from '../../data/shared/reportsData'

import {
  getEvidenceUrl,
  getReportEvidences,
  type ReportEvidence,
} from '../../api/reports'

interface ReportDetailProps {
  report: Report
  actions?: ReactNode
}

interface EvidenceWithUrl extends ReportEvidence {
  url: string
}

function ReportLocationMap({
  latitude,
  longitude,
}: {
  latitude?: number
  longitude?: number
}) {
  const elementRef = useRef<HTMLDivElement | null>(null)
  const mapRef = useRef<L.Map | null>(null)

  useEffect(() => {
    if (
      !elementRef.current ||
      mapRef.current ||
      latitude == null ||
      longitude == null ||
      !Number.isFinite(latitude) ||
      !Number.isFinite(longitude)
    ) {
      return
    }

    const map = L.map(elementRef.current, {
      zoomControl: true,
      attributionControl: true,
      scrollWheelZoom: false,
    }).setView(
      [latitude, longitude],
      16,
    )

    L.tileLayer(
      'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
      {
        maxZoom: 19,
        attribution:
          '&copy; OpenStreetMap contributors',
      },
    ).addTo(map)

    L.circleMarker(
      [latitude, longitude],
      {
        radius: 8,
        weight: 3,
        fillOpacity: 0.9,
      },
    ).addTo(map)

    mapRef.current = map

    window.setTimeout(() => {
      map.invalidateSize()
    }, 100)

    return () => {
      map.remove()
      mapRef.current = null
    }
  }, [latitude, longitude])

  if (
    latitude == null ||
    longitude == null ||
    !Number.isFinite(latitude) ||
    !Number.isFinite(longitude)
  ) {
    return (
      <div className="report-map-empty">
        Ubicación no disponible
      </div>
    )
  }

  return (
    <div
      ref={elementRef}
      className="report-location-map"
    />
  )
}

function ReportDetail({
  report,
  actions,
}: ReportDetailProps) {
  const [evidences, setEvidences] =
    useState<EvidenceWithUrl[]>([])

  const [loadingEvidences, setLoadingEvidences] =
    useState(false)

  const [evidenceError, setEvidenceError] =
    useState<string | null>(null)

  const [selectedImage, setSelectedImage] =
    useState<string | null>(null)

  useEffect(() => {
    let cancelled = false

    async function loadEvidences() {
      try {
        setLoadingEvidences(true)
        setEvidenceError(null)
        setEvidences([])

        const response =
          await getReportEvidences(report.folio)

        const loaded = await Promise.all(
          response.evidences.map(
            async (evidence) => {
              const signed =
                await getEvidenceUrl(evidence.id)

              return {
                ...evidence,
                url: signed.url,
              }
            },
          ),
        )

        if (!cancelled) {
          setEvidences(loaded)
        }
      } catch (error) {
        if (!cancelled) {
          setEvidenceError(
            error instanceof Error
              ? error.message
              : 'No fue posible cargar las evidencias',
          )
        }
      } finally {
        if (!cancelled) {
          setLoadingEvidences(false)
        }
      }
    }

    void loadEvidences()

    return () => {
      cancelled = true
    }
  }, [report.folio])

  return (
    <aside className="report-detail-card">
      <h3>
        DETALLES DEL REPORTE: {report.folio}
      </h3>

      <div className="report-map-container">
        <ReportLocationMap
          latitude={report.latitude}
          longitude={report.longitude}
        />
      </div>

      <div className="report-information">
        <p>
          <strong>Dirección:</strong>{' '}
          {report.address || 'Sin referencia'}
        </p>

        <div className="report-information-grid">
          <p>
            <strong>NNA estimados:</strong>
            <br />
            {report.nnaEstimate}
          </p>

          <p>
            <strong>Rango de edad:</strong>
            <br />
            {report.ageRange} años
          </p>

          <p>
            <strong>Tipo de trabajo:</strong>
            <br />
            {report.workType}
          </p>

          <p>
            <strong>Horario de trabajo:</strong>
            <br />
            {report.schedule}
          </p>
        </div>

        <p className="case-holder">
          <strong>Titular del caso:</strong>{' '}
          {report.caseHolder ?? 'Sin asignar'}
        </p>

        <div className="report-description">
          <strong>Detalles del reportante:</strong>
          <p>{report.description}</p>
        </div>

        {report.solution && (
          <p>
            <strong>Solución:</strong>{' '}
            {report.solution}
          </p>
        )}
      </div>

      {actions && (
        <div className="report-actions">
          {actions}
        </div>
      )}

      <h4>EVIDENCIA FOTOGRÁFICA:</h4>

      {loadingEvidences && (
        <div className="evidence-empty">
          Cargando evidencias...
        </div>
      )}

      {!loadingEvidences &&
        evidenceError && (
          <div className="evidence-empty evidence-error">
            {evidenceError}
          </div>
        )}

      {!loadingEvidences &&
        !evidenceError &&
        evidences.length === 0 && (
          <div className="evidence-empty">
            Sin evidencia fotográfica
          </div>
        )}

      {!loadingEvidences &&
        !evidenceError &&
        evidences.length > 0 && (
          <div className="evidence-container">
            {evidences.map((evidence) => (
              <button
                key={evidence.id}
                type="button"
                className="evidence-image-button"
                onClick={() =>
                  setSelectedImage(evidence.url)
                }
              >
                <img
                  src={evidence.url}
                  alt={`Evidencia ${evidence.id}`}
                  className="evidence-image"
                />
              </button>
            ))}
          </div>
        )}

      {selectedImage && (
        <div
          className="evidence-modal"
          role="dialog"
          aria-modal="true"
          onClick={() =>
            setSelectedImage(null)
          }
        >
          <div
            className="evidence-modal-content"
            onClick={(event) =>
              event.stopPropagation()
            }
          >
            <button
              type="button"
              className="evidence-modal-close"
              onClick={() =>
                setSelectedImage(null)
              }
            >
              ×
            </button>

            <img
              src={selectedImage}
              alt="Evidencia ampliada"
              className="evidence-modal-image"
            />
          </div>
        </div>
      )}
    </aside>
  )
}

export default ReportDetail
