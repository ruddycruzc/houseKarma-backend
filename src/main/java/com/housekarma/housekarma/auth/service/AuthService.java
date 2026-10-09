package com.housekarma.housekarma.auth.service;

import com.housekarma.housekarma.auth.dto.AuthResponse;
import com.housekarma.housekarma.auth.dto.LoginRequest;
import com.housekarma.housekarma.auth.exception.CredencialesInvalidasException;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(
                request.password(),
                usuario.getPassword()
        )) {
            throw new CredencialesInvalidasException();
        }

        String token = jwtService.generarToken(usuario);

        return new AuthResponse(
                token,
                usuario.getEmail(),
                usuario.getRol().name()
        );
    }
}