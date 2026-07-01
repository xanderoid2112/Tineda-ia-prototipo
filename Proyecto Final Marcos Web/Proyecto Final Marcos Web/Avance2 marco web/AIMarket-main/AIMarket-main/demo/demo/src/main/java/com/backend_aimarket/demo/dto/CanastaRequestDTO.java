package com.backend_aimarket.demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public class CanastaRequestDTO {

    @NotNull(message = "El ID de usuario es obligatorio")
    @Positive(message = "El ID de usuario debe ser positivo")
    private Long usuario_id;  // Cambiar de usuarioId a usuario_id

    @NotNull(message = "El presupuesto es obligatorio")
    @DecimalMin(value = "0.01", message = "El presupuesto debe ser mayor a 0")
    private BigDecimal presupuesto;

    private List<Long> productos_deseados;  // Cambiar de productosDeseados a productos_deseados

    private PreferenciasDTO preferencias;

    // Constructores
    public CanastaRequestDTO() {}

    public CanastaRequestDTO(Long usuario_id, BigDecimal presupuesto, List<Long> productos_deseados, PreferenciasDTO preferencias) {
        this.usuario_id = usuario_id;
        this.presupuesto = presupuesto;
        this.productos_deseados = productos_deseados;
        this.preferencias = preferencias;
    }

    // Getters y Setters
    public Long getUsuario_id() { return usuario_id; }
    public void setUsuario_id(Long usuario_id) { this.usuario_id = usuario_id; }

    public BigDecimal getPresupuesto() { return presupuesto; }
    public void setPresupuesto(BigDecimal presupuesto) { this.presupuesto = presupuesto; }

    public List<Long> getProductos_deseados() { return productos_deseados; }
    public void setProductos_deseados(List<Long> productos_deseados) { this.productos_deseados = productos_deseados; }

    public PreferenciasDTO getPreferencias() { return preferencias; }
    public void setPreferencias(PreferenciasDTO preferencias) { this.preferencias = preferencias; }
}