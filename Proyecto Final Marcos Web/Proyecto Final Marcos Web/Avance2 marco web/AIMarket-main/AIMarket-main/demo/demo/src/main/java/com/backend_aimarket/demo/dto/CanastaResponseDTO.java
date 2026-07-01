package com.backend_aimarket.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public class CanastaResponseDTO {

    private List<ProductoCanastaDTO> productos;
    private BigDecimal total;
    private BigDecimal presupuesto;
    private BigDecimal ahorro;
    private Double porcentajeAhorro;
    private Integer productosRecomendados;
    private Integer productosDeseados;

    // Constructores
    public CanastaResponseDTO() {}

    public CanastaResponseDTO(List<ProductoCanastaDTO> productos, BigDecimal total,
                              BigDecimal presupuesto, BigDecimal ahorro,
                              Double porcentajeAhorro, Integer productosRecomendados,
                              Integer productosDeseados) {
        this.productos = productos;
        this.total = total;
        this.presupuesto = presupuesto;
        this.ahorro = ahorro;
        this.porcentajeAhorro = porcentajeAhorro;
        this.productosRecomendados = productosRecomendados;
        this.productosDeseados = productosDeseados;
    }

    // Getters y Setters
    public List<ProductoCanastaDTO> getProductos() { return productos; }
    public void setProductos(List<ProductoCanastaDTO> productos) { this.productos = productos; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getPresupuesto() { return presupuesto; }
    public void setPresupuesto(BigDecimal presupuesto) { this.presupuesto = presupuesto; }

    public BigDecimal getAhorro() { return ahorro; }
    public void setAhorro(BigDecimal ahorro) { this.ahorro = ahorro; }

    public Double getPorcentajeAhorro() { return porcentajeAhorro; }
    public void setPorcentajeAhorro(Double porcentajeAhorro) { this.porcentajeAhorro = porcentajeAhorro; }

    public Integer getProductosRecomendados() { return productosRecomendados; }
    public void setProductosRecomendados(Integer productosRecomendados) { this.productosRecomendados = productosRecomendados; }

    public Integer getProductosDeseados() { return productosDeseados; }
    public void setProductosDeseados(Integer productosDeseados) { this.productosDeseados = productosDeseados; }
}
