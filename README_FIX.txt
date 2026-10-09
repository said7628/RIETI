RIETI WEB/BACK FIX — 2026-10-08

Incluye correcciones para:
- Transiciones de estatus de cualquier estado a cualquier estado.
- Cancelado/Archivado dejan de quedar bloqueados.
- Solución requerida para estados atendidos, pero editable en cualquier estado.
- Guardado de estatus protegido contra doble clic y con estado "Guardando...".
- Cierre del modal apenas el backend confirma; refresco posterior en segundo plano.
- Texto de solución y descripción con wrap real para cadenas largas sin espacios.
- Controles + / - de Leaflet reubicados abajo a la derecha.
- Sesión más resistente a F5 repetido: errores de red/5xx no destruyen la sesión.
- Refresh token se usa cuando /auth/me devuelve 401.
- Reactivación de cuentas inactivas desde Administración de cuentas.
- Nombre de usuario con tamaño/anclaje más consistente entre páginas.
- Mojibake/acentos corregidos en los fuentes detectados.
- “Periodo de análisis / métricas / gráficas” corregidos.

IMPORTANTE SOBRE RECUPERACIÓN DE CONTRASEÑA:
El código deja un mensaje backend más claro si Resend falla, pero el envío NO puede
funcionar mientras ri eti-api esté dentro de una VPC privada sin salida HTTPS a Internet.
Para que el correo funcione de verdad hace falta egress de la VPC (p. ej. NAT correctamente
configurado) o cambiar la arquitectura de correo. No se abrió RDS ni se debilitó seguridad.

Validación realizada:
- `node --check services/api/index.mjs`: OK en el archivo preparado.
- Revisión TypeScript de sintaxis de los archivos modificados: sin errores sintácticos;
  el entorno de validación no tenía las dependencias npm instaladas para ejecutar build completo.
