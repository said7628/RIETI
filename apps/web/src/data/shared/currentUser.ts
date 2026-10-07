import type { UserRole } from './catalogs'

// Usuario con el que abre la sesión. El login puede cambiarlo.
export const currentUser: { name: string; email: string; role: UserRole } = {
  name: 'Manolo Sánchez',
  email: 'manolo@rieti.mx',
  role: 'ADMINISTRADOR',
}
