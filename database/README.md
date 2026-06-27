# Base de datos

PostgreSQL 16 es administrado por Docker Compose. El esquema se versiona con Flyway en
`backend/src/main/resources/db/migration`; no se requieren scripts manuales.

Para respaldo: `docker compose exec -T postgres pg_dump -U glucontrol glucontrol > backup.sql`.
Para restaurar en una base vacía: `Get-Content backup.sql | docker compose exec -T postgres psql -U glucontrol glucontrol`.

