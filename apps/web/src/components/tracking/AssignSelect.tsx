import { useState } from 'react'
import { useAccounts } from '../../data/shared/accounts'
import Modal from '../shared/Modal'

interface AssignSelectProps {
  folio: string
  onConfirm: (caseHolder: string) => void
}

function AssignSelect({ folio, onConfirm }: AssignSelectProps) {
  const staff = useAccounts().filter((account) => account.active)
  const [pendingCaseHolder, setPendingCaseHolder] = useState<string | null>(null)

  function confirmAssignment() {
    if (pendingCaseHolder) onConfirm(pendingCaseHolder)
    setPendingCaseHolder(null)
  }

  return (
    <>
      <label className="assign-select">
        Asignar titular:{' '}
        <select
          value=""
          onChange={(event) => setPendingCaseHolder(event.target.value)}
        >
          <option value="" disabled>
            (Asignar)
          </option>
          {staff.map((account) => (
            <option key={account.email} value={account.name}>
              {account.name}
            </option>
          ))}
        </select>
      </label>

      {pendingCaseHolder && (
        <Modal title="Confirmar asignación">
          <p>
            ¿Asignar el reporte <strong>{folio}</strong> a{' '}
            <strong>{pendingCaseHolder}</strong>?
          </p>
          <p>El estatus cambiará a “En revisión”.</p>

          <div className="modal-buttons">
            <button type="button" onClick={() => setPendingCaseHolder(null)}>
              Cancelar
            </button>
            <button type="button" className="button-primary" onClick={confirmAssignment}>
              Confirmar
            </button>
          </div>
        </Modal>
      )}
    </>
  )
}

export default AssignSelect
