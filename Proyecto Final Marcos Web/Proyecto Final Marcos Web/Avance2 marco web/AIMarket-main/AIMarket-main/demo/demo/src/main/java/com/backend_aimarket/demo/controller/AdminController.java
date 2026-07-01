package com.backend_aimarket.demo.controller;

import com.backend_aimarket.demo.model.Producto;
import com.backend_aimarket.demo.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ProductoService productoService;

    public AdminController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // ========== ENDPOINTS DE CATEGORÍAS ==========

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> obtenerCategorias() {
        try {
            List<String> categorias = productoService.obtenerCategorias();
            return ResponseEntity.ok(categorias);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== ENDPOINTS DE PRODUCTOS ==========

    @GetMapping("/productos")
    public ResponseEntity<List<Producto>> obtenerTodosProductos() {
        try {
            List<Producto> productos = productoService.obtenerTodosProductos();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        try {
            Producto nuevoProducto = productoService.crearProducto(producto);
            return ResponseEntity.ok(nuevoProducto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(id, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/productos/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}