export type ReportStatus =
  | 'SIN ASIGNAR'
  | 'EN ATENCIÓN'
  | 'COMPLETADO'

export interface Report {
  folio: string
  workType: string
  location: string
  status: ReportStatus
  caseHolder: string
  address: string
  children: number
  ages: string
  schedule: string
}

export const reportsData: Report[] = [
  {
    folio: '260930-1805U',
    workType: 'Niños vendiendo',
    location: 'Zona Norte',
    status: 'SIN ASIGNAR',
    caseHolder: '—',
    address: 'Ubicación de ejemplo para el reporte',
    children: 3,
    ages: '8, 10 y 12 años',
    schedule: '8:27',
  },
  {
    folio: '260930-1821A',
    workType: 'Venta de productos',
    location: 'Zona Centro',
    status: 'SIN ASIGNAR',
    caseHolder: '—',
    address: 'Ubicación ficticia',
    children: 2,
    ages: '10 y 13 años',
    schedule: '10:15',
  },
  {
    folio: '260929-2154F',
    workType: 'Venta en semáforos',
    location: 'Zona Oriente',
    status: 'SIN ASIGNAR',
    caseHolder: '—',
    address: 'Ubicación ficticia',
    children: 1,
    ages: '11 años',
    schedule: '13:40',
  },
  {
    folio: '260929-2231M',
    workType: 'Venta de mercancía',
    location: 'Zona Sur',
    status: 'SIN ASIGNAR',
    caseHolder: '—',
    address: 'Ubicación ficticia',
    children: 2,
    ages: '9 y 12 años',
    schedule: '15:20',
  },
  {
    folio: '260928-0839X',
    workType: 'Trabajo en construcción',
    location: 'Zona Poniente',
    status: 'SIN ASIGNAR',
    caseHolder: '—',
    address: 'Ubicación ficticia',
    children: 1,
    ages: '14 años',
    schedule: '16:00',
  },
  {
    folio: '260928-1034C',
    workType: 'Venta de productos',
    location: 'Zona Centro',
    status: 'EN ATENCIÓN',
    caseHolder: 'María López',
    address: 'Ubicación ficticia',
    children: 2,
    ages: '12 y 13 años',
    schedule: '11:30',
  },
  {
    folio: '260927-0758P',
    workType: 'Venta en semáforos',
    location: 'Zona Norte',
    status: 'EN ATENCIÓN',
    caseHolder: 'Joaquín Ramírez',
    address: 'Ubicación ficticia',
    children: 1,
    ages: '10 años',
    schedule: '9:10',
  },
  {
    folio: '260926-0610Q',
    workType: 'Niños vendiendo',
    location: 'Zona Oriente',
    status: 'SIN ASIGNAR',
    caseHolder: '—',
    address: 'Ubicación ficticia',
    children: 3,
    ages: '8, 9 y 11 años',
    schedule: '12:00',
  },
  {
    folio: '260922-1125R',
    workType: 'Venta ambulante',
    location: 'Zona Centro',
    status: 'EN ATENCIÓN',
    caseHolder: 'María López',
    address: 'Ubicación ficticia',
    children: 2,
    ages: '11 y 14 años',
    schedule: '17:10',
  },
  {
    folio: '260919-1017S',
    workType: 'Trabajo en construcción',
    location: 'Zona Sur',
    status: 'COMPLETADO',
    caseHolder: 'María López',
    address: 'Ubicación ficticia',
    children: 1,
    ages: '15 años',
    schedule: '14:45',
  },
]