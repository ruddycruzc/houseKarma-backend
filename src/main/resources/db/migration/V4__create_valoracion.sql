CREATE TABLE valoracion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    experiencia_id BIGINT NOT NULL,
    autor_id BIGINT NOT NULL,
    destinatario_id BIGINT NOT NULL,

    puntuacion TINYINT NOT NULL,
    comentario VARCHAR(1000),

    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_valoracion_experiencia
        FOREIGN KEY (experiencia_id)
        REFERENCES experiencia_alquiler(id),

    CONSTRAINT fk_valoracion_autor
        FOREIGN KEY (autor_id)
        REFERENCES usuario(id),

    CONSTRAINT fk_valoracion_destinatario
        FOREIGN KEY (destinatario_id)
        REFERENCES usuario(id),

    CONSTRAINT uq_valoracion_experiencia_autor
        UNIQUE (experiencia_id, autor_id),

    CONSTRAINT chk_valoracion_puntuacion
        CHECK (puntuacion BETWEEN 1 AND 5),

    CONSTRAINT chk_valoracion_participantes
        CHECK (autor_id <> destinatario_id),

    INDEX idx_valoracion_destinatario (destinatario_id),
    INDEX idx_valoracion_experiencia (experiencia_id)
);