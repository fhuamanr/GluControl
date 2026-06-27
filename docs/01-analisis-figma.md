# 01. Análisis de Figma

## Material revisado

Se revisaron 42 PNG exportados y el lienzo `GluControl.pdf` (una página de 25 676 x 41 272 puntos). Los PNG describen con detalle la experiencia móvil del paciente; el PDF agrega flujos web para médico, administrador y usuario. El sistema visual usa fondo blanco/gris muy claro, azul intenso como acción primaria, cian y verde para estados saludables, rojo/ámbar para alertas, tarjetas redondeadas, iconografía lineal y alta densidad de información clínica.

## Pantallas detectadas

### Paciente móvil

1. Inicio de sesión, con variaciones de estado y acción de crear cuenta.
2. Menú lateral con perfil, resumen, glucosa, medicamentos, alimentos, historial, reportes, alertas, configuración, centro de ayuda y cierre de sesión.
3. Inicio/resumen de salud: saludo, última glucosa, rango, accesos rápidos, próximos medicamentos y consejo.
4. Registro de glucosa: valor en mg/dL, contexto (ayunas/antes/después de comer/dormir), confirmación y resultado correcto.
5. Resumen e historial de glucosa con tendencias y lecturas en rango.
6. Alimentación: registro, fotografía del plato, análisis nutricional, confirmación y resumen de comida.
7. Medicamentos: listado, alta, dosis, frecuencia, horario, recordatorios, evidencia/foto y confirmación de toma.
8. Historial de salud en línea de tiempo, combinando glucosa, comidas y medicación.
9. Alertas inteligentes categorizadas por severidad.
10. Reporte para el médico con KPIs, gráfica, recomendaciones y opción de compartir/descargar.
11. Perfil con información personal, información médica, preferencias y contactos.
12. Adjuntar foto o documento como evidencia clínica.

### Médico web

1. Panel de control con pacientes activos, alertas críticas, seguimientos, HbA1c promedio, gráfica poblacional y prioridades.
2. Videollamada clínica.
3. Gestión/listado de pacientes con búsqueda, filtros y estados.
4. Ficha/historial clínico con métricas, glucosa, alimentación, medicamentos y documentos.
5. Alta y edición de medicamentos.
6. Generación y previsualización de recetas.
7. Gestión de alertas, gráficos y comunicación/videollamada.
8. Informes clínicos.
9. Configuración.

### Administrador web

1. Dashboard operativo.
2. Gestión de médicos y estados de cuenta.
3. Registro/edición de profesionales.
4. Gestión de pacientes.
5. Formularios de configuración y catálogos.

## Módulos funcionales y flujos

- Identidad y roles: login, sesión, perfil y preferencias.
- Pacientes: datos demográficos, diagnóstico, rangos objetivo y contacto de emergencia.
- Glucosa: registrar → validar → clasificar en rango → crear alerta si corresponde → mostrar en historial/reporte.
- Alimentación: registrar comida/foto → almacenar macronutrientes → mostrar resumen e historial.
- Medicación: prescripción → horario/recordatorio → confirmación de toma → adherencia.
- Alertas: regla clínica → prioridad → notificación → reconocimiento/seguimiento.
- Historia clínica: línea de tiempo unificada y documentos adjuntos.
- Reportes: consolidación temporal → indicadores → resumen → compartir con médico.
- Operación clínica: dashboard, búsqueda de paciente, revisión de ficha y seguimiento.

## Entidades de negocio

- `User`: identidad, correo, credencial, rol y estado.
- `Patient`: usuario asociado, documento, fecha de nacimiento, diagnóstico, rango objetivo y emergencia.
- `GlucoseMeasurement`: valor, fecha, contexto y notas.
- `Meal`: tipo, fecha, carbohidratos, calorías, foto y notas.
- `Medication`: nombre, dosis, frecuencia, horario, vigencia e instrucciones.
- `Alert`: tipo, severidad, mensaje, fecha y reconocimiento.
- `MedicalReport`: periodo, promedio, porcentaje en rango, resumen y documento.
- `UserSettings`: alertas, recordatorios, idioma y zona horaria.

En una siguiente iteración son naturales `MedicationIntake`, `Appointment`, `ClinicalDocument`, `Prescription` y `VideoConsultation`.

## Endpoints propuestos e implementados

- `POST /api/auth/login`
- `GET|POST /api/patients`, `GET|PUT|DELETE /api/patients/{id}`
- `GET /api/patients/{id}/glucose`, `POST /api/glucose`, `PUT|DELETE /api/glucose/{id}`
- `GET /api/patients/{id}/meals`, `POST /api/meals`, `PUT|DELETE /api/meals/{id}`
- `GET /api/patients/{id}/medications`, `POST /api/medications`, `PUT|DELETE /api/medications/{id}`
- `GET /api/patients/{id}/alerts`, `POST /api/alerts`, `PUT|DELETE /api/alerts/{id}`, `PATCH /api/alerts/{id}/acknowledge`
- `GET /api/patients/{id}/reports`, `POST /api/reports`, `DELETE /api/reports/{id}`
- `GET|PUT /api/users/{id}/settings`
- `GET /api/doctor/dashboard`
- `GET /api/health`

## Roles

- `PATIENT`: registra y consulta su información de autocuidado.
- `DOCTOR`: supervisa pacientes, alertas e informes.
- `ADMIN`: administra usuarios, profesionales y operación; el modelo lo contempla y la UI completa queda como evolución.

## Supuestos

1. Se usa mg/dL y rangos iniciales 70–180 mg/dL, configurables por paciente.
2. El paciente demo es adulto con diabetes tipo 2; no se emiten diagnósticos automáticos.
3. Las fotos se representan por URL; almacenamiento S3/MinIO no forma parte del alcance actual.
4. El reporte PDF se representa en UI y como entidad; la generación binaria firmada es una evolución.
5. Login se implementa con BCrypt y JWT firmado de 12 horas; la SPA conserva el token localmente. Antes de usar datos reales se recomienda OIDC, autorización por propiedad del recurso, rotación/revocación, recuperación de contraseña, MFA y auditoría.
6. Videollamada, notificaciones push/SMS y firma de receta requieren proveedores externos no proporcionados.
7. El frontend prioriza los flujos principales detectados y conserva la identidad visual responsive; variantes repetidas del diseño se modelan como estados de una misma pantalla.
