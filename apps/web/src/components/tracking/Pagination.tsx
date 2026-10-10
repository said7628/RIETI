const PAGE_SIZES = [10, 25, 50]

interface PaginationProps {
  page: number
  totalPages: number
  pageSize: number
  onPageChange: (page: number) => void
  onPageSizeChange: (pageSize: number) => void
}

/** Pasa de página en la lista de Seguimiento y elige cuántos reportes se ven. */
function Pagination({
  page,
  totalPages,
  pageSize,
  onPageChange,
  onPageSizeChange,
}: PaginationProps) {
  return (
    <div className="pagination">
      <label>
        Mostrar{' '}
        <select
          value={pageSize}
          onChange={(event) => onPageSizeChange(Number(event.target.value))}
        >
          {PAGE_SIZES.map((size) => (
            <option key={size} value={size}>
              {size}
            </option>
          ))}
        </select>{' '}
        por página
      </label>

      <div className="pagination-pages">
        <button
          type="button"
          onClick={() => onPageChange(page - 1)}
          disabled={page === 1}
        >
          Anterior
        </button>

        <span>
          Página {page} de {totalPages}
        </span>

        <button
          type="button"
          onClick={() => onPageChange(page + 1)}
          disabled={page === totalPages}
        >
          Siguiente
        </button>
      </div>
    </div>
  )
}

export default Pagination
