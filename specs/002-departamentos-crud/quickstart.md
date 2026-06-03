# Quickstart: CRUD de Departamentos Relacionado con Empleados

## Prerrequisitos
- Java 17
- Maven 3.9+
- Docker + Docker Compose

## 1) Levantar PostgreSQL en Docker
Desde la carpeta `docker/`:

```bash
docker compose up -d postgres
```

Si se desea levantar todo (app + db):

```bash
docker compose up -d
```

## 2) Verificar configuración principal
En `src/main/resources/application.properties` mantener:
- `spring.datasource.url=${APP_DB_URL:jdbc:postgresql://localhost:5432/dsw01_practica02}`
- `spring.datasource.username=${APP_DB_USERNAME:postgres}`
- `spring.datasource.password=${APP_DB_PASSWORD:postgres}`
- `spring.security.user.name=${APP_SECURITY_USER:admin}`
- `spring.security.user.password=${APP_SECURITY_PASSWORD:admin123}`
- `springdoc.api-docs.path=/api-docs`
- `springdoc.swagger-ui.path=/swagger-ui.html`

## 3) Ejecutar migraciones y aplicación

```bash
./mvnw spring-boot:run
```

Validar que Flyway aplique `V1` y `V2`.

Para esta especificación, los IDs de departamentos deben exponerse con formato `D-` + al menos 3 dígitos (ej. `D-001`, `D-1000`).

## 4) Validar seguridad y documentación
- Swagger UI (público en dev): `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Endpoints de negocio protegidos por Basic Auth

## 5) Flujo mínimo de validación manual

1. Crear departamento:
```bash
curl -i -u admin:admin123 -H 'Content-Type: application/json' \
  -d '{"nombre":"Recursos Humanos","descripcion":"Gestión de talento"}' \
  http://localhost:8080/api/departamentos
```

2. Crear empleado ligado a departamento:
```bash
curl -i -u admin:admin123 -H 'Content-Type: application/json' \
  -d '{"nombre":"Ana","direccion":"Calle 2","telefono":"55555","departamentoId":"D-001"}' \
  http://localhost:8080/api/empleados
```

3. Intentar eliminar departamento con empleados (debe fallar):
```bash
curl -i -u admin:admin123 -X DELETE http://localhost:8080/api/departamentos/D-001
```

4. Consultar empleado y verificar referencia a departamento:
```bash
curl -i -u admin:admin123 http://localhost:8080/api/empleados/E-001
```

## 6) Ejecutar pruebas

```bash
./mvnw test
```

Validar cobertura mínima:
- Contratos API para departamentos.
- Integración de FK empleado-departamento.
- Caso negativo de borrado de departamento con empleados asociados.
- Regresión de endpoints actuales de empleados.
