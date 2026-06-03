# Feature Specification: CRUD de Departamentos Relacionado con Empleados

**Feature Branch**: `002-departamentos-crud`  
**Created**: 2026-03-10  
**Status**: Draft  
**Input**: User description: "Quiero que me generes un crud de departamentos relacionado a la tabla de empleados"

## Clarifications

### Session 2026-03-10

- Q: ¿`descripcion` de departamento debe ser obligatoria? → A: `nombre` obligatorio; `descripcion` opcional en create y en update (puede omitirse).
- Q: ¿Qué código de error devolver al eliminar un departamento con empleados asociados? → A: `409 Conflict`.
- Q: ¿`departamentoId` en empleados debe ser obligatorio en alta y actualización? → A: Sí, obligatorio en create y update.
- Q: ¿El listado de departamentos debe llevar paginación en esta versión? → A: No, listado completo sin paginación en esta versión.
- Q: ¿Qué alcance de autenticación aplica a los endpoints? → A: `/api/departamentos` y `/api/empleados` con Basic Auth; `/swagger-ui` y `/api-docs` públicos solo en desarrollo.
- Q: ¿Qué formato debe tener el identificador de departamento? → A: Formato `D-` + al menos 3 dígitos (iniciando en `D-001`, y creciendo a `D-1000`, etc.).
- Q: ¿Cómo se genera el identificador `D-001`? → A: Secuencia global ascendente (`D-001`, `D-002`, ...) sin reutilizar IDs eliminados.
- Q: ¿Dónde se genera el correlativo `D-###` para evitar colisiones? → A: En PostgreSQL con mecanismo atómico (secuencia/contador transaccional) como fuente de verdad.
- Q: ¿Qué pasa al superar `D-999`? → A: El identificador debe crecer a más dígitos (`D-1000`, `D-1001`, ...) sin reinicio.
- Q: ¿Qué código de error devuelve nombre de departamento duplicado? → A: `409 Conflict`.

## User Scenarios & Testing *(mandatory)*

<!--
  IMPORTANT: User stories should be PRIORITIZED as user journeys ordered by importance.
  Each user story/journey must be INDEPENDENTLY TESTABLE - meaning if you implement just ONE of them,
  you should still have a viable MVP (Minimum Viable Product) that delivers value.
  
  Assign priorities (P1, P2, P3, etc.) to each story, where P1 is the most critical.
  Think of each story as a standalone slice of functionality that can be:
  - Developed independently
  - Tested independently
  - Deployed independently
  - Demonstrated to users independently
-->

### User Story 1 - Alta y consulta de departamentos (Priority: P1)

Como usuario administrativo, quiero registrar departamentos y consultarlos para mantener una estructura organizacional clara.

**Why this priority**: Sin departamentos no existe base organizativa para asociar empleados, por lo que es el valor inicial más crítico.

**Independent Test**: Puede probarse de forma independiente creando un departamento, consultándolo por identificador y listándolo junto con otros departamentos.

**Acceptance Scenarios**:

1. **Given** que no existe un departamento con el nombre "Recursos Humanos", **When** el usuario registra ese departamento, **Then** el sistema crea el departamento y devuelve sus datos completos.
2. **Given** que existen departamentos registrados, **When** el usuario solicita el listado, **Then** el sistema devuelve todos los departamentos registrados.
3. **Given** que existe un departamento, **When** el usuario lo consulta por su identificador, **Then** el sistema devuelve la información del departamento solicitado.

---

### User Story 2 - Edición y eliminación de departamentos (Priority: P2)

Como usuario administrativo, quiero actualizar y eliminar departamentos para mantener información vigente y evitar catálogos obsoletos.

**Why this priority**: Completa el ciclo de vida del catálogo y evita datos desactualizados que impactan reportes y operación.

**Independent Test**: Puede probarse actualizando el nombre de un departamento existente y eliminando otro que no tenga empleados asociados.

**Acceptance Scenarios**:

1. **Given** que existe un departamento, **When** el usuario actualiza su nombre o descripción, **Then** el sistema guarda los cambios y devuelve la versión actualizada.
2. **Given** que existe un departamento sin empleados asociados, **When** el usuario lo elimina, **Then** el sistema elimina el registro y deja de mostrarlo en consultas.

---

### User Story 3 - Relación de empleados con departamento (Priority: P3)

Como usuario administrativo, quiero asociar cada empleado a un departamento existente para consultar correctamente su ubicación organizacional.

**Why this priority**: Aporta trazabilidad organizacional y permite que la información de empleados tenga contexto funcional.

**Independent Test**: Puede probarse asignando un departamento válido al crear o actualizar un empleado y verificando que la relación aparezca al consultar empleados.

**Acceptance Scenarios**:

1. **Given** que existe un departamento válido, **When** el usuario registra un empleado con ese departamento, **Then** el empleado queda asociado correctamente.
2. **Given** que un empleado ya existe, **When** el usuario cambia su departamento por otro válido, **Then** el sistema actualiza la asociación sin perder los demás datos del empleado.
3. **Given** que un departamento tiene empleados asociados, **When** el usuario intenta eliminarlo, **Then** el sistema rechaza la operación e indica que hay empleados vinculados.

---

### Edge Cases

- Intento de crear un departamento con nombre duplicado.
- Intento de consultar, actualizar o eliminar un departamento inexistente.
- Intento de asociar un empleado a un departamento inexistente.
- Intento de eliminar un departamento con uno o más empleados relacionados.
- Registro o actualización con campos obligatorios vacíos o con formato inválido (`nombre` obligatorio, `descripcion` opcional).
- Intento de usar un identificador de departamento con formato distinto a `D-` seguido de al menos 3 dígitos (ej. `D-001`, `D-1000`).
- Eliminación de un departamento y creación posterior de otro NO debe reutilizar el identificador eliminado.
- Creaciones concurrentes de departamentos NO deben producir IDs duplicados.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE permitir crear departamentos con `nombre` obligatorio y `descripcion` opcional.
- **FR-002**: El sistema DEBE impedir la creación de departamentos con nombres duplicados.
- **FR-003**: El sistema DEBE permitir consultar un departamento por su identificador.
- **FR-004**: El sistema DEBE permitir listar todos los departamentos registrados en una respuesta completa (sin paginación en esta versión).
- **FR-005**: El sistema DEBE permitir actualizar los datos de un departamento existente.
- **FR-006**: El sistema DEBE permitir eliminar un departamento únicamente cuando no tenga empleados asociados.
- **FR-007**: El sistema DEBE permitir asociar cada empleado a un único departamento existente.
- **FR-008**: El sistema DEBE rechazar la creación o actualización de empleados cuando el departamento indicado no exista.
- **FR-009**: El sistema DEBE mostrar la referencia del departamento al consultar la información de un empleado.
- **FR-010**: El sistema DEBE devolver mensajes de validación claros ante datos inválidos o recursos no encontrados.
- **FR-011**: El sistema DEBE mantener consistencia referencial entre departamentos y empleados en operaciones de alta, actualización y baja.
- **FR-012**: El sistema DEBE responder con conflicto (`409`) cuando se intente eliminar un departamento con empleados asociados.
- **FR-013**: El sistema DEBE requerir `departamentoId` en la creación y actualización de empleados.
- **FR-014**: El sistema DEBE proteger `/api/departamentos` y `/api/empleados` con HTTP Basic Auth, y permitir acceso público a `/swagger-ui` y `/api-docs` únicamente en desarrollo.
- **FR-015**: El sistema DEBE manejar el identificador de departamento con formato `D-` seguido de al menos 3 dígitos (iniciando en `D-001`).
- **FR-016**: El sistema DEBE generar `id` de departamento en secuencia global ascendente (`D-001`, `D-002`, ...) y NO DEBE reutilizar IDs eliminados.
- **FR-017**: El sistema DEBE generar `id` de departamento en PostgreSQL usando un mecanismo atómico (secuencia/contador transaccional) para evitar duplicados en concurrencia.
- **FR-018**: El sistema DEBE responder con conflicto (`409`) cuando se intente crear o actualizar un departamento con `nombre` duplicado.

### Key Entities *(include if feature involves data)*

- **Departamento**: Unidad organizacional con identificador único en formato `D-` seguido de al menos 3 dígitos (ej. `D-001`, `D-1000`), generado por secuencia global ascendente sin reutilización tras eliminación, nombre único y `descripcion` opcional; puede estar asociado a múltiples empleados.
- **Empleado**: Persona registrada en la organización con datos de identificación y contacto; pertenece a un único departamento.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: El 95% de las operaciones de alta, consulta, actualización y eliminación de departamentos se completan exitosamente en el primer intento con datos válidos.
- **SC-002**: El 100% de los empleados creados o actualizados quedan asociados a un departamento existente y válido.
- **SC-003**: El 100% de los intentos de eliminar departamentos con empleados asociados son bloqueados con mensaje de negocio claro.
- **SC-004**: Usuarios administrativos completan el flujo de gestión de un departamento (crear, consultar, actualizar y eliminar cuando aplique) en menos de 3 minutos en pruebas de aceptación.

## Assumptions

- La gestión de departamentos será utilizada por usuarios administrativos con permisos ya existentes en el sistema.
- Cada empleado pertenece a un solo departamento a la vez.
- El nombre del departamento es el atributo de negocio que debe ser único.
- No se requiere historial de cambios de departamentos para esta iteración.
