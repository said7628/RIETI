import { workTypeLabel } from '../../data/shared/reportUtils'
import {
  useEffect,
  useRef,
  useState,
  type ReactNode,
} from 'react'

import L from 'leaflet'

import type { Report } from '../../data/shared/reportsData'
import ReportAgeBreakdown, { descriptionWithoutAgeHeader } from './ReportAgeBreakdown'

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

export function ReportLocationMap({
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
      zoomControl: false,
      attributionControl: true,
      scrollWheelZoom: false,
    }).setView(
      [latitude, longitude],
      16,
    )

    L.control.zoom({
      position: 'bottomright',
    }).addTo(map)

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
    <div className="report-map-frame">
      <div ref={elementRef} className="report-location-map" />
      <button type="button" className="rieti-map-recenter"
        aria-label="Centrar mapa en la ubicación del reporte"
        title="Centrar mapa en la ubicación del reporte"
        onClick={() => {
          const map = mapRef.current
          if (map) {
            map.flyTo([latitude, longitude], 16)
            map.invalidateSize()
          }
        }}>
        <span aria-hidden="true">⌖</span><span>Centrar</span>
      </button>
    </div>
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
            <strong>Municipio:</strong>
            <br />
            {report.municipality || 'No especificado'}
          </p>
          <p>
            <strong>Colonia:</strong>
            <br />
            {report.colonia || 'No especificada'}
          </p>
          <p>
            <strong>NNA estimados:</strong>
            <br />
            {report.childrenCount ?? report.nnaEstimate}
          </p>

          <p>
            <strong>Edades aproximadas:</strong>
            <br />
            <ReportAgeBreakdown report={report} />
          </p>

          <p>
            <strong>Tipo de trabajo:</strong>
            <br />
            {workTypeLabel(report.workType)}
          </p>

          <p>
            <strong>Horario de trabajo:</strong>
            <br />
            {report.schedule}
          </p>
        </div>

        <p className="case-holder">
          <strong>Titular del caso:</strong>{' '}
          {report.assignedUserInactive ? 'Cuenta desactivada — requiere reasignación' : report.caseHolder ?? 'Sin asignar'}
        </p>

        <div className="report-description">
          <strong>Detalles del reportante:</strong>
          <p>{descriptionWithoutAgeHeader(report.description) || "Sin descripción adicional"}</p>
        </div>

        {report.solution && (
          <div className="report-solution">
            <strong>Solución:</strong>
            <p>{report.solution}</p>
          </div>
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
