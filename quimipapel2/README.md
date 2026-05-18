# QUIMIPAPEL – Gestión DAM v1.3.0

Aplicación de escritorio en **JavaFX 21 + MySQL 8** para la gestión digital de pedidos, clientes, productos y reparto de la empresa QUIMIPAPEL.

Esta versión está centrada en la parte de **Alumnos DAM**. No incluye la parte de **SMR web + documentación**.

---

## Funcionalidades implementadas

| Requisito DAM | Estado |
|---|---|
| Sistema de login conectado a base de datos | Implementado |
| Login real por email y contraseña | Implementado |
| Contraseñas con BCrypt | Implementado |
| Gestión de usuarios | Implementado |
| Navegación por roles | Implementado |
| Gestión de clientes | Implementado |
| Gestión de productos | Implementado |
| Creación y consulta de pedidos | Implementado |
| Añadir productos y cantidades al pedido | Implementado |
| Marcar urgencia y observaciones | Implementado |
| Indicar si el pedido requiere reparto | Implementado |
| Cambio de estado de pedidos | Implementado |
| Vista de reparto | Implementado |
| Repartidor ve cliente, dirección, teléfono, pedido y estado | Implementado |
| Repartidor puede marcar cargado, en reparto, entregado o incidencia | Implementado |
| Filtros por fecha, estado y urgencia | Implementado |
| Buscador avanzado de pedidos | Implementado |
| Exportación de pedidos a CSV | Implementado |
| Indicadores de prioridad / urgencia | Implementado |

---

## Roles y permisos

| Rol | Permisos principales |
|---|---|
| Administrador | Control total: usuarios, clientes, productos, pedidos, reparto y configuración |
| Oficina | Gestiona pedidos, clientes y productos. Puede indicar si un pedido requiere reparto |
| Comercial | Crea pedidos, selecciona cliente, añade productos, cantidades, urgencia y observaciones |
| Repartidor | Consulta entregas y marca pedidos como cargado, en reparto, entregado o incidencia |

---

## Requisitos

| Herramienta | Versión mínima |
|---|---|
| Java JDK | 17 o superior |
| JavaFX SDK | 21.0.2 |
| Maven | 3.8+ |
| MySQL | 8.0+ |

---

## 1. Configurar la base de datos

Abre MySQL Workbench o cualquier cliente MySQL y ejecuta el script:

```sql
source /ruta/quimipapel/database/quimipapel.sql
```

También puedes abrir `database/quimipapel.sql`, copiar todo el contenido y ejecutarlo directamente.

El script crea:

- Base de datos `quimipapel`
- Usuarios
- Clientes
- Conductores / repartidores
- Categorías
- Productos
- Pedidos
- Líneas de pedido
- Incidencias
- Configuración de notificaciones

---

## 2. Configurar la conexión MySQL

Por defecto la app intenta conectar con:

```text
URL:      jdbc:mysql://localhost:3306/quimipapel
Usuario:  root
Password: vacío
```

Puedes cambiarlo de dos formas.

### Opción A: Variables de entorno

```bash
QUIMIPAPEL_DB_URL=jdbc:mysql://localhost:3306/quimipapel?useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true&characterEncoding=UTF-8
QUIMIPAPEL_DB_USER=root
QUIMIPAPEL_DB_PASS=tu_password
```

### Opción B: Parámetros VM

```bash
-Dquimipapel.db.url="jdbc:mysql://localhost:3306/quimipapel?useSSL=false&serverTimezone=Europe/Madrid&allowPublicKeyRetrieval=true&characterEncoding=UTF-8"
-Dquimipapel.db.user="root"
-Dquimipapel.db.pass="tu_password"
```

---

## 3. Ejecutar la aplicación

### Con Maven

```bash
cd quimipapel
mvn clean javafx:run
```

### Crear JAR

```bash
mvn clean package
java --module-path /ruta/a/javafx-sdk-21/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/quimipapel-app-1.3.0.jar
```

### Con IntelliJ IDEA

1. Abre la carpeta `quimipapel`.
2. Espera a que Maven importe las dependencias.
3. Ejecuta `Main.java`.
4. Asegúrate de tener MySQL iniciado y el script SQL ejecutado.

---

## Usuarios demo

Todos los usuarios demo tienen la contraseña:

```text
password
```

| Email | Rol |
|---|---|
| carlos.fernandez@quimipapel.com | Administrador |
| maria.garcia@quimipapel.com | Comercial |
| miguel.fernandez@quimipapel.com | Repartidor |
| ana.rodriguez@quimipapel.com | Oficina |
| pedro.gomez@quimipapel.com | Repartidor |

---

## Estructura del proyecto

```text
quimipapel/
├── database/
│   └── quimipapel.sql
├── pom.xml
└── src/main/java/com/quimipapel/
    ├── Main.java
    ├── model/
    │   ├── Usuario.java
    │   ├── Cliente.java
    │   ├── Producto.java
    │   ├── Pedido.java
    │   └── PedidoItem.java
    ├── dao/
    │   ├── UsuarioDAO.java
    │   ├── ClienteDAO.java
    │   ├── ProductoDAO.java
    │   └── PedidoDAO.java
    ├── util/
    │   ├── DatabaseUtil.java
    │   ├── PasswordUtil.java
    │   ├── SessionManager.java
    │   └── StyleHelper.java
    └── view/
        ├── LoginView.java
        ├── MainView.java
        ├── DashboardView.java
        ├── PedidosView.java
        ├── ClientesView.java
        ├── ProductosView.java
        ├── RepartoView.java
        ├── UsuariosView.java
        └── ConfiguracionView.java
```

---

## Nota sobre pantalla pequeña o DPI de Windows

Si la app se ve recortada en Windows con escalado 125 % o 150 %, puedes ejecutar la JVM con:

```bash
-Dglass.win.uiScale=100%
-Dprism.allowhidpi=false
```

---

© 2026 QUIMIPAPEL – Proyecto DAM


## Correcciones v1.3.1

Esta versión corrige problemas de interacción detectados en la app:

- Perfil superior se actualiza al cambiar el nombre en configuración.
- Botón de cambiar foto funcional en configuración.
- Preferencias de notificaciones activables/desactivables.
- Notificaciones de la barra superior con menú desplegable.
- Botón de cerrar sesión visible y claro.
- Reparto refresca al marcar entregado y oculta el pedido entregado.
- Filtros reales en clientes.
- Edición de pedidos para Administrador y Oficina.
- Botón "Ver todos" del inicio enlazado a Gestión de Pedidos.
- Buscador superior eliminado.
