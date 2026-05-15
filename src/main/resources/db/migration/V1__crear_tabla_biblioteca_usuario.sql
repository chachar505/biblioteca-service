CREATE TABLE biblioteca_usuario (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario        BIGINT      NOT NULL,
    id_juego          BIGINT      NOT NULL,
    fecha_adquisicion DATETIME    NOT NULL,
    estado            VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
UNIQUE KEY uq_usuario_juego (id_usuario, id_juego)
);