# Implementation Plan: CRUD de Departamentos Relacionado con Empleados

**Branch**: `002-departamentos-crud` | **Date**: 2026-03-10 | **Spec**: `/specs/002-departamentos-crud/spec.md`
**Input**: Feature specification from `/specs/002-departamentos-crud/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implementar y mantener el CRUD de departamentos relacionado con empleados, asegurando que cada empleado pertenezca a un departamento existente y que el identificador de departamento use formato `D-001`. El enfoque técnico usa Spring Boot 3 + Java 17, Flyway para evolución de esquema, PostgreSQL en Docker para validación, Basic Auth en endpoints de negocio y contrato OpenAPI sincronizado.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3 (Web, Validation, Data JPA, Security), Flyway, springdoc-openapi, PostgreSQL JDBC  
**Storage**: PostgreSQL 16  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, Testcontainers (PostgreSQL)  
**Target Platform**: Linux server + Docker Compose local/CI  
**Project Type**: Backend web-service monolítico (REST API)  
**Performance Goals**: Operaciones CRUD de catálogo con latencia p95 <= 500 ms en entorno local estándar  
**Constraints**: Basic Auth obligatorio en `/api/*` de negocio, consistencia referencial estricta, compatibilidad con esquema existente y migraciones versionadas  
**Scale/Scope**: Catálogo organizacional de baja/mediana escala (decenas a miles de departamentos y empleados)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Stack gate: Uses Spring Boot 3 and Java 17 only.
- [x] Security gate: Defines HTTP Basic Auth behavior (protected/public endpoints,
  credential source, and non-dev override strategy).
- [x] Data gate: Uses PostgreSQL and defines Docker runtime (`docker-compose` or
  equivalent) for local/CI validation.
- [x] API contract gate: Includes OpenAPI/Swagger update scope for all changed
  endpoints/models/errors.
- [x] Config gate: Lists required `application.properties` keys and identifies
  sensitive values externalized via env/secrets.

### Post-Design Re-check

- [x] Stack gate: Sin cambios fuera de Spring Boot 3 + Java 17.
- [x] Security gate: Contrato/documentación mantienen Basic Auth en endpoints de negocio.
- [x] Data gate: Modelo y quickstart mantienen PostgreSQL + Docker Compose.
- [x] API contract gate: `contracts/openapi.yaml` actualizado con `departamentoId` formato `D-001`.
- [x] Config gate: Sin nuevos secretos hardcodeados; configuración sigue externalizada por variables de entorno.

## Project Structure

### Documentation (this feature)

```text
specs/002-departamentos-crud/
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
├── main/
│   ├── java/com/example/empleados/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── domain/
│   │   ├── dto/
│   │   └── config/
│   └── resources/
│       ├── application.properties
│       └── db/migration/
└── test/
  └── java/com/example/empleados/

docker/
└── docker-compose.yml
```

**Structure Decision**: Se mantiene un único backend Spring Boot monolítico con capas controller/service/repository/domain, pruebas en `src/test` y entorno local/CI con PostgreSQL en `docker/docker-compose.yml`.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |
