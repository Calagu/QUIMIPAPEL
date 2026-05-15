# QUMI Papel - Gestión de pedidos y reparto

Aplicación JavaFX para IntelliJ IDEA y Scene Builder basada en los requisitos del proyecto de prácticas:

- Login por roles.
- Gestión de clientes y productos.
- Creación y consulta de pedidos.
- Cambio de estado de pedidos.
- Vista de reparto.
- Filtros por fecha y urgencia.
- Buscador avanzado.
- Exportación CSV.
- Indicadores de prioridad, reparto y stock.

## Tecnología

- Java 21 o superior.
- JavaFX 21.0.6.
- Maven.
- SQLite local: el archivo `qumi_pedidos.db` se crea automáticamente al iniciar.
- FXML editable con Scene Builder.

## Cómo abrir en IntelliJ

1. Abre IntelliJ IDEA.
2. File > Open.
3. Selecciona la carpeta `QumiPedidos`.
4. IntelliJ detectará Maven y descargará dependencias.
5. Ejecuta:
   - Maven > javafx > `javafx:run`
   - o crea una configuración Maven con el comando `javafx:run`.

## Cómo abrir pantallas en Scene Builder

Los FXML están en:

`src/main/resources/com/qumi/app/view/`

Puedes abrir cualquier archivo `.fxml` con Scene Builder para modificar la interfaz:

- `login-view.fxml`
- `main-view.fxml`
- `dashboard-view.fxml`
- `clientes-view.fxml`
- `productos-view.fxml`
- `pedidos-view.fxml`
- `reparto-view.fxml`
- `usuarios-view.fxml`

## Usuarios de prueba

| Rol | Usuario | Contraseña |
|---|---|---|
| Administrador | admin | admin123 |
| Comercial | comercial | comercial123 |
| Oficina | oficina | oficina123 |
| Repartidor | reparto | reparto123 |

## Roles

- **ADMIN**: acceso completo.
- **COMERCIAL**: clientes, productos y pedidos.
- **OFICINA**: pedidos y reparto.
- **REPARTIDOR**: dashboard y vista de reparto.

## Base de datos

La base de datos se genera automáticamente. Si quieres reiniciar los datos de ejemplo, elimina el archivo:

`qumi_pedidos.db`

Al volver a ejecutar la app, se volverá a crear con datos iniciales.
