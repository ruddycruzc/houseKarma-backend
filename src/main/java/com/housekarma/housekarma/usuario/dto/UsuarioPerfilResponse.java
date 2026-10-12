package com.housekarma.housekarma.usuario.dto;

public record UsuarioPerfilResponse(
        String nombre,
        String apellidos,
        String avatar,
        String rol,
        String email
) {
}