
package com.housekarma.housekarma.usuario.service;

import com.housekarma.housekarma.usuario.dto.UsuarioRegistroRequest;
import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.housekarma.housekarma.usuario.dto.UsuarioPerfilActualizarRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrarUsuario(UsuarioRegistroRequest request) {
        Rol rol;

        try {
            rol = Rol.valueOf(request.rol());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new IllegalArgumentException("El rol indicado no es válido");
        }

        if (rol != Rol.INQUILINO && rol != Rol.CASERO) {
            throw new IllegalArgumentException(
                    "El registro público solo permite los roles INQUILINO y CASERO");
        }

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailYaRegistradoException(request.email());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setApellidos(request.apellidos());
        usuario.setEmail(request.email());
        usuario.setTelefono(request.telefono());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(rol);
        usuario.setFechaRegistro(LocalDateTime.now());

        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(email));
    }
    public List<Usuario> buscarUsuariosPorNombre(String termino, String emailUsuarioActual) {
    if (termino == null || termino.isBlank()) {
        throw new IllegalArgumentException(
                "El término de búsqueda no puede estar vacío"
        );
    }

    Usuario usuarioActual = buscarPorEmail(emailUsuarioActual);

    Rol rolBuscado;

    if (usuarioActual.getRol() == Rol.INQUILINO) {
        rolBuscado = Rol.CASERO;
    } else if (usuarioActual.getRol() == Rol.CASERO) {
        rolBuscado = Rol.INQUILINO;
    } else {
        throw new IllegalArgumentException(
                "El administrador no puede realizar esta búsqueda"
        );
    }

    return usuarioRepository.buscarPorRolYNombreOApellidos(
            rolBuscado,
            termino.trim()
    );
}

    public Usuario actualizarPerfil(
            String email,
            UsuarioPerfilActualizarRequest request) {

        Usuario usuario = buscarPorEmail(email);

        usuario.setNombre(request.nombre());
        usuario.setApellidos(request.apellidos());
        usuario.setTelefono(request.telefono());
        usuario.setAvatar(request.avatar());

        return usuarioRepository.save(usuario);
    }

}
