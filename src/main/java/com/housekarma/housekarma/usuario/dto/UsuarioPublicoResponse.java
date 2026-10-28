package com.housekarma.housekarma.usuario.dto;

public record UsuarioPublicoResponse(
        Long id,
        String nombre,
        String apellidos,
        String avatar,
        String rol
) {
}
