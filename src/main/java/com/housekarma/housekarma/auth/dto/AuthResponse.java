package com.housekarma.housekarma.auth.dto;

public record AuthResponse(
        String token,
        String email,
        String rol
) {
}