
package com.housekarma.housekarma.usuario.repository;

import com.housekarma.housekarma.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

      Optional<Usuario> findByEmail(String email);
}
