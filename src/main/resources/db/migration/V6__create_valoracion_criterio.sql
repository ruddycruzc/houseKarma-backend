CREATE TABLE valoracion_criterio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    valoracion_id BIGINT NOT NULL,
    criterio VARCHAR(50) NOT NULL,
    puntuacion INT NOT NULL,

    CONSTRAINT fk_valoracion_criterio_valoracion
        FOREIGN KEY (valoracion_id)
        REFERENCES valoracion(id),

    CONSTRAINT uq_valoracion_criterio
        UNIQUE (valoracion_id, criterio),

    CONSTRAINT chk_valoracion_criterio_puntuacion
        CHECK (puntuacion BETWEEN 1 AND 5),

    INDEX idx_valoracion_criterio_valoracion (valoracion_id)
);