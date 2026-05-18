-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         10.4.32-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.13.0.7160
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.actividad_usuario: ~0 rows (aproximadamente)

-- Volcando estructura para tabla quimipapel.categorias
CREATE TABLE IF NOT EXISTS `categorias` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(80) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.categorias: ~6 rows (aproximadamente)
INSERT INTO `categorias` (`id`, `nombre`) VALUES
	(1, 'Construcción'),
	(2, 'Pintura'),
	(3, 'Ferretería'),
	(4, 'Electricidad'),
	(5, 'Fontanería'),
	(6, 'Químicos');

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.clientes: ~5 rows (aproximadamente)
INSERT INTO `clientes` (`id`, `empresa`, `contacto`, `telefono`, `email`, `direccion`, `ciudad`, `codigo_postal`, `activo`, `created_at`) VALUES
	(1, 'Ferretería López S.L.', 'Juan López', '+34 912 345 678', 'info@ferreterialopez.com', 'C/ Mayor, 45', 'Madrid', '28013', 1, '2026-05-18 11:25:48'),
	(2, 'Construcciones García', 'María García', '+34 913 456 789', 'garcia@construcciones.com', 'Av. Andalucía, 12', 'Sevilla', '41001', 1, '2026-05-18 11:25:48'),
	(3, 'Pinturas Martínez', 'Luis Martínez', '+34 914 567 890', 'info@pinturasmartinez.com', 'C/ Valencia, 78', 'Valencia', '46001', 1, '2026-05-18 11:25:48'),
	(4, 'Almacén Central', 'Pedro Sánchez', '+34 915 678 901', 'central@almacen.com', 'Pol. Industrial, 5', 'Zaragoza', '50001', 1, '2026-05-18 11:25:48'),
	(5, 'Bricolaje Norte', 'Elena Torres', '+34 916 789 012', 'info@bricolajenorte.com', 'Av. Norte, 33', 'Bilbao', '48001', 1, '2026-05-18 11:25:48');

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
	(1, 3, 'En ruta'),
	(2, 5, 'Disponible');

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.incidencias: ~1 rows (aproximadamente)
INSERT INTO `incidencias` (`id`, `pedido_id`, `descripcion`, `resuelta`, `created_at`) VALUES
	(1, 1, 'Material dañado. Se debe contactar con el cliente y preparar sustitución.', 0, '2026-05-18 11:25:48');

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
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.notificaciones_config: ~5 rows (aproximadamente)
INSERT INTO `notificaciones_config` (`id`, `usuario_id`, `nuevos_pedidos`, `pedidos_urgentes`, `incidencias`, `entregas_completadas`, `actualizaciones_sistema`) VALUES
	(1, 1, 1, 1, 1, 0, 0),
	(2, 2, 1, 1, 1, 0, 0),
	(3, 3, 1, 1, 1, 0, 0),
	(4, 4, 1, 1, 1, 0, 0),
	(5, 5, 1, 1, 1, 0, 0);

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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.pedido_items: ~10 rows (aproximadamente)
INSERT INTO `pedido_items` (`id`, `pedido_id`, `producto_id`, `cantidad`, `precio_unit`) VALUES
	(1, 1, 2, 10, 45.00),
	(2, 1, 6, 20, 12.30),
	(3, 2, 3, 30, 3.20),
	(4, 2, 8, 15, 3.80),
	(5, 3, 1, 40, 8.50),
	(6, 3, 5, 20, 4.75),
	(7, 4, 7, 500, 0.45),
	(8, 4, 4, 100, 1.10),
	(9, 5, 6, 10, 12.30),
	(10, 5, 8, 15, 3.80);

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.pedidos: ~5 rows (aproximadamente)
INSERT INTO `pedidos` (`id`, `cliente_id`, `usuario_id`, `conductor_id`, `fecha`, `estado`, `urgencia`, `reparto`, `notas`, `total`) VALUES
	(1, 4, 2, 1, '2026-05-13 10:30:00', 'Entregado', 'Urgente', 1, 'Cliente indica material dañado en entrega anterior.', 696.00),
	(2, 3, 2, 1, '2026-05-13 09:15:00', 'Entregado', 'Normal', 1, 'Llamar antes de entregar.', 153.00),
	(3, 2, 2, 2, '2026-05-13 08:45:00', 'Preparado', 'Normal', 1, 'Pedido preparado en almacén.', 435.00),
	(4, 1, 2, 1, '2026-05-13 08:00:00', 'Entregado', 'Normal', 1, 'Entregado sin incidencias.', 335.00),
	(5, 5, 2, 2, '2026-05-12 15:00:00', 'Cargado', 'Urgente', 1, 'Prioridad alta. Entrega por la tarde.', 180.00);

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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.productos: ~8 rows (aproximadamente)
INSERT INTO `productos` (`id`, `nombre`, `sku`, `categoria_id`, `precio`, `stock`, `stock_minimo`, `activo`, `created_at`) VALUES
	(1, 'Cemento Portland 25kg', 'CEM-PORT-25', 1, 8.50, 245, 20, 1, '2026-05-18 11:25:48'),
	(2, 'Pintura blanca interior 15L', 'PINT-BLA-15', 2, 45.00, 89, 15, 1, '2026-05-18 11:25:48'),
	(3, 'Tornillos M8 x 50 (caja)', 'TORN-M8-50', 3, 3.20, 500, 50, 1, '2026-05-18 11:25:48'),
	(4, 'Cable eléctrico 2.5mm (m)', 'CAB-ELE-25', 4, 1.10, 300, 30, 1, '2026-05-18 11:25:48'),
	(5, 'Tubo PVC 110mm (m)', 'TUB-PVC-110', 5, 4.75, 42, 10, 0, '2026-05-18 11:25:48'),
	(6, 'Disolvente universal 5L', 'DIS-UNI-05', 6, 12.30, 8, 5, 1, '2026-05-18 11:25:48'),
	(7, 'Ladrillo hueco 24x12', 'LAD-HUE-24', 1, 0.45, 1200, 100, 1, '2026-05-18 11:25:48'),
	(8, 'Silicona neutra 310ml', 'SIL-NEU-31', 3, 3.80, 75, 20, 1, '2026-05-18 11:25:48');

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla quimipapel.usuarios: ~5 rows (aproximadamente)
INSERT INTO `usuarios` (`id`, `nombre`, `email`, `telefono`, `password_hash`, `rol`, `activo`, `ultimo_acceso`, `created_at`) VALUES
	(1, 'Carlos Sospedra', 'carlos.fernandez@quimipapel.com', '+34 912 345 678', 'password', 'Administrador', 1, '2026-05-18 11:44:09', '2026-05-18 11:25:48'),
	(2, 'María García', 'maria.garcia@quimipapel.com', '+34 913 456 789', 'password', 'Comercial', 1, '2026-05-18 11:39:48', '2026-05-18 11:25:48'),
	(3, 'Miguel Fernández', 'miguel.fernandez@quimipapel.com', '+34 914 567 890', 'password', 'Repartidor', 1, NULL, '2026-05-18 11:25:48'),
	(4, 'Ana Rodríguez', 'ana.rodriguez@quimipapel.com', '+34 915 678 901', 'password', 'Oficina', 1, NULL, '2026-05-18 11:25:48'),
	(5, 'Pedro Gómez', 'pedro.gomez@quimipapel.com', '+34 916 789 012', 'password', 'Repartidor', 1, NULL, '2026-05-18 11:25:48');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
