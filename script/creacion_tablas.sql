CREATE TABLE `CAJA` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fecha` text NOT NULL,
  `contado` int NOT NULL,
  `tarjeta` int NOT NULL,
  `cuenta_dni` int NOT NULL,
  `transferencia` int NOT NULL,
  `gastos` int NOT NULL,
  `ganancia` int NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `CLIENTE` (
  `dni` int NOT NULL,
  `nombre` text NOT NULL,
  `fecha_nacimiento` text,
  `direccion` text NOT NULL,
  `telefono` text NOT NULL,
  `email` text,
  `cuit` text,
  `fecha_alta` text,
  `tipo_usuario` int NOT NULL,
  `porcentaje_remarcar` int DEFAULT NULL,
  PRIMARY KEY (`dni`)
);

CREATE TABLE `PEDIDO` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dni_cliente` int NOT NULL,
  `fecha_pedido` text NOT NULL,
  `total` int NOT NULL,
  `estado` varchar(50) NOT NULL,
  `tipo_pedido` int NOT NULL,
  `estado_envio` int NOT NULL,
  `numero_comprobante` varchar(8) NOT NULL,
  `con_sena` tinyint NOT NULL,
  `descripcion` text NOT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE `PRODUCTO` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` text NOT NULL,
  `codigo_barra` text,
  `precio_compra` int NOT NULL,
  `precio_venta` int NOT NULL,
  `id_proveedor` int NOT NULL,
  `stock` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_proveedor_idx` (`id_proveedor`),
  CONSTRAINT `id_proveedor` FOREIGN KEY (`id_proveedor`) REFERENCES `CLIENTE` (`dni`)
);

CREATE TABLE `COMPROBANTE_ITEM` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_pedido` int NOT NULL,
  `id_producto` int NOT NULL,
  `stock` int NOT NULL,
  `precio` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_pedido_idx` (`id_pedido`),
  KEY `id_producto_idx` (`id_producto`),
  CONSTRAINT `id_pedido` FOREIGN KEY (`id_pedido`) REFERENCES `PEDIDO` (`id`),
  CONSTRAINT `id_producto` FOREIGN KEY (`id_producto`) REFERENCES `PRODUCTO` (`id`)
);

CREATE TABLE `NUMERACION` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero_comprobante` int NOT NULL,
  `tipo_comprobante` int NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `PAGO` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_pedido` int NOT NULL,
  `fecha_pago` text NOT NULL,
  `valor` int NOT NULL,
  `forma_pago` int NOT NULL,
  `descripcion` text NOT NULL,
  PRIMARY KEY (`id`)
);


CREATE TABLE `TIPO_USUARIO` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
);