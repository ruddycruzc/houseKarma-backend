
package com.housekarma.housekarma.usuario.repository;

import com.housekarma.housekarma.usuario.entity.Rol;
import com.housekarma.housekarma.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

      Optional<Usuario> findByEmail(String email);

      boolean existsByEmail(String email);

      @Query("""
                  SELECT u
                  FROM Usuario u
                  WHERE u.rol = :rol
                    AND (
                        LOWER(u.nombre) LIKE LOWER(CONCAT('%', :termino, '%'))
                        OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :termino, '%'))
                    )
                  """)
      List<Usuario> buscarPorRolYNombreOApellidos(
                  @Param("rol") Rol rol,
                  @Param("termino") String termino);
}
