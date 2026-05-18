-- ============================================================
--  QUIMIPAPEL - Script de Base de Datos MySQL
--  Versión 1.3.0 | Proyecto DAM | © 2026 QUIMIPAPEL
-- ============================================================

CREATE DATABASE IF NOT EXISTS quimipapel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE quimipapel;

-- Contraseña común de los usuarios demo: password
-- Hash BCrypt válido para "password".
SET @DEMO_HASH = '$2a$06$DCq7YPn5Rq63x1Lad4cll.0qI9g7SywogHpo3sGmKxX4Jj3cG3mG';

-- ─── USUARIOS ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuarios (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    telefono      VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    rol           ENUM('Administrador','Comercial','Repartidor','Oficina') NOT NULL DEFAULT 'Oficina',
    activo        TINYINT(1) NOT NULL DEFAULT 1,
    ultimo_acceso DATETIME,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO usuarios (id, nombre, email, telefono, password_hash, rol, activo) VALUES
(1, 'Carlos Fernández', 'carlos.fernandez@quimipapel.com', '+34 912 345 678', @DEMO_HASH, 'Administrador', 1),
(2, 'María García',     'maria.garcia@quimipapel.com',     '+34 913 456 789', @DEMO_HASH, 'Comercial',     1),
(3, 'Miguel Fernández', 'miguel.fernandez@quimipapel.com', '+34 914 567 890', @DEMO_HASH, 'Repartidor',    1),
(4, 'Ana Rodríguez',    'ana.rodriguez@quimipapel.com',    '+34 915 678 901', @DEMO_HASH, 'Oficina',       1),
(5, 'Pedro Gómez',      'pedro.gomez@quimipapel.com',      '+34 916 789 012', @DEMO_HASH, 'Repartidor',    1)
ON DUPLICATE KEY UPDATE
    nombre=VALUES(nombre), telefono=VALUES(telefono), password_hash=VALUES(password_hash), rol=VALUES(rol), activo=VALUES(activo);

-- ─── CLIENTES ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS clientes (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    empresa        VARCHAR(150) NOT NULL,
    contacto       VARCHAR(100),
    telefono       VARCHAR(30),
    email          VARCHAR(150),
    direccion      VARCHAR(255),
    ciudad         VARCHAR(100),
    codigo_postal  VARCHAR(10),
    activo         TINYINT(1) NOT NULL DEFAULT 1,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO clientes (id, empresa, contacto, telefono, email, direccion, ciudad, codigo_postal, activo) VALUES
(1, 'Ferretería López S.L.', 'Juan López',       '+34 912 345 678', 'info@ferreterialopez.com',  'C/ Mayor, 45',        'Madrid',  '28013', 1),
(2, 'Construcciones García', 'María García',     '+34 913 456 789', 'garcia@construcciones.com', 'Av. Andalucía, 12',   'Sevilla', '41001', 1),
(3, 'Pinturas Martínez',     'Luis Martínez',    '+34 914 567 890', 'info@pinturasmartinez.com', 'C/ Valencia, 78',     'Valencia','46001', 1),
(4, 'Almacén Central',       'Pedro Sánchez',    '+34 915 678 901', 'central@almacen.com',       'Pol. Industrial, 5',  'Zaragoza','50001', 1),
(5, 'Bricolaje Norte',       'Elena Torres',     '+34 916 789 012', 'info@bricolajenorte.com',   'Av. Norte, 33',       'Bilbao',  '48001', 1)
ON DUPLICATE KEY UPDATE
    empresa=VALUES(empresa), contacto=VALUES(contacto), telefono=VALUES(telefono), email=VALUES(email),
    direccion=VALUES(direccion), ciudad=VALUES(ciudad), codigo_postal=VALUES(codigo_postal), activo=VALUES(activo);

-- ─── CONDUCTORES / REPARTIDORES ─────────────────────────────
CREATE TABLE IF NOT EXISTS conductores (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  INT NOT NULL,
    estado      ENUM('Disponible','En ruta','Cargando') NOT NULL DEFAULT 'Disponible',
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

INSERT INTO conductores (id, usuario_id, estado) VALUES
(1, 3, 'En ruta'),
(2, 5, 'Disponible')
ON DUPLICATE KEY UPDATE usuario_id=VALUES(usuario_id), estado=VALUES(estado);

-- ─── CATEGORÍAS DE PRODUCTO ─────────────────────────────────
CREATE TABLE IF NOT EXISTS categorias (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(80) NOT NULL UNIQUE
);

INSERT INTO categorias (id, nombre) VALUES
(1, 'Construcción'),
(2, 'Pintura'),
(3, 'Ferretería'),
(4, 'Electricidad'),
(5, 'Fontanería'),
(6, 'Químicos')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- ─── PRODUCTOS ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS productos (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(200) NOT NULL,
    sku           VARCHAR(50) NOT NULL UNIQUE,
    categoria_id  INT,
    precio        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock         INT NOT NULL DEFAULT 0,
    stock_minimo  INT NOT NULL DEFAULT 10,
    activo        TINYINT(1) NOT NULL DEFAULT 1,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

INSERT INTO productos (id, nombre, sku, categoria_id, precio, stock, stock_minimo, activo) VALUES
(1, 'Cemento Portland 25kg',       'CEM-PORT-25', 1,  8.50, 245, 20, 1),
(2, 'Pintura blanca interior 15L', 'PINT-BLA-15', 2, 45.00,  89, 15, 1),
(3, 'Tornillos M8 x 50 (caja)',    'TORN-M8-50',  3,  3.20, 500, 50, 1),
(4, 'Cable eléctrico 2.5mm (m)',   'CAB-ELE-25',  4,  1.10, 300, 30, 1),
(5, 'Tubo PVC 110mm (m)',          'TUB-PVC-110', 5,  4.75,  42, 10, 1),
(6, 'Disolvente universal 5L',     'DIS-UNI-05',  6, 12.30,   8,  5, 1),
(7, 'Ladrillo hueco 24x12',        'LAD-HUE-24',  1,  0.45,1200,100, 1),
(8, 'Silicona neutra 310ml',       'SIL-NEU-31',  3,  3.80,  75, 20, 1)
ON DUPLICATE KEY UPDATE
    nombre=VALUES(nombre), categoria_id=VALUES(categoria_id), precio=VALUES(precio), stock=VALUES(stock),
    stock_minimo=VALUES(stock_minimo), activo=VALUES(activo);

-- ─── PEDIDOS ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS pedidos (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id    INT NOT NULL,
    usuario_id    INT,
    conductor_id  INT,
    fecha         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado        ENUM('Pendiente','Preparado','Cargado','En reparto','Entregado','Incidencia') NOT NULL DEFAULT 'Pendiente',
    urgencia      ENUM('Normal','Urgente') NOT NULL DEFAULT 'Normal',
    reparto       TINYINT(1) NOT NULL DEFAULT 1,
    notas         TEXT,
    total         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (conductor_id) REFERENCES conductores(id)
);

-- Si la tabla venía de una versión anterior, asegura que existe el estado Cargado.
ALTER TABLE pedidos MODIFY estado ENUM('Pendiente','Preparado','Cargado','En reparto','Entregado','Incidencia') NOT NULL DEFAULT 'Pendiente';

-- ─── LÍNEAS DE PEDIDO ───────────────────────────────────────
CREATE TABLE IF NOT EXISTS pedido_items (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id    INT NOT NULL,
    producto_id  INT NOT NULL,
    cantidad     INT NOT NULL DEFAULT 1,
    precio_unit  DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- Pedidos de muestra con estados, urgencia, observaciones, reparto y repartidores.
INSERT INTO pedidos (id, cliente_id, usuario_id, conductor_id, fecha, estado, urgencia, reparto, notas, total) VALUES
(1, 4, 2, 1, '2026-05-13 10:30:00', 'Incidencia', 'Urgente', 1, 'Cliente indica material dañado en entrega anterior.', 696.00),
(2, 3, 2, 1, '2026-05-13 09:15:00', 'Pendiente',  'Normal',  1, 'Llamar antes de entregar.',                         153.00),
(3, 2, 2, 2, '2026-05-13 08:45:00', 'Preparado',  'Normal',  1, 'Pedido preparado en almacén.',                       435.00),
(4, 1, 2, 1, '2026-05-13 08:00:00', 'Entregado',  'Normal',  1, 'Entregado sin incidencias.',                         335.00),
(5, 5, 2, 2, '2026-05-12 15:00:00', 'Cargado',    'Urgente', 1, 'Prioridad alta. Entrega por la tarde.',               180.00)
ON DUPLICATE KEY UPDATE
    cliente_id=VALUES(cliente_id), usuario_id=VALUES(usuario_id), conductor_id=VALUES(conductor_id), fecha=VALUES(fecha),
    estado=VALUES(estado), urgencia=VALUES(urgencia), reparto=VALUES(reparto), notas=VALUES(notas), total=VALUES(total);

INSERT INTO pedido_items (id, pedido_id, producto_id, cantidad, precio_unit) VALUES
(1, 1, 2, 10, 45.00),
(2, 1, 6, 20, 12.30),
(3, 2, 3, 30, 3.20),
(4, 2, 8, 15, 3.80),
(5, 3, 1, 40, 8.50),
(6, 3, 5, 20, 4.75),
(7, 4, 7, 500, 0.45),
(8, 4, 4, 100, 1.10),
(9, 5, 6, 10, 12.30),
(10, 5, 8, 15, 3.80)
ON DUPLICATE KEY UPDATE
    pedido_id=VALUES(pedido_id), producto_id=VALUES(producto_id), cantidad=VALUES(cantidad), precio_unit=VALUES(precio_unit);

-- ─── INCIDENCIAS ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS incidencias (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id    INT NOT NULL,
    descripcion  TEXT,
    resuelta     TINYINT(1) NOT NULL DEFAULT 0,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
);

INSERT INTO incidencias (id, pedido_id, descripcion, resuelta) VALUES
(1, 1, 'Material dañado. Se debe contactar con el cliente y preparar sustitución.', 0)
ON DUPLICATE KEY UPDATE descripcion=VALUES(descripcion), resuelta=VALUES(resuelta);

-- ─── ACTIVIDAD DE USUARIO ───────────────────────────────────
CREATE TABLE IF NOT EXISTS actividad_usuario (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  INT NOT NULL,
    accion      VARCHAR(255),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- ─── NOTIFICACIONES ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notificaciones_config (
    id                       INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id               INT NOT NULL UNIQUE,
    nuevos_pedidos           TINYINT(1) DEFAULT 1,
    pedidos_urgentes         TINYINT(1) DEFAULT 1,
    incidencias              TINYINT(1) DEFAULT 1,
    entregas_completadas     TINYINT(1) DEFAULT 0,
    actualizaciones_sistema  TINYINT(1) DEFAULT 0,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

INSERT INTO notificaciones_config (usuario_id) VALUES (1),(2),(3),(4),(5)
ON DUPLICATE KEY UPDATE usuario_id=VALUES(usuario_id);
