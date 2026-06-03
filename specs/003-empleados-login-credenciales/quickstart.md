# Quickstart: Login de Empleados con Credenciales

## Prerrequisitos
- Java 17
- Maven 3.9+
- Node.js 20+
- Angular CLI 19
- Docker + Docker Compose

## 1) Levantar PostgreSQL en Docker

```bash
cd docker
docker compose up -d postgres
```

## 2) Configurar backend

Verificar en `apps/backend/src/main/resources/application.properties` (o ruta equivalente durante migración):
- `spring.datasource.url=${APP_DB_URL:jdbc:postgresql://localhost:5432/dsw01_practica02}`
- `spring.datasource.username=${APP_DB_USERNAME:postgres}`
- `spring.datasource.password=${APP_DB_PASSWORD:postgres}`
- `spring.security.user.name=${APP_SECURITY_USER:admin}`
- `spring.security.user.password=${APP_SECURITY_PASSWORD:admin123}`
- `app.auth.session-hours=8`
- `app.auth.lockout.max-failures=5`
- `app.auth.lockout.minutes=15`

## 3) Ejecutar backend

```bash
cd apps/backend
./mvnw spring-boot:run
```

## 4) Ejecutar frontend Angular 19

```bash
cd apps/frontend
npm install
npm run start
```

Frontend esperado en `http://localhost:4200` con pantalla de login de empleados.

Nota de seguridad:
- El flujo UI usa `POST /auth/login` y `POST /auth/logout`.
- Los endpoints de negocio (`/api/*`) permanecen protegidos con HTTP Basic.

## 5) Validar endpoints de autenticación

### Login válido
```bash
curl -i -H 'Content-Type: application/json' \
  -d '{"email":"empleado@empresa.com","password":"Passw0rd"}' \
  http://localhost:8080/auth/login
```

### Logout
```bash
curl -i -X POST --cookie 'SESSION=<session-id>' \
  http://localhost:8080/auth/logout
```

### Endpoint de negocio con Basic (ejemplo)
```bash
curl -i -u admin:admin123 http://localhost:8080/api/empleados
```

## 6) Validar reglas críticas
- 5 fallos consecutivos bloquean cuenta por 15 minutos.
- Un login exitoso reinicia el contador de fallos.
- Sesión vence a las 8 horas.
- Solo una sesión UI activa por empleado; un nuevo login invalida la previa.
- `password` no aparece en respuestas de empleados.
- En actualización de empleado sin `password`, la contraseña previa se conserva.
- Auditoría de intentos de autenticación con retención mínima de 90 días.

## 7) Ejecutar pruebas

Backend:
```bash
cd apps/backend
./mvnw test
```

Frontend:
```bash
cd apps/frontend
npm run test -- --watch=false
```

## 8) Evidencia de validacion actual

- `apps/backend`: compilacion de codigo principal OK con `mvn -DskipTests compile`
- `apps/backend`: compilacion de pruebas OK con `mvn -DskipTests test-compile`
- `apps/backend`: `mvn test` ejecutado correctamente (exit code 0)
- `apps/frontend`: `npm run test -- --watch=false --browsers=ChromeHeadless` ejecutado OK usando `CHROME_BIN` de Puppeteer
  - Resultado: `TOTAL: 2 SUCCESS`
- Validacion e2e API sobre contenedores Docker (`docker compose up -d --build`):
  - `POST /api/empleados` (Basic): `201`
  - `POST /auth/login`: `200`
  - `GET /api/empleados` (Basic): `200`
  - `POST /auth/logout` (cookie session): `204`
- Contrato compartido centralizado en `packages/contracts/openapi.yaml`
- Estructura monorepo inicializada en `apps/backend` y `apps/frontend`
