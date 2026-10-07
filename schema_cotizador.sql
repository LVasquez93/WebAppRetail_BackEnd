-- =============================================================================
-- ESQUEMA RELACIONAL: SISTEMA COTIZADOR MULTI-TENANT (WEBAPP RETAIL)
-- Compatible para importación directa en MySQL Workbench (Reverse Engineer Script)
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS `cotizador_db` DEFAULT CHARACTER SET utf8mb4;
USE `cotizador_db`;

-- -----------------------------------------------------------------------------
-- 1. TABLA: EMPRESAS (Tenants / Organizaciones)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `empresas` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(150) NOT NULL,
  `razon_social` VARCHAR(200) NULL,
  `nit` VARCHAR(30) NULL,
  `telefono` VARCHAR(50) NULL,
  `correo` VARCHAR(100) NULL,
  `direccion` VARCHAR(250) NULL,
  `logo_base64` LONGTEXT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 2. TABLA: SUCURSALES (Sedes operativas por Empresa)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sucursales` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `empresa_id` BIGINT NULL,
  `codigo` VARCHAR(50) NOT NULL,
  `nombre` VARCHAR(150) NOT NULL,
  `razon_social` VARCHAR(200) NOT NULL,
  `nombre_comercial` VARCHAR(200) NULL,
  `direccion` VARCHAR(250) NULL,
  `telefono` VARCHAR(50) NULL,
  `correo` VARCHAR(100) NULL,
  `prefijo_cotizacion` VARCHAR(20) NOT NULL DEFAULT 'COT',
  `header_banner_base64` LONGTEXT NULL,
  `footer_banner_base64` LONGTEXT NULL,
  `firma_base64` LONGTEXT NULL,
  `nombre_firmante` VARCHAR(150) NULL,
  `cargo_firmante` VARCHAR(150) NULL,
  `forma_pago_predeterminada` VARCHAR(250) NULL,
  `nota_predeterminada` TEXT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sucursal_codigo` (`codigo`),
  CONSTRAINT `fk_sucursales_empresa`
    FOREIGN KEY (`empresa_id`)
    REFERENCES `empresas` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 3. TABLA: USUARIOS (SuperAdmins, Gerentes y Ejecutivos de Ventas)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(120) NOT NULL,
  `nombre_completo` VARCHAR(150) NOT NULL,
  `correo` VARCHAR(100) NULL,
  `cargo` VARCHAR(100) NULL,
  `rol` VARCHAR(50) NOT NULL DEFAULT 'ROLE_VENTAS',
  `empresa_id` BIGINT NULL,
  `sucursal_id` BIGINT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_usuarios_username` (`username`),
  CONSTRAINT `fk_usuarios_empresa`
    FOREIGN KEY (`empresa_id`)
    REFERENCES `empresas` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_usuarios_sucursal`
    FOREIGN KEY (`sucursal_id`)
    REFERENCES `sucursales` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 4. TABLA: CLIENTES (Catálogo de clientes segregado)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `clientes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `razon_social` VARCHAR(200) NOT NULL,
  `nombre_comercial` VARCHAR(200) NULL,
  `contacto_principal` VARCHAR(150) NULL,
  `telefono` VARCHAR(50) NULL,
  `correo` VARCHAR(100) NULL,
  `direccion` VARCHAR(255) NULL,
  `empresa_id` BIGINT NULL,
  `sucursal_id` BIGINT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_clientes_empresa`
    FOREIGN KEY (`empresa_id`)
    REFERENCES `empresas` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_clientes_sucursal`
    FOREIGN KEY (`sucursal_id`)
    REFERENCES `sucursales` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 5. TABLA: EQUIPOS (Catálogo de productos/equipos y especificaciones)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `equipos` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `descripcion` TEXT NOT NULL,
  `part_number` VARCHAR(100) NULL,
  `caracteristicas` TEXT NULL,
  `precio_referencial` DECIMAL(12,2) NULL,
  `tiempo_entrega_predeterminado` VARCHAR(80) NULL,
  `categoria` VARCHAR(100) NULL,
  `empresa_id` BIGINT NULL,
  `sucursal_id` BIGINT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_equipos_empresa`
    FOREIGN KEY (`empresa_id`)
    REFERENCES `empresas` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_equipos_sucursal`
    FOREIGN KEY (`sucursal_id`)
    REFERENCES `sucursales` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 6. TABLA: COTIZACIONES (Encabezado y totales de cotizaciones)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cotizaciones` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `codigo_cotizacion` VARCHAR(30) NOT NULL,
  `usuario_emisor` VARCHAR(50) NOT NULL,
  `fecha_emision` DATE NOT NULL,
  `contacto_cliente` VARCHAR(150) NOT NULL,
  `razon_social_cliente` VARCHAR(200) NOT NULL,
  `nombre_comercial` VARCHAR(200) NULL,
  `forma_pago` VARCHAR(250) NOT NULL,
  `nota_importante` TEXT NULL,
  `subtotal_sin_iva` DECIMAL(12,2) NOT NULL,
  `monto_iva` DECIMAL(12,2) NOT NULL,
  `total_inversion` DECIMAL(12,2) NOT NULL,
  `total_en_letras` VARCHAR(255) NOT NULL,
  `empresa_id` BIGINT NULL,
  `sucursal_id` BIGINT NULL,
  `fecha_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cotizaciones_codigo` (`codigo_cotizacion`),
  CONSTRAINT `fk_cotizaciones_empresa`
    FOREIGN KEY (`empresa_id`)
    REFERENCES `empresas` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT `fk_cotizaciones_sucursal`
    FOREIGN KEY (`sucursal_id`)
    REFERENCES `sucursales` (`id`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 7. TABLA: COTIZACION_ITEMS (Detalle de líneas cotizadas)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `cotizacion_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `cotizacion_id` BIGINT NOT NULL,
  `item_numero` INT NOT NULL,
  `descripcion_equipo` TEXT NOT NULL,
  `part_number` VARCHAR(100) NULL,
  `caracteristicas` TEXT NULL,
  `tiempo_entrega` VARCHAR(80) NOT NULL,
  `cantidad` INT NOT NULL,
  `precio_unitario` DECIMAL(12,2) NOT NULL,
  `total_linea` DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_cotizacion_items_cotizacion`
    FOREIGN KEY (`cotizacion_id`)
    REFERENCES `cotizaciones` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
