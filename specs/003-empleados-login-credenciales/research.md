# Research: Login de Empleados con Credenciales

## Decision 1: Alineación de seguridad con constitución
- Decision: Mantener HTTP Basic como autorización para endpoints de negocio y usar `POST /auth/login` como flujo público de validación para la UI; `POST /auth/logout` cierra sesión UI.
- Rationale: Cumple la constitución vigente (Basic por defecto) y permite experiencia de login frontend sin romper gobernanza.
- Alternatives considered:
  - Usar sesión UI como autorización de negocio: descartado por conflicto con constitución.
  - Eliminar `POST /auth/login`: descartado por impacto UX en frontend.

## Decision 2: Política de credenciales de empleado
- Decision: Agregar `email` (único, validado por formato) y `password` al CRUD de empleados; en update, `password` es opcional y conserva valor vigente si no se envía.
- Rationale: Cumple requerimientos funcionales del feature y evita reseteos innecesarios de contraseña en cambios administrativos de datos no sensibles.
- Alternatives considered:
  - Hacer `password` obligatoria en cada update: descartado por fricción operativa.
  - Gestionar credenciales en entidad separada desde inicio: diferido para evitar sobre-diseño en primera entrega.

## Decision 3: Seguridad de contraseña y bloqueo
- Decision: Exigir contraseña con mínimo 8 caracteres, al menos una mayúscula, una minúscula y un número; bloquear cuenta 15 minutos tras 5 intentos fallidos consecutivos y reiniciar contador al login exitoso.
- Rationale: Balancea seguridad y usabilidad para entorno interno.
- Alternatives considered:
  - Solo longitud mínima: descartado por protección insuficiente.
  - Reglas muy estrictas (12+ y símbolo): descartado por impacto UX en MVP.

## Decision 4: Expiración y cierre de sesión
- Decision: Expirar sesión UI automáticamente a las 8 horas e invalidar mediante `POST /auth/logout`; solo se permite una sesión UI activa por empleado.
- Rationale: Controla riesgo de sesión persistente y evita ambigüedad en concurrencia.
- Alternatives considered:
  - Sin logout explícito: descartado por menor control de sesión.
  - Sesión 24 horas: descartado por mayor ventana de exposición.

## Decision 5: Persistencia y auditoría de autenticación
- Decision: Registrar intentos de autenticación (éxito/fallo) con timestamp y referencia de cuenta, con retención mínima de 90 días.
- Rationale: Permite aplicar la política de bloqueo y apoyar diagnóstico operativo.
- Alternatives considered:
  - No registrar intentos: descartado por imposibilidad de enforcement de lockout.
  - Registro solo en memoria: descartado por inconsistencia entre reinicios/instancias.

## Decision 6: Contratos API y sincronización frontend-backend
- Decision: Actualizar OpenAPI con endpoints de auth UI y CRUD de empleados con `email`/`password` solo en requests, usando `basicAuth` para endpoints de negocio y `sessionAuth` para logout UI.
- Rationale: Mantiene contrato único y versionado para backend y frontend dentro del monorepo.
- Alternatives considered:
  - Documentar solo en anotaciones backend: descartado por menor visibilidad cross-app.
  - Mantener contrato separado por app: descartado por riesgo de drift.

## Decision 7: Estructura monorepo objetivo
- Decision: Estandarizar layout con `apps/backend`, `apps/frontend` y `packages/contracts`; mantener `docker/docker-compose.yml` como runtime local.
- Rationale: Cumple constitución del proyecto y permite evolución coordinada backend/frontend.
- Alternatives considered:
  - Continuar con backend en raíz + frontend aparte: descartado por incumplir baseline de monorepo.
  - Mover infraestructura a herramienta nueva de orquestación: descartado en esta iteración.

## Decision 8: Normalización y unicidad de email
- Decision: Normalizar email con `trim + lowercase` antes de persistencia/comparación y aplicar unicidad case-insensitive.
- Rationale: Evita duplicados lógicos (`User@Empresa.com` vs `user@empresa.com`) y reduce errores de autenticación.
- Alternatives considered:
  - Case-sensitive en base de datos: descartado por inconsistencias operativas.
  - Normalización solo en login: descartado por divergencia entre lectura/escritura.
