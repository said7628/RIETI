import {
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react'

import L from 'leaflet'
import 'leaflet.heat'

import heatmapIcon from '../../assets/heatmap.png'

import {
  getMapPoints,
  type MapPoint,
} from '../../api/map'

function riskWeight(risk: string) {
  switch (risk) {
    case 'CRITICO':
      return 1

    case 'ALTO':
      return 0.85

    case 'MEDIO':
      return 0.6

    case 'BAJO':
      return 0.35

    default:
      return 0.45
  }
}

function prettyCode(value: string) {
  return value
    .replaceAll('_', ' ')
    .toLowerCase()
    .replace(/\b\w/g, (letter) => letter.toUpperCase())
}

function HeatMapSection() {
  const mapElementRef = useRef<HTMLDivElement | null>(null)
  const mapRef = useRef<L.Map | null>(null)

  const heatLayerRef = useRef<L.Layer | null>(null)
  const markersLayerRef = useRef<L.LayerGroup | null>(null)

  const [points, setPoints] = useState<MapPoint[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [risk, setRisk] = useState('')
  const [priority, setPriority] = useState('')
  const [status, setStatus] = useState('')

  useEffect(() => {
    let cancelled = false

    async function load() {
      try {
        setLoading(true)
        setError(null)

        const result = await getMapPoints({
          risk: risk || undefined,
          priority: priority || undefined,
          status: status || undefined,
        })

        if (!cancelled) {
          setPoints(result.points)
        }
      } catch (loadError) {
        if (!cancelled) {
          setPoints([])

          setError(
            loadError instanceof Error
              ? loadError.message
              : 'No fue posible cargar el mapa',
          )
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    void load()

    return () => {
      cancelled = true
    }
  }, [risk, priority, status])

  useEffect(() => {
    if (!mapElementRef.current || mapRef.current) {
      return
    }

    const map = L.map(mapElementRef.current, {
      zoomControl: false,
      attributionControl: true,
    }).setView(
      [19.5576, -99.2655],
      14,
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

    const markers = L.layerGroup().addTo(map)

    mapRef.current = map
    markersLayerRef.current = markers

    window.setTimeout(() => {
      map.invalidateSize()
    }, 100)

    return () => {
      map.remove()

      mapRef.current = null
      heatLayerRef.current = null
      markersLayerRef.current = null
    }
  }, [])

  useEffect(() => {
    const map = mapRef.current
    const markers = markersLayerRef.current

    if (!map || !markers) {
      return
    }

    if (heatLayerRef.current) {
      map.removeLayer(heatLayerRef.current)
      heatLayerRef.current = null
    }

    markers.clearLayers()

    if (points.length === 0) {
      return
    }

    const heatPoints = points.map(
      (point) =>
        [
          point.lat,
          point.lng,
          riskWeight(point.risk),
        ] as [number, number, number],
    )

    const heatLayer = L.heatLayer(
      heatPoints,
      {
        radius: 55,
        blur: 42,
        maxZoom: 17,
        minOpacity: 0.30,
        max: 1,
        gradient: {
          0.15: '#22c55e',
          0.35: '#84cc16',
          0.55: '#facc15',
          0.75: '#f97316',
          1.0: '#ef4444',
        },
      },
    )

    heatLayer.addTo(map)
    heatLayerRef.current = heatLayer

    for (const point of points) {
      const marker = L.circleMarker(
        [point.lat, point.lng],
        {
          radius: 4,
          weight: 1,
          opacity: 0.65,
          fillOpacity: 0.45,
        },
      )

      marker.bindPopup(`
        <div class="rieti-map-popup">
          <strong>Reporte ${point.reportId}</strong>

          <div>
            Riesgo:
            <b>${prettyCode(point.risk)}</b>
          </div>

          <div>
            Prioridad:
            <b>${prettyCode(point.priority)}</b>
          </div>

          <div>
            Estado:
            <b>${prettyCode(point.status)}</b>
          </div>

          <div>
            NNA estimados:
            <b>${prettyCode(point.estimatedChildren)}</b>
          </div>

          <div>
            Rango de edad:
            <b>${prettyCode(point.ageRange)}</b>
          </div>

          <div>
            Tipo:
            <b>${prettyCode(point.workType)}</b>
          </div>
        </div>
      `)

      marker.addTo(markers)
    }

    const bounds = L.latLngBounds(
      points.map(
        (point) =>
          [point.lat, point.lng] as [number, number],
      ),
    )

    if (bounds.isValid()) {
      map.fitBounds(bounds, {
        padding: [45, 45],
        maxZoom: 16,
      })
    }

    window.setTimeout(() => {
      map.invalidateSize()
    }, 50)
  }, [points])

  const counts = useMemo(() => {
    const result = {
      total: points.length,
      critical: 0,
      high: 0,
      medium: 0,
      low: 0,
    }

    for (const point of points) {
      switch (point.risk) {
        case 'CRITICO':
          result.critical += 1
          break

        case 'ALTO':
          result.high += 1
          break

        case 'MEDIO':
          result.medium += 1
          break

        case 'BAJO':
          result.low += 1
          break
      }
    }

    return result
  }, [points])

  function clearFilters() {
    setRisk('')
    setPriority('')
    setStatus('')
  }

  return (
    <section className="heatmap-section">
      <div className="section-label">
        <img src={heatmapIcon} alt="" />
        <span>Mapa de calor</span>
      </div>

      <div className="heatmap-toolbar">
        <div className="heatmap-filter">
          <label htmlFor="heatmap-risk">
            Riesgo
          </label>

          <select
            id="heatmap-risk"
            value={risk}
            onChange={(event) =>
              setRisk(event.target.value)
            }
          >
            <option value="">Todos</option>
            <option value="BAJO">Bajo</option>
            <option value="MEDIO">Medio</option>
            <option value="ALTO">Alto</option>
            <option value="CRITICO">Crítico</option>
          </select>
        </div>

        <div className="heatmap-filter">
          <label htmlFor="heatmap-priority">
            Prioridad
          </label>

          <select
            id="heatmap-priority"
            value={priority}
            onChange={(event) =>
              setPriority(event.target.value)
            }
          >
            <option value="">Todas</option>
            <option value="MEDIA">Media</option>
            <option value="ALTA">Alta</option>
            <option value="URGENTE">Urgente</option>
          </select>
        </div>

        <div className="heatmap-filter">
          <label htmlFor="heatmap-status">
            Estado
          </label>

          <select
            id="heatmap-status"
            value={status}
            onChange={(event) =>
              setStatus(event.target.value)
            }
          >
            <option value="">Todos</option>
            <option value="REGISTRADO">Registrado</option>
            <option value="EN_REVISION">En revisión</option>
            <option value="EN_SEGUIMIENTO">En seguimiento</option>
            <option value="CANALIZADO">Canalizado</option>
            <option value="CONCLUIDO">Concluido</option>
            <option value="ARCHIVADO">Archivado</option>
            <option value="CANCELADO">Cancelado</option>
            <option value="REINCIDENTE">Reincidente</option>
          </select>
        </div>

        <button
          type="button"
          className="heatmap-clear-button"
          onClick={clearFilters}
        >
          Limpiar filtros
        </button>
      </div>

      <div className="heatmap-summary">
        <span>
          <strong>{counts.total}</strong>
          {' '}reportes visibles
        </span>

        <span>
          Crítico: <strong>{counts.critical}</strong>
        </span>

        <span>
          Alto: <strong>{counts.high}</strong>
        </span>

        <span>
          Medio: <strong>{counts.medium}</strong>
        </span>

        <span>
          Bajo: <strong>{counts.low}</strong>
        </span>
      </div>

      <div className="heatmap-map-wrapper">
        <div
          ref={mapElementRef}
          className="heatmap-map"
        />

        {loading && (
          <div className="heatmap-overlay">
            Cargando información territorial...
          </div>
        )}

        {!loading && error && (
          <div className="heatmap-overlay heatmap-error">
            {error}
          </div>
        )}

        {!loading &&
          !error &&
          points.length === 0 && (
            <div className="heatmap-overlay">
              No hay reportes para los filtros seleccionados.
            </div>
          )}
      </div>
    </section>
  )
}

export default HeatMapSection

