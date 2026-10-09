CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    avatar VARCHAR(500),
    rol VARCHAR(20) NOT NULL,
    fecha_registro DATETIME NOT NULL
);