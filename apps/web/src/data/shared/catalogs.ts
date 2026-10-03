export type ReportStatus =
  | 'Registrado'
  | 'En revisión'
  | 'En seguimiento'
  | 'Canalizado'
  | 'Concluido'
  | 'Archivado'
  | 'Cancelado'
  | 'Reincidente'

export type Semaforo = 'SIN_ATENDER' | 'ATENDIENDO' | 'ATENDIDO' | 'INACTIVO'

export type NnaEstimate = '1' | '2' | '3' | '4' | '5+'

export type AgeRange = '0-5' | '6-11' | '12-14' | '15-17'

export type Priority = 'MEDIA' | 'ALTA' | 'URGENTE'

export type UserRole = 'ADMINISTRADOR' | 'GESTOR_MUNICIPAL' | 'PROCURADOR'

export const reportStatuses: ReportStatus[] = [
  'Registrado',
  'En revisión',
  'En seguimiento',
  'Canalizado',
  'Concluido',
  'Archivado',
  'Cancelado',
  'Reincidente',
]

// Ordenadas de mayor a menor urgencia.
export const priorities: Priority[] = ['URGENTE', 'ALTA', 'MEDIA']

export const semaforoByStatus: Record<ReportStatus, Semaforo> = {
  Registrado: 'SIN_ATENDER',
  Reincidente: 'SIN_ATENDER',
  'En revisión': 'ATENDIENDO',
  'En seguimiento': 'ATENDIENDO',
  Canalizado: 'ATENDIENDO',
  Concluido: 'ATENDIDO',
  Archivado: 'ATENDIDO',
  Cancelado: 'INACTIVO',
}

export const semaforoLabels: Record<Semaforo, string> = {
  SIN_ATENDER: 'Sin atender',
  ATENDIENDO: 'Atendiendo',
  ATENDIDO: 'Atendido',
  INACTIVO: 'Inactivo',
}
