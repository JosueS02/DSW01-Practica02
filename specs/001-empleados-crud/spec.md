# Feature Specification: CRUD de Empleados

**Feature Branch**: `001-empleados-crud`  
**Created**: 2026-02-25  
**Status**: Draft  
**Input**: User description: "crear un crud de empleados con los campos clave nombre, direccion y telefon. Donde clave sea pk, y los demas campos sean de 100 espacios"

## Clarifications

### Session 2026-02-25

- Q: ¿Cómo se define el origen de `clave` (PK)? → A: `clave` autogenerada por la base de datos (no se envía en el alta).
- Q: ¿Qué exposición tendrán `/swagger-ui` y `/api-docs`? → A: CRUD de empleados protegido y `/swagger-ui` + `/api-docs` públicos en desarrollo.
- Q: ¿Qué tipo de eliminación aplica al borrar empleados? → A: Eliminación física (hard delete).
- Q: ¿Qué formato tendrá `clave` autogenerada? → A: prefijo `E-` seguido de número secuencial con padding de 3 dígitos (ej. `E-001`).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registrar empleado (Priority: P1)

Como usuario autenticado, quiero registrar empleados para disponer de un padrón inicial confiable.

**Why this priority**: Sin alta de empleados no existe base de datos operativa para consultar, editar o eliminar.

**Independent Test**: Se prueba creando un empleado con datos válidos y verificando que queda disponible para consulta posterior.

**Acceptance Scenarios**:

1. **Given** que no existe un empleado con la misma combinación de datos, **When** se registra un empleado con nombre, dirección y teléfono válidos, **Then** el sistema confirma creación, almacena los datos y retorna una `clave` generada.
2. **Given** que el cuerpo de alta incluye `clave`, **When** se intenta registrar un empleado, **Then** el sistema rechaza la operación indicando que `clave` no debe enviarse.

---

### User Story 2 - Consultar empleados (Priority: P2)

Como usuario autenticado, quiero consultar un empleado por clave y listar empleados para ubicar información de manera rápida.

**Why this priority**: La consulta es el segundo flujo crítico para usar el padrón después de crearlo.

**Independent Test**: Se prueba recuperando un empleado existente por clave y ejecutando listado general sin depender de actualización o borrado.

**Acceptance Scenarios**:

1. **Given** que existe al menos un empleado registrado, **When** se consulta por una clave existente, **Then** el sistema devuelve los datos correctos del empleado.
2. **Given** que existe al menos un empleado registrado, **When** se solicita el listado de empleados, **Then** el sistema devuelve la colección completa de registros existentes.

---

### User Story 3 - Actualizar y eliminar empleado (Priority: P3)

Como usuario autenticado, quiero actualizar o eliminar empleados para mantener el padrón vigente.

**Why this priority**: Este flujo completa el ciclo de vida del registro y permite mantenimiento continuo de datos.

**Independent Test**: Se prueba modificando datos de un empleado existente y luego eliminándolo, validando que no vuelva a aparecer en consulta.

**Acceptance Scenarios**:

1. **Given** un empleado existente, **When** se actualizan nombre, dirección o teléfono con valores válidos, **Then** el sistema guarda los cambios y los refleja en consultas posteriores.
2. **Given** un empleado existente, **When** se elimina por su clave, **Then** el sistema confirma la eliminación y la consulta por esa clave indica que ya no existe.

### Edge Cases

- Intento de crear o actualizar con `nombre`, `direccion` o `telefono` con más de 100 caracteres.
- Intento de crear o actualizar con campos obligatorios vacíos o solo espacios.
- Consulta, actualización o eliminación de clave inexistente.
- Envío de clave nula o no válida en operaciones de consulta, actualización o eliminación.
- Consulta, actualización o eliminación con formato de clave inválido (distinto de `E-` + número).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema MUST permitir registrar empleados con los campos `nombre`, `direccion` y `telefono`.
- **FR-002**: El campo `clave` MUST ser autogenerado por el sistema, único, con formato `E-` + número secuencial con padding mínimo de 3 dígitos, y funcionar como identificador primario del empleado.
- **FR-003**: Los campos `nombre`, `direccion` y `telefono` MUST aceptar como máximo 100 caracteres cada uno.
- **FR-004**: El sistema MUST rechazar altas o actualizaciones cuando `nombre`, `direccion` o `telefono` excedan 100 caracteres.
- **FR-005**: El sistema MUST rechazar altas o actualizaciones cuando `nombre`, `direccion` o `telefono` estén vacíos.
- **FR-006**: El sistema MUST permitir consultar un empleado por su `clave`.
- **FR-007**: El sistema MUST permitir listar empleados registrados.
- **FR-008**: El sistema MUST permitir actualizar `nombre`, `direccion` y `telefono` de un empleado existente.
- **FR-009**: El sistema MUST permitir eliminar físicamente (hard delete) un empleado por su `clave`.
- **FR-010**: El sistema MUST devolver mensajes de error claros para clave duplicada, validaciones fallidas y registros no encontrados.
- **FR-011**: El sistema MUST requerir autenticación para todas las operaciones del CRUD de empleados.
- **FR-012**: La documentación funcional del API MUST reflejar todas las operaciones y validaciones del CRUD de empleados.
- **FR-013**: El sistema MUST rechazar solicitudes de alta que incluyan manualmente el campo `clave`.
- **FR-014**: En entorno de desarrollo, los endpoints `/swagger-ui` y `/api-docs` MUST permanecer públicos; las operaciones CRUD de empleados MUST mantenerse autenticadas.
- **FR-015**: La numeración secuencial de `clave` MUST iniciar en `001` y anteponerse con `E-` para su representación externa (ej. `E-001`, `E-002`).

### Key Entities *(include if feature involves data)*

- **Empleado**: Representa una persona registrada en el padrón con `clave` (identificador primario único autogenerado en formato `E-` + secuencia, ej. `E-001`), `nombre` (máximo 100), `direccion` (máximo 100) y `telefono` (máximo 100).

### Assumptions

- `clave` se maneja como identificador primario autogenerado con formato visible `E-` + secuencia; no es editable por cliente.
- No se requiere paginación ni filtros avanzados para el listado en esta versión.
- El CRUD aplica a un único tipo de usuario autenticado sin roles diferenciados.
- La exposición pública de `/swagger-ui` y `/api-docs` aplica solo en desarrollo.
- La operación de borrado elimina definitivamente el registro y no requiere recuperación lógica en esta versión.

### Dependencies

- Disponibilidad del mecanismo de autenticación del proyecto para proteger endpoints.
- Disponibilidad del almacenamiento persistente del proyecto para conservar empleados.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 100% de operaciones de alta válidas crean un empleado persistente consultable por su clave en formato `E-` + secuencia.
- **SC-002**: El 100% de intentos con `nombre`, `direccion` o `telefono` mayores a 100 caracteres son rechazados con mensaje de validación.
- **SC-003**: Al menos el 95% de consultas por clave existente responden en menos de 2 segundos bajo carga operativa normal.
- **SC-004**: El 100% de operaciones de eliminación exitosas impiden que el registro eliminado aparezca en consultas posteriores.
- **SC-005**: En pruebas funcionales del flujo principal (crear, consultar, actualizar, eliminar), al menos el 90% de casos se completan sin intervención manual adicional.
