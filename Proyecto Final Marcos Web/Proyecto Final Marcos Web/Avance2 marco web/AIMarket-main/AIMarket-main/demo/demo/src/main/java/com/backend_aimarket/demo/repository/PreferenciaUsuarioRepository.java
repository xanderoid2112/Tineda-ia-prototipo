package com.backend_aimarket.demo.repository;

import com.backend_aimarket.demo.model.PreferenciaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenciaUsuarioRepository extends JpaRepository<PreferenciaUsuario, Long> {

    List<PreferenciaUsuario> findByUsuarioId(Long usuarioId);

    @Query("SELECT p.valor FROM PreferenciaUsuario p WHERE p.usuario.id = :usuarioId AND p.tipoPreferencia = :tipo")
    List<String> findValoresByUsuarioIdAndTipo(@Param("usuarioId") Long usuarioId, @Param("tipo") String tipo);

    void deleteByUsuarioIdAndTipoPreferenciaAndValor(Long usuarioId, String tipoPreferencia, String valor);
}
