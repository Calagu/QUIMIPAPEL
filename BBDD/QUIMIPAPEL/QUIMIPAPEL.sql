-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         10.4.32-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.17.0.7270
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para qumi_pedidos
CREATE DATABASE IF NOT EXISTS `qumi_pedidos` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `qumi_pedidos`;

-- Volcando estructura para tabla qumi_pedidos.clientes
CREATE TABLE IF NOT EXISTS `clientes` (
  `id_cliente` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(120) NOT NULL,
  `cif_nif` varchar(20) DEFAULT NULL,
  `telefono` varchar(30) DEFAULT NULL,
  `email` varchar(120) DEFAULT NULL,
  `direccion` varchar(200) NOT NULL,
  `ciudad` varchar(80) NOT NULL,
  `provincia` varchar(80) DEFAULT NULL,
  `codigo_postal` varchar(10) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_alta` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_cliente`),
  KEY `idx_clientes_nombre` (`nombre`),
  KEY `idx_clientes_ciudad` (`ciudad`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla qumi_pedidos.clientes: ~4 rows (aproximadamente)
INSERT INTO `clientes` (`id_cliente`, `nombre`, `cif_nif`, `telefono`, `email`, `direccion`, `ciudad`, `provincia`, `codigo_postal`, `activo`, `fecha_alta`) VALUES
	(1, 'Papeleria Centro', 'B12345678', '600111222', 'centro@example.com', 'Calle Mayor 12', 'Madrid', 'Madrid', '28013', 1, '2026-05-11 10:07:28'),
	(2, 'Colegio San Miguel', 'G87654321', '600333444', 'compras@sanjmiguel.example.com', 'Avenida de la Escuela 5', 'Madrid', 'Madrid', '28020', 1, '2026-05-11 10:07:28'),
	(3, 'Oficinas Norte SL', 'B11223344', '600555666', 'pedidos@oficinasnorte.example.com', 'Poligono Norte Nave 8', 'Alcobendas', 'Madrid', '28100', 1, '2026-05-11 10:07:28'),
	(4, 'Libreria Alameda', 'B99887766', '600777888', 'alameda@example.com', 'Paseo Alameda 21', 'Valencia', 'Valencia', '46010', 1, '2026-05-11 10:07:28');

-- Volcando estructura para tabla qumi_pedidos.historial_estado_pedido
CREATE TABLE IF NOT EXISTS `historial_estado_pedido` (
  `id_historial` int(11) NOT NULL AUTO_INCREMENT,
  `id_pedido` int(11) NOT NULL,
  `estado_anterior` varchar(30) DEFAULT NULL,
  `estado_nuevo` varchar(30) NOT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  `fecha_cambio` datetime NOT NULL DEFAULT current_timestamp(),
  `comentario` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_historial`),
  KEY `fk_historial_usuarios` (`id_usuario`),
  KEY `idx_historial_pedido` (`id_pedido`),
  KEY `idx_historial_fecha` (`fecha_cambio`),
  CONSTRAINT `fk_historial_pedidos` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id_pedido`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_historial_usuarios` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla qumi_pedidos.historial_estado_pedido: ~4 rows (aproximadamente)
INSERT INTO `historial_estado_pedido` (`id_historial`, `id_pedido`, `estado_anterior`, `estado_nuevo`, `id_usuario`, `fecha_cambio`, `comentario`) VALUES
	(1, 1, 'PENDIENTE', 'PENDIENTE', 2, '2026-05-11 10:07:28', 'Pedido creado'),
	(2, 2, 'EN_PREPARACION', 'EN_PREPARACION', 3, '2026-05-11 10:07:28', 'Pedido enviado a preparacion'),
	(3, 3, 'LISTO_REPARTO', 'LISTO_REPARTO', 3, '2026-05-11 10:07:28', 'Pedido preparado para reparto'),
	(4, 3, 'LISTO_REPARTO', 'LISTO_REPARTO', 3, '2026-05-11 10:07:28', 'Pedido asignado a repartidor');

-- Volcando estructura para tabla qumi_pedidos.pedido_detalle
CREATE TABLE IF NOT EXISTS `pedido_detalle` (
  `id_detalle` int(11) NOT NULL AUTO_INCREMENT,
  `id_pedido` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL DEFAULT 0.00,
  `subtotal` decimal(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id_detalle`),
  KEY `idx_detalle_pedido` (`id_pedido`),
  KEY `idx_detalle_producto` (`id_producto`),
  CONSTRAINT `fk_detalle_pedidos` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id_pedido`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_detalle_productos` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla qumi_pedidos.pedido_detalle: ~9 rows (aproximadamente)
INSERT INTO `pedido_detalle` (`id_detalle`, `id_pedido`, `id_producto`, `cantidad`, `precio_unitario`, `subtotal`) VALUES
	(1, 1, 1, 10, 4.50, 45.00),
	(2, 1, 3, 30, 0.45, 13.50),
	(3, 1, 5, 5, 2.20, 11.00),
	(4, 2, 1, 20, 4.50, 90.00),
	(5, 2, 7, 2, 24.90, 49.80),
	(6, 2, 8, 10, 3.75, 37.50),
	(7, 3, 2, 6, 8.90, 53.40),
	(8, 3, 4, 40, 0.45, 18.00),
	(9, 3, 6, 8, 2.20, 17.60);

-- Volcando estructura para tabla qumi_pedidos.pedidos
CREATE TABLE IF NOT EXISTS `pedidos` (
  `id_pedido` int(11) NOT NULL AUTO_INCREMENT,
  `id_cliente` int(11) NOT NULL,
  `id_usuario_creador` int(11) NOT NULL,
  `fecha_pedido` datetime NOT NULL DEFAULT current_timestamp(),
  `fecha_entrega_estimada` date DEFAULT NULL,
  `urgencia` enum('BAJA','MEDIA','ALTA','CRITICA') NOT NULL DEFAULT 'MEDIA',
  `estado` enum('PENDIENTE','EN_PREPARACION','LISTO_REPARTO','EN_REPARTO','ENTREGADO','CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
  `direccion_entrega` varchar(200) NOT NULL,
  `observaciones` text DEFAULT NULL,
  `total` decimal(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id_pedido`),
  KEY `fk_pedidos_usuarios` (`id_usuario_creador`),
  KEY `idx_pedidos_fecha` (`fecha_pedido`),
  KEY `idx_pedidos_estado` (`estado`),
  KEY `idx_pedidos_urgencia` (`urgencia`),
  KEY `idx_pedidos_cliente` (`id_cliente`),
  CONSTRAINT `fk_pedidos_clientes` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`) ON UPDATE CASCADE,
  CONSTRAINT `fk_pedidos_usuarios` FOREIGN KEY (`id_usuario_creador`) REFERENCES `usuarios` (`id_usuario`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla qumi_pedidos.pedidos: ~3 rows (aproximadamente)
INSERT INTO `pedidos` (`id_pedido`, `id_cliente`, `id_usuario_creador`, `fecha_pedido`, `fecha_entrega_estimada`, `urgencia`, `estado`, `direccion_entrega`, `observaciones`, `total`) VALUES
	(1, 1, 2, '2026-05-11 10:07:28', '2026-05-13', 'MEDIA', 'PENDIENTE', 'Calle Mayor 12, Madrid', 'Entregar por la manana', 69.50),
	(2, 2, 2, '2026-05-11 10:07:28', '2026-05-12', 'ALTA', 'EN_PREPARACION', 'Avenida de la Escuela 5, Madrid', 'Pedido urgente para secretaria', 177.30),
	(3, 3, 3, '2026-05-11 10:07:28', '2026-05-14', 'BAJA', 'LISTO_REPARTO', 'Poligono Norte Nave 8, Alcobendas', 'Llamar antes de entregar', 89.00);

-- Volcando estructura para tabla qumi_pedidos.productos
CREATE TABLE IF NOT EXISTS `productos` (
  `id_producto` int(11) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(40) NOT NULL,
  `nombre` varchar(120) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `categoria` varchar(80) DEFAULT NULL,
  `precio` decimal(10,2) NOT NULL DEFAULT 0.00,
  `stock` int(11) NOT NULL DEFAULT 0,
  `stock_minimo` int(11) NOT NULL DEFAULT 5,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_alta` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `idx_productos_nombre` (`nombre`),
  KEY `idx_productos_categoria` (`categoria`),
  KEY `idx_productos_stock` (`stock`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla qumi_pedidos.productos: ~8 rows (aproximadamente)
INSERT INTO `productos` (`id_producto`, `codigo`, `nombre`, `descripcion`, `categoria`, `precio`, `stock`, `stock_minimo`, `activo`, `fecha_alta`) VALUES
	(1, 'PAP-A4-80', 'Papel A4 80g', 'Paquete de papel blanco A4 de 500 hojas', 'Papel', 4.50, 90, 20, 1, '2026-05-11 10:07:28'),
	(2, 'PAP-A3-80', 'Papel A3 80g', 'Paquete de papel blanco A3 de 500 hojas', 'Papel', 8.90, 54, 10, 1, '2026-05-11 10:07:28'),
	(3, 'BOL-AZ-001', 'Boligrafo azul', 'Boligrafo azul de escritura suave', 'Escritura', 0.45, 470, 50, 1, '2026-05-11 10:07:28'),
	(4, 'BOL-NE-001', 'Boligrafo negro', 'Boligrafo negro de escritura suave', 'Escritura', 0.45, 390, 50, 1, '2026-05-11 10:07:28'),
	(5, 'CAR-AZ-010', 'Carpeta azul', 'Carpeta de anillas color azul', 'Archivo', 2.20, 75, 15, 1, '2026-05-11 10:07:28'),
	(6, 'CAR-RO-010', 'Carpeta roja', 'Carpeta de anillas color rojo', 'Archivo', 2.20, 67, 15, 1, '2026-05-11 10:07:28'),
	(7, 'TON-HP-001', 'Toner compatible HP', 'Toner compatible para impresoras HP', 'Consumibles', 24.90, 16, 5, 1, '2026-05-11 10:07:28'),
	(8, 'ETQ-100', 'Etiquetas adhesivas', 'Pack de etiquetas adhesivas multiuso', 'Oficina', 3.75, 80, 15, 1, '2026-05-11 10:07:28');

-- Volcando estructura para tabla qumi_pedidos.roles
CREATE TABLE IF NOT EXISTS `roles` (
  `id_rol` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(30) NOT NULL,
  `descripcion` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla qumi_pedidos.roles: ~4 rows (aproximadamente)
INSERT INTO `roles` (`id_rol`, `nombre`, `descripcion`) VALUES
	(1, 'ADMIN', 'Acceso completo al sistema'),
	(2, 'COMERCIAL', 'Gestion de clientes, productos y pedidos'),
	(3, 'OFICINA', 'Gestion administrativa de pedidos'),
	(4, 'REPARTIDOR', 'Vista y actualizacion de reparto');

-- Volcando estructura para tabla qumi_pedidos.rutas_reparto
CREATE TABLE IF NOT EXISTS `rutas_reparto` (
  `id_ruta` int(11) NOT NULL AUTO_INCREMENT,
  `id_pedido` int(11) NOT NULL,
  `id_repartidor` int(11) NOT NULL,
  `fecha_asignacion` datetime NOT NULL DEFAULT current_timestamp(),
  `fecha_salida` datetime DEFAULT NULL,
  `fecha_entrega` datetime DEFAULT NULL,
  `estado_reparto` enum('ASIGNADO','EN_CAMINO','ENTREGADO','INCIDENCIA') NOT NULL DEFAULT 'ASIGNADO',
  `incidencia` text DEFAULT NULL,
  PRIMARY KEY (`id_ruta`),
  UNIQUE KEY `id_pedido` (`id_pedido`),
  KEY `idx_rutas_repartidor` (`id_repartidor`),
  KEY `idx_rutas_estado` (`estado_reparto`),
  KEY `idx_rutas_fecha` (`fecha_asignacion`),
  CONSTRAINT `fk_rutas_pedidos` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id_pedido`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_rutas_repartidor` FOREIGN KEY (`id_repartidor`) REFERENCES `usuarios` (`id_usuario`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla qumi_pedidos.rutas_reparto: ~1 rows (aproximadamente)
INSERT INTO `rutas_reparto` (`id_ruta`, `id_pedido`, `id_repartidor`, `fecha_asignacion`, `fecha_salida`, `fecha_entrega`, `estado_reparto`, `incidencia`) VALUES
	(1, 3, 4, '2026-05-11 10:07:28', NULL, NULL, 'ASIGNADO', NULL);

-- Volcando estructura para procedimiento qumi_pedidos.sp_asignar_repartidor
DELIMITER //
CREATE PROCEDURE `sp_asignar_repartidor`(
  IN p_id_pedido INT,
  IN p_id_repartidor INT,
  IN p_id_usuario INT
)
BEGIN
  DECLARE v_rol_repartidor VARCHAR(30);

  SELECT r.nombre
  INTO v_rol_repartidor
  FROM usuarios u
  INNER JOIN roles r ON r.id_rol = u.id_rol
  WHERE u.id_usuario = p_id_repartidor
    AND u.activo = 1;

  IF v_rol_repartidor <> 'REPARTIDOR' THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El usuario seleccionado no es repartidor o no esta activo.';
  END IF;

  INSERT INTO rutas_reparto (id_pedido, id_repartidor, estado_reparto)
  VALUES (p_id_pedido, p_id_repartidor, 'ASIGNADO')
  ON DUPLICATE KEY UPDATE
    id_repartidor = VALUES(id_repartidor),
    fecha_asignacion = CURRENT_TIMESTAMP,
    estado_reparto = 'ASIGNADO',
    incidencia = NULL;

  CALL sp_cambiar_estado_pedido(
    p_id_pedido,
    'LISTO_REPARTO',
    p_id_usuario,
    'Pedido asignado a repartidor'
  );
END//
DELIMITER ;

-- Volcando estructura para procedimiento qumi_pedidos.sp_cambiar_estado_pedido
DELIMITER //
CREATE PROCEDURE `sp_cambiar_estado_pedido`(
  IN p_id_pedido INT,
  IN p_estado_nuevo VARCHAR(30),
  IN p_id_usuario INT,
  IN p_comentario VARCHAR(255)
)
BEGIN
  DECLARE v_estado_anterior VARCHAR(30);

  SELECT estado
  INTO v_estado_anterior
  FROM pedidos
  WHERE id_pedido = p_id_pedido;

  IF v_estado_anterior IS NULL THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El pedido no existe.';
  END IF;

  UPDATE pedidos
  SET estado = p_estado_nuevo
  WHERE id_pedido = p_id_pedido;

  INSERT INTO historial_estado_pedido (
    id_pedido,
    estado_anterior,
    estado_nuevo,
    id_usuario,
    comentario
  ) VALUES (
    p_id_pedido,
    v_estado_anterior,
    p_estado_nuevo,
    p_id_usuario,
    p_comentario
  );
END//
DELIMITER ;

-- Volcando estructura para procedimiento qumi_pedidos.sp_recalcular_total_pedido
DELIMITER //
CREATE PROCEDURE `sp_recalcular_total_pedido`(IN p_id_pedido INT)
BEGIN
  UPDATE pedidos
  SET total = COALESCE((
    SELECT SUM(subtotal)
    FROM pedido_detalle
    WHERE id_pedido = p_id_pedido
  ), 0.00)
  WHERE id_pedido = p_id_pedido;
END//
DELIMITER ;

-- Volcando estructura para tabla qumi_pedidos.usuarios
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(120) DEFAULT NULL,
  `id_rol` int(11) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_creacion` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_usuarios_roles` (`id_rol`),
  CONSTRAINT `fk_usuarios_roles` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla qumi_pedidos.usuarios: ~4 rows (aproximadamente)
INSERT INTO `usuarios` (`id_usuario`, `username`, `password_hash`, `nombre`, `email`, `id_rol`, `activo`, `fecha_creacion`) VALUES
	(1, 'admin', 'admin123', 'Administrador Qumi', 'admin@qumi.local', 1, 1, '2026-05-11 10:07:28'),
	(2, 'comercial', 'comercial123', 'Usuario Comercial', 'comercial@qumi.local', 2, 1, '2026-05-11 10:07:28'),
	(3, 'oficina', 'oficina123', 'Usuario Oficina', 'oficina@qumi.local', 3, 1, '2026-05-11 10:07:28'),
	(4, 'reparto', 'reparto123', 'Usuario Reparto', 'reparto@qumi.local', 4, 1, '2026-05-11 10:07:28');

-- Volcando estructura para vista qumi_pedidos.v_dashboard_indicadores
-- Creando tabla temporal para superar errores de dependencia de VIEW
CREATE TABLE `v_dashboard_indicadores` (
	`pedidos_totales` BIGINT(21) NULL,
	`pedidos_pendientes` BIGINT(21) NULL,
	`pedidos_en_preparacion` BIGINT(21) NULL,
	`pedidos_listos_reparto` BIGINT(21) NULL,
	`pedidos_en_reparto` BIGINT(21) NULL,
	`pedidos_entregados` BIGINT(21) NULL,
	`pedidos_urgentes_abiertos` BIGINT(21) NULL,
	`clientes_activos` BIGINT(21) NULL,
	`productos_activos` BIGINT(21) NULL,
	`productos_bajo_stock` BIGINT(21) NULL,
	`facturacion_total` DECIMAL(32,2) NULL
);

-- Volcando estructura para vista qumi_pedidos.v_pedidos_completo
-- Creando tabla temporal para superar errores de dependencia de VIEW
CREATE TABLE `v_pedidos_completo` (
	`id_pedido` INT(11) NOT NULL,
	`fecha_pedido` DATETIME NOT NULL,
	`fecha_entrega_estimada` DATE NULL,
	`urgencia` ENUM('BAJA','MEDIA','ALTA','CRITICA') NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`estado` ENUM('PENDIENTE','EN_PREPARACION','LISTO_REPARTO','EN_REPARTO','ENTREGADO','CANCELADO') NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`total` DECIMAL(10,2) NOT NULL,
	`id_cliente` INT(11) NOT NULL,
	`cliente` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`telefono_cliente` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`email_cliente` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`direccion_entrega` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`creado_por` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`lineas_pedido` BIGINT(21) NOT NULL,
	`total_articulos` DECIMAL(32,0) NULL
);

-- Volcando estructura para vista qumi_pedidos.v_productos_bajo_stock
-- Creando tabla temporal para superar errores de dependencia de VIEW
CREATE TABLE `v_productos_bajo_stock` (
	`id_producto` INT(11) NOT NULL,
	`codigo` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`nombre` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`categoria` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`stock` INT(11) NOT NULL,
	`stock_minimo` INT(11) NOT NULL,
	`precio` DECIMAL(10,2) NOT NULL
);

-- Volcando estructura para vista qumi_pedidos.v_reparto
-- Creando tabla temporal para superar errores de dependencia de VIEW
CREATE TABLE `v_reparto` (
	`id_ruta` INT(11) NOT NULL,
	`id_pedido` INT(11) NOT NULL,
	`estado_pedido` ENUM('PENDIENTE','EN_PREPARACION','LISTO_REPARTO','EN_REPARTO','ENTREGADO','CANCELADO') NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`estado_reparto` ENUM('ASIGNADO','EN_CAMINO','ENTREGADO','INCIDENCIA') NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`fecha_asignacion` DATETIME NOT NULL,
	`fecha_salida` DATETIME NULL,
	`fecha_entrega` DATETIME NULL,
	`repartidor` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`cliente` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`telefono_cliente` VARCHAR(1) NULL COLLATE 'utf8mb4_unicode_ci',
	`direccion_entrega` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`ciudad` VARCHAR(1) NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`urgencia` ENUM('BAJA','MEDIA','ALTA','CRITICA') NOT NULL COLLATE 'utf8mb4_unicode_ci',
	`total` DECIMAL(10,2) NOT NULL,
	`incidencia` TEXT NULL COLLATE 'utf8mb4_unicode_ci'
);

-- Volcando estructura para disparador qumi_pedidos.trg_pedido_detalle_ad
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER trg_pedido_detalle_ad
AFTER DELETE ON pedido_detalle
FOR EACH ROW
BEGIN
  UPDATE productos
  SET stock = stock + OLD.cantidad
  WHERE id_producto = OLD.id_producto;

  CALL sp_recalcular_total_pedido(OLD.id_pedido);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Volcando estructura para disparador qumi_pedidos.trg_pedido_detalle_ai
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER trg_pedido_detalle_ai
AFTER INSERT ON pedido_detalle
FOR EACH ROW
BEGIN
  UPDATE productos
  SET stock = stock - NEW.cantidad
  WHERE id_producto = NEW.id_producto;

  CALL sp_recalcular_total_pedido(NEW.id_pedido);
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Volcando estructura para disparador qumi_pedidos.trg_pedido_detalle_au
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER trg_pedido_detalle_au
AFTER UPDATE ON pedido_detalle
FOR EACH ROW
BEGIN
  IF NEW.id_producto = OLD.id_producto THEN
    UPDATE productos
    SET stock = stock - (NEW.cantidad - OLD.cantidad)
    WHERE id_producto = NEW.id_producto;
  ELSE
    UPDATE productos
    SET stock = stock + OLD.cantidad
    WHERE id_producto = OLD.id_producto;

    UPDATE productos
    SET stock = stock - NEW.cantidad
    WHERE id_producto = NEW.id_producto;
  END IF;

  CALL sp_recalcular_total_pedido(NEW.id_pedido);

  IF NEW.id_pedido <> OLD.id_pedido THEN
    CALL sp_recalcular_total_pedido(OLD.id_pedido);
  END IF;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Volcando estructura para disparador qumi_pedidos.trg_pedido_detalle_bi
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER trg_pedido_detalle_bi
BEFORE INSERT ON pedido_detalle
FOR EACH ROW
BEGIN
  DECLARE v_precio DECIMAL(10,2);
  DECLARE v_stock INT;
  DECLARE v_activo TINYINT(1);

  IF NEW.cantidad <= 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'La cantidad debe ser mayor que 0.';
  END IF;

  SELECT precio, stock, activo
  INTO v_precio, v_stock, v_activo
  FROM productos
  WHERE id_producto = NEW.id_producto;

  IF v_activo = 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'El producto no esta activo.';
  END IF;

  IF v_stock < NEW.cantidad THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Stock insuficiente para el producto.';
  END IF;

  IF NEW.precio_unitario IS NULL OR NEW.precio_unitario <= 0 THEN
    SET NEW.precio_unitario = v_precio;
  END IF;

  SET NEW.subtotal = NEW.cantidad * NEW.precio_unitario;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Volcando estructura para disparador qumi_pedidos.trg_pedido_detalle_bu
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER trg_pedido_detalle_bu
BEFORE UPDATE ON pedido_detalle
FOR EACH ROW
BEGIN
  DECLARE v_stock INT;
  DECLARE v_activo TINYINT(1);
  DECLARE v_diferencia INT;

  IF NEW.cantidad <= 0 THEN
    SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'La cantidad debe ser mayor que 0.';
  END IF;

  IF NEW.id_producto = OLD.id_producto THEN
    SET v_diferencia = NEW.cantidad - OLD.cantidad;

    IF v_diferencia > 0 THEN
      SELECT stock, activo
      INTO v_stock, v_activo
      FROM productos
      WHERE id_producto = NEW.id_producto;

      IF v_activo = 0 THEN
        SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'El producto no esta activo.';
      END IF;

      IF v_stock < v_diferencia THEN
        SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'Stock insuficiente para aumentar la cantidad.';
      END IF;
    END IF;
  ELSE
    SELECT stock, activo
    INTO v_stock, v_activo
    FROM productos
    WHERE id_producto = NEW.id_producto;

    IF v_activo = 0 THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El producto no esta activo.';
    END IF;

    IF v_stock < NEW.cantidad THEN
      SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stock insuficiente para cambiar el producto.';
    END IF;
  END IF;

  SET NEW.subtotal = NEW.cantidad * NEW.precio_unitario;
END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Eliminando tabla temporal y crear estructura final de VIEW
DROP TABLE IF EXISTS `v_dashboard_indicadores`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_dashboard_indicadores` AS SELECT
  (SELECT COUNT(*) FROM pedidos) AS pedidos_totales,
  (SELECT COUNT(*) FROM pedidos WHERE estado = 'PENDIENTE') AS pedidos_pendientes,
  (SELECT COUNT(*) FROM pedidos WHERE estado = 'EN_PREPARACION') AS pedidos_en_preparacion,
  (SELECT COUNT(*) FROM pedidos WHERE estado = 'LISTO_REPARTO') AS pedidos_listos_reparto,
  (SELECT COUNT(*) FROM pedidos WHERE estado = 'EN_REPARTO') AS pedidos_en_reparto,
  (SELECT COUNT(*) FROM pedidos WHERE estado = 'ENTREGADO') AS pedidos_entregados,
  (SELECT COUNT(*) FROM pedidos WHERE urgencia IN ('ALTA','CRITICA') AND estado <> 'ENTREGADO') AS pedidos_urgentes_abiertos,
  (SELECT COUNT(*) FROM clientes WHERE activo = 1) AS clientes_activos,
  (SELECT COUNT(*) FROM productos WHERE activo = 1) AS productos_activos,
  (SELECT COUNT(*) FROM v_productos_bajo_stock) AS productos_bajo_stock,
  (SELECT COALESCE(SUM(total), 0) FROM pedidos WHERE estado <> 'CANCELADO') AS facturacion_total 
;

-- Eliminando tabla temporal y crear estructura final de VIEW
DROP TABLE IF EXISTS `v_pedidos_completo`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_pedidos_completo` AS SELECT
  p.id_pedido,
  p.fecha_pedido,
  p.fecha_entrega_estimada,
  p.urgencia,
  p.estado,
  p.total,
  c.id_cliente,
  c.nombre AS cliente,
  c.telefono AS telefono_cliente,
  c.email AS email_cliente,
  p.direccion_entrega,
  u.nombre AS creado_por,
  COUNT(d.id_detalle) AS lineas_pedido,
  COALESCE(SUM(d.cantidad), 0) AS total_articulos
FROM pedidos p
INNER JOIN clientes c ON c.id_cliente = p.id_cliente
INNER JOIN usuarios u ON u.id_usuario = p.id_usuario_creador
LEFT JOIN pedido_detalle d ON d.id_pedido = p.id_pedido
GROUP BY
  p.id_pedido,
  p.fecha_pedido,
  p.fecha_entrega_estimada,
  p.urgencia,
  p.estado,
  p.total,
  c.id_cliente,
  c.nombre,
  c.telefono,
  c.email,
  p.direccion_entrega,
  u.nombre 
;

-- Eliminando tabla temporal y crear estructura final de VIEW
DROP TABLE IF EXISTS `v_productos_bajo_stock`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_productos_bajo_stock` AS SELECT
  id_producto,
  codigo,
  nombre,
  categoria,
  stock,
  stock_minimo,
  precio
FROM productos
WHERE activo = 1
  AND stock <= stock_minimo 
;

-- Eliminando tabla temporal y crear estructura final de VIEW
DROP TABLE IF EXISTS `v_reparto`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `v_reparto` AS SELECT
  r.id_ruta,
  p.id_pedido,
  p.estado AS estado_pedido,
  r.estado_reparto,
  r.fecha_asignacion,
  r.fecha_salida,
  r.fecha_entrega,
  rep.nombre AS repartidor,
  c.nombre AS cliente,
  c.telefono AS telefono_cliente,
  p.direccion_entrega,
  c.ciudad,
  p.urgencia,
  p.total,
  r.incidencia
FROM rutas_reparto r
INNER JOIN pedidos p ON p.id_pedido = r.id_pedido
INNER JOIN clientes c ON c.id_cliente = p.id_cliente
INNER JOIN usuarios rep ON rep.id_usuario = r.id_repartidor 
;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
