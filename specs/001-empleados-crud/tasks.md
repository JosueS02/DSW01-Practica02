# Tasks: CRUD de Empleados

**Input**: Design documents from `/specs/001-empleados-crud/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Se incluyen tareas de pruebas de contrato, unitarias e integración para cumplir validación constitucional y criterios de calidad del feature.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Inicialización del proyecto y estructura base Spring Boot

- [X] T001 Crear estructura base del proyecto en `src/main/java/com/example/empleados/` y `src/test/java/com/example/empleados/`
- [X] T002 Inicializar dependencias Spring Boot 3 + Java 17 en `pom.xml`
- [X] T003 [P] Configurar `application.properties` base en `src/main/resources/application.properties`
- [X] T004 [P] Crear configuración Docker de PostgreSQL en `docker/docker-compose.yml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Infraestructura bloqueante para todas las historias

**⚠️ CRITICAL**: Ninguna historia de usuario debe iniciar antes de completar esta fase

- [X] T005 Configurar entidad persistente base `EmpleadoEntity` en `src/main/java/com/example/empleados/domain/EmpleadoEntity.java`
- [X] T006 [P] Implementar repositorio base en `src/main/java/com/example/empleados/repository/EmpleadoRepository.java`
- [X] T007 [P] Configurar seguridad HTTP Basic y reglas para `/swagger-ui` + `/api-docs` en `src/main/java/com/example/empleados/config/SecurityConfig.java`
- [X] T008 [P] Implementar manejo global de errores en `src/main/java/com/example/empleados/config/GlobalExceptionHandler.java`
- [X] T009 Implementar generador secuencial de clave formato `E-001` en `src/main/java/com/example/empleados/service/ClaveEmpleadoGenerator.java`
- [X] T010 Habilitar integración Swagger/OpenAPI en `src/main/java/com/example/empleados/config/OpenApiConfig.java`
- [X] T011 Definir DTOs base de request/response en `src/main/java/com/example/empleados/dto/CreateEmpleadoRequest.java`, `src/main/java/com/example/empleados/dto/UpdateEmpleadoRequest.java` y `src/main/java/com/example/empleados/dto/EmpleadoResponse.java`
- [X] T012 Configurar Flyway en `pom.xml` y `src/main/resources/application.properties`
- [X] T013 Crear migración inicial de tabla empleados en `src/main/resources/db/migration/V1__create_empleados.sql`

**Checkpoint**: Foundation ready - implementación por historias puede comenzar

---

## Phase 3: User Story 1 - Registrar empleado (Priority: P1) 🎯 MVP

**Goal**: Permitir alta de empleados con `clave` autogenerada en formato `E-001`

**Independent Test**: Crear empleado sin `clave` y verificar respuesta con `clave` autogenerada válida; enviar `clave` manual en alta y verificar rechazo

### Tests for User Story 1

- [X] T014 [P] [US1] Crear prueba de contrato para `POST /api/empleados` en `src/test/java/com/example/empleados/contract/EmpleadoCreateContractTest.java`
- [X] T015 [P] [US1] Crear prueba de integración de alta con clave `E-001` en `src/test/java/com/example/empleados/integration/EmpleadoCreateIntegrationTest.java`
- [X] T016 [P] [US1] Crear prueba unitaria de generación secuencial de clave en `src/test/java/com/example/empleados/unit/ClaveEmpleadoGeneratorTest.java`

### Implementation for User Story 1

- [X] T017 [US1] Implementar validaciones de entrada (no vacío, máximo 100) en `src/main/java/com/example/empleados/dto/CreateEmpleadoRequest.java`
- [X] T018 [US1] Implementar lógica de creación y mapeo a response en `src/main/java/com/example/empleados/service/EmpleadoService.java`
- [X] T019 [US1] Implementar endpoint `POST /api/empleados` en `src/main/java/com/example/empleados/controller/EmpleadoController.java`
- [X] T020 [US1] Actualizar contrato de creación y errores en `specs/001-empleados-crud/contracts/openapi.yaml`

**Checkpoint**: US1 funcional e independiente (MVP)

---

## Phase 4: User Story 2 - Consultar empleados (Priority: P2)

**Goal**: Consultar empleado por `clave` y listar empleados

**Independent Test**: Consultar `GET /api/empleados/{clave}` con una clave existente (`E-001`) y ejecutar `GET /api/empleados` obteniendo colección válida

### Tests for User Story 2

- [X] T021 [P] [US2] Crear prueba de contrato para `GET /api/empleados` y `GET /api/empleados/{clave}` en `src/test/java/com/example/empleados/contract/EmpleadoReadContractTest.java`
- [X] T022 [P] [US2] Crear prueba de integración de consulta por clave existente en `src/test/java/com/example/empleados/integration/EmpleadoReadIntegrationTest.java`
- [X] T023 [P] [US2] Crear prueba unitaria de validación de formato `^E-[0-9]{3,}$` en `src/test/java/com/example/empleados/unit/ClaveFormatValidatorTest.java`

### Implementation for User Story 2

- [X] T024 [US2] Implementar consulta por clave y listado en `src/main/java/com/example/empleados/service/EmpleadoService.java`
- [X] T025 [US2] Implementar endpoints `GET /api/empleados` y `GET /api/empleados/{clave}` en `src/main/java/com/example/empleados/controller/EmpleadoController.java`
- [X] T026 [US2] Implementar validación de formato de clave `^E-[0-9]{3,}$` para path params en `src/main/java/com/example/empleados/controller/EmpleadoController.java`
- [X] T027 [US2] Actualizar contrato OpenAPI de consultas y `404` en `specs/001-empleados-crud/contracts/openapi.yaml`

**Checkpoint**: US1 y US2 funcionales e independientes

---

## Phase 5: User Story 3 - Actualizar y eliminar empleado (Priority: P3)

**Goal**: Mantener el padrón con actualización y eliminación física

**Independent Test**: Actualizar un empleado existente y validar cambios; eliminar por clave y verificar no existencia posterior

### Tests for User Story 3

- [X] T028 [P] [US3] Crear prueba de contrato para `PUT /api/empleados/{clave}` y `DELETE /api/empleados/{clave}` en `src/test/java/com/example/empleados/contract/EmpleadoWriteContractTest.java`
- [X] T029 [P] [US3] Crear prueba de integración de actualización y hard delete en `src/test/java/com/example/empleados/integration/EmpleadoWriteIntegrationTest.java`
- [X] T030 [P] [US3] Crear prueba unitaria de reglas de actualización de campos en `src/test/java/com/example/empleados/unit/EmpleadoUpdateValidationTest.java`

### Implementation for User Story 3

- [X] T031 [US3] Implementar lógica de actualización y hard delete en `src/main/java/com/example/empleados/service/EmpleadoService.java`
- [X] T032 [US3] Implementar endpoints `PUT /api/empleados/{clave}` y `DELETE /api/empleados/{clave}` en `src/main/java/com/example/empleados/controller/EmpleadoController.java`
- [X] T033 [US3] Implementar validaciones de actualización en `src/main/java/com/example/empleados/dto/UpdateEmpleadoRequest.java`
- [X] T034 [US3] Actualizar contrato OpenAPI de actualización/eliminación en `specs/001-empleados-crud/contracts/openapi.yaml`

**Checkpoint**: Todas las historias funcionales e independientes

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Ajustes transversales y verificación final

- [X] T035 [P] Documentar ejecución y verificación final en `specs/001-empleados-crud/quickstart.md`
- [X] T036 Endurecer configuración de credenciales por entorno en `src/main/resources/application.properties`
- [X] T037 [P] Alinear documentación de diseño final en `specs/001-empleados-crud/plan.md` y `specs/001-empleados-crud/data-model.md`
- [X] T038 Ejecutar suite de pruebas unitarias e integración y registrar evidencia en `specs/001-empleados-crud/quickstart.md`
- [X] T039 Validar flujo completo CRUD + seguridad + Swagger en `specs/001-empleados-crud/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: sin dependencias
- **Phase 2 (Foundational)**: depende de Phase 1; bloquea todas las historias
- **Phase 3-5 (User Stories)**: dependen de Phase 2
- **Phase 6 (Polish)**: depende de historias completadas

### User Story Dependencies

- **US1 (P1)**: inicia después de Foundation; define MVP
- **US2 (P2)**: inicia después de Foundation; depende funcionalmente de datos creados por US1 para validación completa
- **US3 (P3)**: inicia después de Foundation; depende funcionalmente de existencia de registros (US1/US2)

### Parallel Opportunities

- **Setup**: T003 y T004 en paralelo
- **Foundational**: T006, T007 y T008 en paralelo
- **US1**: T014, T015 y T016 en paralelo; luego T019 y T020 en paralelo tras T018
- **US2**: T021, T022 y T023 en paralelo; luego T026 y T027 en paralelo tras T025
- **US3**: T028, T029 y T030 en paralelo; luego T033 y T034 en paralelo tras T032
- **Polish**: T035 y T037 en paralelo

---

## Parallel Example: User Story 1

```bash
Task: "T014 [US1] Crear prueba de contrato POST en src/test/java/com/example/empleados/contract/EmpleadoCreateContractTest.java"
Task: "T015 [US1] Crear prueba de integración alta en src/test/java/com/example/empleados/integration/EmpleadoCreateIntegrationTest.java"
Task: "T016 [US1] Crear prueba unitaria de clave en src/test/java/com/example/empleados/unit/ClaveEmpleadoGeneratorTest.java"
```

## Parallel Example: User Story 2

```bash
Task: "T021 [US2] Crear prueba de contrato de lectura en src/test/java/com/example/empleados/contract/EmpleadoReadContractTest.java"
Task: "T022 [US2] Crear prueba de integración de lectura en src/test/java/com/example/empleados/integration/EmpleadoReadIntegrationTest.java"
Task: "T023 [US2] Crear prueba unitaria de formato de clave en src/test/java/com/example/empleados/unit/ClaveFormatValidatorTest.java"
```

## Parallel Example: User Story 3

```bash
Task: "T028 [US3] Crear prueba de contrato de escritura en src/test/java/com/example/empleados/contract/EmpleadoWriteContractTest.java"
Task: "T029 [US3] Crear prueba de integración de update/delete en src/test/java/com/example/empleados/integration/EmpleadoWriteIntegrationTest.java"
Task: "T030 [US3] Crear prueba unitaria de validaciones update en src/test/java/com/example/empleados/unit/EmpleadoUpdateValidationTest.java"
```

---

## Implementation Strategy

### MVP First (US1 only)

1. Completar Phase 1 (Setup)
2. Completar Phase 2 (Foundational)
3. Completar Phase 3 (US1)
4. Validar creación con `clave` formato `E-001`

### Incremental Delivery

1. MVP con US1
2. Añadir US2 (consultas)
3. Añadir US3 (actualizar/eliminar)
4. Cerrar con Phase 6 (polish)

### Suggested MVP Scope

- **MVP recomendado**: exclusivamente **User Story 1 (Registrar empleado)** después de Setup + Foundational
