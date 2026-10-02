import type { AgeRange, NnaEstimate, ReportStatus } from './catalogs'

export interface Report {
  folio: string
  workType: string
  location: string
  status: ReportStatus
  caseHolder: string
  address: string
  nnaEstimate: NnaEstimate
  ageRange: AgeRange
  schedule: string
}

export const reportsData: Report[] = [
  {
    folio: '260930-1805U',
    workType: 'Niños vendiendo',
    location: 'Zona Norte',
    status: 'Registrado',
    caseHolder: '—',
    address: 'Ubicación de ejemplo para el reporte',
    nnaEstimate: '3',
    ageRange: '6-11',
    schedule: '8:27',
  },
  {
    folio: '260930-1821A',
    workType: 'Venta de productos',
    location: 'Zona Centro',
    status: 'Registrado',
    caseHolder: '—',
    address: 'Ubicación ficticia',
    nnaEstimate: '2',
    ageRange: '12-14',
    schedule: '10:15',
  },
  {
    folio: '260929-2154F',
    workType: 'Venta en semáforos',
    location: 'Zona Oriente',
    status: 'Reincidente',
    caseHolder: '—',
    address: 'Ubicación ficticia',
    nnaEstimate: '1',
    ageRange: '6-11',
    schedule: '13:40',
  },
  {
    folio: '260929-2231M',
    workType: 'Venta de mercancía',
    location: 'Zona Sur',
    status: 'Registrado',
    caseHolder: '—',
    address: 'Ubicación ficticia',
    nnaEstimate: '2',
    ageRange: '6-11',
    schedule: '15:20',
  },
  {
    folio: '260928-0839X',
    workType: 'Trabajo en construcción',
    location: 'Zona Poniente',
    status: 'Registrado',
    caseHolder: '—',
    address: 'Ubicación ficticia',
    nnaEstimate: '1',
    ageRange: '12-14',
    schedule: '16:00',
  },
  {
    folio: '260928-1034C',
    workType: 'Venta de productos',
    location: 'Zona Centro',
    status: 'En revisión',
    caseHolder: 'María López',
    address: 'Ubicación ficticia',
    nnaEstimate: '2',
    ageRange: '12-14',
    schedule: '11:30',
  },
  {
    folio: '260927-0758P',
    workType: 'Venta en semáforos',
    location: 'Zona Norte',
    status: 'En seguimiento',
    caseHolder: 'Joaquín Ramírez',
    address: 'Ubicación ficticia',
    nnaEstimate: '1',
    ageRange: '6-11',
    schedule: '9:10',
  },
  {
    folio: '260926-0610Q',
    workType: 'Niños vendiendo',
    location: 'Zona Oriente',
    status: 'Registrado',
    caseHolder: '—',
    address: 'Ubicación ficticia',
    nnaEstimate: '3',
    ageRange: '6-11',
    schedule: '12:00',
  },
  {
    folio: '260922-1125R',
    workType: 'Venta ambulante',
    location: 'Zona Centro',
    status: 'Canalizado',
    caseHolder: 'María López',
    address: 'Ubicación ficticia',
    nnaEstimate: '2',
    ageRange: '12-14',
    schedule: '17:10',
  },
  {
    folio: '260919-1017S',
    workType: 'Trabajo en construcción',
    location: 'Zona Sur',
    status: 'Concluido',
    caseHolder: 'María López',
    address: 'Ubicación ficticia',
    nnaEstimate: '1',
    ageRange: '15-17',
    schedule: '14:45',
  },
]
