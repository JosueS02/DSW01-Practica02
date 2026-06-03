# Data Model: CRUD de Departamentos Relacionado con Empleados

## Entity: Departamento

### Description
Representa una unidad organizacional a la que se asignan empleados.

### Fields
- `id`: `VARCHAR(32)`
  - Primary Key
  - Formato obligatorio `D-` + al menos 3 dígitos (ej. `D-001`, `D-1000`)
  - Inmutable tras la creación
- `nombre`: `VARCHAR(100)`
  - Requerido
  - No vacío
  - Único a nivel de tabla
- `descripcion`: `VARCHAR(255)`
  - Opcional
  - Si se envía, no puede ser solo espacios

### Validation Rules
- `nombre`:
  - Rechazar `null`
  - Rechazar vacío o solo espacios
  - Longitud máxima 100
  - Debe ser único (case-sensitive según collation de BD)
- `descripcion`:
  - Permitir `null`
  - Si existe, rechazar solo espacios
  - Longitud máxima 255
- `id`:
  - Requerido para get/update/delete
  - Debe cumplir patrón `^D-[0-9]{3,}$`
  - Debe referenciar fila existente

### State Transitions
- **Create**: se crea departamento con `id` generado por secuencia global en PostgreSQL (`D-001`, `D-002`, ...), sin reutilización.
- **Update**: se puede modificar `nombre` y `descripcion`.
- **Delete**: solo permitido si no existen empleados asociados.

## Entity: Empleado (extendida)

### Description
Registro de empleado existente, extendido con la referencia obligatoria al departamento.

### Fields
- `clave`: `VARCHAR(16)`
  - Primary Key existente
  - Formato `E-001` y variantes secuenciales
- `nombre`: `VARCHAR(100)` (requerido)
- `direccion`: `VARCHAR(100)` (requerido)
- `telefono`: `VARCHAR(100)` (requerido)
- `departamentoId`: `VARCHAR(32)`
  - Foreign Key obligatoria hacia `departamentos.id`

### Validation Rules
- Reglas actuales de `nombre`, `direccion`, `telefono` se mantienen.
- `departamentoId`:
  - Rechazar `null` en create/update
  - Debe existir en `departamentos`

### State Transitions
- **Create**: empleado se crea con clave autogenerada y departamento existente.
- **Update**: puede cambiar departamento si el nuevo existe.
- **Delete**: hard delete de empleado sin efecto sobre departamento.

## Relationships
- `Departamento (1) --- (N) Empleado`
- Restricción: un departamento con empleados no puede eliminarse.

## API Payload Models

### CreateDepartamentoRequest
- `nombre`: string (1..100, requerido)
- `descripcion`: string (1..255, opcional)

### UpdateDepartamentoRequest
- `nombre`: string (1..100, requerido)
- `descripcion`: string (1..255, opcional)

### DepartamentoResponse
- `id`: string (pattern `^D-[0-9]{3,}$`)
- `nombre`: string
- `descripcion`: string | null

### CreateEmpleadoRequest (ajustada)
- `nombre`: string (1..100)
- `direccion`: string (1..100)
- `telefono`: string (1..100)
- `departamentoId`: string (pattern `^D-[0-9]{3,}$`, requerido)

### UpdateEmpleadoRequest (ajustada)
- `nombre`: string (1..100)
- `direccion`: string (1..100)
- `telefono`: string (1..100)
- `departamentoId`: string (pattern `^D-[0-9]{3,}$`, requerido)

### EmpleadoResponse (ajustada)
- `clave`: string (pattern `^E-[0-9]{3,}$`)
- `nombre`: string
- `direccion`: string
- `telefono`: string
- `departamento`: objeto resumido con `id` y `nombre`

### ErrorResponse
- `timestamp`: string(date-time)
- `status`: integer
- `error`: string
- `message`: string
- `path`: string
