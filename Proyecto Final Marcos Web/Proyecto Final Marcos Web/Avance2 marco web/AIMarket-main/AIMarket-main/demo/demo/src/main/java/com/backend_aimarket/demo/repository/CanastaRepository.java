package com.backend_aimarket.demo.repository;

import com.backend_aimarket.demo.model.Canasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CanastaRepository extends JpaRepository<Canasta, Long> {

    List<Canasta> findByUsuarioId(Long usuarioId);

    @Query("SELECT c FROM Canasta c WHERE c.usuario.id = :usuarioId ORDER BY c.fechaCreacion DESC")
    List<Canasta> findHistorialByUsuarioId(@Param("usuarioId") Long usuarioId);
}