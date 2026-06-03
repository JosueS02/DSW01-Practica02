# Tasks: CRUD de Departamentos Relacionado con Empleados

**Input**: Design documents from `/specs/002-departamentos-crud/`
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/openapi.yaml`, `quickstart.md`

**Tests**: Se incluyen tareas de pruebas porque la especificación define criterios de prueba independientes por historia.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Alinear base técnica y documentación operativa del feature.

- [X] T001 Verificar dependencias de Spring Boot 3/Java 17/Flyway/PostgreSQL en `pom.xml`
- [X] T002 [P] Verificar variables `APP_DB_*` y `APP_SECURITY_*` en `src/main/resources/application.properties`
- [X] T003 [P] Validar servicios `app` y `postgres` para ejecución local en `docker/docker-compose.yml`
- [X] T004 Sincronizar pasos de ejecución y validación manual en `specs/002-departamentos-crud/quickstart.md`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Construir infraestructura de datos y dominio que bloquea todas las historias.

**⚠️ CRITICAL**: Ninguna historia de usuario inicia antes de completar esta fase.

- [X] T005 Crear migración para `departamentos` con `id` formato `D-001` y FK desde `empleados` en `src/main/resources/db/migration/V2__create_departamentos_and_relation.sql`
- [X] T006 Implementar entidad `DepartamentoEntity` con `id` `String` y reglas de formato en `src/main/java/com/example/empleados/domain/DepartamentoEntity.java`
- [X] T007 [P] Implementar generador de ID secuencial `D-001` en `src/main/java/com/example/empleados/service/DepartamentoIdGenerator.java`
- [X] T008 Ajustar relación `ManyToOne` hacia departamento en `src/main/java/com/example/empleados/domain/EmpleadoEntity.java`
- [X] T009 [P] Implementar repositorio de departamentos con búsquedas por `id` y `nombre` en `src/main/java/com/example/empleados/repository/DepartamentoRepository.java`
- [X] T010 [P] Agregar consulta de existencia por departamento en `src/main/java/com/example/empleados/repository/EmpleadoRepository.java`
- [X] T011 Crear DTOs base de departamentos (`Create`, `Update`, `Response`, `Resumen`) en `src/main/java/com/example/empleados/dto/`
- [X] T012 Ajustar mapeo de errores para `400`, `404` y `409` en `src/main/java/com/example/empleados/config/GlobalExceptionHandler.java`

**Checkpoint**: Datos y dominio listos para implementar historias de usuario.

---

## Phase 3: User Story 1 - Alta y consulta de departamentos (Priority: P1) 🎯 MVP

**Goal**: Permitir crear departamentos y consultarlos por ID y listado completo.

**Independent Test**: Crear un departamento y consultar `GET /api/departamentos` y `GET /api/departamentos/{id}` devolviendo `id` con formato `D-001`.

### Tests for User Story 1

- [X] T013 [P] [US1] Crear pruebas de contrato para `POST` y `GET` de departamentos en `src/test/java/com/example/empleados/contract/DepartamentoReadCreateContractTest.java`
- [X] T014 [P] [US1] Crear prueba de integración de alta y consulta de departamentos en `src/test/java/com/example/empleados/integration/DepartamentoCreateReadIntegrationTest.java`

### Implementation for User Story 1

- [X] T015 [US1] Implementar creación y consultas en `src/main/java/com/example/empleados/service/DepartamentoService.java`
- [X] T016 [US1] Implementar endpoints `POST /api/departamentos`, `GET /api/departamentos` y `GET /api/departamentos/{id}` en `src/main/java/com/example/empleados/controller/DepartamentoController.java`
- [X] T017 [US1] Aplicar validaciones de `nombre`/`descripcion` en `src/main/java/com/example/empleados/dto/CreateDepartamentoRequest.java`
- [X] T018 [US1] Publicar contrato de US1 con IDs `D-001` en `specs/002-departamentos-crud/contracts/openapi.yaml`

**Checkpoint**: US1 funcional y demostrable de forma independiente como MVP.

---

## Phase 4: User Story 2 - Edición y eliminación de departamentos (Priority: P2)

**Goal**: Permitir actualizar y eliminar departamentos con regla de bloqueo por empleados vinculados.

**Independent Test**: Actualizar un departamento existente y eliminar uno sin empleados; eliminar uno con empleados debe devolver `409`.

### Tests for User Story 2

- [X] T019 [P] [US2] Crear pruebas de contrato para `PUT` y `DELETE` de departamentos en `src/test/java/com/example/empleados/contract/DepartamentoWriteContractTest.java`
- [X] T020 [P] [US2] Crear pruebas de integración para actualización y eliminación de departamentos en `src/test/java/com/example/empleados/integration/DepartamentoWriteIntegrationTest.java`
- [X] T021 [P] [US2] Crear prueba de integración para bloqueo `409` al eliminar con empleados asociados en `src/test/java/com/example/empleados/integration/DepartamentoDeleteWithEmpleadosIntegrationTest.java`

### Implementation for User Story 2

- [X] T022 [US2] Implementar actualización de departamento en `src/main/java/com/example/empleados/service/DepartamentoService.java`
- [X] T023 [US2] Implementar eliminación condicionada por empleados asociados en `src/main/java/com/example/empleados/service/DepartamentoService.java`
- [X] T024 [US2] Exponer endpoints `PUT /api/departamentos/{id}` y `DELETE /api/departamentos/{id}` en `src/main/java/com/example/empleados/controller/DepartamentoController.java`
- [X] T025 [US2] Ajustar mensajes de error de conflicto y no encontrado en `src/main/java/com/example/empleados/config/GlobalExceptionHandler.java`
- [X] T026 [US2] Publicar respuestas `404/409` de US2 en `specs/002-departamentos-crud/contracts/openapi.yaml`

**Checkpoint**: US2 funcional e independiente con regla de negocio de eliminación aplicada.

---

## Phase 5: User Story 3 - Relación de empleados con departamento (Priority: P3)

**Goal**: Exigir `departamentoId` válido (`D-001`) al crear/actualizar empleados y devolver resumen de departamento en lectura.

**Independent Test**: Crear y actualizar empleado con `departamentoId` válido; rechazar `departamentoId` inexistente o formato inválido.

### Tests for User Story 3

- [X] T027 [P] [US3] Crear pruebas de contrato para payloads de empleados con `departamentoId` `D-001` en `src/test/java/com/example/empleados/contract/EmpleadoDepartamentoContractTest.java`
- [X] T028 [P] [US3] Crear pruebas de integración de alta/edición de empleados con departamento válido en `src/test/java/com/example/empleados/integration/EmpleadoDepartamentoIntegrationTest.java`
- [X] T029 [P] [US3] Crear pruebas unitarias de validación de formato `D-001` en `src/test/java/com/example/empleados/unit/DepartamentoIdFormatValidatorTest.java`

### Implementation for User Story 3

- [X] T030 [US3] Requerir y validar `departamentoId` en creación de empleados en `src/main/java/com/example/empleados/dto/CreateEmpleadoRequest.java`
- [X] T031 [US3] Requerir y validar `departamentoId` en actualización de empleados en `src/main/java/com/example/empleados/dto/UpdateEmpleadoRequest.java`
- [X] T032 [US3] Incluir resumen de departamento en `EmpleadoResponse` en `src/main/java/com/example/empleados/dto/EmpleadoResponse.java`
- [X] T033 [US3] Validar existencia de departamento y mapear relación en `src/main/java/com/example/empleados/service/EmpleadoService.java`
- [X] T034 [US3] Ajustar endpoints de empleados al nuevo contrato en `src/main/java/com/example/empleados/controller/EmpleadoController.java`
- [X] T035 [US3] Publicar contrato actualizado de empleados relacionados en `specs/002-departamentos-crud/contracts/openapi.yaml`

**Checkpoint**: US3 funcional e independiente, con relación empleado-departamento validada extremo a extremo.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Completar validaciones transversales, documentación final y verificación de calidad.

- [X] T036 [P] Alinear `data-model.md` con implementación final de IDs `D-001` en `specs/002-departamentos-crud/data-model.md`
- [X] T037 [P] Alinear decisiones finales y tradeoffs en `specs/002-departamentos-crud/research.md`
- [X] T038 Verificar reglas de acceso Basic Auth para `/api/departamentos` y `/api/empleados` en `src/main/java/com/example/empleados/config/SecurityConfig.java`
- [X] T039 Ejecutar pruebas completas del feature y revisar reportes en `target/surefire-reports/`
- [X] T040 Verificar publicación de Swagger en `/api-docs` y `/swagger-ui.html` en `src/main/resources/application.properties`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: inicia sin dependencias.
- **Phase 2 (Foundational)**: depende de Phase 1 y bloquea todo trabajo de historias.
- **Phase 3 (US1)**: depende de Phase 2; entrega el MVP.
- **Phase 4 (US2)**: depende de Phase 2 y usa componentes de departamentos de US1.
- **Phase 5 (US3)**: depende de Phase 2 y de los componentes de departamentos ya operativos.
- **Phase 6 (Polish)**: depende de todas las historias objetivo completadas.

### User Story Dependencies

- **US1 (P1)**: puede implementarse inmediatamente tras Foundational.
- **US2 (P2)**: requiere dominio de departamentos implementado (US1).
- **US3 (P3)**: requiere dominio de departamentos implementado (US1/US2) para validar relación completa.

### Within Each User Story

- Pruebas (siempre que estén incluidas) antes de implementación.
- DTOs/modelos antes de servicios.
- Servicios antes de controladores.
- Contrato OpenAPI sincronizado al cierre de cada historia.

### Parallel Opportunities

- Setup: `T002` y `T003`.
- Foundational: `T007`, `T009` y `T010` tras `T006`.
- US1: `T013` y `T014`.
- US2: `T019`, `T020` y `T021`.
- US3: `T027`, `T028` y `T029`.
- Polish: `T036` y `T037`.

---

## Parallel Example: User Story 1

```bash
# Ejecutar en paralelo pruebas de US1:
T013 src/test/java/com/example/empleados/contract/DepartamentoReadCreateContractTest.java
T014 src/test/java/com/example/empleados/integration/DepartamentoCreateReadIntegrationTest.java

# Ejecutar en paralelo tareas base de configuración:
T002 src/main/resources/application.properties
T003 docker/docker-compose.yml
```

---

## Parallel Example: User Story 2

```bash
# Ejecutar en paralelo pruebas de US2:
T019 src/test/java/com/example/empleados/contract/DepartamentoWriteContractTest.java
T020 src/test/java/com/example/empleados/integration/DepartamentoWriteIntegrationTest.java
T021 src/test/java/com/example/empleados/integration/DepartamentoDeleteWithEmpleadosIntegrationTest.java
```

---

## Parallel Example: User Story 3

```bash
# Ejecutar en paralelo pruebas de US3:
T027 src/test/java/com/example/empleados/contract/EmpleadoDepartamentoContractTest.java
T028 src/test/java/com/example/empleados/integration/EmpleadoDepartamentoIntegrationTest.java
T029 src/test/java/com/example/empleados/unit/DepartamentoIdFormatValidatorTest.java

# Ejecutar en paralelo ajustes de DTOs:
T030 src/main/java/com/example/empleados/dto/CreateEmpleadoRequest.java
T031 src/main/java/com/example/empleados/dto/UpdateEmpleadoRequest.java
T032 src/main/java/com/example/empleados/dto/EmpleadoResponse.java
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1.
2. Completar Phase 2.
3. Completar Phase 3 (US1).
4. Validar criterio independiente de US1 con IDs `D-001`.

### Incremental Delivery

1. Entregar MVP con US1.
2. Añadir US2 y validar edición/eliminación con `409`.
3. Añadir US3 y validar relación obligatoria empleado-departamento.
4. Ejecutar Phase 6 para cierre transversal.

### Parallel Team Strategy

1. Todo el equipo completa Phase 1 y 2.
2. Luego trabajo paralelo por historia:
   - Dev A: US1
   - Dev B: US2
   - Dev C: US3
3. Integración final y validación de calidad en Phase 6.
