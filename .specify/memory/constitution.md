<!--
Sync Impact Report
- Version change: 1.0.0 → 1.1.0
- Modified principles:
	- V. Configuration Discipline via application.properties → V. Monorepo + Angular 19 Frontend Baseline (NON-NEGOTIABLE)
	- Added new VI. Configuration Discipline via application.properties (renumbered from prior V)
- Added sections:
	- Monorepo Structure Standards
- Removed sections:
	- None
- Templates requiring updates:
	- ✅ updated: .specify/templates/plan-template.md
	- ✅ updated: .specify/templates/spec-template.md
	- ✅ updated: .specify/templates/tasks-template.md
	- ⚠ pending: .specify/templates/commands/*.md (directory not present)
	- ⚠ pending: README.md / docs/quickstart.md (files not present)
- Follow-up TODOs:
	- None
-->

# DSW01-Practica02 Constitution

## Core Principles

### I. Spring Boot 3 + Java 17 Baseline (NON-NEGOTIABLE)
All backend services MUST be implemented with Spring Boot 3 and Java 17. New code
MUST follow Spring conventions (controller-service-repository layering, dependency
injection, and configuration via Spring profiles). Introducing alternative backend
frameworks or Java versions is not allowed without a constitutional amendment.
Rationale: a single, enforced baseline reduces maintenance cost and onboarding time.

### II. Basic Authentication by Default
All exposed API endpoints MUST be protected with HTTP Basic Authentication unless an
endpoint is explicitly declared public in the specification. The default credentials
for local/dev environments are `admin` / `admin123`. Production-like deployments MUST
override credentials through environment variables or secret stores and MUST NOT rely
on default credentials.
Rationale: secure-by-default behavior is mandatory, while preserving local setup speed.

### III. PostgreSQL via Docker as Standard Runtime
Persistent data MUST be stored in PostgreSQL. Local and CI execution MUST run
PostgreSQL through Docker (Docker Compose or equivalent). Features MUST be validated
against PostgreSQL behavior; in-memory databases are allowed only for narrowly scoped
unit tests that do not validate SQL behavior.
Rationale: environment parity prevents runtime drift and SQL incompatibilities.

### IV. API Contract Visibility with Swagger/OpenAPI
Every HTTP API MUST publish and maintain OpenAPI documentation through Swagger.
Endpoint definitions, request/response models, authentication requirements, and error
codes MUST be documented and kept synchronized with implementation changes in the same
feature cycle.
Rationale: explicit API contracts reduce integration errors and speed up testing.

### V. Monorepo + Angular 19 Frontend Baseline (NON-NEGOTIABLE)
The repository MUST be organized and maintained as a monorepo containing backend and
frontend applications. The frontend application MUST use Angular 19 as its baseline
framework version. New UI work MUST be implemented in Angular within the monorepo and
MUST NOT introduce an alternative frontend framework without constitutional amendment.
Cross-app contracts (API endpoints, DTOs, auth flows) MUST be versioned and validated
as part of the same feature cycle.
Rationale: monorepo coordination and a fixed frontend baseline reduce integration drift
and make backend/frontend delivery predictable.

### VI. Configuration Discipline via application.properties
Spring Boot runtime configuration MUST be centralized in `application.properties`
(and profile-specific variants where needed). Configuration keys for datasource,
security, server port, and Swagger exposure MUST be explicitly defined. Sensitive
values MUST be externalized and MUST NOT be hardcoded in source code.
Rationale: predictable configuration management improves operability and security.

## Technical Standards

- Packaging MUST follow layered architecture: controllers, services, repositories,
	domain models, and configuration classes.
- Database changes MUST be versioned via migrations (Flyway or Liquibase).
- API responses MUST use consistent HTTP semantics and structured error payloads.
- Swagger UI and OpenAPI docs endpoints MUST be available in non-production profiles.
- Docker artifacts (`Dockerfile`, `docker-compose.yml`) MUST be kept runnable for local
	backend + PostgreSQL startup.

## Monorepo Structure Standards

- The monorepo MUST define separate top-level application directories for backend and
	frontend (for example, `apps/backend` and `apps/frontend`) or an equivalent clearly
	documented layout.
- The frontend application MUST declare and maintain Angular 19 in its project manifest
	and lockfile.
- Shared contracts (OpenAPI specs, generated clients, or shared schemas) MUST reside in
	a versioned location accessible to both backend and frontend workflows.
- CI tasks MUST support targeted execution per app and full monorepo validation for
	integration-critical changes.

## Development Workflow & Quality Gates

- Every feature specification MUST include authentication behavior, database impact,
	API documentation impact, monorepo/frontend impact, and configuration impact.
- Implementation plans MUST pass the Constitution Check before coding starts.
- Pull requests MUST include evidence of:
	- passing unit/integration tests,
	- verified Basic Auth behavior,
	- validated PostgreSQL connectivity in Docker runtime,
	- validated Angular 19 frontend build/test impact when frontend code is touched,
	- updated Swagger/OpenAPI documentation,
	- updated `application.properties` keys when configuration changes.
- Code review MUST reject changes that bypass any Core Principle.

## Governance

This constitution supersedes local conventions and ad-hoc practices for backend work
in this repository.

Amendment process:
1. Propose change with explicit rationale and affected principles/sections.
2. Document migration impact on templates and active specs/plans/tasks.
3. Obtain maintainer approval before merging constitutional changes.

Versioning policy (semantic):
- MAJOR: incompatible governance changes or principle removals/redefinitions.
- MINOR: new principle/section or materially expanded mandatory guidance.
- PATCH: clarifications, wording improvements, or non-semantic refinements.

Compliance review expectations:
- Every `/speckit.plan` MUST include a Constitution Check result.
- Every `/speckit.tasks` output MUST include tasks for security, data, docs, and
	configuration, and monorepo coordination when applicable.
- Reviewers MUST block merges that violate constitutional MUST statements.

**Version**: 1.1.0 | **Ratified**: 2026-02-25 | **Last Amended**: 2026-03-11
