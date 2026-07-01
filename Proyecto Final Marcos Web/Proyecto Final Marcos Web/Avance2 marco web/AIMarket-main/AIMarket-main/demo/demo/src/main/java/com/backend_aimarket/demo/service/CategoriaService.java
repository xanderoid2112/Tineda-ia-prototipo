package com.backend_aimarket.demo.service;

import com.backend_aimarket.demo.model.Categoria;
import com.backend_aimarket.demo.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> getAll() {
        return categoriaRepository.findAll();
    }

    public Categoria create(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria update(Long id, Categoria updated) {
        return categoriaRepository.findById(id).map(c -> {
            c.setNombre(updated.getNombre());
            c.setSlug(updated.getSlug());
            return categoriaRepository.save(c);
        }).orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
    }

    public void delete(Long id) {
        categoriaRepository.deleteById(id);
    }
}
