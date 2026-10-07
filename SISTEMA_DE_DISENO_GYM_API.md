# Sistema de Diseño — PULSE Gym OS

## Dirección visual

Panel deportivo moderno, limpio y profesional. Fondo gris azulado claro, superficies blancas, acento azul cobalto y densidad moderada para operaciones de recepción. El login utiliza un fondo azul pastel.

## Tokens

| Token | Valor | Uso |
| --- | --- | --- |
| Fondo | `#f3f6fb` | Área principal |
| Superficie | `#ffffff` | Tarjetas |
| Borde | `#e2e8f0` | Separadores |
| Texto principal | `#172338` | Títulos |
| Texto secundario | `#64748b` | Ayuda |
| Acento | `#2563eb` | CTA y navegación |
| Error | `#b91c1c` | Rechazos |
| Advertencia | `#9a3412` | Próximos vencimientos |

Tokens CSS en `src/base.css`; Tailwind v4 declara `pulse` y `cloud` en `src/index.css`. `--accent` comparte el azul de botones, navegación y gráficos. Los estados de éxito conservan el verde semántico.

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
| Primary / Secondary | CTA azul con texto blanco y acción secundaria con borde |
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
