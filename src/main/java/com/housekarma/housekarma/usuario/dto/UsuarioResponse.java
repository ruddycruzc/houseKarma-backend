
package com.housekarma.housekarma.usuario.dto;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellidos,
        String email,
        String telefono,
        String avatar,
        String rol
) {
}
