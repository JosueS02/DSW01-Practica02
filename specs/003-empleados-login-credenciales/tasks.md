# Tasks: Login de Empleados con Credenciales

**Input**: Design documents from `/specs/003-empleados-login-credenciales/`
**Prerequisites**: `plan.md` (required), `spec.md` (required), `research.md`, `data-model.md`, `contracts/openapi.yaml`, `quickstart.md`

**Tests**: Se incluyen tareas de pruebas porque la especificación define historias con criterios de prueba independiente.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar estructura monorepo y baseline de ejecución para backend + frontend.

- [X] T001 Crear estructura monorepo en `apps/backend`, `apps/frontend` y `packages/contracts`
- [X] T002 Migrar código backend actual hacia `apps/backend/` conservando paquetes en `apps/backend/src/main/java/com/example/empleados/`
- [X] T003 Inicializar Angular 19 en `apps/frontend/` con estructura base de autenticación
- [X] T004 [P] Configurar scripts de build/test/run para backend y frontend en `apps/backend/pom.xml` y `apps/frontend/package.json`
- [X] T005 [P] Centralizar contrato OpenAPI del feature en `packages/contracts/openapi.yaml`
- [X] T006 [P] Ajustar `docker/docker-compose.yml` para ejecutar backend desde `apps/backend` con PostgreSQL

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Implementar base de datos, seguridad y utilidades comunes que bloquean todas las historias.

**⚠️ CRITICAL**: No comenzar historias de usuario hasta completar esta fase.

- [X] T007 Crear migración de `empleados` con `email`, `password_hash`, `activo` y unicidad case-insensitive en `apps/backend/src/main/resources/db/migration/V3__empleados_credentials.sql`
- [X] T008 [P] Agregar configuración de lockout/sesión UI en `apps/backend/src/main/resources/application.properties`
- [X] T009 [P] Extender `EmpleadoEntity` con `email`, `passwordHash`, `activo` en `apps/backend/src/main/java/com/example/empleados/domain/EmpleadoEntity.java`
- [X] T010 [P] Crear entidades de auth (`SesionAutenticada`, `IntentoAutenticacion`) en `apps/backend/src/main/java/com/example/empleados/domain/`
- [X] T011 [P] Crear repositorios de auth y búsqueda por email normalizado en `apps/backend/src/main/java/com/example/empleados/repository/`
- [X] T012 [P] Implementar utilitario de hash y verificación de contraseña en `apps/backend/src/main/java/com/example/empleados/security/PasswordHasher.java`
- [X] T013 [P] Implementar política de lockout (5 fallos / 15 min + reset tras éxito) en `apps/backend/src/main/java/com/example/empleados/service/LockoutPolicyService.java`
- [X] T014 Configurar seguridad para Basic Auth en `/api/**`, `POST /auth/login` público y política de autenticación de `POST /auth/logout` en `apps/backend/src/main/java/com/example/empleados/config/SecurityConfig.java`
- [X] T015 Configurar sesión UI única por empleado y expiración 8h en `apps/backend/src/main/java/com/example/empleados/service/AuthSessionService.java`
- [X] T016 Actualizar manejo global de errores (`401`, `409`, `423`) en `apps/backend/src/main/java/com/example/empleados/config/GlobalExceptionHandler.java`
- [X] T017 Sincronizar OpenAPI base (securitySchemes, modelos de credenciales, auth endpoints) en `packages/contracts/openapi.yaml`

**Checkpoint**: Base técnica lista para implementar historias de usuario de manera independiente.

---

## Phase 3: User Story 1 - Inicio de sesión de empleado (Priority: P1) 🎯 MVP

**Goal**: Permitir login UI de empleados con `POST /auth/login` y cierre con `POST /auth/logout`, manteniendo Basic Auth en endpoints de negocio.

**Independent Test**: Login válido/ inválido desde UI + API y verificación de que `/api/**` sigue rechazando sin Basic Auth válido.

### Tests for User Story 1

- [X] T018 [P] [US1] Crear pruebas de contrato para `POST /auth/login` y `POST /auth/logout` en `apps/backend/src/test/java/com/example/empleados/contract/AuthContractTest.java`
- [X] T019 [P] [US1] Crear pruebas de integración de login/logout con sesión UI en `apps/backend/src/test/java/com/example/empleados/integration/AuthIntegrationTest.java`
- [X] T020 [P] [US1] Crear prueba de integración para confirmar protección Basic de `/api/empleados` en `apps/backend/src/test/java/com/example/empleados/integration/ApiBasicProtectionIntegrationTest.java`
- [X] T021 [P] [US1] Crear pruebas UI del formulario de login en `apps/frontend/src/app/auth/login/login.component.spec.ts`

### Implementation for User Story 1

- [X] T022 [P] [US1] Implementar DTOs `LoginRequest` y `LoginResponse` en `apps/backend/src/main/java/com/example/empleados/dto/`
- [X] T023 [US1] Implementar `AuthService` para validación de credenciales UI y emisión de sesión UI en `apps/backend/src/main/java/com/example/empleados/service/AuthService.java`
- [X] T024 [US1] Implementar `AuthController` con `POST /auth/login` y `POST /auth/logout` en `apps/backend/src/main/java/com/example/empleados/controller/AuthController.java`
- [X] T025 [P] [US1] Implementar cliente HTTP de auth en `apps/frontend/src/app/core/services/auth-api.service.ts`
- [X] T026 [US1] Implementar pantalla de login Angular 19 en `apps/frontend/src/app/auth/login/login.component.ts`
- [X] T027 [US1] Configurar rutas de auth y estado de sesión UI en `apps/frontend/src/app/app.routes.ts`
- [X] T028 [US1] Actualizar contrato OpenAPI de auth y ejemplos en `packages/contracts/openapi.yaml`

**Checkpoint**: MVP funcional de login UI completado sin romper protección Basic de negocio.

---

## Phase 4: User Story 2 - Gestión de credenciales en CRUD de empleados (Priority: P2)

**Goal**: Extender CRUD de empleados con `email/password`, normalización de email y password opcional en update.

**Independent Test**: Crear/actualizar empleado con credenciales válidas; rechazar duplicados equivalentes por casing y formato inválido.

### Tests for User Story 2

- [X] T029 [P] [US2] Crear pruebas de contrato de alta de empleado con `email/password` en `apps/backend/src/test/java/com/example/empleados/contract/EmpleadoCreateWithCredentialsContractTest.java`
- [X] T030 [P] [US2] Crear pruebas de contrato de actualización con `password` opcional en `apps/backend/src/test/java/com/example/empleados/contract/EmpleadoUpdateWithCredentialsContractTest.java`
- [X] T031 [P] [US2] Crear pruebas de integración para unicidad case-insensitive de email en `apps/backend/src/test/java/com/example/empleados/integration/EmpleadoEmailUniquenessIntegrationTest.java`
- [X] T032 [P] [US2] Crear pruebas unitarias de política de contraseña y normalización de email en `apps/backend/src/test/java/com/example/empleados/unit/EmpleadoCredentialsValidationTest.java`

### Implementation for User Story 2

- [X] T033 [P] [US2] Extender `CreateEmpleadoRequest` con `email` y `password` en `apps/backend/src/main/java/com/example/empleados/dto/CreateEmpleadoRequest.java`
- [X] T034 [P] [US2] Extender `UpdateEmpleadoRequest` con `email` y `password` opcional en `apps/backend/src/main/java/com/example/empleados/dto/UpdateEmpleadoRequest.java`
- [X] T035 [P] [US2] Extender `EmpleadoResponse` con `email` sin exponer secretos en `apps/backend/src/main/java/com/example/empleados/dto/EmpleadoResponse.java`
- [X] T036 [US2] Implementar normalización `trim + lowercase` y reglas de unicidad en `apps/backend/src/main/java/com/example/empleados/service/EmpleadoService.java`
- [X] T037 [US2] Aplicar hash de password y conservación de hash en updates sin `password` en `apps/backend/src/main/java/com/example/empleados/service/EmpleadoService.java`
- [X] T038 [US2] Ajustar repositorio y consultas por email normalizado en `apps/backend/src/main/java/com/example/empleados/repository/EmpleadoRepository.java`
- [X] T039 [US2] Ajustar controlador y validaciones del CRUD de empleados en `apps/backend/src/main/java/com/example/empleados/controller/EmpleadoController.java`
- [X] T040 [US2] Actualizar esquemas OpenAPI de empleados en `packages/contracts/openapi.yaml`

**Checkpoint**: CRUD de empleados con credenciales completo y consistente con reglas de seguridad.

---

## Phase 5: User Story 3 - Experiencia de error y recuperación en login (Priority: P3)

**Goal**: Entregar UX de error clara y robustez operativa con lockout, sesión única, expiración y auditoría retenida 90 días.

**Independent Test**: Validaciones UI, lockout funcional, sesión única por empleado, expiración a 8h y auditoría con metadatos de retención.

### Tests for User Story 3

- [X] T041 [P] [US3] Crear pruebas de integración de lockout y reset en login exitoso en `apps/backend/src/test/java/com/example/empleados/integration/AuthLockoutIntegrationTest.java`
- [X] T042 [P] [US3] Crear pruebas de integración para sesión UI única por empleado en `apps/backend/src/test/java/com/example/empleados/integration/AuthSingleSessionIntegrationTest.java`
- [X] T043 [P] [US3] Crear pruebas de integración de expiración de sesión UI (8h) en `apps/backend/src/test/java/com/example/empleados/integration/AuthSessionExpiryIntegrationTest.java`
- [X] T044 [P] [US3] Crear pruebas de UI para mensajes de error en login en `apps/frontend/src/app/auth/login/login-errors.component.spec.ts`

### Implementation for User Story 3

- [X] T045 [US3] Implementar invalidación de sesión UI previa en nuevo login en `apps/backend/src/main/java/com/example/empleados/service/AuthSessionService.java`
- [X] T046 [US3] Implementar expiración de sesión UI a 8h en `apps/backend/src/main/java/com/example/empleados/service/AuthSessionService.java`
- [X] T047 [US3] Implementar auditoría de intentos con retención mínima de 90 días en `apps/backend/src/main/java/com/example/empleados/service/AuthAuditService.java`
- [X] T048 [US3] Mapear mensajes de error funcionales en backend (`401`, `423`) en `apps/backend/src/main/java/com/example/empleados/config/GlobalExceptionHandler.java`
- [X] T049 [US3] Implementar feedback de errores y estados bloqueado/expirado en `apps/frontend/src/app/auth/login/login.component.ts`

**Checkpoint**: Flujo de login robusto con políticas de seguridad y observabilidad completas.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Consolidar documentación, verificación integral y cierre de entrega.

- [X] T050 [P] Actualizar guía operativa y pruebas manuales en `specs/003-empleados-login-credenciales/quickstart.md`
- [X] T051 [P] Verificar consistencia entre `specs/003-empleados-login-credenciales/spec.md`, `specs/003-empleados-login-credenciales/plan.md`, `specs/003-empleados-login-credenciales/data-model.md`, `specs/003-empleados-login-credenciales/research.md` y `packages/contracts/openapi.yaml`
- [X] T052 Ejecutar pruebas backend y suite de regresión de comportamientos existentes de CRUD 001, registrando resultados en `specs/003-empleados-login-credenciales/quickstart.md`
- [X] T053 Ejecutar pruebas frontend y registrar resultados en `specs/003-empleados-login-credenciales/quickstart.md`
- [X] T054 Validar flujo end-to-end (login UI + CRUD empleados + Basic en negocio) usando `docker/docker-compose.yml`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Phase 1 (Setup)**: inicia sin dependencias.
- **Phase 2 (Foundational)**: depende de Phase 1 y bloquea todas las historias.
- **Phase 3 (US1)**: depende de Phase 2 y entrega MVP.
- **Phase 4 (US2)**: depende de Phase 2 y se integra con componentes base.
- **Phase 5 (US3)**: depende de Phase 3 para construir resiliencia del flujo de login.
- **Phase 6 (Polish)**: depende de completar las historias requeridas.

### User Story Dependencies

- **US1 (P1)**: puede iniciar después de Foundational.
- **US2 (P2)**: puede iniciar después de Foundational; no bloquea US3.
- **US3 (P3)**: requiere endpoints de auth UI implementados en US1.

### Within Each User Story

- Pruebas (si están incluidas) antes de implementar.
- DTOs/modelos antes de servicios.
- Servicios antes de controladores.
- Contrato OpenAPI actualizado antes de cerrar historia.

### Parallel Opportunities

- Setup: T004, T005 y T006 en paralelo tras T001.
- Foundational: T008, T009, T010, T011, T012, T013 en paralelo tras T007.
- US1: T018, T019, T020, T021 en paralelo; T022 y T025 en paralelo.
- US2: T029, T030, T031, T032 en paralelo; T033, T034, T035 en paralelo.
- US3: T041, T042, T043 y T044 en paralelo.

---

## Parallel Example: User Story 1

```bash
# Pruebas paralelas de US1
T018 apps/backend/src/test/java/com/example/empleados/contract/AuthContractTest.java
T019 apps/backend/src/test/java/com/example/empleados/integration/AuthIntegrationTest.java
T020 apps/backend/src/test/java/com/example/empleados/integration/ApiBasicProtectionIntegrationTest.java
T021 apps/frontend/src/app/auth/login/login.component.spec.ts

# Implementación paralela inicial
T022 apps/backend/src/main/java/com/example/empleados/dto/LoginRequest.java
T025 apps/frontend/src/app/core/services/auth-api.service.ts
```

---

## Parallel Example: User Story 2

```bash
# Pruebas paralelas de US2
T029 apps/backend/src/test/java/com/example/empleados/contract/EmpleadoCreateWithCredentialsContractTest.java
T030 apps/backend/src/test/java/com/example/empleados/contract/EmpleadoUpdateWithCredentialsContractTest.java
T032 apps/backend/src/test/java/com/example/empleados/unit/EmpleadoCredentialsValidationTest.java

# DTOs paralelos
T033 apps/backend/src/main/java/com/example/empleados/dto/CreateEmpleadoRequest.java
T034 apps/backend/src/main/java/com/example/empleados/dto/UpdateEmpleadoRequest.java
T035 apps/backend/src/main/java/com/example/empleados/dto/EmpleadoResponse.java
```

---

## Parallel Example: User Story 3

```bash
# Pruebas paralelas de US3
T041 apps/backend/src/test/java/com/example/empleados/integration/AuthLockoutIntegrationTest.java
T042 apps/backend/src/test/java/com/example/empleados/integration/AuthSingleSessionIntegrationTest.java
T043 apps/backend/src/test/java/com/example/empleados/integration/AuthSessionExpiryIntegrationTest.java
T044 apps/frontend/src/app/auth/login/login-errors.component.spec.ts
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1: Setup.
2. Completar Phase 2: Foundational.
3. Completar Phase 3: US1.
4. Validar login UI + `POST /auth/logout` + protección Basic de `/api/**`.

### Incremental Delivery

1. Entregar MVP con US1.
2. Añadir US2 para credenciales en CRUD.
3. Añadir US3 para resiliencia, auditoría y políticas operativas.
4. Cerrar en Phase 6 con validación integral.

### Parallel Team Strategy

1. Equipo completo en Setup + Foundational.
2. Luego distribución por historia:
   - Dev A: backend auth y políticas (US1/US3).
   - Dev B: frontend login/errores (US1/US3).
   - Dev C: CRUD credenciales empleado (US2).
3. Integración final en Polish.
