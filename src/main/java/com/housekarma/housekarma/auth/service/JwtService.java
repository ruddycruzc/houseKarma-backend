package com.housekarma.housekarma.auth.service;

import com.housekarma.housekarma.usuario.entity.Usuario;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String generarToken(Usuario usuario) {
        throw new UnsupportedOperationException(
                "La generación JWT se implementará en el siguiente paso."
        );
    }
}