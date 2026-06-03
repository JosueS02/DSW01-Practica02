# Implementation Plan: Login de Empleados con Credenciales

**Branch**: `003-empleados-login-credenciales` | **Date**: 2026-03-13 | **Spec**: `/specs/003-empleados-login-credenciales/spec.md`
**Input**: Feature specification from `/specs/003-empleados-login-credenciales/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Implementar frontend Angular 19 para login de empleados y extender el CRUD de empleados con `email`/`password` (con política de seguridad definida), manteniendo autorización de endpoints de negocio por HTTP Basic según constitución. El flujo UI usa `POST /auth/login` y `POST /auth/logout`, con lockout (5 fallos/15 min), sesión UI de 8 horas, sesión UI única por empleado, normalización de email (`trim + lowercase`) y auditoría de intentos con retención mínima de 90 días.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: Java 17 (backend), TypeScript 5 + Angular 19 (frontend)
**Primary Dependencies**: Spring Boot 3.4.x (Web, Security, Data JPA, Validation), Flyway, PostgreSQL JDBC, springdoc-openapi, Angular 19, Angular Router, RxJS
**Storage**: PostgreSQL 16
**Testing**: JUnit 5 + Spring Boot Test + MockMvc + Testcontainers (backend), Angular test runner (Karma/Jasmine) para frontend
**Target Platform**: Linux container runtime + navegadores modernos
**Project Type**: Monorepo web application (backend + frontend + shared contracts)
**Performance Goals**: Login p95 < 300ms; tiempo de interacción inicial de login < 2s en entorno local estándar
**Constraints**: HTTP Basic obligatorio en endpoints de negocio; lockout 5/15; sesión UI única y expiración 8h; retención de auditoría 90 días; no exponer contraseñas
**Scale/Scope**: Sistema interno de empleados (~5k cuentas activas)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] Stack gate: Uses Spring Boot 3 and Java 17 only.
- [x] Security gate: Defines HTTP Basic Auth behavior (protected/public endpoints,
  credential source, and non-dev override strategy).
- [x] Data gate: Uses PostgreSQL and defines Docker runtime (`docker-compose` or
  equivalent) for local/CI validation.
- [x] API contract gate: Includes OpenAPI/Swagger update scope for all changed
  endpoints/models/errors.
- [x] Monorepo/frontend gate: Defines monorepo impact and confirms Angular 19
  compatibility for frontend changes.
- [x] Config gate: Lists required `application.properties` keys and identifies
  sensitive values externalized via env/secrets.

### Post-Design Re-check

- [x] Stack gate: Backend permanece en Spring Boot 3 + Java 17.
- [x] Security gate: Endpoints de negocio bajo HTTP Basic; `POST /auth/login` público para flujo UI y `POST /auth/logout` para cierre de sesión UI.
- [x] Data gate: Persistencia y validación en PostgreSQL con Docker.
- [x] API contract gate: OpenAPI actualizado con auth UI y campos de credenciales en CRUD de empleados.
- [x] Monorepo/frontend gate: Estructura objetivo `apps/backend`, `apps/frontend`, `packages/contracts`.
- [x] Config gate: Configuración de seguridad/sesión definida en `application.properties` con externalización.

## Project Structure

### Documentation (this feature)

```text
specs/003-empleados-login-credenciales/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)
<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
apps/
├── backend/
│   ├── src/main/java/com/example/empleados/
│   ├── src/main/resources/
│   └── src/test/java/com/example/empleados/
└── frontend/
    ├── src/app/auth/
    ├── src/app/core/
    └── src/environments/

packages/
└── contracts/
    └── openapi.yaml

docker/
└── docker-compose.yml
```

**Structure Decision**: Se adopta monorepo con backend y frontend desacoplados por carpeta, contrato OpenAPI compartido en `packages/contracts` y runtime local Docker para backend+PostgreSQL.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |
