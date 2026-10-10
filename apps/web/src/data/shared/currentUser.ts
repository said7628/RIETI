import type { UserRole } from './catalogs'

/** Persona con la que abre la sesión. El login puede cambiarla. */
export const currentUser: { name: string; email: string; role: UserRole } = {
  name: 'Manolo Sánchez',
  email: 'manolo@rieti.mx',
  role: 'ADMINISTRADOR_FUNDACION',
}

