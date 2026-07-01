package com.backend_aimarket.demo.repository;

import com.backend_aimarket.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Métodos básicos sin filtro 'activo'
    List<Producto> findByCategoria(String categoria);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Obtener marcas distintas
    @Query("SELECT DISTINCT p.marca FROM Producto p WHERE p.marca IS NOT NULL")
    List<String> findDistinctMarcas();

    // Obtener categorías distintas
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL")
    List<String> findDistinctCategorias();
}