package com.backend_aimarket.demo.controller;

import com.backend_aimarket.demo.model.Producto;
import com.backend_aimarket.demo.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerProductosActivos() {
        try {
            List<Producto> productos = productoService.obtenerProductosActivos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        try {
            return productoService.obtenerProductoPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/categoria/{categoria}") // Cambia de categoriaId a categoria
    public ResponseEntity<List<Producto>> obtenerProductosPorCategoria(@PathVariable String categoria) {
        try {
            List<Producto> productos = productoService.obtenerProductosPorCategoria(categoria);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String q) {
        try {
            List<Producto> productos = productoService.buscarProductos(q);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/marcas")
    public ResponseEntity<List<String>> obtenerMarcas() {
        try {
            List<String> marcas = productoService.obtenerMarcas();
            return ResponseEntity.ok(marcas);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        try {
            List<String> categorias = productoService.obtenerCategorias();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}