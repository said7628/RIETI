import {
  useEffect,
  useMemo,
  useState,
} from 'react'

import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  Line,
  LineChart,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'

import graphIcon from '../../assets/graph-icon.png'

import {
  getDashboardSummary,
  type DashboardResponse,
} from '../../api/dashboard'

const chartColors = [
  '#1fc5b4',
  '#d10073',
  '#ff8aca',
  '#95e3ea',
  '#ffb300',
  '#7bc043',
  '#7d5fff',
  '#ff5c5c',
]

/** Pasa códigos como CRITICO a un texto para la gráfica. */
function labelCode(value: string) {
  const labels: Record<string, string> = {
    MASCULINO: 'Masculino',
    FEMENINO: 'Femenino',
    NO_ESPECIFICADO: 'No especificado',

    '0_5': '0-5',
    '6_11': '6-11',
    '12_14': '12-14',
    '15_17': '15-17',

    BAJO: 'Bajo',
    MEDIO: 'Medio',
    ALTO: 'Alto',
    CRITICO: 'Crítico',

    MEDIA: 'Media',
    ALTA: 'Alta',
    URGENTE: 'Urgente',

    REGISTRADO: 'Registrado',
    EN_REVISION: 'En revisión',
    EN_SEGUIMIENTO: 'En seguimiento',
    CANALIZADO: 'Canalizado',
    CONCLUIDO: 'Concluido',
    ARCHIVADO: 'Archivado',
    CANCELADO: 'Cancelado',
    REINCIDENTE: 'Reincidente',

    SIN_ATENDER: 'Sin atender',
    ATENDIENDO: 'Atendiendo',
    ATENDIDO: 'Atendido',
    INACTIVO: 'Inactivo',

    WEB: 'Web',
    ANDROID: 'Android',
    IMPORTACION: 'Importación',

    GPS: 'GPS',
    MAPA: 'Mapa',
  }

  return (
    labels[value] ??
    value
      .replaceAll('_', ' ')
      .toLowerCase()
      .replace(/\b\w/g, (letter) =>
        letter.toUpperCase(),
      )
  )
}

/** Convierte un conteo del servidor en datos para una gráfica. */
function objectToChart(
  value: Record<string, number>,
) {
  return Object.entries(value).map(
    ([name, total]) => ({
      name: labelCode(name),
      total,
    }),
  )
}

/** Pasa 2026-03 a Mar 2026. */
function formatMonth(value: string) {
  const [year, month] = value.split('-')

  const monthNames = [
    'Ene',
    'Feb',
    'Mar',
    'Abr',
    'May',
    'Jun',
    'Jul',
    'Ago',
    'Sep',
    'Oct',
    'Nov',
    'Dic',
  ]

  const index = Number(month) - 1

  return `${
    monthNames[index] ?? month
  } ${year}`
}

/** Muestra las horas de atención. Si pasa de un día, las muestra en días. */
function formatHours(hours: number | null) {
  if (hours == null) {
    return '—'
  }

  if (hours < 24) {
    return `${hours.toFixed(1)} h`
  }

  return `${(hours / 24).toFixed(1)} días`
}

/** Tarjeta con un número del inicio, por ejemplo reportes atendidos. */
function MetricCard({
  title,
  value,
  detail,
}: {
  title: string
  value: string | number
  detail?: string
}) {
  return (
    <article className="analytics-metric-card">
      <span className="analytics-metric-title">
        {title}
      </span>

      <strong className="analytics-metric-value">
        {value}
      </strong>

      {detail && (
        <span className="analytics-metric-detail">
          {detail}
        </span>
      )}
    </article>
  )
}

/** Recuadro de una gráfica del inicio. */
function ChartCard({
  title,
  subtitle,
  children,
  wide = false,
}: {
  title: string
  subtitle?: string
  children: React.ReactNode
  wide?: boolean
}) {
  return (
    <article
      className={
        wide
          ? 'analytics-chart-card analytics-chart-wide'
          : 'analytics-chart-card'
      }
    >
      <div className="analytics-chart-heading">
        <h3>{title}</h3>

        {subtitle && (
          <p>{subtitle}</p>
        )}
      </div>

      <div className="analytics-chart-body">
        {children}
      </div>
    </article>
  )
}

/** Gráficas y números del inicio. Se pueden filtrar por fechas. */
function StatisticsSection() {
  const [data, setData] =
    useState<DashboardResponse | null>(null)

  const [loading, setLoading] =
    useState(true)

  const [error, setError] =
    useState<string | null>(null)

  const [from, setFrom] =
    useState('')

  const [to, setTo] =
    useState('')

  const [appliedFrom, setAppliedFrom] =
    useState('')

  const [appliedTo, setAppliedTo] =
    useState('')

  /** Carga las estadísticas cuando se aplica un periodo. */
  useEffect(() => {
    let cancelled = false

    async function load() {
      try {
        setLoading(true)
        setError(null)

        const response =
          await getDashboardSummary({
            from:
              appliedFrom || undefined,

            to:
              appliedTo || undefined,
          })

        if (!cancelled) {
          setData(response)
        }
      } catch (loadError) {
        if (!cancelled) {
          setError(
            loadError instanceof Error
              ? loadError.message
              : 'No fue posible cargar las estadísticas',
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
  }, [appliedFrom, appliedTo])

  const monthly = useMemo(
    () =>
      (data?.monthly ?? []).map(
        (item) => ({
          ...item,
          label: formatMonth(item.month),
        }),
      ),
    [data],
  )

  const ageData = useMemo(
    () =>
      objectToChart(
        data?.byAgeRange ?? {},
      ),
    [data],
  )

  const riskData = useMemo(
    () =>
      objectToChart(
        data?.byRisk ?? {},
      ),
    [data],
  )

  const priorityData = useMemo(
    () =>
      objectToChart(
        data?.byPriority ?? {},
      ),
    [data],
  )

  const workTypeData = useMemo(
    () =>
      objectToChart(
        data?.byWorkType ?? {},
      ),
    [data],
  )

  const trafficData = useMemo(
    () =>
      objectToChart(
        data?.byTrafficLight ?? {},
      ),
    [data],
  )

  /** Aplica las fechas solo si la inicial no es posterior a la final. */
  function applyFilters() {
    if (from && to && from > to) return
    setAppliedFrom(from)
    setAppliedTo(to)
  }

  /** Quita el periodo y vuelve a mostrar todas las fechas. */
  function clearFilters() {
    setFrom('')
    setTo('')
    setAppliedFrom('')
    setAppliedTo('')
  }

  const summary = data?.summary

  return (
    <section className="statistics-section">
      <div className="statistics-content">
        <div className="section-label">
          <img src={graphIcon} alt="" />
          <span>Estadísticas</span>
        </div>

        <h2 className="statistics-title">
          Inteligencia e impacto de RIETI
        </h2>

        <p className="statistics-intro">
          Indicadores construidos a partir de
          los reportes registrados en el sistema.
        </p>

        <div className="analytics-filters">
          <div className="analytics-filter-header">
            <div>
              <strong>Periodo de análisis</strong>
              <span>
                Filtra todas las métricas y gráficas por fecha de registro.
              </span>
            </div>
          </div>

          <div className="analytics-filter-controls">
            <div className="analytics-date-field">
              <label htmlFor="stats-from">
                Desde
              </label>

              <input
                id="stats-from"
                type="date"
                value={from}
                max={to || undefined}
                onChange={(event) => {
                  const value = event.target.value
                  setFrom(value)
                  if (to && value && value > to) setTo(value)
                }}
              />
            </div>

            <div className="analytics-date-field">
              <label htmlFor="stats-to">
                Hasta
              </label>

              <input
                id="stats-to"
                type="date"
                value={to}
                min={from || undefined}
                onChange={(event) => {
                  const value = event.target.value
                  if (!from || !value || value >= from) setTo(value)
                }}
              />
            </div>

            <div className="analytics-filter-actions">
              <button
                type="button"
                className="analytics-filter-apply"
                onClick={applyFilters}
              >
                Aplicar periodo
              </button>

              <button
                type="button"
                className="analytics-filter-secondary"
                onClick={clearFilters}
              >
                Todo el historial
              </button>
            </div>
          </div>
        </div>

        {loading && (
          <div className="analytics-state">
            Calculando estadísticas...
          </div>
        )}

        {!loading && error && (
          <div className="analytics-state analytics-state-error">
            {error}
          </div>
        )}

        {!loading && !error && summary && data && (
          <>
            <div className="analytics-metrics-grid">
              <MetricCard
                title="Reportes"
                value={summary.totalReports}
                detail="Casos registrados"
              />

              <MetricCard
                title="NNA mínimos estimados"
                value={
                  summary.minimumEstimatedChildren
                }
                detail="5+ se cuenta como mínimo 5"
              />

              <MetricCard
                title="Casos atendidos"
                value={summary.attended}
                detail={`${summary.attentionRate}% del total`}
              />

              <MetricCard
                title="En atención"
                value={summary.attending}
                detail="Casos actualmente activos"
              />

              <MetricCard
                title="Sin atender"
                value={summary.unattended}
                detail="Pendientes de intervención"
              />

              <MetricCard
                title="Urgentes"
                value={summary.urgent}
                detail={`${summary.urgentAttentionRate}% atendidos`}
              />

              <MetricCard
                title="NNA atendidos"
                value={
                  summary.minimumChildrenAttended
                }
                detail={`${summary.minimumChildrenAttentionRate}% del mínimo estimado`}
              />

              <MetricCard
                title="Resolución promedio"
                value={formatHours(
                  summary.averageResolutionHours,
                )}
                detail={`${summary.closedReports} casos cerrados`}
              />

              <MetricCard
                title="Con evidencia"
                value={
                  summary.reportsWithEvidence
                }
                detail={`${summary.evidenceCoverageRate}% de cobertura`}
              />

              <MetricCard
                title="Duplicidades confirmadas"
                value={
                  summary.confirmedDuplicates
                }
                detail="Posibles reincidencias relacionadas"
              />
            </div>

            <div className="analytics-charts-grid">

              <ChartCard
                title="Evolución mensual"
                subtitle="Reportes registrados y casos atendidos"
                wide
              >
                <ResponsiveContainer
                  width="100%"
                  height="100%"
                >
                  <LineChart data={monthly}>
                    <CartesianGrid
                      strokeDasharray="3 3"
                    />

                    <XAxis
                      dataKey="label"
                    />

                    <YAxis
                      allowDecimals={false}
                    />

                    <Tooltip />

                    <Legend />

                    <Line
                      type="monotone"
                      dataKey="reports"
                      name="Reportes"
                      stroke="#d10073"
                      strokeWidth={3}
                    />

                    <Line
                      type="monotone"
                      dataKey="attended"
                      name="Atendidos"
                      stroke="#1fc5b4"
                      strokeWidth={3}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </ChartCard>


              <ChartCard
                title="Rangos de edad"
                subtitle="Número de reportes por grupo"
              >
                <ResponsiveContainer
                  width="100%"
                  height="100%"
                >
                  <BarChart data={ageData}>
                    <CartesianGrid
                      strokeDasharray="3 3"
                    />

                    <XAxis dataKey="name" />
                    <YAxis allowDecimals={false} />
                    <Tooltip />

                    <Bar
                      dataKey="total"
                      name="Reportes"
                      fill="#1fc5b4"
                      radius={[7, 7, 0, 0]}
                    />
                  </BarChart>
                </ResponsiveContainer>
              </ChartCard>


              <ChartCard
                title="Nivel de riesgo"
                subtitle="Distribución territorial de riesgo"
              >
                <ResponsiveContainer
                  width="100%"
                  height="100%"
                >
                  <BarChart data={riskData}>
                    <CartesianGrid
                      strokeDasharray="3 3"
                    />

                    <XAxis dataKey="name" />
                    <YAxis allowDecimals={false} />
                    <Tooltip />

                    <Bar
                      dataKey="total"
                      name="Reportes"
                      fill="#d10073"
                      radius={[7, 7, 0, 0]}
                    />
                  </BarChart>
                </ResponsiveContainer>
              </ChartCard>


              <ChartCard
                title="Prioridad"
                subtitle="Nivel de intervención requerido"
              >
                <ResponsiveContainer
                  width="100%"
                  height="100%"
                >
                  <PieChart>
                    <Pie
                      data={priorityData}
                      dataKey="total"
                      nameKey="name"
                      outerRadius={90}
                    >
                      {priorityData.map(
                        (_, index) => (
                          <Cell
                            key={index}
                            fill={
                              chartColors[
                                (index + 2) %
                                  chartColors.length
                              ]
                            }
                          />
                        ),
                      )}
                    </Pie>

                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </ChartCard>


              <ChartCard
                title="Estado de atención"
                subtitle="Semáforo operacional"
              >
                <ResponsiveContainer
                  width="100%"
                  height="100%"
                >
                  <BarChart data={trafficData}>
                    <CartesianGrid
                      strokeDasharray="3 3"
                    />

                    <XAxis dataKey="name" />
                    <YAxis allowDecimals={false} />
                    <Tooltip />

                    <Bar
                      dataKey="total"
                      name="Reportes"
                      fill="#95e3ea"
                      radius={[7, 7, 0, 0]}
                    />
                  </BarChart>
                </ResponsiveContainer>
              </ChartCard>


              <ChartCard
                title="Tipos de trabajo infantil"
                subtitle="Actividades observadas con mayor frecuencia"
              >
                <ResponsiveContainer
                  width="100%"
                  height="100%"
                >
                  <BarChart
                    data={workTypeData}
                    layout="vertical"
                    margin={{
                      left: 40,
                      right: 30,
                    }}
                  >
                    <CartesianGrid
                      strokeDasharray="3 3"
                    />

                    <XAxis
                      type="number"
                      allowDecimals={false}
                    />

                    <YAxis
                      type="category"
                      dataKey="name"
                      width={170}
                    />

                    <Tooltip />

                    <Bar
                      dataKey="total"
                      name="Reportes"
                      fill="#ff8aca"
                      radius={[0, 7, 7, 0]}
                    />
                  </BarChart>
                </ResponsiveContainer>
              </ChartCard>


              <ChartCard
                title="Colonias con más reportes"
                subtitle="Principales zonas registradas"
                wide
              >
                <ResponsiveContainer
                  width="100%"
                  height="100%"
                >
                  <BarChart
                    data={data.topColonies}
                    layout="vertical"
                    margin={{
                      left: 35,
                      right: 30,
                    }}
                  >
                    <CartesianGrid
                      strokeDasharray="3 3"
                    />

                    <XAxis
                      type="number"
                      allowDecimals={false}
                    />

                    <YAxis
                      type="category"
                      dataKey="name"
                      width={170}
                    />

                    <Tooltip />

                    <Bar
                      dataKey="total"
                      name="Reportes"
                      fill="#1fc5b4"
                      radius={[0, 7, 7, 0]}
                    />
                  </BarChart>
                </ResponsiveContainer>
              </ChartCard>

            </div>

            <div className="analytics-small-metrics">
              <div>
                <strong>Captura de ubicación</strong>

                {Object.entries(
                  data.byLocationSource,
                ).map(([key, value]) => (
                  <span key={key}>
                    {labelCode(key)}: {value}
                  </span>
                ))}
              </div>

              <div>
                <strong>Evidencia fotográfica</strong>

                <span>
                  Con evidencia:{' '}
                  {summary.reportsWithEvidence}
                </span>

                <span>
                  Sin evidencia:{' '}
                  {summary.reportsWithoutEvidence}
                </span>
              </div>
            </div>
          </>
        )}
      </div>
    </section>
  )
}

export default StatisticsSection
