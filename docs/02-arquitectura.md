# 02. Arquitectura

## Vista general

```text
Navegador
  └─ HTTP/HTTPS → frontend (React compilado + Nginx)
                    └─ /api/* → backend:8080 (Spring Boot REST)
                                      └─ JDBC → postgres:5432
```

Los tres servicios comparten `glucontrol_internal`. Solo frontend y, para diagnóstico local, backend publican puertos. PostgreSQL no se publica y persiste en el volumen `postgres_data`. En Coolify se recomienda asociar el dominio al puerto 80 de `frontend` y retirar la exposición pública de backend si la plataforma permite sobrescribirla.

## Monorepo

- `frontend/`: React, Vite, React Router, cliente HTTP, Nginx y Dockerfile multi-stage.
- `backend/`: Java 21, Spring Boot REST, capas controller/service/repository/entity/dto/mapper/config/exception.
- `backend/src/main/resources/db/migration/`: fuente de verdad del esquema Flyway.
- `database/`: operación, respaldo y restauración.
- `docs/`: análisis, decisiones y entrega.
- `docker-compose.yml`: ejecución reproducible local/Coolify.

## Backend

Spring Web expone JSON; Validation verifica contratos; Spring Data JPA maneja persistencia; Flyway crea y evoluciona el esquema; springdoc publica OpenAPI. `GlobalExceptionHandler` normaliza errores. Las consultas de series históricas y pacientes son paginadas. El healthcheck comprueba una conexión real a PostgreSQL.

La separación de DTOs evita filtrar hashes o detalles internos. `ApiMapper` concentra conversiones; `ClinicalService` contiene reglas como la creación automática de alertas. Para crecer, este servicio puede dividirse por dominio sin cambiar los contratos REST.

## Frontend

React Router separa las rutas paciente y médico. El cliente centralizado usa `VITE_API_URL`; en contenedor su valor recomendado es `/api`. Nginx sirve la SPA, resuelve el fallback de rutas y hace proxy al hostname interno `backend`, por lo que el navegador nunca necesita resolver nombres Docker.

Los estados visuales loading, error, empty y success son reutilizables. La navegación paciente usa shell móvil con barra inferior y drawer; el panel médico usa sidebar y tablas responsive.

## Configuración

| Variable | Uso | Predeterminado local |
|---|---|---|
| `POSTGRES_DB` | Base | `glucontrol` |
| `POSTGRES_USER` | Usuario PostgreSQL | `glucontrol` |
| `POSTGRES_PASSWORD` | Secreto PostgreSQL | solo desarrollo |
| `DB_PORT` | Reservado para herramientas locales | `5432` |
| `BACKEND_PORT` | Puerto host API | `8080` |
| `FRONTEND_PORT` | Puerto host web | `3000` |
| `APP_CORS_ALLOWED_ORIGINS` | Orígenes permitidos, separados por coma | localhost |
| `APP_JWT_SECRET` | Firma HS256; mínimo 32 caracteres | solo desarrollo |
| `VITE_API_URL` | Base HTTP compilada en frontend | `/api` |

## Persistencia, salud y arranque

1. `postgres` arranca y pasa `pg_isready`.
2. `backend` ejecuta migraciones y valida JPA; su healthcheck prueba API y base.
3. `frontend` inicia cuando backend está sano y expone `/healthz`.

Todos usan `restart: unless-stopped`. Los Dockerfiles son multi-stage y el backend se ejecuta como usuario no root. No hay rutas absolutas ni dependencias del host en Compose.

## Seguridad y evolución

- Cambiar la contraseña PostgreSQL y mantener `.env` fuera de Git.
- Terminar TLS en Coolify.
- JWT y autorización por rol ya protegen la API; agregar OIDC, revocación y autorización por propiedad del recurso antes de un entorno con pacientes reales.
- Usar secretos de Coolify en vez de valores versionados.
- Añadir auditoría, cifrado de adjuntos, backups probados y políticas de retención para cumplimiento sanitario.
- Sustituir URLs de imágenes externas por almacenamiento privado S3/MinIO.
