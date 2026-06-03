# Research: CRUD de Empleados

## Decision 1: Stack backend en Spring Boot 3 + Java 17
- Decision: Implementar el CRUD como servicio REST en Spring Boot 3 con Java 17.
- Rationale: Es mandato explícito de la constitución y asegura coherencia de arquitectura, seguridad y mantenimiento.
- Alternatives considered:
  - Node.js/NestJS: descartado por violar baseline constitucional.
  - Spring Boot 2.x: descartado por versión fuera del estándar definido.

## Decision 2: Persistencia en PostgreSQL con ejecución local/CI por Docker
- Decision: Usar PostgreSQL como base de datos primaria y levantarla en Docker Compose para dev/CI.
- Rationale: Garantiza paridad de entorno y evita desvíos entre pruebas locales y ejecución real.
- Alternatives considered:
  - H2 como base principal: descartado por incompatibilidades potenciales de SQL y dialecto.
  - MySQL: descartado por no alinearse a la norma de almacenamiento del proyecto.

## Decision 3: Estrategia de clave primaria
- Decision: `clave` autogenerada por el sistema en formato `E-` + número secuencial con padding de 3 dígitos (ej. `E-001`); no se permite enviarla en alta.
- Rationale: Mantiene una clave legible para negocio, conserva unicidad y evita colisiones por asignación manual.
- Alternatives considered:
  - Clave numérica `BIGINT` expuesta: descartada por no cumplir formato de negocio requerido.
  - UUID: descartado en este alcance por menor legibilidad y mayor complejidad operativa.

## Decision 4: Modelo de eliminación
- Decision: Aplicar eliminación física (hard delete) para el recurso empleado.
- Rationale: Mantiene el alcance MVP del CRUD sin requerir estados lógicos ni filtros adicionales.
- Alternatives considered:
  - Soft delete: descartado por introducir complejidad adicional (banderas, consultas filtradas, recuperación).
  - Estrategia híbrida: descartada por sobrealcance para esta iteración.

## Decision 5: Seguridad y acceso a documentación
- Decision: Proteger endpoints del CRUD con HTTP Basic Auth y dejar `/swagger-ui` + `/api-docs` públicos en entorno de desarrollo.
- Rationale: Balancea seguridad de negocio con facilidad de validación manual e integración durante desarrollo.
- Alternatives considered:
  - Proteger todo, incluyendo Swagger: descartado por fricción en validación temprana.
  - API pública en desarrollo: descartado por violar postura secure-by-default del dominio funcional.

## Decision 6: Validaciones de dominio de campos de texto
- Decision: `nombre`, `direccion` y `telefono` obligatorios, no vacíos y con longitud máxima de 100 caracteres.
- Rationale: Cumple requerimiento funcional explícito y permite validar consistentemente en capa de entrada.
- Alternatives considered:
  - Permitir vacíos para algunos campos: descartado por degradar calidad mínima de datos.
  - Longitudes variables superiores: descartado por contradecir alcance definido por negocio.

## Decision 7: Contrato API y documentación
- Decision: Publicar contrato OpenAPI 3.0 para operaciones de crear, consultar, listar, actualizar y eliminar empleado, con errores de validación/not found.
- Rationale: Reduce ambigüedad entre desarrollo, pruebas y consumo de API.
- Alternatives considered:
  - Documentación solo textual: descartada por menor precisión y verificabilidad.
  - Contrato diferido para fase posterior: descartado por regla constitucional de sincronización en el mismo ciclo.

## Decision 8: Configuración centralizada
- Decision: Usar `application.properties` como fuente base de configuración con override de secretos por variables de entorno.
- Rationale: Cumple disciplina de configuración y evita hardcodeo de credenciales en ambientes no-dev.
- Alternatives considered:
  - Hardcode de credenciales por perfil: descartado por riesgo de seguridad.
  - Configuración dispersa en múltiples archivos sin convención: descartado por mantenibilidad.
