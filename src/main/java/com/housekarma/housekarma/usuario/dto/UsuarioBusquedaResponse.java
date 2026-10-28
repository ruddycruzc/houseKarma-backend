package com.housekarma.housekarma.usuario.dto;

public record UsuarioBusquedaResponse(
        Long id,
        String nombre,
        String apellidos,
        String avatar,
        String rol
) {
}