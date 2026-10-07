# Documentación del frente — RIETI

Este documento describe cómo funciona la aplicación web hoy, para conectar el backend. No hay llamadas a un servidor: los datos viven en el navegador y se pierden al recargar.

Para verla en local: `npm install` y `npm run dev`.

## Roles

Hay dos tipos de cuenta.

| Tipo | Qué ve |
| --- | --- |
| Administrador | Inicio con mapa de calor y estadísticas (esas dos secciones están vacías), seguimiento, cuentas |
| Empleado | Lo mismo, sin mapa de calor, sin estadísticas y sin el menú Cuentas |

Cuentas de prueba (contraseña `rieti`):

- `manolo@rieti.mx` — administrador
- `joaquin@rieti.mx` — empleado
- `maria@rieti.mx` — empleado

Al abrir la app se entra ya como Manolo, sin pasar por el login.

## Pantallas

| Ruta | Qué hace |
| --- | --- |
| `/login` | Correo y contraseña. Si fallan, no entra. |
| `/recuperar` | Pide el correo de una cuenta activa y genera un código. |
| `/recuperar/codigo` | Pide el código, la contraseña nueva y la confirmación. Si coinciden, guarda la contraseña y regresa al login. |
| `/` | Inicio. Tarjetas de conteo, reportes recientes y el detalle del más reciente. El mapa de calor y las estadísticas solo se muestran al administrador, y están vacíos. |
| `/crear-reporte` | Formulario de un reporte nuevo. |
| `/reporte-enviado` | Aviso de éxito con el folio. Si se abre sin haber enviado nada, regresa a crear reporte. |
| `/seguimiento` | Lista, filtros y detalle. El menú lateral cambia la lista. |
| `/cuentas` | Crear una cuenta. Solo administrador. |
| `/cuentas?vista=administrar` | Lista de cuentas, ficha, cambio de tipo y eliminación. Solo administrador. |

`/perfil` existe, pero dice que está en construcción y ninguna pantalla lleva ahí.

## Reporte

Lo que el formulario pide:

- Número de niños
- Edades (texto libre, por ejemplo `6, 12, 16 años`)
- Tipo de trabajo
- Horario
- Detalles
- Archivos (uno o varios; se pueden quitar de la lista)
- Dirección, municipio, colonia y código postal

Obligatorios para enviar: número de niños, tipo de trabajo, detalles, dirección y colonia.

Lo que hoy se guarda del reporte:

| Dato | Valores | Notas |
| --- | --- | --- |
| Folio | Texto `AAMMDD-HHMM` más una letra | Lo genera el navegador |
| Fecha | ISO 8601 en UTC | Se muestra en hora local |
| Tipo de trabajo | Texto | |
| Colonia | Texto | Sale de la colonia; si viene vacía, del municipio |
| Dirección | Texto | Junta dirección, colonia, municipio y código postal en un solo texto |
| Estatus | Ver lista de abajo | Uno nuevo queda en Registrado |
| Prioridad | `URGENTE`, `ALTA`, `MEDIA` | Uno nuevo siempre queda en Media. Nadie la elige |
| Titular | Nombre o vacío | Uno nuevo queda sin titular |
| Niños estimados | `1`, `2`, `3`, `4`, `5+` | Sale del número capturado. 5 o más se guarda como `5+` |
| Rango de edad | `0-5`, `6-11`, `12-14`, `15-17` | Sale del primer número escrito en las edades |
| Horario | Texto | |
| Descripción | Texto | Los detalles del formulario |
| Solución | Texto o vacío | Solo al cerrar un caso |

El municipio y el código postal no se guardan por separado. Los archivos tampoco: en seguimiento la evidencia sigue siendo de prueba.

### Estatus

| Estatus | Grupo en la interfaz |
| --- | --- |
| Registrado | Sin atender |
| Reincidente | Sin atender |
| En revisión | Atendiendo |
| En seguimiento | Atendiendo |
| Canalizado | Atendiendo |
| Concluido | Atendido |
| Archivado | Atendido |
| Cancelado | Inactivo |

Concluido y Archivado piden una solución escrita. Sin ese texto no se puede guardar. Cancelado no la pide.

Al asignar un titular, el estatus pasa a En revisión.

### Listas de seguimiento

La ruta base es `/seguimiento`. La lista cambia con `?vista=`.

| Vista | Qué entra |
| --- | --- |
| `mis-reportes` (la de inicio) | El titular es la persona que inició sesión |
| `todos` | Todos |
| `sin-asignar` | Registrado o Reincidente, y además sin titular |
| `en-atencion` | En revisión, En seguimiento o Canalizado |
| `cerrados` | Concluido o Archivado |
| `cancelados` | Cancelado |

También se puede buscar por folio, filtrar por prioridad y por colonia, ordenar por más recientes, más antiguos o prioridad, y paginar de 10, 25 o 50.

## Cuenta

| Dato | Valores |
| --- | --- |
| Nombre | Texto |
| Correo | Texto, único |
| Contraseña | Texto, mínimo 4 caracteres. Hay que escribirla dos veces al crear o al recuperarla |
| Tipo | Administrador o empleado |
| Activa | Sí o no. Una cuenta inactiva no puede iniciar sesión ni pedir código |

Al crear, el tipo por defecto es empleado.

En administrar se puede ordenar por nombre (a-z, z-a) o por cantidad de reportes activos. Un reporte activo es uno cuyo titular es esa persona y cuyo estatus está en sin atender o atendiendo.

Desde la ficha se puede convertir la cuenta en administrador o en empleado, o eliminarla. La cuenta de la sesión actual no se puede eliminar. La ficha muestra la contraseña en claro; eso es de esta versión de prueba.

## Acciones que el servidor tiene que cubrir

- Iniciar sesión con correo y contraseña.
- Cerrar sesión.
- Pedir un código de recuperación al correo de una cuenta activa, y cambiar la contraseña si el código es correcto.
- Crear un reporte con los datos de arriba y devolver el folio.
- Listar reportes con las vistas, la búsqueda, los filtros, el orden y la paginación.
- Ver el detalle de un reporte.
- Asignar titular. El frente espera que el estatus pase a En revisión.
- Cambiar estatus. Si el nuevo estatus es Concluido o Archivado, la solución es obligatoria.
- Crear una cuenta.
- Listar cuentas, con el conteo de reportes activos de cada una.
- Cambiar el tipo de una cuenta.
- Eliminar una cuenta que no sea la de la sesión actual.

## Qué no hay que buscar en el frente

- No hay endpoints ni cliente HTTP. Los datos de prueba están en `src/data`.
- Estadísticas y mapa de calor no tienen datos ni interacción.
- Los archivos elegidos al crear un reporte no se envían con el reporte.
- El código de recuperación lo inventa el navegador y lo muestra en pantalla. El envío del correo corresponde al servidor.
