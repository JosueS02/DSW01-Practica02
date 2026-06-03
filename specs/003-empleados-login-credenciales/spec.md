# Feature Specification: Login de Empleados con Credenciales

**Feature Branch**: `003-empleados-login-credenciales`  
**Created**: 2026-03-11  
**Status**: Draft  
**Input**: User description: "Crea el front para el login de empleados en base al crud 001-empleados-crud, ademas en el crud 001-empleados-crud añade email y password"

## Clarifications

### Session 2026-03-11

- Q: ¿Qué mecanismo de autenticación usará el login web de empleados? → A: Endpoint público `POST /auth/login` para validar credenciales del flujo UI; los endpoints de negocio mantienen autorización con HTTP Basic.
- Q: ¿Qué política mínima de contraseña aplica para empleados? → A: Longitud mínima de 8 caracteres, con al menos 1 mayúscula, 1 minúscula y 1 número.
- Q: En actualización de empleado, ¿la contraseña debe ser obligatoria? → A: No, `password` es opcional; si no se envía, se conserva la contraseña actual.
- Q: ¿Qué política de bloqueo aplica ante intentos fallidos de login? → A: Bloqueo temporal de 15 minutos tras 5 intentos fallidos consecutivos por cuenta.
- Q: ¿Cómo se gestiona cierre y expiración de sesión? → A: Se expone `POST /auth/logout` para invalidar sesión y la sesión expira automáticamente a las 8 horas.
- Q: ¿Cómo se alinea autorización de negocio con la constitución vigente? → A: Se mantiene HTTP Basic para endpoints de negocio; `POST /auth/login` no reemplaza el mecanismo de autorización de negocio.
- Q: ¿Cuándo se reinicia el contador de intentos fallidos? → A: El contador se reinicia inmediatamente después de un login exitoso.
- Q: ¿Cuántas sesiones UI concurrentes se permiten por empleado? → A: Solo una sesión UI activa por empleado; un nuevo login invalida la sesión UI anterior.
- Q: ¿Cómo se maneja la sensibilidad de mayúsculas en email? → A: El email se normaliza con `trim + lowercase` y su unicidad se evalúa de forma case-insensitive.
- Q: ¿Cuál es la retención mínima para auditoría de intentos de autenticación? → A: 90 días.

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

### User Story 1 - Inicio de sesión de empleado (Priority: P1)

Como empleado, quiero iniciar sesión con mis credenciales para acceder al sistema de forma segura.

**Why this priority**: Sin autenticación no existe acceso controlado al sistema ni trazabilidad por usuario.

**Independent Test**: Se prueba de forma independiente al intentar iniciar sesión con credenciales válidas e inválidas y verificar acceso/denegación.

**Acceptance Scenarios**:

1. **Given** que existe un empleado activo con correo y contraseña válidos, **When** ingresa sus credenciales en la pantalla de login, **Then** el sistema permite el acceso.
2. **Given** que las credenciales son incorrectas, **When** el empleado intenta autenticarse, **Then** el sistema rechaza el acceso y muestra un mensaje claro.

---

### User Story 2 - Gestión de credenciales en CRUD de empleados (Priority: P2)

Como administrador, quiero registrar y mantener correo y contraseña en el CRUD de empleados para habilitar el login de cada empleado.

**Why this priority**: El login depende de que cada empleado tenga credenciales completas y válidas.

**Independent Test**: Se prueba creando y actualizando empleados con correo/contraseña y validando persistencia, unicidad de correo y reglas mínimas de contraseña.

**Acceptance Scenarios**:

1. **Given** que se crea un nuevo empleado, **When** el administrador registra correo y contraseña válidos, **Then** el sistema guarda las credenciales junto con el empleado.
2. **Given** que el correo ya pertenece a otro empleado, **When** se intenta crear o actualizar con ese correo, **Then** el sistema rechaza la operación por conflicto.

---

### User Story 3 - Experiencia de error y recuperación en login (Priority: P3)

Como empleado, quiero mensajes claros en la pantalla de login para corregir errores de captura y reintentar rápidamente.

**Why this priority**: Mejora adopción y reduce tickets de soporte por fallas de acceso.

**Independent Test**: Se prueba forzando errores típicos (campos vacíos, formato inválido, credenciales incorrectas) y verificando mensajes accionables.

**Acceptance Scenarios**:

1. **Given** que el usuario deja campos obligatorios vacíos, **When** intenta iniciar sesión, **Then** la interfaz muestra validaciones antes del envío.
2. **Given** que el backend rechaza la autenticación, **When** la interfaz recibe el error, **Then** se muestra retroalimentación comprensible sin exponer datos sensibles.

---

### Edge Cases
- Intento de login con correo inexistente.
- Intento de login con contraseña incorrecta múltiples veces en una misma sesión de uso.
- Intento de login durante ventana de bloqueo temporal por demasiados fallos.
- Reinicio del contador de fallos tras login exitoso debe permitir un nuevo ciclo completo de intentos.
- Reutilización de sesión expirada del flujo UI (debe requerir nuevo login UI).
- Inicio de sesión UI en segundo dispositivo debe invalidar sesión UI previa del mismo empleado.
- Acceso a endpoint de negocio sin credenciales Basic válidas tras login UI exitoso (debe rechazarse).
- Creación o actualización de empleado con correo mal formado.
- Creación o actualización de empleado con correo duplicado.
- Creación de correos equivalentes por casing/espacios (ej. ` User@Empresa.com ` vs `user@empresa.com`) debe tratarse como duplicado.
- Creación o actualización de empleado con contraseña sin cumplir la política mínima (8+, mayúscula, minúscula y número).
- Actualización de un empleado existente sin intención de cambiar contraseña (debe conservarse la actual).
- Ingreso de espacios en blanco al inicio o final de correo.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE proporcionar una pantalla de login para empleados con campos de correo y contraseña.
- **FR-002**: El sistema DEBE exponer un endpoint público `POST /auth/login` para autenticar empleados con correo y contraseña.
- **FR-003**: El sistema DEBE denegar el acceso cuando las credenciales sean inválidas y mostrar un mensaje de error claro.
- **FR-003A**: El sistema DEBE mantener `POST /auth/login` como flujo de autenticación UI sin sustituir HTTP Basic como autorización de endpoints de negocio.
- **FR-004**: El CRUD de empleados DEBE permitir registrar correo electrónico y contraseña al crear un empleado.
- **FR-005**: El CRUD de empleados DEBE permitir actualizar correo electrónico y contraseña en empleados existentes.
- **FR-005A**: En actualización de empleado, el campo `password` DEBE ser opcional; cuando no se envíe, el sistema DEBE conservar la contraseña vigente.
- **FR-006**: El correo electrónico DEBE ser único por empleado dentro del sistema.
- **FR-006A**: El correo electrónico DEBE normalizarse con `trim + lowercase` antes de persistencia y comparación.
- **FR-007**: El sistema DEBE validar formato de correo electrónico antes de aceptar el alta o actualización.
- **FR-008**: El sistema DEBE exigir una contraseña con longitud mínima de 8 caracteres, al menos 1 mayúscula, 1 minúscula y 1 número en altas y cambios de contraseña.
- **FR-009**: El sistema DEBE almacenar la contraseña de forma segura y no exponerla en respuestas de consulta de empleados.
- **FR-010**: El sistema DEBE mantener los comportamientos existentes del CRUD 001 de empleados que no estén relacionados con credenciales.
- **FR-011**: El sistema DEBE registrar el resultado de intentos de autenticación (éxito/fallo) para soporte operativo y auditoría.
- **FR-011A**: Los registros de auditoría de intentos de autenticación DEBEN conservarse por al menos 90 días.
- **FR-012**: La documentación funcional del feature DEBE reflejar el nuevo flujo de login y el impacto de correo/contraseña en el ciclo de vida del empleado.
- **FR-013**: Todos los endpoints de negocio distintos a `POST /auth/login` DEBEN mantenerse protegidos y requerir HTTP Basic válido.
- **FR-014**: El sistema DEBE bloquear temporalmente por 15 minutos una cuenta tras 5 intentos fallidos consecutivos de autenticación.
- **FR-014A**: El sistema DEBE reiniciar el contador de intentos fallidos inmediatamente cuando ocurre un login exitoso.
- **FR-015**: El sistema DEBE exponer `POST /auth/logout` para cerrar la sesión del flujo UI.
- **FR-016**: La sesión del flujo UI DEBE expirar automáticamente a las 8 horas desde el login UI.
- **FR-017**: El sistema DEBE permitir máximo una sesión UI activa por empleado; un login UI nuevo DEBE invalidar la sesión UI previa de ese empleado.

### Key Entities *(include if feature involves data)*

- **Empleado**: Representa a la persona operativa del sistema; agrega atributos de credencial (`email`, `password`) y mantiene atributos existentes del CRUD 001.
- **Credencial de Acceso**: Representa la combinación de identificador de acceso (correo único) y secreto de autenticación (contraseña protegida) asociada 1:1 con un empleado.
- **Intento de Autenticación**: Evento de acceso con estado (éxito o fallo), marca temporal y referencia del empleado/correo usado.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Al menos 95% de empleados con credenciales válidas completan inicio de sesión exitoso en su primer intento durante pruebas de aceptación.
- **SC-002**: El 100% de altas de empleado nuevas requieren correo y contraseña válidos.
- **SC-003**: El 100% de intentos de alta o actualización con correo duplicado son rechazados con resultado de conflicto.
- **SC-004**: El tiempo promedio para iniciar sesión desde la pantalla de login es menor a 30 segundos en pruebas de usuario.

## Assumptions

- El feature aplica al alcance del CRUD `001-empleados-crud` existente.
- No se requiere recuperación de contraseña por correo en esta iteración.
- Solo empleados activos pueden autenticarse.
- Los usuarios administrativos ya cuentan con permisos para crear/editar empleados.

## Dependencies

- El CRUD de empleados del feature 001 debe estar disponible y operativo.
- Debe existir un mecanismo de autenticación activo en el sistema para validar credenciales.
- El equipo de producto debe proporcionar textos finales de mensajes de error de login.
