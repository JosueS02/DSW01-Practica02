# Quickstart: CRUD de Empleados

## Prerrequisitos
- Java 17
- Maven 3.9+
- Docker + Docker Compose

## 1) Levantar PostgreSQL en Docker
Crear/usar un `docker-compose.yml` con un servicio postgres y ejecutar:

```bash
docker compose up -d
```

Valores esperados para entorno local:
- Host: `localhost`
- Port: `5432`
- Database: `dsw01_practica02`
- Username: `postgres`
- Password: `postgres`

## 2) Verificar configuración de Spring Boot
En `src/main/resources/application.properties`:
- `spring.datasource.url=${APP_DB_URL:jdbc:postgresql://localhost:5432/dsw01_practica02}`
- `spring.datasource.username=${APP_DB_USERNAME:postgres}`
- `spring.datasource.password=${APP_DB_PASSWORD:postgres}`
- `spring.security.user.name=${APP_SECURITY_USER:admin}`
- `spring.security.user.password=${APP_SECURITY_PASSWORD:admin123}`
- `springdoc.api-docs.path=/api-docs`
- `springdoc.swagger-ui.path=/swagger-ui.html`

Opcional (sobrescritura por entorno):

```bash
export APP_DB_URL=jdbc:postgresql://localhost:5432/dsw01_practica02
export APP_DB_USERNAME=postgres
export APP_DB_PASSWORD=postgres
export APP_SECURITY_USER=admin
export APP_SECURITY_PASSWORD=admin123
```

## 3) Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

## 4) Validar documentación y seguridad
- Swagger UI (dev público): `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON (dev público): `http://localhost:8080/api-docs`
- Endpoints CRUD de empleados: requieren Basic Auth (`admin` / `admin123` en dev)

## 5) Pruebas recomendadas

```bash
./mvnw test
```

Validar mínimo:
- Alta sin `clave` en payload retorna `clave` autogenerada en formato `E-001`.
- Alta/actualización rechaza longitudes >100 y campos vacíos.
- Listado y consulta por `clave` funcionan usando formato `E-` + secuencia.
- Eliminación hard delete remueve definitivamente el registro.

## 6) Evidencia de ejecución

Comando ejecutado:

```bash
mvn test
```

Resultado observado:
- `BUILD SUCCESS`
- `Tests run: 16, Failures: 0, Errors: 0, Skipped: 4`

Nota sobre integración:
- Las pruebas de integración con Testcontainers se configuran con `@Testcontainers(disabledWithoutDocker = true)`.
- Si Docker no está disponible en el entorno, esas pruebas se marcan como `Skipped` en lugar de fallar.

## 7) Validación manual de flujo completo (CRUD + seguridad + Swagger)

1. Validar Swagger público en desarrollo:
```bash
curl -i http://localhost:8080/swagger-ui.html
curl -i http://localhost:8080/api-docs
```

2. Validar que CRUD sin autenticación retorna 401:
```bash
curl -i http://localhost:8080/api/empleados
```

3. Crear empleado autenticado:
```bash
curl -i -u admin:admin123 -H 'Content-Type: application/json' \
	-d '{"nombre":"Ana","direccion":"Calle 2","telefono":"55555"}' \
	http://localhost:8080/api/empleados
```

4. Consultar/listar empleado autenticado:
```bash
curl -i -u admin:admin123 http://localhost:8080/api/empleados/E-001
curl -i -u admin:admin123 http://localhost:8080/api/empleados
```

5. Actualizar y eliminar empleado autenticado:
```bash
curl -i -u admin:admin123 -X PUT -H 'Content-Type: application/json' \
	-d '{"nombre":"Ana Maria","direccion":"Calle 3","telefono":"77777"}' \
	http://localhost:8080/api/empleados/E-001

curl -i -u admin:admin123 -X DELETE http://localhost:8080/api/empleados/E-001
curl -i -u admin:admin123 http://localhost:8080/api/empleados/E-001
```

Resultado esperado:
- Swagger disponible sin auth en desarrollo.
- Endpoints CRUD protegidos con Basic Auth.
- Respuesta final del último `GET` con `404` después de `DELETE` (hard delete).
