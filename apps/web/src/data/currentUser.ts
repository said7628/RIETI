import type { UserRole } from './catalogs'

// Usuario de prueba mientras no exista el login. Cambia el rol para ver lo que ve cada perfil.
export const currentUser: { name: string; role: UserRole } = {
  name: 'Usuario',
  role: 'ADMINISTRADOR',
}
