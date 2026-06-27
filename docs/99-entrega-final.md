# 99. Entrega final

## Qué se construyó

Monorepo desplegable de GluControl con frontend React/Vite, API Spring Boot Java 21, PostgreSQL 16, Flyway, Nginx y Docker Compose. La UI reproduce el lenguaje visual azul/cian, tarjetas, navegación inferior móvil, menú lateral y panel clínico de los diseños.

## Módulos implementados

- Login con contraseñas BCrypt, JWT firmado y autorización de rutas por rol.
- Experiencia paciente: dashboard, glucosa, alimentación, medicamentos, historial unificado, alertas, reporte y perfil.
- Experiencia médico: dashboard, KPIs, gráfica, alertas prioritarias, pacientes, informes y configuración.
- API CRUD para pacientes, glucosa, comidas, medicamentos, alertas y reportes; configuración de usuario.
- Regla automática de alerta para lecturas fuera de 70–180 mg/dL.
- Paginación de históricos, validación, errores globales, CORS configurable y Swagger/OpenAPI.
- Esquema PostgreSQL versionado, índices y datos demo.
- Healthchecks de PostgreSQL, backend con comprobación de base y frontend.

## Ejecución

```bash
cp .env.example .env
# editar secretos
docker compose up -d --build
docker compose ps
```

Aplicación en `http://localhost:3000`, health de API en `http://localhost:8080/api/health` y Swagger en `http://localhost:8080/swagger-ui.html`.

## Coolify

Crear un recurso Docker Compose apuntando al archivo raíz, definir `POSTGRES_PASSWORD`, `APP_JWT_SECRET`, `APP_CORS_ALLOWED_ORIGINS` y `VITE_API_URL=/api`; asociar dominio/HTTPS al puerto 80 del servicio `frontend` y conservar `postgres_data`. No se usan rutas absolutas.

## Credenciales demo

- Paciente: `paciente@glucontrol.pe` / `password`
- Médico: `medico@glucontrol.pe` / `password`

Cambiar o eliminar las cuentas antes de exponer el sistema.

## Validación realizada

- Revisión visual de 42 PNG y render a baja escala del lienzo PDF de médico/administrador/usuario.
- Frontend: 2 pruebas Vitest aprobadas y build Vite de producción aprobado.
- Backend: 2 pruebas JUnit aprobadas y build Maven Java 21 aprobado.
- Docker Compose: PostgreSQL, backend y frontend levantados con healthchecks correctos.
- API/base: `UP`; frontend `/healthz`: HTTP 200; Swagger: HTTP 200.
- Seguridad: anónimo y paciente bloqueados con HTTP 403 donde corresponde; médico autorizado.
- Flujo clínico: lectura baja, clasificación y alerta automática verificadas y limpiadas.
- Navegador: login/dashboard paciente, registro de glucosa, navegación completa y dashboard médico revisados en móvil/escritorio, sin errores de consola.

La evidencia detallada está en `docs/03-pruebas.md`. Para repetir:

```bash
docker compose build
docker compose up -d
docker compose ps
curl http://localhost:8080/api/health
curl http://localhost:3000/healthz
```

## Pendientes y supuestos

- Implementar almacenamiento privado para fotos/documentos; hoy se persisten URLs.
- Generar PDF binario y firma/compartición real de reportes.
- Integrar notificaciones push/SMS, videollamada y receta electrónica con proveedores elegidos.
- Antes de datos reales: autorización por propiedad del paciente, OIDC/MFA, revocación, auditoría, cifrado, consentimiento y cumplimiento legal.
- Añadir suites automatizadas E2E/integración una vez disponibles los servicios de build.
