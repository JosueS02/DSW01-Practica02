# Data Model: Login de Empleados con Credenciales

## Entity: Empleado (extendida)

### Description
Representa al empleado del CRUD 001 con atributos de acceso para autenticación.

### Fields
- `clave`: `VARCHAR(16)`
  - Primary Key
  - Formato `E-` + al menos 3 dígitos
- `nombre`: `VARCHAR(100)` (requerido)
- `direccion`: `VARCHAR(100)` (requerido)
- `telefono`: `VARCHAR(100)` (requerido)
- `email`: `VARCHAR(255)`
  - Requerido
  - Único
  - Normalizado (trim/lowercase)
- `passwordHash`: `VARCHAR(255)`
  - Requerido
  - Nunca expuesto en respuestas API
- `activo`: `BOOLEAN`
  - Requerido
  - Default `true`

### Validation Rules
- `email`:
  - Rechazar `null`, vacío, formato inválido
  - Debe normalizarse con `trim + lowercase`
  - Debe ser único a nivel de sistema (comparación case-insensitive)
- `password` (input):
  - Mínimo 8 caracteres
  - Al menos 1 mayúscula, 1 minúscula y 1 número
- `passwordHash`:
  - Solo persistencia (derivado de `password`)

### State Transitions
- **Create**: requiere `email` y `password`; se persiste `passwordHash`.
- **Update**:
  - `email` puede cambiar manteniendo unicidad.
  - `password` opcional: si no se envía, se conserva hash vigente.
- **Delete**: mantiene semántica existente del CRUD 001.

## Entity: SesionAutenticada

### Description
Representa una sesión activa del flujo UI de autenticación de empleado.

### Fields
- `sessionId`: string (token de sesión/cookie)
- `empleadoClave`: referencia a `Empleado.clave`
- `createdAt`: timestamp
- `expiresAt`: timestamp (`createdAt + 8h`)
- `invalidatedAt`: timestamp nullable

### Validation Rules
- Debe considerarse válida solo si `now < expiresAt` e `invalidatedAt` es `null`.
- Debe existir como máximo una sesión UI activa por empleado.

### State Transitions
- **Create**: tras login exitoso.
- **Invalidate**: por logout explícito o expiración automática.

## Entity: IntentoAutenticacion

### Description
Registro de cada intento de login para auditoría y política de bloqueo.

### Fields
- `id`: bigint/uuid
- `email`: string
- `empleadoClave`: nullable (si se pudo resolver)
- `resultado`: enum (`SUCCESS`, `FAILURE`)
- `motivo`: enum/string (`INVALID_CREDENTIALS`, `LOCKED`, `EXPIRED`, etc.)
- `createdAt`: timestamp

### Validation Rules
- Debe registrarse siempre en login, independiente de éxito o fallo.
- Debe conservarse por al menos 90 días.

## Derived Rules (Business)
- Lockout: 5 fallos consecutivos por cuenta ⇒ bloqueo 15 minutos.
- Reset de lockout: un login exitoso reinicia el contador de fallos.
- Sesión UI: expiración automática a 8 horas.
- Concurrencia UI: un nuevo login invalida sesión UI previa del mismo empleado.
- Logout: invalida sesión UI activa actual.

## API Payload Models

### LoginRequest
- `email`: string (formato email, requerido)
- `password`: string (requerido)

### LoginResponse
- `empleado`: objeto resumido (`clave`, `nombre`, `email`)
- `uiSessionExpiresAt`: timestamp

### CreateEmpleadoRequest (ajustada)
- Campos actuales + `email` (requerido) + `password` (requerido)

### UpdateEmpleadoRequest (ajustada)
- Campos actuales + `email` (requerido) + `password` (opcional)

### EmpleadoResponse (ajustada)
- Campos públicos del empleado incluyendo `email`
- No incluye `password` ni `passwordHash`
