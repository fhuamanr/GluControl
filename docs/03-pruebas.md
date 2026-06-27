# 03. Pruebas y evidencia

Fecha de ejecución: 27 de junio de 2026.

## Resultado

| Área | Prueba | Resultado |
|---|---|---|
| Frontend | Vitest: login paciente, persistencia JWT y render médico | 2/2 OK |
| Backend | JUnit: hash del seed y claims JWT | 2/2 OK |
| Build | Vite producción | OK, 1 585 módulos |
| Build | Maven/Spring Boot Java 21 | OK |
| PostgreSQL | Inicio, migraciones Flyway y seed | OK |
| Health | PostgreSQL, backend y frontend | Healthy / `UP` / HTTP 200 |
| Seguridad | Recurso autenticado sin JWT | HTTP 403 |
| Seguridad | Paciente intentando `/api/doctor/dashboard` | HTTP 403 |
| Seguridad | Médico consultando dashboard | HTTP 200 |
| OpenAPI | `/swagger-ui.html` | HTTP 200 |
| Regla clínica | Glucosa 59 mg/dL | `LOW` + alerta `WARNING` |
| Limpieza | Datos temporales creados por pruebas | OK |
| Navegador | Consola después de los flujos | 0 errores/warnings |

## Contraste visual con Figma

### Paciente, viewport 390 x 844

- Login: logo, claim, campos, CTA azul, cuentas demo y mensaje de privacidad.
- Dashboard: saludo, card azul de lectura, rango saludable, acceso a registro, indicadores, medicamentos y consejo.
- Shell: topbar, drawer, cinco accesos inferiores y todos los módulos detectados.
- Registro de glucosa: encabezado, acción circular, formulario, unidad mg/dL, contexto, confirmación e historial.
- Paleta, radios, espaciado, jerarquía y comportamiento móvil son coherentes con los PNG revisados.

### Médico, viewport 1280 x 720 y responsive 390 x 844

- Sidebar, encabezado clínico, cuatro KPIs, gráfica, alertas prioritarias y tabla de pacientes.
- La jerarquía y composición corresponden al panel médico del PDF.
- En móvil, la navegación se transforma en barra inferior y conserva nombres accesibles.

## Flujos ejercitados en navegador

1. Login paciente con credenciales seed.
2. Carga de dashboard mediante Nginx `/api` → backend → PostgreSQL.
3. Navegación a glucosa.
4. Alta de una lectura de 111 mg/dL y confirmación visible.
5. Apertura del drawer y verificación de todos los módulos.
6. Cierre de sesión, selección de cuenta médica y login.
7. Carga del dashboard médico y tabla con datos reales del seed.
8. Eliminación de la lectura temporal.

## Defectos encontrados y corregidos

1. Una comilla mal cerrada en la ruta de medicamentos impedía compilar JSX.
2. El hash BCrypt del seed no correspondía a la contraseña demo documentada.
3. Botones solo-icono y navegación médica responsive carecían de nombres accesibles.
4. Los Dockerfiles omitían las suites durante el build; ahora `docker compose build` falla si una prueba falla.
5. El healthcheck frontend resolvía `localhost` como IPv6 aunque Nginx escuchaba IPv4; ahora usa `127.0.0.1`.

## Repetición

```bash
docker compose build
docker compose up -d
docker compose ps

# Suites individuales dentro de contenedores efímeros
docker build --target build -t glucontrol-frontend-test ./frontend
docker build --target build -t glucontrol-backend-test ./backend
```

La validación visual sigue siendo una prueba de aceptación, no una comparación pixel-perfect automatizada. Para CI se recomienda añadir Playwright con snapshots en los breakpoints 390 x 844 y 1280 x 720.
