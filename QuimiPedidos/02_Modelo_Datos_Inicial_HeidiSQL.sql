-- ==========================================================
-- QUMI PAPEL - Modelo de datos inicial
-- Semana 1: Análisis y diseño
-- Base de datos para MySQL / MariaDB / HeidiSQL
-- ==========================================================

DROP DATABASE IF EXISTS qumi_pedidos_semana1;
CREATE DATABASE qumi_pedidos_semana1
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE qumi_pedidos_semana1;

-- ----------------------------------------------------------
-- Tabla: usuarios
-- Guarda los usuarios que accederán a la aplicación.
-- Roles previstos: ADMIN, COMERCIAL, OFICINA, REPARTIDOR
-- ----------------------------------------------------------
CREATE TABLE usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN','COMERCIAL','OFICINA','REPARTIDOR') NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_alta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------
-- Tabla: clientes
-- Datos básicos de clientes para pedidos y reparto.
-- ----------------------------------------------------------
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    telefono VARCHAR(30),
    email VARCHAR(120),
    direccion VARCHAR(255) NOT NULL,
    localidad VARCHAR(100),
    codigo_postal VARCHAR(10),
    observaciones VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_alta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------
-- Tabla: productos
-- Catálogo inicial de productos de Qumi Papel.
-- ----------------------------------------------------------
CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    categoria VARCHAR(80),
    descripcion VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 5,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_alta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------
-- Tabla: pedidos
-- Cabecera principal del pedido.
-- ----------------------------------------------------------
CREATE TABLE pedidos (
    id_pedido INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_comercial INT NULL,
    id_repartidor INT NULL,
    fecha_pedido DATE NOT NULL DEFAULT (CURRENT_DATE),
    estado ENUM('PENDIENTE','PREPARANDO','CARGADO','EN_REPARTO','ENTREGADO','INCIDENCIA','CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
    urgencia ENUM('NORMAL','URGENTE','MUY_URGENTE') NOT NULL DEFAULT 'NORMAL',
    requiere_retorno BOOLEAN NOT NULL DEFAULT FALSE,
    direccion_entrega VARCHAR(255),
    observaciones VARCHAR(255),
    total DECIMAL(10,2) NOT NULL DEFAULT 0,

    CONSTRAINT fk_pedidos_cliente
        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),

    CONSTRAINT fk_pedidos_comercial
        FOREIGN KEY (id_comercial) REFERENCES usuarios(id_usuario),

    CONSTRAINT fk_pedidos_repartidor
        FOREIGN KEY (id_repartidor) REFERENCES usuarios(id_usuario)
);

-- ----------------------------------------------------------
-- Tabla: detalle_pedido
-- Productos incluidos en cada pedido.
-- ----------------------------------------------------------
CREATE TABLE detalle_pedido (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) GENERATED ALWAYS AS (cantidad * precio_unitario) STORED,

    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
        ON DELETE CASCADE,

    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto),

    CONSTRAINT chk_detalle_cantidad
        CHECK (cantidad > 0)
);

-- ----------------------------------------------------------
-- Tabla: historial_estados
-- Trazabilidad de cambios de estado del pedido.
-- ----------------------------------------------------------
CREATE TABLE historial_estados (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT NOT NULL,
    estado_anterior VARCHAR(30),
    estado_nuevo VARCHAR(30) NOT NULL,
    id_usuario INT NULL,
    fecha_cambio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones VARCHAR(255),

    CONSTRAINT fk_historial_pedido
        FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
        ON DELETE CASCADE,

    CONSTRAINT fk_historial_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- ----------------------------------------------------------
-- Datos iniciales de prueba
-- Nota: en una versión final se debería guardar la contraseña cifrada.
-- En esta primera semana se deja como texto de ejemplo para pruebas.
-- ----------------------------------------------------------
INSERT INTO usuarios (nombre, usuario, password_hash, rol) VALUES
('Administrador', 'admin', 'admin123', 'ADMIN'),
('Comercial Demo', 'comercial', 'comercial123', 'COMERCIAL'),
('Oficina Demo', 'oficina', 'oficina123', 'OFICINA'),
('Repartidor Demo', 'reparto', 'reparto123', 'REPARTIDOR');

INSERT INTO clientes (nombre, telefono, email, direccion, localidad, codigo_postal, observaciones) VALUES
('Papelería Centro', '600111222', 'centro@ejemplo.com', 'Calle Mayor 12', 'Alzira', '46600', 'Cliente habitual'),
('Colegio La Ribera', '600333444', 'colegio@ejemplo.com', 'Avenida del Parque 8', 'Alzira', '46600', 'Entregar por recepción'),
('Oficinas Mediterráneo', '600555666', 'oficinas@ejemplo.com', 'Polígono Industrial 4', 'Alzira', '46600', 'Llamar antes de entregar');

INSERT INTO productos (nombre, categoria, descripcion, precio, stock, stock_minimo) VALUES
('Papel A4 80g', 'Papel', 'Paquete de papel A4 para oficina', 4.50, 120, 20),
('Carpeta archivadora', 'Archivo', 'Carpeta de anillas tamaño A4', 2.20, 60, 10),
('Bolígrafo azul', 'Escritura', 'Bolígrafo azul básico', 0.35, 250, 50),
('Tóner impresora', 'Consumibles', 'Tóner compatible para oficina', 32.00, 12, 5);

-- Pedido de ejemplo
INSERT INTO pedidos (id_cliente, id_comercial, id_repartidor, estado, urgencia, requiere_retorno, direccion_entrega, observaciones)
VALUES (1, 2, 4, 'PENDIENTE', 'URGENTE', FALSE, 'Calle Mayor 12, Alzira', 'Pedido de prueba para la semana 1');

INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario) VALUES
(1, 1, 5, 4.50),
(1, 3, 20, 0.35);

UPDATE pedidos
SET total = (
    SELECT SUM(subtotal)
    FROM detalle_pedido
    WHERE detalle_pedido.id_pedido = pedidos.id_pedido
)
WHERE id_pedido = 1;

INSERT INTO historial_estados (id_pedido, estado_anterior, estado_nuevo, id_usuario, observaciones)
VALUES (1, NULL, 'PENDIENTE', 2, 'Pedido creado como ejemplo inicial');

-- ----------------------------------------------------------
-- Consultas útiles para comprobar el modelo desde HeidiSQL
-- ----------------------------------------------------------

-- Ver pedidos con cliente y repartidor:
-- SELECT p.id_pedido, c.nombre AS cliente, p.estado, p.urgencia, u.nombre AS repartidor, p.total
-- FROM pedidos p
-- JOIN clientes c ON p.id_cliente = c.id_cliente
-- LEFT JOIN usuarios u ON p.id_repartidor = u.id_usuario;

-- Ver productos con poco stock:
-- SELECT nombre, stock, stock_minimo
-- FROM productos
-- WHERE stock <= stock_minimo;

-- Ver historial de estados:
-- SELECT h.id_historial, h.id_pedido, h.estado_anterior, h.estado_nuevo, u.nombre, h.fecha_cambio
-- FROM historial_estados h
-- LEFT JOIN usuarios u ON h.id_usuario = u.id_usuario;
