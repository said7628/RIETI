import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import ReportDetail from '../components/shared/ReportDetail'
import AssignSelect from '../components/tracking/AssignSelect'
import Pagination from '../components/tracking/Pagination'
import ReportDetailsModal from '../components/tracking/ReportDetailsModal'
import ReportsTable from '../components/tracking/ReportsTable'
import UpdateStatusModal from '../components/tracking/UpdateStatusModal'
import { priorities, type Priority, type ReportStatus } from '../data/shared/catalogs'
import { reportsData, type Report } from '../data/shared/reportsData'
import { sortOptions, sortReports, type SortOption } from '../data/shared/reportUtils'
import { matchesView, parseTrackingView } from '../data/tracking/trackingViews'
import './TrackingPage.css'

function TrackingPage() {
  const [searchParams] = useSearchParams()
  const view = parseTrackingView(searchParams.get('vista'))

  // Solo guarda cambios de sesión; el resto siempre viene del mock (evita campos vacíos con HMR).
  const [reportOverrides, setReportOverrides] = useState<
    Record<string, Partial<Report>>
  >({})

  const reports = reportsData.map((report) => ({
    ...report,
    ...reportOverrides[report.folio],
  }))

  const [sort, setSort] = useState<SortOption>('recientes')
  const [folioSearch, setFolioSearch] = useState('')
  const [priorityFilter, setPriorityFilter] = useState<Priority | ''>('')
  const [coloniaFilter, setColoniaFilter] = useState('')
  const [pagination, setPagination] = useState({ view, page: 1 })
  const [pageSize, setPageSize] = useState(10)
  const page = pagination.view === view ? pagination.page : 1
  const [selectedFolio, setSelectedFolio] = useState<string | null>(null)
  const [isStatusModalOpen, setIsStatusModalOpen] = useState(false)
  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState(false)

  const colonias = [...new Set(reports.map((report) => report.colonia))].sort()

  const filteredReports = reports.filter(
    (report) =>
      matchesView(report, view) &&
      report.folio.toLowerCase().includes(folioSearch.trim().toLowerCase()) &&
      (priorityFilter === '' || report.priority === priorityFilter) &&
      (coloniaFilter === '' || report.colonia === coloniaFilter),
  )
  const sortedReports = sortReports(filteredReports, sort)

  const totalPages = Math.max(1, Math.ceil(sortedReports.length / pageSize))
  const currentPage = Math.min(page, totalPages)
  const pageReports = sortedReports.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize,
  )

  const selectedReport =
    sortedReports.find((report) => report.folio === selectedFolio) ?? pageReports[0]

  function updateReport(folio: string, changes: Partial<Report>) {
    setReportOverrides((current) => ({
      ...current,
      [folio]: { ...current[folio], ...changes },
    }))
  }

  function assignCaseHolder(caseHolder: string) {
    updateReport(selectedReport.folio, { caseHolder, status: 'En revisión' })
  }

  function updateStatus(status: ReportStatus, solution: string | null) {
    updateReport(selectedReport.folio, { status, solution })
    setIsStatusModalOpen(false)
  }

  function renderActions() {
    return (
      <>
        {!selectedReport.caseHolder && (
          <AssignSelect folio={selectedReport.folio} onConfirm={assignCaseHolder} />
        )}

        <button
          type="button"
          className="button-primary"
          onClick={() => setIsStatusModalOpen(true)}
        >
          Actualizar estatus
        </button>

        <button
          type="button"
          className="button-primary"
          onClick={() => setIsDetailsModalOpen(true)}
        >
          Ver detalles
        </button>
      </>
    )
  }

  return (
    <div className="tracking-page">
      <main className="tracking-content">
        <section className="tracking-list">
          <div className="tracking-toolbar">
            <input
              type="search"
              placeholder="Buscar por folio"
              aria-label="Buscar por folio"
              value={folioSearch}
              onChange={(event) => {
                setFolioSearch(event.target.value)
                setPagination({ view, page: 1 })
              }}
            />

            <select
              aria-label="Filtrar por prioridad"
              value={priorityFilter}
              onChange={(event) => {
                setPriorityFilter(event.target.value as Priority | '')
                setPagination({ view, page: 1 })
              }}
            >
              <option value="">Todas las prioridades</option>
              {priorities.map((priority) => (
                <option key={priority} value={priority}>
                  {priority}
                </option>
              ))}
            </select>

            <select
              aria-label="Filtrar por colonia"
              value={coloniaFilter}
              onChange={(event) => {
                setColoniaFilter(event.target.value)
                setPagination({ view, page: 1 })
              }}
            >
              <option value="">Todas las colonias</option>
              {colonias.map((colonia) => (
                <option key={colonia} value={colonia}>
                  {colonia}
                </option>
              ))}
            </select>

            <label className="tracking-sort">
              Ordenar por:{' '}
              <select
                value={sort}
                onChange={(event) => setSort(event.target.value as SortOption)}
              >
                {sortOptions.map((option) => (
                  <option key={option.id} value={option.id}>
                    {option.label}
                  </option>
                ))}
              </select>
            </label>
          </div>

          <ReportsTable
            reports={pageReports}
            selectedFolio={selectedReport?.folio}
            onSelect={setSelectedFolio}
          />

          <Pagination
            page={currentPage}
            totalPages={totalPages}
            pageSize={pageSize}
            onPageChange={(nextPage) => setPagination({ view, page: nextPage })}
            onPageSizeChange={(size) => {
              setPageSize(size)
              setPagination({ view, page: 1 })
            }}
          />
        </section>

        {selectedReport && (
          <ReportDetail report={selectedReport} actions={renderActions()} />
        )}
      </main>

      {isStatusModalOpen && selectedReport && (
        <UpdateStatusModal
          report={selectedReport}
          onCancel={() => setIsStatusModalOpen(false)}
          onConfirm={updateStatus}
        />
      )}

      {isDetailsModalOpen && selectedReport && (
        <ReportDetailsModal
          report={selectedReport}
          onClose={() => setIsDetailsModalOpen(false)}
        />
      )}
    </div>
  )
}

export default TrackingPage
