package com.backend_aimarket.demo.controller;

import com.backend_aimarket.demo.model.Categoria;
import com.backend_aimarket.demo.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<Categoria> getAll() {
        return categoriaService.getAll();
    }

    @PostMapping
    public ResponseEntity<Categoria> create(@RequestBody Categoria categoria) {
        Categoria saved = categoriaService.create(categoria);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable Long id, @RequestBody Categoria categoria) {
        Categoria updated = categoriaService.update(id, categoria);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
