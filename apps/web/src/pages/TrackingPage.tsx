import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'

import ReportDetail from '../components/shared/ReportDetail'
import AssignSelect from '../components/tracking/AssignSelect'
import Pagination from '../components/tracking/Pagination'
import ReportDetailsModal from '../components/tracking/ReportDetailsModal'
import ReportsTable from '../components/tracking/ReportsTable'
import UpdateStatusModal from '../components/tracking/UpdateStatusModal'

import {
  priorities,
  type Priority,
  type ReportStatus,
} from '../data/shared/catalogs'

import {
  sortOptions,
  sortReports,
  type SortOption,
} from '../data/shared/reportUtils'

import {
  refreshReports,
  useReports,
  useSessionUser,
} from '../data/shared/session'

import {
  assignReport,
  updateReport as updateReportApi,
} from '../api/reports'

import type { ApiUser } from '../api/users'

import {
  matchesView,
  parseTrackingView,
} from '../data/tracking/trackingViews'

import './TrackingPage.css'

function TrackingPage() {
  const [searchParams] = useSearchParams()

  const view = parseTrackingView(
    searchParams.get('vista'),
  )

  const user = useSessionUser()
  const reports = useReports()

  const [sort, setSort] =
    useState<SortOption>('recientes')

  const [folioSearch, setFolioSearch] =
    useState('')

  const [priorityFilter, setPriorityFilter] =
    useState<Priority | ''>('')

  const [coloniaFilter, setColoniaFilter] =
    useState('')

  const [pagination, setPagination] =
    useState({
      view,
      page: 1,
    })

  const [pageSize, setPageSize] =
    useState(10)

  const page =
    pagination.view === view
      ? pagination.page
      : 1

  const [
    selectedFolio,
    setSelectedFolio,
  ] = useState<string | null>(null)

  const [
    isStatusModalOpen,
    setIsStatusModalOpen,
  ] = useState(false)

  const [
    isDetailsModalOpen,
    setIsDetailsModalOpen,
  ] = useState(false)

  const [actionError, setActionError] =
    useState('')

  const [isSavingStatus, setIsSavingStatus] =
    useState(false)

  const colonias = [
    ...new Set(
      reports.map(
        (report) => report.colonia,
      ),
    ),
  ].sort()

  const filteredReports = reports.filter(
    (report) =>
      matchesView(
        report,
        view,
        user?.name ?? null,
      ) &&
      report.folio
        .toLowerCase()
        .includes(
          folioSearch
            .trim()
            .toLowerCase(),
        ) &&
      (priorityFilter === '' ||
        report.priority ===
          priorityFilter) &&
      (coloniaFilter === '' ||
        report.colonia ===
          coloniaFilter),
  )

  const sortedReports =
    sortReports(filteredReports, sort)

  const totalPages = Math.max(
    1,
    Math.ceil(
      sortedReports.length / pageSize,
    ),
  )

  const currentPage = Math.min(
    page,
    totalPages,
  )

  const pageReports =
    sortedReports.slice(
      (currentPage - 1) * pageSize,
      currentPage * pageSize,
    )

  const selectedReport =
    sortedReports.find(
      (report) =>
        report.folio ===
        selectedFolio,
    ) ??
    pageReports[0] ??
    null

  async function assignCaseHolder(
    assignedUser: ApiUser,
  ) {
    if (!selectedReport) return

    setActionError('')

    try {
      await assignReport(
        selectedReport.folio,
        {
          userId: assignedUser.id,
        },
      )

      await refreshReports()
    } catch (error) {
      setActionError(
        error instanceof Error
          ? error.message
          : 'No fue posible asignar el reporte.',
      )

      throw error
    }
  }

  async function updateStatus(
    status: ReportStatus,
    solution: string | null,
  ) {
    if (!selectedReport || isSavingStatus) return

    setActionError('')
    setIsSavingStatus(true)

    try {
      await updateReportApi(
        selectedReport.folio,
        {
          status,
          solution,
        },
      )

      setIsStatusModalOpen(false)

      void refreshReports().catch((error) => {
        setActionError(
          error instanceof Error
            ? error.message
            : 'El cambio se guardó, pero no fue posible refrescar la lista.',
        )
      })
    } catch (error) {
      setActionError(
        error instanceof Error
          ? error.message
          : 'No fue posible actualizar el estatus.',
      )
    } finally {
      setIsSavingStatus(false)
    }
  }

  function renderActions() {
    if (!selectedReport) {
      return null
    }

    return (
      <>
        {(user?.roleCode === 'ADMINISTRADOR_FUNDACION' || user?.roleCode === 'GESTOR_MUNICIPAL') && (
          <AssignSelect
            folio={
              selectedReport.folio
            }
            onConfirm={
              assignCaseHolder
            }
          />
        )}

        <button
          type="button"
          className="button-primary"
          onClick={() =>
            setIsStatusModalOpen(true)
          }
        >
          Actualizar estatus
        </button>

        <button
          type="button"
          className="button-primary"
          onClick={() =>
            setIsDetailsModalOpen(true)
          }
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
                setFolioSearch(
                  event.target.value,
                )

                setPagination({
                  view,
                  page: 1,
                })
              }}
            />

            <select
              aria-label="Filtrar por prioridad"
              value={priorityFilter}
              onChange={(event) => {
                setPriorityFilter(
                  event.target
                    .value as
                    | Priority
                    | '',
                )

                setPagination({
                  view,
                  page: 1,
                })
              }}
            >
              <option value="">
                Todas las prioridades
              </option>

              {priorities.map(
                (priority) => (
                  <option
                    key={priority}
                    value={priority}
                  >
                    {priority}
                  </option>
                ),
              )}
            </select>

            <select
              aria-label="Filtrar por colonia"
              value={coloniaFilter}
              onChange={(event) => {
                setColoniaFilter(
                  event.target.value,
                )

                setPagination({
                  view,
                  page: 1,
                })
              }}
            >
              <option value="">
                Todas las colonias
              </option>

              {colonias.map(
                (colonia) => (
                  <option
                    key={colonia}
                    value={colonia}
                  >
                    {colonia}
                  </option>
                ),
              )}
            </select>

            <label className="tracking-sort">
              Ordenar por:{' '}

              <select
                value={sort}
                onChange={(event) =>
                  setSort(
                    event.target
                      .value as SortOption,
                  )
                }
              >
                {sortOptions.map(
                  (option) => (
                    <option
                      key={option.id}
                      value={option.id}
                    >
                      {option.label}
                    </option>
                  ),
                )}
              </select>
            </label>
          </div>

          {actionError && (
            <p className="modal-error">
              {actionError}
            </p>
          )}

          <ReportsTable
            reports={pageReports}
            selectedFolio={
              selectedReport?.folio
            }
            onSelect={
              setSelectedFolio
            }
          />

          <Pagination
            page={currentPage}
            totalPages={totalPages}
            pageSize={pageSize}
            onPageChange={(
              nextPage,
            ) =>
              setPagination({
                view,
                page: nextPage,
              })
            }
            onPageSizeChange={(
              size,
            ) => {
              setPageSize(size)

              setPagination({
                view,
                page: 1,
              })
            }}
          />
        </section>

        {selectedReport && (
          <ReportDetail
            report={selectedReport}
            actions={renderActions()}
          />
        )}
      </main>

      {isStatusModalOpen &&
        selectedReport && (
          <UpdateStatusModal
            report={selectedReport}
            saving={isSavingStatus}
            onCancel={() =>
              setIsStatusModalOpen(
                false,
              )
            }
            onConfirm={
              updateStatus
            }
          />
        )}

      {isDetailsModalOpen &&
        selectedReport && (
          <ReportDetailsModal
            report={selectedReport}
            onClose={() =>
              setIsDetailsModalOpen(
                false,
              )
            }
          />
        )}
    </div>
  )
}

export default TrackingPage