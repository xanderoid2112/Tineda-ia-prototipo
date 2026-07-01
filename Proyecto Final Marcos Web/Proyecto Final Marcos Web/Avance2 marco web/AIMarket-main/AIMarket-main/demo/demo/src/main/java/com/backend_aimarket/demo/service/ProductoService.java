package com.backend_aimarket.demo.service;

import com.backend_aimarket.demo.model.Producto;
import com.backend_aimarket.demo.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // ========== MÉTODOS PÚBLICOS ==========

    public List<Producto> obtenerProductosActivos() {
        // Como no tenemos campo 'activo', devolvemos todos
        return productoRepository.findAll();
    }

    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    public List<Producto> buscarProductos(String query) {
        return productoRepository.findByNombreContainingIgnoreCase(query);
    }

    public List<String> obtenerMarcas() {
        return productoRepository.findDistinctMarcas();
    }

    public List<String> obtenerCategorias() {
        return productoRepository.findDistinctCategorias();
    }

    // ========== MÉTODOS ADMIN ==========

    public List<Producto> obtenerTodosProductos() {
        return productoRepository.findAll();
    }

    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, Producto productoDetails) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        // Actualizar solo los campos que existen en tu modelo
        if (productoDetails.getNombre() != null) {
            producto.setNombre(productoDetails.getNombre());
        }
        if (productoDetails.getCategoria() != null) {
            producto.setCategoria(productoDetails.getCategoria());
        }
        if (productoDetails.getPrecio() != null) {
            producto.setPrecio(productoDetails.getPrecio());
        }
        if (productoDetails.getMarca() != null) {
            producto.setMarca(productoDetails.getMarca());
        }
        if (productoDetails.getDieta() != null) {
            producto.setDieta(productoDetails.getDieta());
        }
        if (productoDetails.getUrlImagen() != null) {
            producto.setUrlImagen(productoDetails.getUrlImagen());
        }

        return productoRepository.save(producto);
    }

    // Eliminamos los métodos de activar/desactivar ya que no tenemos campo 'activo'
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        productoRepository.delete(producto);
    }
}