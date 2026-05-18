# Cambios añadidos para la parte DAM

Se ha eliminado del enfoque la parte de alumno SMR web + documentación y se ha reforzado la app de escritorio DAM.

## Añadido / corregido

- Login real conectado a MySQL.
- Contraseñas validadas con BCrypt.
- Script SQL actualizado con hashes válidos y usuarios demo.
- Navegación por roles desde `SessionManager` y `MainView`.
- Permisos diferenciados para Administrador, Oficina, Comercial y Repartidor.
- Comercial puede crear pedidos, seleccionar cliente, añadir productos, cantidades, urgencia y observaciones.
- Oficina y Administrador pueden gestionar pedidos, clientes y productos.
- Pedido permite marcar si requiere reparto o no.
- Repartidor tiene vista específica de reparto.
- Repartidor puede marcar pedidos como `Cargado`, `En reparto`, `Entregado` o `Incidencia`.
- Vista de reparto muestra datos del cliente: nombre, dirección, teléfono, notas, total y productos.
- Pedidos con filtros por estado, urgencia y fechas.
- Buscador avanzado en pedidos por ID, cliente, teléfono o notas.
- Exportación de pedidos a CSV.
- Estado nuevo `Cargado` añadido a modelo, interfaz y base de datos.
- Líneas de pedido conectadas a productos reales de la base de datos.
- Al crear un pedido se descuentan unidades del stock.
- Gestión de usuarios permite guardar contraseñas hasheadas.
- Configuración permite cambiar contraseña validando la contraseña actual.

## Usuarios demo

Contraseña para todos: `password`

- `carlos.fernandez@quimipapel.com` → Administrador
- `maria.garcia@quimipapel.com` → Comercial
- `miguel.fernandez@quimipapel.com` → Repartidor
- `ana.rodriguez@quimipapel.com` → Oficina
- `pedro.gomez@quimipapel.com` → Repartidor
