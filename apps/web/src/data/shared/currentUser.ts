import type { UserRole } from './catalogs'

// Usuario con el que abre la sesiÃ³n. El login puede cambiarlo.
export const currentUser: { name: string; email: string; role: UserRole } = {
  name: 'Manolo SÃ¡nchez',
  email: 'manolo@rieti.mx',
  role: 'ADMINISTRADOR_FUNDACION',
}

