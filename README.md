# GluControl

Aplicación fullstack de seguimiento de glucosa basada en los diseños de `Figma/`. Incluye experiencia móvil para paciente, panel web para médico, API Spring Boot, PostgreSQL, migraciones y despliegue reproducible con Docker Compose/Coolify.

## Inicio rápido

Requisitos: Docker con Compose v2.

```bash
cp .env.example .env
# Cambia POSTGRES_PASSWORD en .env
docker compose -f docker-compose.yml -f docker-compose.local.yml up -d --build
docker compose -f docker-compose.yml -f docker-compose.local.yml ps
```

- Aplicación: `http://localhost:3000`
- API health: `http://localhost:8080/api/health`
- Swagger: `http://localhost:8080/swagger-ui.html`

Credenciales demo:

| Rol | Correo | Contraseña |
|---|---|---|
| Paciente | `paciente@glucontrol.pe` | `password` |
| Médico | `medico@glucontrol.pe` | `password` |

Estas credenciales son únicamente para desarrollo.

## Desarrollo sin contenedores

Frontend (Node 22):

```bash
cd frontend
npm install
npm run dev
```

Backend (Java 21 + Maven 3.9), con PostgreSQL disponible:

```bash
cd backend
mvn spring-boot:run
```

Vite proxifica `/api` a `http://localhost:8080` en desarrollo.

## Despliegue en Coolify

1. Sube el repositorio a Git y crea un recurso **Docker Compose** en Coolify.
2. Selecciona `docker-compose.yml` en la raíz.
3. Configura `POSTGRES_PASSWORD` y `APP_JWT_SECRET` (32+ caracteres aleatorios) como secretos fuertes. Mantén `VITE_API_URL=/api`.
4. Configura `APP_CORS_ALLOWED_ORIGINS=https://tu-dominio`.
5. Asocia el dominio al servicio `frontend`, puerto `80`, y habilita HTTPS.
6. Despliega y comprueba `/healthz`, `/api/health` y Swagger (puedes no exponer Swagger públicamente).
7. Conserva los volúmenes `postgres_data` y `uploads_data` entre despliegues y programa backups.

No configures `localhost` ni `backend:8080` como URL del navegador: Nginx ya enruta `/api` por la red interna.

## API principal

Los contratos completos están en Swagger. El registro público usa `POST /api/auth/register`; crea usuario, paciente y preferencias, devuelve JWT e inicia sesión. El médico consulta pacientes registrados mediante `/api/doctor/patients` y `/api/doctor/patients/{id}/summary|measurements|meals|medications|alerts`.

Las fotos de comida se cargan con `POST /api/uploads/meals` (`multipart/form-data`). Se aceptan JPEG, PNG y WebP hasta 5 MB; se sirven desde `/api/uploads/{archivo}` y persisten en `uploads_data`.

## Comandos útiles

```bash
docker compose -f docker-compose.yml -f docker-compose.local.yml logs -f backend
docker compose -f docker-compose.yml -f docker-compose.local.yml exec postgres psql -U glucontrol -d glucontrol
docker compose build --no-cache
docker compose -f docker-compose.yml -f docker-compose.local.yml down
docker compose -f docker-compose.yml -f docker-compose.local.yml down -v  # elimina datos e imágenes
```

## Pruebas

Las suites se ejecutan obligatoriamente durante `docker compose build`: 4 pruebas Vitest para registro/login/foto/roles y 6 pruebas JUnit para registro, archivos, credenciales y JWT. La entrega de estas mejoras está en [docs/10-mejoras-paciente-doctor-comida.md](docs/10-mejoras-paciente-doctor-comida.md).

## Troubleshooting

- Backend `unhealthy`: revisa que las credenciales coincidan y ejecuta `docker compose logs backend postgres`.
- Error CORS: agrega el dominio exacto, con protocolo, a `APP_CORS_ALLOWED_ORIGINS` y recrea backend.
- Rutas React devuelven 404: accede mediante el servicio frontend; Nginx contiene el fallback SPA.
- Cambió `VITE_API_URL`: es variable de compilación; reconstruye la imagen frontend.
- No se guardan imágenes: confirma que `uploads_data` está montado y que backend inicia mediante `docker-entrypoint.sh`.
- Migración fallida: no edites una migración ya aplicada; crea `V3__descripcion.sql`.

## Checklist de producción

- [ ] Secretos fuertes en Coolify, sin `.env` versionado.
- [ ] HTTPS y dominio asociados a frontend.
- [ ] Backup y restauración de PostgreSQL probados.
- [ ] Reemplazar el JWT local por OIDC o añadir revocación, autorización por recurso y auditoría antes de usar datos reales.
- [ ] CORS limitado al dominio real.
- [ ] Healthchecks en verde y volumen persistente confirmado.
- [ ] Política de privacidad, consentimiento y retención aprobados.

Más detalle: [análisis Figma](docs/01-analisis-figma.md) y [arquitectura](docs/02-arquitectura.md).
