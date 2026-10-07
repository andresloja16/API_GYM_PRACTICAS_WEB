# Estado del Proyecto — PULSE Gym OS

## Implementación

| Área | Estado |
| --- | --- |
| Monorepo GymApi / frontend-app-ing-web | Implementado |
| Spring Boot, JPA, H2 persistente y Maven Wrapper 3.9.9 | Implementado |
| Login, registro Socio, BCrypt y JWT HS256 | Implementado |
| RBAC de API y rutas React | Implementado |
| Siembra demo sin borrado | Implementado |
| Socios: crear, editar, buscar, filtrar y paginar | Implementado |
| Planes, renovación transaccional y pagos | Implementado |
| Comprobantes con QR e impresión | Implementado |
| Check-in y alertas | Implementado |
| Dashboard con KPIs y afluencia de API | Implementado |
| Portal Socio con QR, pagos y visitas propios | Implementado |
| Reportes y CSV | Implementado |
| Personal y auditoría JWT | Implementado |
| Diseño oscuro adaptable | Implementado |
| Prototipo previo en docs/legacy-demo | Conservado |

## Validación

- Compilación frontend Vite: correcta.
- Cuatro pruebas de integración backend: correctas, sin fallos, con rollback entre casos.
- Seis recorridos Playwright en Microsoft Edge: correctos, sin fallos; roles, registro, check-in, renovación, comprobante y móvil.
- Once capturas reales generadas en `docs/screenshots/`.
- API HTTP en 3000 y frontend en 5173 verificados conectados; auditoría JWT probada también por integración.
- Última verificación: 6 de octubre de 2026, America/Bogota, con JDK 17 y Node 22.20.0.

## Límites

- Repositorio de entrega: [andresloja16/API_GYM_PRACTICAS_WEB](https://github.com/andresloja16/API_GYM_PRACTICAS_WEB). La aplicación full-stack se ejecuta localmente; no tiene despliegue web de producción.
- H2 local; sin backups administrados ni migraciones Flyway.
- Cobros registrados después de recibir el pago; sin pasarela ni factura fiscal.
- QR de identificación/referencia; sin escaneo de cámara.
- Sin recuperación de contraseña, verificación de correo, turnos, clases o entrenadores.
- Sin afirmación de auditoría formal WCAG o certificación de producción.

## Cambio respecto al prototipo

Los datos ahora están en la API, los indicadores proceden de registros persistidos y las rutas requieren autenticación y permisos. El prototipo estático se conserva separado de la aplicación full-stack.
