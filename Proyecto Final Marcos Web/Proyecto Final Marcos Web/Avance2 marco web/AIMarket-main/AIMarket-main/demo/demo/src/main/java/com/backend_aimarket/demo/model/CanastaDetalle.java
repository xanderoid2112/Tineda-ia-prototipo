package com.backend_aimarket.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "canasta_detalle")
public class CanastaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canasta_id", nullable = false)
    private Canasta canasta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad = 1;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    // Constructores
    public CanastaDetalle() {}

    public CanastaDetalle(Canasta canasta, Producto producto, Integer cantidad) {
        this.canasta = canasta;
        this.producto = producto;
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Canasta getCanasta() { return canasta; }
    public void setCanasta(Canasta canasta) { this.canasta = canasta; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    // Método para calcular subtotal
    public void calcularSubtotal() {
        if (producto != null && producto.getPrecio() != null && cantidad != null) {
            this.subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
        }
    }
}
