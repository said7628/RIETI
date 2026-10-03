import { useEffect, type ReactNode } from 'react'

interface ModalProps {
  title: string
  children: ReactNode
  onClose?: () => void
  large?: boolean
}

function Modal({ title, children, onClose, large = false }: ModalProps) {
  useEffect(() => {
    if (!onClose) return

    const close = onClose

    function handleKeyDown(event: KeyboardEvent) {
      if (event.key === 'Escape') close()
    }

    document.addEventListener('keydown', handleKeyDown)
    return () => document.removeEventListener('keydown', handleKeyDown)
  }, [onClose])

  return (
    <div
      className="modal-overlay"
      onClick={onClose ? (event) => {
        if (event.target === event.currentTarget) onClose()
      } : undefined}
    >
      <div
        className={`modal${large ? ' modal-large' : ''}`}
        role="dialog"
        aria-modal="true"
        aria-labelledby="modal-title"
      >
        <div className="modal-header">
          <h2 id="modal-title">{title}</h2>

          {onClose && (
            <button
              type="button"
              className="modal-close"
              onClick={onClose}
              aria-label="Cerrar"
            >
              ×
            </button>
          )}
        </div>

        <div className="modal-body">{children}</div>
      </div>
    </div>
  )
}

export default Modal
