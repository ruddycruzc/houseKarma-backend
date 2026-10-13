ALTER TABLE vivienda
    ADD COLUMN casero_id BIGINT NULL;

ALTER TABLE vivienda
    ADD CONSTRAINT fk_vivienda_casero
    FOREIGN KEY (casero_id)
    REFERENCES usuario(id);

CREATE INDEX idx_vivienda_casero
    ON vivienda(casero_id);