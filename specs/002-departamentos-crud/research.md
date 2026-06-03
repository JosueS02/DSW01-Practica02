# Research: CRUD de Departamentos Relacionado con Empleados

## Decision 1: Modelo relacional Departamento 1:N Empleado
- Decision: Definir `departamentos` como catálogo independiente con identificador de negocio en formato `D-001` y asociar `empleados` mediante una FK obligatoria (`departamento_id`) hacia `departamentos.id`.
- Rationale: Cumple el requisito de que cada empleado pertenezca a un único departamento y permite validación por integridad referencial en base de datos.
- Alternatives considered:
  - Guardar nombre de departamento como texto en empleado: descartado por duplicidad e inconsistencia.
  - Relación N:M: descartada por sobre-diseño, no requerida por la especificación.

## Decision 1.1: Formato del identificador de departamento
- Decision: Estandarizar el identificador de departamento como `D-` + al menos 3 dígitos (iniciando en `D-001`) en dominio, API y validaciones.
- Rationale: Hace explícita la semántica de catálogo, facilita pruebas/lectura operativa y alinea el feature con la aclaración más reciente del negocio.
- Alternatives considered:
  - Identificador numérico autoincremental (`BIGINT`): descartado por no cumplir formato solicitado.
  - Identificador UUID: descartado por baja legibilidad para usuarios administrativos.

## Decision 2: Política de eliminación de departamentos
- Decision: Aplicar hard delete para departamentos solo cuando no existan empleados vinculados.
- Rationale: Respeta FR-006 y mantiene comportamiento explícito para negocio sin introducir estados lógicos adicionales.
- Alternatives considered:
  - Soft delete: descartado por complejidad no requerida en esta iteración.
  - Cascade delete de empleados: descartado por riesgo de pérdida de datos de empleados.

## Decision 3: Validación de relación desde API y servicio
- Decision: Validar existencia de departamento al crear/actualizar empleados y devolver error de negocio claro en caso de ausencia.
- Rationale: Cumple FR-008 y evita errores de base de datos menos legibles para consumidores API.
- Alternatives considered:
  - Confiar solo en excepción de FK de PostgreSQL: descartado por mala experiencia de error.
  - Permitir departamento opcional: descartado por incumplir suposición y requisito funcional.

## Decision 4: Seguridad de endpoints
- Decision: Mantener Basic Auth para todos los endpoints de negocio (`/api/empleados`, `/api/departamentos`) y conservar Swagger/API docs públicos en desarrollo.
- Rationale: Mantiene el comportamiento secure-by-default ya definido en el proyecto y evita cambios disruptivos en operación local.
- Alternatives considered:
  - Publicar endpoints de departamentos sin auth: descartado por violar principio de seguridad.
  - Proteger también Swagger en desarrollo: descartado por fricción en validación manual.

## Decision 5: Evolución de migraciones
- Decision: Crear una nueva migración Flyway `V2__create_departamentos_and_relation.sql` para tabla, restricciones de unicidad y FK en empleados.
- Rationale: Preserva historial de cambios y compatibilidad con despliegues incrementales.
- Alternatives considered:
  - Modificar `V1` existente: descartado por romper trazabilidad de migraciones ya aplicadas.
  - Ejecutar DDL manual fuera de Flyway: descartado por no cumplir estándar del repositorio.

## Decision 6: Contrato API unificado y consistente
- Decision: Documentar CRUD de departamentos y ajuste de modelos de empleado en OpenAPI 3, incluyendo `departamentoId` con patrón `^D-[0-9]{3,}$` y errores `400`, `404`, `409`.
- Rationale: Hace explícita la nueva relación y mantiene sincronización contrato/implementación exigida por constitución.
- Alternatives considered:
  - Contratos separados por recurso: descartado para evitar duplicación innecesaria.
  - Documentación fuera de OpenAPI: descartado por falta de verificabilidad automática.

## Decision 7: Configuración y despliegue local
- Decision: Reutilizar `application.properties` actual y Docker Compose existente, sin nuevas credenciales, manteniendo externalización por `APP_DB_*` y `APP_SECURITY_*`.
- Rationale: El feature no requiere nueva infraestructura, solo evolución de esquema y endpoints.
- Alternatives considered:
  - Introducir nuevos perfiles o variables para departamentos: descartado por ser innecesario.
  - Base de datos separada para catálogo: descartado por sobrecoste operativo.

## Decision 8: Generación de IDs de departamento en concurrencia
- Decision: Generar el correlativo en PostgreSQL mediante secuencia atómica, con avance global ascendente y sin reutilización de IDs eliminados.
- Rationale: Evita colisiones en concurrencia y define una única fuente de verdad para la secuencia de identificadores.
- Alternatives considered:
  - Generación en aplicación leyendo `MAX(id)`: descartado por riesgo de race conditions.
  - Lock en memoria de aplicación: descartado por no escalar en múltiples instancias.
