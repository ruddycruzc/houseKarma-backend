package com.housekarma.housekarma.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}
