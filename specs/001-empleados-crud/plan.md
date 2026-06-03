# Implementation Plan: CRUD de Empleados

**Branch**: `001-empleados-crud` | **Date**: 2026-02-25 | **Spec**: `/specs/001-empleados-crud/spec.md`
**Input**: Feature specification from `/specs/001-empleados-crud/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implementar un CRUD de empleados con `clave` autogenerada en formato `E-001`,
campos `nombre`, `direccion` y `telefono` de hasta 100 caracteres, autenticación
HTTP Basic para operaciones de negocio, Swagger/OpenAPI público solo en desarrollo y
persistencia PostgreSQL ejecutada en Docker. El diseño seguirá arquitectura por capas
de Spring Boot 3 con Java 17 y contrato API explícito.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3 (Web, Validation, Data JPA, Security), PostgreSQL Driver, springdoc-openapi  
**Storage**: PostgreSQL  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, Testcontainers (PostgreSQL)  
**Target Platform**: Linux server y entorno local Linux con Docker  
**Project Type**: backend web-service monolítico  
**Performance Goals**: p95 < 2s en consulta por clave para carga operativa normal  
**Constraints**: `clave` autogenerada en formato `E-` + secuencia (`E-001`), hard delete, campos de texto máx. 100, `/swagger-ui` y `/api-docs` públicos solo en desarrollo  
**Scale/Scope**: CRUD de entidad única Empleado, sin paginación ni roles avanzados en esta iteración

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Stack gate: Uses Spring Boot 3 and Java 17 only.
- [x] Security gate: Defines HTTP Basic Auth for CRUD and public Swagger/docs only in development.
- [x] Data gate: Uses PostgreSQL and defines Docker runtime (`docker-compose`) for local/CI validation.
- [x] API contract gate: Includes OpenAPI/Swagger for CRUD endpoints, payload validation and errors.
- [x] Config gate: Lists required `application.properties` keys and externalization of sensitive values via environment.

**Initial Gate Assessment**: PASS. No constitutional violations before Phase 0.

**Post-Design Gate Assessment (after Phase 1 artifacts)**: PASS. Design artifacts
preserve Spring Boot 3/Java 17 baseline, Basic Auth policy, PostgreSQL + Docker,
Swagger contract sync and centralized configuration.

**Final Implementation Alignment (2026-03-03)**: El backend implementado conserva los
gates constitucionales y agrega externalización por entorno para credenciales
(`APP_DB_*`, `APP_SECURITY_*`) con defaults de desarrollo. Las pruebas de integración
con Testcontainers se ejecutan cuando Docker está disponible y se omiten de forma
controlada (`disabledWithoutDocker = true`) cuando el entorno no provee daemon.

## Project Structure

### Documentation (this feature)

```text
specs/001-empleados-crud/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
src/
└── main/
    ├── java/
    │   └── com/example/empleados/
    │       ├── controller/
    │       ├── service/
    │       ├── repository/
    │       ├── domain/
    │       ├── dto/
    │       └── config/
    └── resources/
        └── application.properties

src/test/
└── java/
    └── com/example/empleados/
        ├── unit/
        ├── integration/
        └── contract/

docker/
└── docker-compose.yml
```

**Structure Decision**: Se usa una estructura de web-service backend monolítico
con capas Spring estándar. No aplica separación frontend/backend ni arquitectura
multi-aplicación.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |
