# 10. Mejoras paciente, doctor y comidas

## Revisión inicial

### Pantallas afectadas

- Login: existe y autentica contra `POST /api/auth/login`, pero inicialmente no ofrecía registro.
- Registro: no existía.
- Shell paciente: funciona bien en móvil, pero estaba limitado a 540 px y centrado en escritorio.
- Alimentación: permitía registrar datos nutricionales, pero no seleccionar, previsualizar ni persistir una foto.
- Panel médico: consumía datos del backend, aunque mostraba acciones de “Nuevo/Añadir paciente”, prioridades estáticas y no tenía detalle clínico por paciente.
- Pacientes médico: consultaba `/api/patients`, sin detalle agregado específico para el rol médico.

### Modelo actual

- `User` contiene identidad, BCrypt, estado y roles `PATIENT`, `DOCTOR`, `ADMIN`.
- `Patient` mantiene una relación 1:1 con `User`, datos demográficos, diagnóstico y rango objetivo.
- Glucosa, comidas, medicamentos, alertas e informes pertenecen a `Patient`.
- JWT contiene correo, rol e identificador de usuario. Antes de esta mejora, la API protegía rutas por autenticación/rol médico, pero no comprobaba propiedad de cada paciente.
- `Meal.photoUrl` ya existía, sin servicio de archivos asociado.

### Endpoints preexistentes relevantes

- `POST /api/auth/login`
- CRUD `/api/patients`
- `GET /api/patients/{id}/glucose|meals|medications|alerts|reports`
- CRUD `/api/glucose`, `/api/meals`, `/api/medications`, `/api/alerts`, `/api/reports`
- `GET /api/doctor/dashboard`

## Cambios necesarios

1. Añadir registro público que cree atómicamente `User`, `Patient` y `UserSettings`, siempre con rol `PATIENT`.
2. Asociar el frontend a la identidad devuelta por JWT y validar propiedad de recursos en backend.
3. Sustituir el CRUD visual médico por consulta, filtros y detalle de pacientes registrados.
4. Eliminar KPIs/listados médicos codificados en frontend y derivarlos de PostgreSQL.
5. Crear un shell paciente desktop real sin alterar el breakpoint móvil.
6. Añadir carga multipart de imágenes, validaciones, volumen persistente y preview en comidas.
7. Mantener el Compose Coolify sin puertos; proporcionar un override local para exponer 3000/8080 solo durante desarrollo.

## Riesgos de compatibilidad

- Quitar `ports` del Compose principal cambia el comando local: deberá usarse el override documentado.
- Las URLs de imágenes requieren conservar el nuevo volumen `uploads_data` entre despliegues.
- La migración de demo existente permanece por compatibilidad con instalaciones ya desplegadas; las cuentas están claramente identificadas como demo.
- Endurecer autorización puede revelar llamadas frontend que antes dependían de acceso cruzado entre pacientes.
- Los archivos locales no son adecuados para réplicas múltiples; en despliegue horizontal deberá usarse S3/MinIO.

## Supuestos funcionales

- El registro inicia sesión automáticamente para evitar repetir credenciales.
- Documento y datos clínicos son opcionales en UI; si falta documento, backend genera un identificador interno único.
- Las imágenes admitidas son JPEG, PNG y WebP, con máximo configurable de 5 MB.
- El médico tiene lectura clínica global y no puede crear pacientes desde su módulo.
- El seed demo se conserva para facilitar aceptación y se documenta; producción deberá desactivarlo/eliminarlo en una migración operativa si la política lo exige.

## Implementación y validación

### Implementado

- Login enlaza a `/register`; el registro crea `User(PATIENT)`, `Patient` y `UserSettings` en una sola transacción e inicia sesión con JWT.
- Los pacientes solo leen/escriben su propio perfil y recursos. Un intento cruzado devuelve HTTP 403.
- El layout paciente mantiene la navegación inferior móvil y desde 960 px usa sidebar, contenido ancho y dashboard en columnas.
- Comidas ofrece cámara (`accept="image/*"`, `capture="environment"`), archivo desktop, preview, reemplazo/eliminación y persistencia de URL.
- Backend valida JPEG/PNG/WebP y 5 MB. Las imágenes usan nombres UUID, se guardan en `/app/uploads`, persisten en `uploads_data` y se sirven por `/api/uploads/{archivo}`.
- El entrypoint ajusta el propietario del volumen y ejecuta Java como usuario `app`, no como root.
- Médico ya no muestra “Nuevo/Añadir paciente”. Lista solo registros `PATIENT` reales, con correo, edad, última glucosa, estado y última actividad.
- La ficha médica agrega resumen, glucosa, comidas/fotos, medicamentos y alertas mediante endpoints de solo lectura.
- El Compose principal conserva `expose` sin publicar puertos; `docker-compose.local.yml` añade 3000/8080 y CORS localhost solo para desarrollo.

### Endpoints añadidos

- `POST /api/auth/register`
- `POST /api/uploads/meals`
- `GET /api/uploads/{filename}`
- `GET /api/doctor/patients`
- `GET /api/doctor/patients/{id}/summary`
- `GET /api/doctor/patients/{id}/measurements`
- `GET /api/doctor/patients/{id}/meals`
- `GET /api/doctor/patients/{id}/medications`
- `GET /api/doctor/patients/{id}/alerts`

### Pruebas ejecutadas

- 4/4 Vitest: registro, login/JWT, render médico y comida con foto.
- 6/6 JUnit: registro transaccional, email duplicado, almacenamiento/tipo de imagen, BCrypt y JWT.
- Builds Vite y Maven aprobados.
- PostgreSQL, backend y frontend saludables en Compose.
- Flujo real verificado: registro de Lucía → medición 117 → comida con PNG → médico la lista → ficha muestra 117, 100% en rango y comida.
- Imagen servida por Nginx/API con HTTP 200.
- Paciente consultando otro paciente: HTTP 403.
- Consola del navegador: sin errores ni warnings.
- Los pacientes, registros e imagen QA se eliminaron al finalizar.

### Pendientes técnicos

- El almacenamiento local funciona con una réplica. Para escalado horizontal, migrar `FileStorageService` a S3/MinIO.
- El `GET` de imagen usa URL opaca pública para que `<img>` funcione sin cabecera JWT. Para información clínica real conviene usar URLs firmadas de corta duración.
- El seed demo permanece como migración documentada por compatibilidad. Una instalación productiva que no lo necesite debe retirarlo con una migración nueva, nunca editando una migración ya aplicada.
