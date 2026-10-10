
CREATE TABLE vivienda (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    direccion VARCHAR(255) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(50),
    localidad VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(10) NOT NULL,
    provincia VARCHAR(100) NOT NULL,

    direccion_normalizada VARCHAR(500) NOT NULL UNIQUE,

    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_vivienda_localidad (localidad),
    INDEX idx_vivienda_codigo_postal (codigo_postal)
);

CREATE TABLE experiencia_alquiler (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    vivienda_id BIGINT NOT NULL,
    inquilino_id BIGINT NOT NULL,
    casero_id BIGINT NOT NULL,

    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,

    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_CONFIRMACION',

    confirmacion_inquilino BOOLEAN NOT NULL DEFAULT FALSE,
    confirmacion_casero BOOLEAN NOT NULL DEFAULT FALSE,

    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_experiencia_vivienda
        FOREIGN KEY (vivienda_id)
        REFERENCES vivienda(id),

    CONSTRAINT fk_experiencia_inquilino
        FOREIGN KEY (inquilino_id)
        REFERENCES usuario(id),

    CONSTRAINT fk_experiencia_casero
        FOREIGN KEY (casero_id)
        REFERENCES usuario(id),

    CONSTRAINT chk_experiencia_participantes
        CHECK (inquilino_id <> casero_id),

    CONSTRAINT chk_experiencia_fechas
        CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),

    CONSTRAINT chk_experiencia_estado
        CHECK (
            estado IN (
                'PENDIENTE_CONFIRMACION',
                'CONFIRMADA',
                'FINALIZADA',
                'CANCELADA'
            )
        ),

    INDEX idx_experiencia_vivienda (vivienda_id),
    INDEX idx_experiencia_inquilino (inquilino_id),
    INDEX idx_experiencia_casero (casero_id),
    INDEX idx_experiencia_estado (estado)
);
