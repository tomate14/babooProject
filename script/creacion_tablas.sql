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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `CLIENTE` (
  `dni` int NOT NULL,
  `nombre` text NOT NULL,
  `fecha_nacimiento` text,
  `direccion` text,
  `telefono` text,
  `email` text,
  `cuit` text NOT NULL,
  `fecha_alta` text,
  PRIMARY KEY (`dni`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `NUMERACION` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero_comprobante` int NOT NULL,
  `tipo_comprobante` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `PAGO` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_pedido` int NOT NULL,
  `fecha_pago` text NOT NULL,
  `valor` int NOT NULL,
  `forma_pago` int NOT NULL,
  `descripcion` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `PEDIDO` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dni_cliente` int NOT NULL,
  `fecha_pedido` text NOT NULL,
  `total` int NOT NULL,
  `estado` varchar(50) NOT NULL,
  `descripcion` text NOT NULL,
  `tipo_pedido` int NOT NULL,
  `estado_envio` int NOT NULL,
  `numero_comprobante` varchar(8) NOT NULL,
  `con_sena` tinyint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
