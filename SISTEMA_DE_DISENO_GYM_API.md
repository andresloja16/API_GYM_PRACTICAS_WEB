# Sistema de Diseño — PULSE Gym OS

## Dirección visual

Panel deportivo moderno, limpio y profesional. Fondo grafito, superficies oscuras, acento verde lima y densidad moderada para operaciones de recepción.

## Tokens

| Token | Valor | Uso |
| --- | --- | --- |
| Fondo | `#111413` | Área principal |
| Superficie | `#191d1b` | Tarjetas |
| Borde | `#2a302c` | Separadores |
| Texto principal | `#f1f3ef` | Títulos |
| Texto secundario | `#8b948e` | Ayuda |
| Acento | `#c2f66b` | CTA y navegación |
| Error | `#fa8585` | Rechazos |
| Advertencia | `#eabb78` | Próximos vencimientos |

Tokens CSS en `src/base.css`; Tailwind v4 declara `pulse` y `graphite` en `src/index.css`.

## Tipografía y espaciado

- DM Sans para interfaz y tablas; Barlow Condensed para el titular deportivo del login.
- Alternativa sans-serif cuando Google Fonts no está disponible.
- Título de pantalla de 35 px en escritorio; texto de interfaz de 12–14 px.
- Pasos de espaciado de 4 px, tarjetas con padding de 20–25 px.
- Radios de 9 px en tarjetas y 5–6 px en botones e inputs.
- Sidebar de 238 px; rail de 76 px en pantallas pequeñas.

## Componentes

| Componente | Comportamiento |
| --- | --- |
| Layout / PageHeading | Menú por rol, usuario, API y fecha Bogotá |
| Primary / Secondary | CTA lima y acción secundaria con borde |
| Badge | Verde Activo, rojo Vencido, gris Inactivo; texto además de color |
| Alert / ErrorBox | Icono, título y explicación accesible |
| Modal | Dialog nativo, cierre con botón y Escape |
| MemberTable | Búsqueda, filtro, paginación, exportación y acciones |
| MemberEditor | Validaciones y errores de servidor |
| RenewalModal / Receipt | Confirmación de pago, QR e impresión |
| AttendanceChart | Barras y descripción accesible |
| CheckInPanel | Cédula y validación de API |

## Estados y accesibilidad

- Carga visible; botones deshabilitados durante envíos.
- Errores de red y API con mensajes y reintento.
- Sesión vencida lleva al login; ruta sin permiso lleva a 403.
- Resultados con `role="status"` y errores con `role="alert"`.
- Formularios con labels, patrones y tipos semánticos; validación adicional en backend.
- Foco visible, nombres accesibles en iconos de navegación y títulos en QR.
- API consultada cada 15 segundos.
- KPIs apilados y tablas con desplazamiento interno en móvil.
- Estilo específico para impresión de comprobantes.
- Verificación en escritorio 1440×1000 y móvil 390×844. No se afirma certificación WCAG; una auditoría formal queda fuera del alcance.
