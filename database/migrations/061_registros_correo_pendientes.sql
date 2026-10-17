-- Ejecutar en la base RIETI antes de publicar una Lambda con registro por codigo.
-- Solo almacena contrasenas como hashes bcrypt, nunca contrasenas en claro.
CREATE TABLE IF NOT EXISTS registros_correo_pendientes (
  correo VARCHAR(254) NOT NULL,
  nombre VARCHAR(150) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  codigo_hash CHAR(64) NOT NULL,
  intentos TINYINT UNSIGNED NOT NULL DEFAULT 0,
  expira_at DATETIME(6) NOT NULL,
  ultimo_envio_at DATETIME(6) NOT NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (correo),
  INDEX ix_registros_correo_expira (expira_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- Borrar periodicamente retos vencidos (no usuarios), por ejemplo con tarea programada:
-- DELETE FROM registros_correo_pendientes WHERE expira_at < DATE_SUB(UTC_TIMESTAMP(6), INTERVAL 1 DAY);
