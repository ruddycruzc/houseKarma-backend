
package com.housekarma.housekarma.usuario.repository;

import com.housekarma.housekarma.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
