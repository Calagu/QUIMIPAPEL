-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         10.4.32-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.14.0.7165
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para quimipapel
CREATE DATABASE IF NOT EXISTS `quimipapel` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `quimipapel`;

-- Volcando estructura para tabla quimipapel.actividad_usuario
CREATE TABLE IF NOT EXISTS `actividad_usuario` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) NOT NULL,
  `accion` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `actividad_usuario_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.actividad_usuario: ~8 rows (aproximadamente)
INSERT INTO `actividad_usuario` (`id`, `usuario_id`, `accion`, `created_at`) VALUES
	(1, 1, 'Alta inicial del sistema', '2026-05-18 10:43:25'),
	(2, 2, 'Creó pedido #1', '2026-05-18 10:43:25'),
	(3, 3, 'Registró cliente nuevo', '2026-05-18 10:43:25'),
	(4, 4, 'Actualizó stock de productos', '2026-05-18 10:43:25'),
	(5, 5, 'Inició reparto', '2026-05-18 10:43:25'),
	(6, 6, 'Confirmó entrega', '2026-05-18 10:43:25'),
	(7, 7, 'Revisó incidencias', '2026-05-18 10:43:25'),
	(8, 8, 'Generó presupuesto', '2026-05-18 10:43:25');

-- Volcando estructura para tabla quimipapel.categorias
CREATE TABLE IF NOT EXISTS `categorias` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(80) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.categorias: ~10 rows (aproximadamente)
INSERT INTO `categorias` (`id`, `nombre`) VALUES
	(1, 'Papel'),
	(2, 'Cartón'),
	(3, 'Embalaje'),
	(4, 'Limpieza'),
	(5, 'Oficina'),
	(6, 'Higiene'),
	(7, 'Desechables'),
	(8, 'Consumibles'),
	(9, 'Etiquetado'),
	(10, 'Seguridad');

-- Volcando estructura para tabla quimipapel.clientes
CREATE TABLE IF NOT EXISTS `clientes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `empresa` varchar(150) NOT NULL,
  `contacto` varchar(100) DEFAULT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `ciudad` varchar(100) DEFAULT NULL,
  `codigo_postal` varchar(10) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.clientes: ~10 rows (aproximadamente)
INSERT INTO `clientes` (`id`, `empresa`, `contacto`, `telefono`, `email`, `direccion`, `ciudad`, `codigo_postal`, `activo`, `created_at`) VALUES
	(1, 'Supermercados Sol', 'Elena Martín', '910000001', 'compras@supermercadossol.es', 'Calle Mayor 12', 'Madrid', '28001', 1, '2026-05-18 10:43:25'),
	(2, 'Distribuciones Norte', 'Ángel Pérez', '910000002', 'info@distnorte.es', 'Avenida Europa 45', 'Madrid', '28008', 1, '2026-05-18 10:43:25'),
	(3, 'Oficinas Central', 'Mónica López', '910000003', 'pedidos@oficinascentral.es', 'Paseo del Prado 22', 'Madrid', '28014', 1, '2026-05-18 10:43:25'),
	(4, 'Papelería Estrella', 'Raúl Sánchez', '910000004', 'pedidos@papeleriaestrella.es', 'Calle Alcalá 88', 'Madrid', '28009', 1, '2026-05-18 10:43:25'),
	(5, 'Almacenes Vega', 'Lucía Romero', '910000005', 'compras@almacenesvega.es', 'Calle Toledo 101', 'Madrid', '28005', 1, '2026-05-18 10:43:25'),
	(6, 'Residencias Vida', 'Pedro Navarro', '910000006', 'compras@residenciasvida.es', 'Calle Goya 33', 'Madrid', '28002', 1, '2026-05-18 10:43:25'),
	(7, 'Hospital del Sur', 'Sara Molina', '910000007', 'logistica@hospitalsur.es', 'Avenida del Sur 77', 'Madrid', '28031', 1, '2026-05-18 10:43:25'),
	(8, 'Colegio Nuevo Futuro', 'Diego Herrera', '910000008', 'admin@colegionuevofuturo.es', 'Calle López de Hoyos 150', 'Madrid', '28020', 1, '2026-05-18 10:43:25'),
	(9, 'Restauración Iberia', 'Clara Vega', '910000009', 'compras@restib.es', 'Calle Atocha 55', 'Madrid', '28012', 1, '2026-05-18 10:43:25'),
	(10, 'Logística Rapid', 'Iván Castro', '910000010', 'pedidos@lograpid.es', 'Calle Leganés 9', 'Madrid', '28914', 1, '2026-05-18 10:43:25');

-- Volcando estructura para tabla quimipapel.conductores
CREATE TABLE IF NOT EXISTS `conductores` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) NOT NULL,
  `estado` enum('Disponible','En ruta','Cargando') NOT NULL DEFAULT 'Disponible',
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `conductores_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.conductores: ~2 rows (aproximadamente)
INSERT INTO `conductores` (`id`, `usuario_id`, `estado`) VALUES
	(1, 5, 'Disponible'),
	(2, 6, 'En ruta');

-- Volcando estructura para tabla quimipapel.incidencias
CREATE TABLE IF NOT EXISTS `incidencias` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pedido_id` int(11) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `resuelta` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `pedido_id` (`pedido_id`),
  CONSTRAINT `incidencias_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.incidencias: ~3 rows (aproximadamente)
INSERT INTO `incidencias` (`id`, `pedido_id`, `descripcion`, `resuelta`, `created_at`) VALUES
	(1, 7, 'Producto faltante detectado en preparación.', 0, '2026-05-18 10:43:25'),
	(2, 2, 'Retraso por exceso de carga en ruta.', 0, '2026-05-18 10:43:25'),
	(3, 10, 'Cliente solicitó cambio de franja horaria.', 1, '2026-05-18 10:43:25');

-- Volcando estructura para tabla quimipapel.notificaciones_config
CREATE TABLE IF NOT EXISTS `notificaciones_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usuario_id` int(11) NOT NULL,
  `nuevos_pedidos` tinyint(1) DEFAULT 1,
  `pedidos_urgentes` tinyint(1) DEFAULT 1,
  `incidencias` tinyint(1) DEFAULT 1,
  `entregas_completadas` tinyint(1) DEFAULT 0,
  `actualizaciones_sistema` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `notificaciones_config_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.notificaciones_config: ~8 rows (aproximadamente)
INSERT INTO `notificaciones_config` (`id`, `usuario_id`, `nuevos_pedidos`, `pedidos_urgentes`, `incidencias`, `entregas_completadas`, `actualizaciones_sistema`) VALUES
	(1, 1, 1, 1, 1, 1, 1),
	(2, 2, 1, 1, 1, 0, 1),
	(3, 3, 1, 1, 1, 0, 0),
	(4, 4, 1, 1, 1, 1, 0),
	(5, 5, 1, 1, 1, 1, 0),
	(6, 6, 1, 1, 1, 1, 0),
	(7, 7, 1, 1, 1, 0, 1),
	(8, 8, 1, 1, 0, 0, 1);

-- Volcando estructura para tabla quimipapel.pedido_items
CREATE TABLE IF NOT EXISTS `pedido_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pedido_id` int(11) NOT NULL,
  `producto_id` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 1,
  `precio_unit` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pedido_id` (`pedido_id`),
  KEY `producto_id` (`producto_id`),
  CONSTRAINT `pedido_items_ibfk_1` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`) ON DELETE CASCADE,
  CONSTRAINT `pedido_items_ibfk_2` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.pedido_items: ~30 rows (aproximadamente)
INSERT INTO `pedido_items` (`id`, `pedido_id`, `producto_id`, `cantidad`, `precio_unit`) VALUES
	(1, 1, 1, 10, 4.95),
	(2, 1, 6, 20, 1.85),
	(3, 1, 9, 15, 1.10),
	(4, 2, 11, 8, 8.90),
	(5, 2, 15, 12, 6.80),
	(6, 2, 29, 4, 11.95),
	(7, 3, 17, 2, 9.90),
	(8, 3, 19, 10, 1.95),
	(9, 3, 35, 6, 1.20),
	(10, 4, 1, 20, 4.95),
	(11, 4, 7, 40, 1.55),
	(12, 4, 24, 30, 2.95),
	(13, 5, 26, 1, 42.00),
	(14, 5, 27, 2, 8.90),
	(15, 5, 31, 5, 14.20),
	(16, 6, 21, 8, 4.60),
	(17, 6, 23, 20, 2.95),
	(18, 6, 36, 10, 2.10),
	(19, 7, 12, 15, 2.35),
	(20, 7, 13, 5, 11.60),
	(21, 7, 38, 2, 24.90),
	(22, 8, 18, 4, 9.90),
	(23, 8, 20, 10, 2.40),
	(24, 8, 34, 25, 1.35),
	(25, 9, 3, 12, 5.40),
	(26, 9, 8, 3, 14.70),
	(27, 9, 30, 6, 19.90),
	(28, 10, 2, 25, 9.80),
	(29, 10, 5, 10, 12.90),
	(30, 10, 33, 50, 1.25);

-- Volcando estructura para tabla quimipapel.pedidos
CREATE TABLE IF NOT EXISTS `pedidos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cliente_id` int(11) NOT NULL,
  `usuario_id` int(11) DEFAULT NULL,
  `conductor_id` int(11) DEFAULT NULL,
  `fecha` datetime NOT NULL DEFAULT current_timestamp(),
  `estado` enum('Pendiente','Preparado','Cargado','En reparto','Entregado','Incidencia') NOT NULL DEFAULT 'Pendiente',
  `urgencia` enum('Normal','Urgente') NOT NULL DEFAULT 'Normal',
  `reparto` tinyint(1) NOT NULL DEFAULT 1,
  `notas` text DEFAULT NULL,
  `total` decimal(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id`),
  KEY `cliente_id` (`cliente_id`),
  KEY `usuario_id` (`usuario_id`),
  KEY `conductor_id` (`conductor_id`),
  CONSTRAINT `pedidos_ibfk_1` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`),
  CONSTRAINT `pedidos_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `pedidos_ibfk_3` FOREIGN KEY (`conductor_id`) REFERENCES `conductores` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.pedidos: ~10 rows (aproximadamente)
INSERT INTO `pedidos` (`id`, `cliente_id`, `usuario_id`, `conductor_id`, `fecha`, `estado`, `urgencia`, `reparto`, `notas`, `total`) VALUES
	(1, 1, 2, 1, '2026-05-01 09:15:00', 'Entregado', 'Normal', 1, 'Entrega sin incidencias.', 126.40),
	(2, 2, 3, 1, '2026-05-02 11:20:00', 'En reparto', 'Urgente', 1, 'Prioridad por rotura de stock.', 248.75),
	(3, 3, 4, 2, '2026-05-03 08:40:00', 'Preparado', 'Normal', 1, 'Preparar para mañana.', 89.60),
	(4, 4, 2, 2, '2026-05-04 10:10:00', 'Cargado', 'Normal', 1, 'Pedido grande de papelería.', 312.95),
	(5, 5, 3, NULL, '2026-05-05 13:30:00', 'Pendiente', 'Urgente', 0, 'Recogida en almacén.', 54.20),
	(6, 6, 4, 1, '2026-05-06 15:45:00', 'Entregado', 'Normal', 1, 'Cliente satisfecho.', 177.30),
	(7, 7, 7, 2, '2026-05-07 09:05:00', 'Incidencia', 'Urgente', 1, 'Falta un producto del pedido.', 98.90),
	(8, 8, 8, NULL, '2026-05-08 12:00:00', 'Pendiente', 'Normal', 0, 'Pedido interno de oficina.', 64.35),
	(9, 9, 9, 1, '2026-05-09 16:10:00', 'Entregado', 'Normal', 1, 'Entregado en recepción.', 205.70),
	(10, 10, 1, 2, '2026-05-10 10:25:00', 'En reparto', 'Urgente', 1, 'Cliente con urgencia máxima.', 411.20);

-- Volcando estructura para tabla quimipapel.productos
CREATE TABLE IF NOT EXISTS `productos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(200) NOT NULL,
  `sku` varchar(50) NOT NULL,
  `categoria_id` int(11) DEFAULT NULL,
  `precio` decimal(10,2) NOT NULL DEFAULT 0.00,
  `stock` int(11) NOT NULL DEFAULT 0,
  `stock_minimo` int(11) NOT NULL DEFAULT 10,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `sku` (`sku`),
  KEY `categoria_id` (`categoria_id`),
  CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.productos: ~41 rows (aproximadamente)
INSERT INTO `productos` (`id`, `nombre`, `sku`, `categoria_id`, `precio`, `stock`, `stock_minimo`, `activo`, `created_at`) VALUES
	(1, 'Papel A4 80g 500 hojas', 'PAP-A4-500', 1, 4.95, 450, 50, 1, '2026-05-18 10:43:25'),
	(2, 'Papel A3 80g 500 hojas', 'PAP-A3-500', 1, 9.80, 180, 30, 1, '2026-05-18 10:43:25'),
	(3, 'Papel reciclado A4 500 hojas', 'PAP-REC-A4', 1, 5.40, 220, 40, 1, '2026-05-18 10:43:25'),
	(4, 'Cartulina blanca 50 hojas', 'CAR-BL-50', 2, 7.25, 90, 20, 1, '2026-05-18 10:43:25'),
	(5, 'Cartón ondulado doble canal', 'CAR-OND-DOB', 2, 12.90, 60, 10, 1, '2026-05-18 10:43:25'),
	(6, 'Caja de archivo grande', 'CAJ-ARCH-G', 3, 1.85, 1000, 100, 1, '2026-05-18 10:43:25'),
	(7, 'Caja de archivo mediana', 'CAJ-ARCH-M', 3, 1.55, 850, 100, 1, '2026-05-18 10:43:25'),
	(8, 'Rollo film estirable 500mm', 'FILM-500', 3, 14.70, 120, 20, 1, '2026-05-18 10:43:25'),
	(9, 'Cinta adhesiva transparente', 'CINT-TRA-48', 3, 1.10, 1500, 150, 1, '2026-05-18 10:43:25'),
	(10, 'Cinta precinto marrón', 'CINT-PRE-M', 3, 1.25, 1100, 150, 1, '2026-05-18 10:43:25'),
	(11, 'Detergente multiusos 5L', 'LIM-MULT-5L', 4, 8.90, 75, 15, 1, '2026-05-18 10:43:25'),
	(12, 'Limpiacristales 1L', 'LIM-CRIS-1L', 4, 2.35, 260, 30, 1, '2026-05-18 10:43:25'),
	(13, 'Desinfectante superficies 5L', 'DESINF-5L', 4, 11.60, 95, 15, 1, '2026-05-18 10:43:25'),
	(14, 'Ambientador spray 400ml', 'AMB-SPR-400', 4, 3.20, 140, 20, 1, '2026-05-18 10:43:25'),
	(15, 'Guantes nitrilo talla M', 'GUA-NIT-M', 5, 6.80, 300, 50, 1, '2026-05-18 10:43:25'),
	(16, 'Guantes nitrilo talla L', 'GUA-NIT-L', 5, 6.80, 280, 50, 1, '2026-05-18 10:43:25'),
	(17, 'Bolígrafo azul caja 50', 'BOL-AZ-50', 5, 9.90, 200, 40, 1, '2026-05-18 10:43:25'),
	(18, 'Bolígrafo negro caja 50', 'BOL-NE-50', 5, 9.90, 190, 40, 1, '2026-05-18 10:43:25'),
	(19, 'Cuaderno A4 rayado', 'CUE-A4-RAY', 5, 1.95, 500, 80, 1, '2026-05-18 10:43:25'),
	(20, 'Cuaderno A5 espiral', 'CUE-A5-ESP', 5, 2.40, 420, 80, 1, '2026-05-18 10:43:25'),
	(21, 'Papel higiénico industrial 6 uds', 'HIG-PHI-6', 6, 4.60, 310, 50, 1, '2026-05-18 10:43:25'),
	(22, 'Papel secamanos 2 capas', 'HIG-SEC-2C', 6, 18.50, 110, 20, 1, '2026-05-18 10:43:25'),
	(23, 'Servilleta blanca 40x40', 'SER-BLA-40', 7, 2.95, 600, 100, 1, '2026-05-18 10:43:25'),
	(24, 'Plato desechable pack 50', 'PLA-50', 7, 3.75, 260, 40, 1, '2026-05-18 10:43:25'),
	(25, 'Vaso desechable pack 100', 'VAS-100', 7, 4.10, 240, 40, 1, '2026-05-18 10:43:25'),
	(26, 'Tóner compatible HP 117A negro', 'TON-HP117A', 8, 42.00, 45, 10, 1, '2026-05-18 10:43:25'),
	(27, 'Tinta compatible Epson 104 cyan', 'TIN-EPS-104-C', 8, 8.90, 55, 10, 1, '2026-05-18 10:43:25'),
	(28, 'Etiquetas adhesivas A4 100 hojas', 'ETQ-A4-100', 9, 11.95, 130, 20, 1, '2026-05-18 10:43:25'),
	(29, 'Etiquetadora manual', 'ETQ-MAN-01', 9, 19.90, 35, 8, 1, '2026-05-18 10:43:25'),
	(30, 'Señal de seguridad salida', 'SEG-SALIDA', 10, 6.40, 70, 10, 1, '2026-05-18 10:43:25'),
	(31, 'Señal de uso obligatorio guantes', 'SEG-GUA', 10, 5.80, 85, 10, 1, '2026-05-18 10:43:25'),
	(32, 'Cono señalización 75 cm', 'SEG-CONO-75', 10, 14.20, 40, 8, 1, '2026-05-18 10:43:25'),
	(33, 'Papel kraft 70cm rollo', 'KRAFT-70', 3, 13.50, 95, 15, 1, '2026-05-18 10:43:25'),
	(34, 'Cinta doble cara industrial', 'CIN-DOB-IND', 3, 2.65, 210, 30, 1, '2026-05-18 10:43:25'),
	(35, 'Marcador permanente negro', 'MAR-PER-NEG', 5, 1.35, 700, 100, 1, '2026-05-18 10:43:25'),
	(36, 'Rotulador fluorescente amarillo', 'ROT-FLU-AM', 5, 1.20, 620, 100, 1, '2026-05-18 10:43:25'),
	(37, 'Bloc notas adhesivas 76x76', 'BLOC-ADH-76', 5, 2.10, 480, 80, 1, '2026-05-18 10:43:25'),
	(38, 'Jabón de manos 5L', 'JAB-MAN-5L', 4, 7.45, 88, 15, 1, '2026-05-18 10:43:25'),
	(39, 'Gel hidroalcohólico 5L', 'GEL-HID-5L', 4, 15.30, 66, 12, 1, '2026-05-18 10:43:25'),
	(40, 'Mascarillas FFP2 caja 50', 'MAS-FFP2-50', 10, 24.90, 52, 10, 1, '2026-05-18 10:43:25'),
	(41, 'jabon ', 'TH-495U', 4, 99.99, 11, 1, 1, '2026-05-18 12:48:15');

-- Volcando estructura para tabla quimipapel.usuarios
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `rol` enum('Administrador','Comercial','Repartidor','Oficina') NOT NULL DEFAULT 'Oficina',
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `ultimo_acceso` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.usuarios: ~8 rows (aproximadamente)
INSERT INTO `usuarios` (`id`, `nombre`, `email`, `telefono`, `password_hash`, `rol`, `activo`, `ultimo_acceso`, `created_at`) VALUES
	(1, 'Admin General', 'admin@quimipapel.com', '600100101', 'password', 'Administrador', 1, '2026-05-18 12:46:00', '2026-05-18 10:43:25'),
	(2, 'Laura Gómez', 'laura.gomez@quimipapel.com', '600100102', 'password', 'Oficina', 1, '2026-05-18 10:43:25', '2026-05-18 10:43:25'),
	(3, 'Carlos Ruiz', 'carlos.ruiz@quimipapel.com', '600100103', 'password', 'Comercial', 1, '2026-05-18 10:43:25', '2026-05-18 10:43:25'),
	(4, 'Marta León', 'marta.leon@quimipapel.com', '600100104', 'password', 'Oficina', 1, '2026-05-18 10:43:25', '2026-05-18 10:43:25'),
	(5, 'Javier Torres', 'javier.torres@quimipapel.com', '600100105', 'password', 'Repartidor', 1, '2026-05-18 13:04:18', '2026-05-18 10:43:25'),
	(6, 'Sergio Vidal', 'sergio.vidal@quimipapel.com', '600100106', 'password', 'Repartidor', 1, '2026-05-18 13:05:59', '2026-05-18 10:43:25'),
	(7, 'Ana Prieto', 'ana.prieto@quimipapel.com', '600100107', 'password', 'Oficina', 1, '2026-05-18 10:43:25', '2026-05-18 10:43:25'),
	(8, 'Rubén Gil', 'ruben.gil@quimipapel.com', '600100108', 'password', 'Comercial', 1, '2026-05-18 10:43:25', '2026-05-18 10:43:25');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
