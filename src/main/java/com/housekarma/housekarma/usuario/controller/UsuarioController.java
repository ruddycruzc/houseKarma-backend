
package com.housekarma.housekarma.usuario.controller;

import com.housekarma.housekarma.usuario.dto.UsuarioPerfilResponse;
import com.housekarma.housekarma.usuario.dto.UsuarioPublicoResponse;
import com.housekarma.housekarma.usuario.dto.UsuarioRegistroRequest;
import com.housekarma.housekarma.usuario.dto.UsuarioResponse;
import com.housekarma.housekarma.usuario.entity.Usuario;
import com.housekarma.housekarma.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.housekarma.housekarma.usuario.dto.UsuarioPerfilActualizarRequest;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.housekarma.housekarma.usuario.dto.UsuarioBusquedaResponse;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrarUsuario(
            @Valid @RequestBody UsuarioRegistroRequest request) {
        Usuario usuario = usuarioService.registrarUsuario(request);

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getAvatar(),
                usuario.getRol().name());
    }

    @GetMapping("/buscar")
    public UsuarioPerfilResponse buscarPorEmail(
            @RequestParam String email) {

        Usuario usuario = usuarioService.buscarPorEmail(email);

        return new UsuarioPerfilResponse(
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getAvatar(),
                usuario.getRol().name(),
                usuario.getEmail());
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obtenerMiPerfil(
            Authentication authentication) {

        String email = authentication.getName();

        Usuario usuario = usuarioService.buscarPorEmail(email);

        UsuarioResponse response = new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getAvatar(),
                usuario.getRol().name());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<UsuarioResponse> actualizarMiPerfil(
            Authentication authentication,
            @Valid @RequestBody UsuarioPerfilActualizarRequest request) {

        String email = authentication.getName();

        Usuario usuario = usuarioService.actualizarPerfil(email, request);

        UsuarioResponse response = new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getAvatar(),
                usuario.getRol().name());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/buscar-usuarios")
    public ResponseEntity<List<UsuarioBusquedaResponse>> buscarUsuariosPorNombre(
            @RequestParam String termino,
            Authentication authentication) {

        String emailUsuarioActual = authentication.getName();

        List<Usuario> usuarios = usuarioService.buscarUsuariosPorNombre(
                termino,
                emailUsuarioActual);

        List<UsuarioBusquedaResponse> respuesta = usuarios.stream()
                .map(usuario -> new UsuarioBusquedaResponse(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getApellidos(),
                        usuario.getAvatar(),
                        usuario.getRol().name()))
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioPublicoResponse> obtenerPerfilPublico(
            @PathVariable Long id) {

        Usuario usuario = usuarioService.buscarPorId(id);

        UsuarioPublicoResponse response = new UsuarioPublicoResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getAvatar(),
                usuario.getRol().name());

        return ResponseEntity.ok(response);
    }

}
